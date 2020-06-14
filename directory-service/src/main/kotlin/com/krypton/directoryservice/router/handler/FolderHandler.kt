package com.krypton.directoryservice.router.handler

import com.krypton.directoryservice.client.file.IFileRepositoryClient
import com.krypton.directoryservice.client.folder.IFolderRepositoryClient
import com.krypton.directoryservice.client.folder.IFolderStorageClient
import com.krypton.directoryservice.model.DirectoryItems
import com.krypton.directoryservice.model.FolderResponse
import com.krypton.directoryservice.model.FolderMoveData
import com.krypton.directoryservice.service.folder.FolderMoveOperationService
import common.models.File
import common.models.Folder
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import java.text.SimpleDateFormat
import java.util.*

@Component
class FolderHandler @Autowired constructor(
	private val helper: FolderHandlerHelper,
	private val folderRepository: IFolderRepositoryClient,
	private val folderStorage: IFolderStorageClient
)
{
	/**j
	 * Create folder in storage and save to repository
	 *
	 * @param request	incoming request with parent folder body and "name" param
	 * @return created [Folder]
	 * */
	suspend fun create(request: ServerRequest): ServerResponse
	{
		val folder 	= request.awaitBodyOrNull<Folder>()		// parent folder where to create the new one
		val name 	= request.queryParam("name")		// new folder name

		return if (folder != null && name.isPresent)
			helper.create(name.get(), folder)
		else
			badRequest().buildAndAwait()
	}

	/**
	 * Delete a folder from storage and repository
	 *
	 * @param request 	incoming request with folder "id" query param
	 * @return http status
	 * */
	suspend fun delete(request: ServerRequest): ServerResponse
	{
		val id = request.queryParam("id")

		return if (id.isPresent)
			helper.delete(id.get())
		else
			badRequest().buildAndAwait()
	}

	/**
	 * Move folder from one directory to another
	 *
	 * @param request	incoming request with [FolderMoveData] body
	 * @return updated [Folder] model
	 * */
	suspend fun move(request: ServerRequest) = moveRequest(request, helper::move)

	/**
	 * Copy folder from one directory to another
	 *
	 * @param request	incoming request with [FolderMoveData] body
	 * @return response from [moveRequest]
	 * */
	suspend fun copy(request: ServerRequest) = moveRequest(request, helper::copy)

	suspend fun root(): ServerResponse
	{
		val root = folderRepository.root().body

		return if (root != null)
			ok().bodyValueAndAwait(root)
		else
			notFound().buildAndAwait()
	}

	suspend fun rootStats(): ServerResponse
	{
		val stats = folderStorage.getRootStats("/root")

		return if (stats.body != null)
			ok().bodyValueAndAwait(stats.body)
		else
			notFound().buildAndAwait()
	}

	suspend fun directoryItems(request: ServerRequest): ServerResponse
	{
		val id = request.queryParam("id")

		return if (id.isPresent)
		{
			helper.directoryItems(id.get())
		} else badRequest().buildAndAwait()
	}

	/**
	 * Move operation, main goal is to validate moveData body
	 *
	 * @param request	request with [FolderMoveData] body
	 * @param move		operation to perform
	 * @return [move] response or bad request status
	 * */
	private suspend fun moveRequest(
		request: ServerRequest,
		move: suspend (moveData: FolderMoveData) -> ServerResponse
	): ServerResponse
	{
		val moveData = request.awaitBodyOrNull<FolderMoveData>()

		return if (moveData != null)
			move(moveData)
		else
			badRequest().buildAndAwait()
	}
}

@Component
class FolderHandlerHelper @Autowired constructor(
	private val storage: IFolderStorageClient,
	private val repository: IFolderRepositoryClient,
	private val fileRepository: IFileRepositoryClient,
	private val moveOperationService: FolderMoveOperationService
)
{
	/**
	 * Create folder in storage and repository
	 *
	 * @param name		new folder name
	 * @param folder	parent folder where to create the new one
	 * @return error status or [Folder] model on success
	 * */
	suspend fun create(name: String, folder: Folder): ServerResponse
	{
		// create in storage
		val storageResponse = storage.create(folder, name)
		// check if created in storage
		if (storageResponse.body == null)
			return status(HttpStatus.valueOf(storageResponse.status)).buildAndAwait()

		/** [Folder] model representation of the storage created one */
		val model = folderFromStorageResponse(storageResponse.body, folder)
		// save to repository
		val repositoryResponse = repository.save(model)

		// return saved to repository model or status on error
		return if (repositoryResponse.body == null)
			status(HttpStatus.valueOf(repositoryResponse.status)).buildAndAwait()
		else
			ok().bodyValueAndAwait(repositoryResponse.body)
	}

	/**
	 * Delete folder from storage and repository
	 *
	 * @param id
	 * @return http status
	 * */
	suspend fun delete(id: String): ServerResponse
	{
		val folder = repository.find(id).body
			?: return notFound().buildAndAwait()

		// delete from storage
		val storageResponse = storage.delete(folder)
		// check if deleted in storage
		if (storageResponse != HttpStatus.OK)
			return status(storageResponse).buildAndAwait()

		// delete in repository
		val repositoryResponse = repository.delete(folder.id, true)

		return status(repositoryResponse).buildAndAwait()
	}

	/**
	 * Move a folder from a directory to another one
	 *
	 * @param moveData
	 * @return error status or updated [Folder]
	 * */
	suspend fun move(moveData: FolderMoveData): ServerResponse
	{
		// move in storage
		val storageResponse = storage.move(moveData)
		// check if moved
		if (storageResponse.body == null)
			return status(HttpStatus.valueOf(storageResponse.status)).buildAndAwait()

		// update model in repository
		val repositoryResponse = repository.update(storageResponse.body.apply {
			folder = moveData.targetFolder.id
		})

		return if (repositoryResponse.body == null)
			status(HttpStatus.valueOf(repositoryResponse.status)).buildAndAwait()
		else
		{
			// update the all tree children path, because with shifted all the folder tree
			val childPathUpdate = repository.updateChildPaths(repositoryResponse.body.id)
			// return folder body or error status
			if (childPathUpdate == HttpStatus.OK)
				ok().bodyValueAndAwait(repositoryResponse.body)
			else
				status(childPathUpdate).buildAndAwait()
		}
	}

	suspend fun copy(moveData: FolderMoveData): ServerResponse
	{
		// copy folder in the storage
		val storageResponse = storage.copy(moveData)
		// check if copied
		if (storageResponse.body == null)
			return status(HttpStatus.valueOf(storageResponse.status)).buildAndAwait()

		// save folder copy to repository
		val repositoryResponse = repository.save(storageResponse.body.apply {
			id 		= UUID.randomUUID().toString()	// new id for the copy
			folder 	= moveData.targetFolder.id		// update parent folder
		})

		return when
		{
			// if failed to save to repo
			repositoryResponse.body == null ->
			{
				// status error from repo
				status(HttpStatus.valueOf(repositoryResponse.status)).buildAndAwait()
			}
			// if failed to save copy directory tree
			saveCopyTreeToRepository(repositoryResponse.body) ->
			{
				// 500
				status(HttpStatus.INTERNAL_SERVER_ERROR).buildAndAwait()
			}
			// if success
			else -> ok().bodyValueAndAwait(repositoryResponse.body)
		}
	}

	/**
	 * When copying a folder, we need to save new directory tree items to repostory as well
	 *
	 * @param folder	folder that was copied(parent)
	 * @return boolean depending on success
	 * */
	private suspend fun saveCopyTreeToRepository(folder: Folder): Boolean
	{
		val files 	= arrayListOf<File>()
		val folders = arrayListOf<Folder>()

		// get directory tree from storage
		val storageResponse = storage.getTree(folder)

		if (storageResponse.body != null)
		{
			// fill files and folders list with models build from directory tree data
			moveOperationService.modelsFromDirectory(files, folders, storageResponse.body, folder.id)

			// save all new files and folders to repository
			return coroutineScope {
				val fileRepoResponse 	= async { fileRepository.saveAll(files) }
				val folderRepoResponse 	= async { repository.saveAll(folders) }

				val filesResponse 	= fileRepoResponse.await().body
				val foldersResponse = folderRepoResponse.await().body

				return@coroutineScope !filesResponse.isNullOrEmpty() && !foldersResponse.isNullOrEmpty()
			}
		}
		return false
	}

	suspend fun directoryItems(id: String): ServerResponse = coroutineScope {
		val files 	= async { files(id) }
		val folders = async { folders(id) }

		return@coroutineScope ok().bodyValueAndAwait(DirectoryItems(files.await(), folders.await()))
	}

	/**
	 * Get all [File]'s of an folder
	 *
	 * @param id	folder id
	 * @return [File] list
	 * */
	private suspend fun files(id: String): List<File>
	{
		val fileRepoResponse = fileRepository.all(id)

		return fileRepoResponse.body ?: listOf()
	}

	/**
	 * Get all [Folder]'s of an folder
	 *
	 * @param id	folder id
	 * @return [Folder] list
	 * */
	private suspend fun folders(id: String): List<Folder>
	{
		val folderRepoResponse = repository.all(id)

		return folderRepoResponse.body ?: listOf()
	}

	/**
	 * When a folder was created in storage,
	 * we can create a folder model [Folder] from it
	 *
	 * @param response	folder data response storage
	 * @param folder	parent folder
	 * @return [Folder] build from [FolderResponse] data
	 * */
	private fun folderFromStorageResponse(response: FolderResponse, folder: Folder): Folder
	{
		return Folder(
			id 			= UUID.randomUUID().toString(),
			name 		= response.name,
			path 		= response.path,
			folder 		= folder.id,
			size		= response.size.toInt(),
			dateCreated = SimpleDateFormat("dd/MM/yyyy  hh:mm").format(Date()),
			root		= false
		)
	}
}

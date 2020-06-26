package com.krypton.storagedatabaseservice.router.handler

import com.krypton.storagedatabaseservice.model.DirectoryTree
import com.krypton.storagedatabaseservice.service.file.IFileService
import com.krypton.storagedatabaseservice.service.folder.FolderService
import common.models.File
import common.models.Folder
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.FOUND
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*

/**
 * Handler for folder router
 * */
@Component
class FolderHandler @Autowired constructor(
	private val helper: FolderHandlerHelper,
	private val folderService: FolderService
)
{
	/**
	 * Get cloud storage root folder
	 *
	 * @return root [Folder] body or 404 not found
	 * */
	suspend fun root(): ServerResponse
	{
		val rootFolder = folderService.getRootFolder()

		return if (rootFolder != null)
			ok().bodyValueAndAwait(rootFolder)
		else
			notFound().buildAndAwait()
	}

	/**
	 * Save folder entity
	 *
	 * @param request	incoming request with [Folder] body
	 * @return saved [Folder] body
	 * */
	suspend fun save(request: ServerRequest): ServerResponse
	{
		val folder = request.awaitBodyOrNull<Folder>()

		return if (folder != null)
			helper.save(folder)
		else
			badRequest().buildAndAwait()
	}

	/***
	 * Save multiple folder to repository
	 *
	 * @param request	incoming request with [Folder] list
	 * @return list of save folders
	 */
	suspend fun saveAll(request: ServerRequest): ServerResponse
	{
		val folders = request.awaitBodyOrNull<List<Folder>>()

		return if (folders != null)
			helper.saveAll(folders)
		else
			badRequest().buildAndAwait()
	}

	/**
	 * Update folder entity
	 *
	 * @param request	incoming request with [Folder] body
	 * @return updated [Folder] body
	 * */
	suspend fun update(request: ServerRequest): ServerResponse
	{
		val folder = request.awaitBodyOrNull<Folder>()

		return if (folder != null)
			helper.update(folder)
		else
			badRequest().buildAndAwait()
	}

	/**
	 * Update child path property with parent folder actual
	 *
	 * @param request	incoming request with "id" query param
	 * @return http status, bad request, ok or not found
	 * */
	suspend fun updateChildPaths(request: ServerRequest): ServerResponse
	{
		val id = request.queryParam("id")

		return if (id.isPresent)
			helper.updateChildPaths(id.get())
		else
			badRequest().buildAndAwait()
	}

	/**
	 * Get all child folders of a folder
	 *
	 * @param request	incoming request with parent folder "id" query param
	 * @return a list of [Folder]'s
	 * */
	suspend fun all(request: ServerRequest): ServerResponse
	{
		val folder = request.queryParam("id")

		return if (folder.isPresent)
			helper.all(folder.get())
		else
			badRequest().buildAndAwait()
	}

	/**
	 * Find folder by id
	 *
	 * @param request	incoming request with "id" query param
	 * @return found [Folder] body or 404 if not found
	 * */
	suspend fun find(request: ServerRequest): ServerResponse
	{
		val id = request.queryParam("id")

		return if (id.isPresent)
			helper.find(id.get())
		else
			badRequest().buildAndAwait()
	}

	suspend fun delete(request: ServerRequest): ServerResponse
	{
		val id = request.queryParam("id")
		val recursively = request.queryParam("recursively")

		return if (id.isPresent)
			helper.delete(id.get(), recursively.orElse("false")!!.toBoolean())
		else
			badRequest().buildAndAwait()
	}

	/**
	 * Get all directory tree data, folders and files
	 *
	 * @param request	incoming request with "id" query param
	 * @return [DirectoryTree] or error http status
	 * */
	suspend fun getTree(request: ServerRequest): ServerResponse
	{
		val id = request.queryParam("id")

		return if (id.isPresent)
			helper.byIdFolderAction(id.get(), helper::getTree)
		else
			badRequest().buildAndAwait()
	}

	/**
	 * Get all previous folders of an folder
	 *
	 * @param request	icoming request with "id" query param
	 * @return list of [Folder]'s or error http status
	 * */
	suspend fun getPreviousFolders(request: ServerRequest): ServerResponse
	{
		val id = request.queryParam("id")

		return if (id.isPresent)
			helper.byIdFolderAction(id.get(), helper::getPreviousFolders)
		else
			badRequest().buildAndAwait()
	}
}

/**
 * Helper class for [FolderHandler]
 *
 * The goal of this class is to handle the logic of the handler
 * */
@Component
class FolderHandlerHelper @Autowired constructor(
	private val folderService: FolderService,
	private val fileService: IFileService
)
{
	/**
	 * Save folder to repository
	 *
	 * @param folder	[Folder] body
	 * @return saved folder or corresponding status on error
	 * */
	suspend fun save(folder: Folder): ServerResponse
	{
		val exists = folderService.findByPath(folder.path) != null

		return if (exists)
			status(FOUND).bodyValueAndAwait("Folder already exist")
		else
		{
			val savedFolder = folderService.save(folder)

			if (savedFolder != null)
				ok().bodyValueAndAwait(savedFolder)
			else
				status(INTERNAL_SERVER_ERROR).buildAndAwait()
		}
	}

	/**
	 * Save a list of folder to repository
	 *
	 * @param folders	folders list
	 * @return list of saved folder
	 * */
	suspend fun saveAll(folders: List<Folder>): ServerResponse
	{
		return ok().bodyValueAndAwait(folderService.saveAll(folders))
	}

	/**
	 * Update folder in repository
	 *
	 * @param folder	[Folder] body
	 * @return updated folder from repository or internal server error status
	 * */
	suspend fun update(folder: Folder): ServerResponse
	{
		val savedFolder = folderService.save(folder)

		return if (savedFolder != null)
			ok().bodyValueAndAwait(savedFolder)
		else
			status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}

	/**
	 * Update all children nodes path with parent folder actual
	 *
	 * @param id	folder id
	 * @return status ok or notFound if folder not found in repo
	 * */
	suspend fun updateChildPaths(id: String): ServerResponse
	{
		val folder = folderService.findById(id)

		if (folder != null)
		{
			updateChildNodesPaths(folder)

			return ok().buildAndAwait()
		}
		return notFound().buildAndAwait()
	}

	/**
	 * Get all child folders of a folder as a list
	 *
	 * @param folderId		id
	 * @return list of [Folder]'s
	 * */
	suspend fun all(folderId: String): ServerResponse
	{
		val folders = folderService.findByFolder(folderId).toList()

		return ok().bodyValueAndAwait(folders)
	}

	/**
	 * Find folder by id
	 *
	 * @param id	folder id
	 * @return [Folder] or 404 if not found
	 * */
	suspend fun find(id: String): ServerResponse
	{
		val folder = folderService.findById(id)

		return if (folder != null)
			ok().bodyValueAndAwait(folder)
		else
			notFound().buildAndAwait()
	}

	/**
	 * Delete a folder
	 *
	 * @param id			folder id
	 * @param recursively	run this operation recursively for all folder tree
	 * @return response depending on delete result success
	 * */
	suspend fun delete(id: String, recursively: Boolean? = false): ServerResponse
	{
		val deleted = if (recursively == true)
			folderService.deleteRecursively(id, fileService::deleteByFolder)
		else
			folderService.deleteById(id)

		return if (deleted)
			ok().buildAndAwait()
		else
			status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}

	/**
	 * Get directory tree with [Folder]'s and [File]'s
	 *
	 * @param id	parent folder id
	 * @return [DirectoryTree]
	 * */
	suspend fun <T> byIdFolderAction(
		id: String,
		action: suspend (folder: Folder) -> T
	): ServerResponse
	{
		val folder = folderService.findById(id)

		if (folder != null)
		{
			return ok().bodyValueAndAwait(action(folder)!!)
		}
		return notFound().buildAndAwait()
	}

	/**
	 * Get directory tree
	 *
	 * @param folder	folder from which get tree
	 * @return [DirectoryTree]
	 * */
	suspend fun getTree(folder: Folder) : DirectoryTree = coroutineScope {
		val tree = DirectoryTree(folder, arrayListOf(), arrayListOf())

		val populateFiles 	= async(IO) { fileService.findByFolder(folder.id).toList(tree.files) }
		val getFolders 		= async(IO) { folderService.findByFolder(folder.id) }

		getFolders.await().map { getTree(it) }.toList(tree.folders)
		populateFiles.await()
		return@coroutineScope tree
	}

	/**
	 * Get all previous folders of an folder,
	 * list will start from the root to the folder
	 *
	 * @param folder	target folder
	 * @return list of [Folder]'s
	 * */
	suspend fun getPreviousFolders(folder: Folder): List<Folder>
	{
		val folders = arrayListOf(folder)

		suspend fun addPreviousToList(list: ArrayList<Folder>, folder: Folder)
		{
			val previous = folderService.findById(folder.folder)

			if (previous != null)
			{
				list.add(previous)

				addPreviousToList(list, previous)
			}
		}

		addPreviousToList(folders, folder)

		return folders.reversed()
	}

	/**
	 * Update all folder items path with actual folder path
	 * By default will run recursively for all folders inside as well
	 *
	 * @param folder		target folder
	 * @param recursively	run same process for folders inside
	 * */
	private suspend fun updateChildNodesPaths(folder: Folder, recursively: Boolean? = true): Unit = coroutineScope {
		val updatedFolders 	= async { updateFoldersPath(folder) }
		val updatedFiles 	= async { updateFilesPath(folder) }

		folderService.saveAll(updatedFolders.await())
		fileService.saveAll(updatedFiles.await())

		if (recursively == true)
			updatedFolders.await().asFlow().collect { updateChildNodesPaths(it) }
	}

	/**
	 * Update child folders path from a folder
	 * Will not save to repository, just return an updated list
	 *
	 * @param folder	parent folder
	 * @return updated folders list
	 * */
	private suspend fun updateFoldersPath(folder: Folder): List<Folder>
	{
		val folders = folderService.findByFolder(folder.id)

		return folders.map { f -> f.apply { path = "${folder.path}/${name}" }}.toList()
	}

	/**
	 * Update child files path from a folder
	 * Will not save to repository, just return an updated list
	 *
	 * @param folder	parent folder
	 * @return updated files list
	 * */
	private suspend fun updateFilesPath(folder: Folder): List<File>
	{
		val files = fileService.findByFolder(folder.id)

		return files.map { f -> f.apply { path = "${folder.path}/${name}" }}.toList()
	}
}

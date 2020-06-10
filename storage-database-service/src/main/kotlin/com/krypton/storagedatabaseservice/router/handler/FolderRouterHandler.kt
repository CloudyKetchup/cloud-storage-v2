package com.krypton.storagedatabaseservice.router.handler

import com.krypton.storagedatabaseservice.service.file.IFileService
import com.krypton.storagedatabaseservice.service.folder.FolderService
import common.models.Folder
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
}

package com.krypton.storageservice.router

import com.krypton.storageservice.model.Directory
import com.krypton.storageservice.model.FolderMoveData
import com.krypton.storageservice.model.response.FolderResponse
import com.krypton.storageservice.service.storage.folder.IFolderDataService
import com.krypton.storageservice.service.storage.folder.IStorageFolderService
import common.models.Folder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import java.io.File
import kotlin.streams.toList

/**
 * Router for handling requests related to folders storage management
 * */
@Component
class FolderRouter @Autowired constructor(handler: FolderHandler)
{
	val router = coRouter {
		"/folder".nest {
			POST("/create", handler::create)
			DELETE("/delete", handler::delete)
			PUT("/move", handler::move)
			PUT("/copy", handler::copy)
			PUT("/rename", handler::rename)
			GET("/tree", handler::getTree)
			"/root".nest {
				GET("/find", handler::findRoot)
				POST("/create", handler::createRoot)
			}
		}
	}
}

/**
 * Handler for [FolderRouter]
 *
 * Handles all incoming requests data, like json bodies, query params, ...
 * and filters invalid requests
 * */
@Component
class FolderHandler @Autowired constructor(
	private val helper: FolderHandlerHelper,
	private val storageFolderService: IStorageFolderService
)
{
	/**
	 * Creates a new folder in storage on specified path
	 *
	 * @param request	incoming request with [Folder] body and "name" query param
	 * @return response with newly created folder data -> [FolderResponse]
	 * */
	suspend fun create(request: ServerRequest): ServerResponse
	{
		val parentFolder 	= request.awaitBodyOrNull<Folder>()
		val name			= request.queryParam("name")

		return if (parentFolder != null && name.isPresent)
			helper.create(parentFolder.path, name.get())
		else
			badRequest().buildAndAwait()
	}

	/**
	 * Delete folder from storage
	 *
	 * @param request	incoming request with [Folder] body
	 * @return response of [ok] or [status] with internal server error code on fail
	 * */
	suspend fun delete(request: ServerRequest): ServerResponse
	{
		val folder = request.awaitBodyOrNull<Folder>()

		return if (folder != null)
			helper.delete(folder)
		else
			badRequest().buildAndAwait()
	}

	/**
	 * Move folder to another directory in storage
	 *
	 * @param request	incoming request with [FolderMoveData] object
	 * @return [Folder] object with refreshed path
	 * */
	suspend fun move(request: ServerRequest) = moveOperation(request, helper::move)

	/**
	 * Copy folder to another directory in storage
	 *
	 * @param request	incoming request with [FolderMoveData] object
	 * @return newly copied [Folder] object
	 * */
	suspend fun copy(request: ServerRequest) = moveOperation(request, helper::copy)

	/**
	 * Rename folder
	 *
	 * @param request	incoming request with [Folder] body and "name" query param
	 * @return updated [Folder] with new name and path
	 * */
	suspend fun rename(request: ServerRequest): ServerResponse
	{
		val folder 	= request.awaitBodyOrNull<Folder>()
		val name	= request.queryParam("name")

		return if (folder != null && name.isPresent)
			helper.rename(folder, name.get())
		else
			badRequest().buildAndAwait()
	}

	/**
	 * Move operation for folders on storage, basically move or copy from one
	 * directory to another, will accept a callback for selected operation
	 *
	 * Main goal is to check if data for move is valid, if not valid will return
	 * bad request status and will not execute callback
	 *
	 * ex: moveOperation(request) { folder, targetFolder -> // do something }
	 *
	 * @param request	incoming request with [FolderMoveData] body
	 * @param move		move operation callback with folder and target folder params
	 * @return server response with updated [Folder] or error code
	 * */
	private suspend fun moveOperation(
		request: ServerRequest,
		move : suspend (folder: Folder, targetFolder: Folder) -> ServerResponse
	): ServerResponse
	{
		val folderMoveData = request.awaitBodyOrNull<FolderMoveData>()

		return if (folderMoveData != null)
			move(folderMoveData.folder, folderMoveData.targetFolder)
		else
			badRequest().buildAndAwait()
	}

	suspend fun findRoot(request: ServerRequest): ServerResponse
	{
		return if (storageFolderService.exists("/root"))
			ok().bodyValueAndAwait(FolderResponse(
				name = "root",
				path = "/root",
				size = 64
			))
		else
			notFound().buildAndAwait()
	}

	suspend fun createRoot(request: ServerRequest): ServerResponse
	{
		return if (!storageFolderService.exists("/root"))
			helper.create("", "root")
		else
			status(HttpStatus.FOUND).bodyValueAndAwait("Root folder already exists")
	}

	suspend fun getTree(request: ServerRequest): ServerResponse
	{
		val folder = request.awaitBodyOrNull<Folder>()

		return if (folder != null)
			helper.getTree(folder)
		else
			badRequest().buildAndAwait()
	}
}

/**
 * Helper class that handler logic of [FolderHandler], purpose of handler is
 * to get and validate the data, and helper will execute all the operations
 * */
@Component
class FolderHandlerHelper @Autowired constructor(
	private val storageService: IStorageFolderService,
	private val dataService: IFolderDataService
)
{
	/**
	 * Create new folder
	 *
	 * @param parentPath	directory where to create new folder
	 * @param name			new folder name
	 * @return [FolderResponse] or internal server error status
	 * */
	suspend fun create(parentPath: String, name: String): ServerResponse
	{
		val path = "${parentPath}/$name"
		// check if folder already exists
		if (storageService.exists(path))
			return status(INTERNAL_SERVER_ERROR).bodyValueAndAwait("Folder already exist")

		val folder = storageService.createFolder(path)

		return if (folder != null)
			ok().bodyValueAndAwait(FolderResponse(folder.name, path, folder.length()))
		else
			status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}

	/**
	 * Delete folder from storage
	 *
	 * @param folder	folder to delete
	 * @return [ok] or [status] with internal server error code
	 * */
	suspend fun delete(folder : Folder): ServerResponse
	{
		val path = folder.path

		if (!storageService.exists(path)) return notFound().buildAndAwait()

		val deleted = storageService.delete(path)

		return if (deleted)
			ok().buildAndAwait()
		else
			status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}

	/**
	 * Move folder to another directory
	 *
	 * @param folder		folder to move
	 * @param targetFolder	directory where to move folder
	 * @return [Folder] with updated path
	 * */
	suspend fun move(folder: Folder, targetFolder: Folder): ServerResponse
	{
		return moveFolderAndNewPath(folder, targetFolder, storageService::move)
	}

	/**
	 * Copy folder to another directory
	 *
	 * @param folder		folder to copy
	 * @param targetFolder	directory where to copy folder
	 * @return newly created [Folder]
	 * */
	suspend fun copy(folder: Folder, targetFolder: Folder): ServerResponse
	{
		return moveFolderAndNewPath(folder, targetFolder, storageService::copy)
	}

	/**
	 * Rename folder
	 *
	 * @param folder		[Folder] model
	 * @param newName		new folder name
	 * @return [Folder] with updated name
	 * */
	suspend fun rename(folder: Folder, newName: String): ServerResponse
	{
		val path = folder.path
		// check if folder exists
		if (!storageService.exists(path)) return notFound().buildAndAwait()

		val newPath	= "${File(folder.path).parent}/${newName}"

		val renamedFile = storageService.move(path, newPath)

		return if (renamedFile != null)
			ok().bodyValueAndAwait(folder.apply {
				this.name = newName
				this.path = newPath
			})
		else
			status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}

	suspend fun getTree(folder: Folder): ServerResponse
	{
		val data = dataService.getData(folder.path)
			?: return notFound().buildAndAwait()

		val directory = Directory(data, arrayListOf(), arrayListOf())

		populateDirectory(directory)

		return ok().bodyValueAndAwait(directory)
	}

	private fun populateDirectory(directory: Directory)
	{
		// add all files
		dataService.getFiles(directory.data.path)?.let {
			directory.files.addAll(it)
		}
		// add all folders
		dataService.getFolders(directory.data.path)?.let {
			val folders = it.parallelStream()
				.map { folder -> Directory(folder, arrayListOf(), arrayListOf()) }
				.toList()
			directory.folders.addAll(folders)
		}
		// run same population for all folders inside
		directory.folders.parallelStream().forEach { populateDirectory(it) }
	}

	/**
	 * Move operation of the folder, will check if folder and target
	 * directory are valid, well execute move operation
	 *
	 * @param folder			folder for move
	 * @param targetFolder		directory where to move folder
	 * @param move				move operation
	 * @return [Folder] with updated path or error code
	 * */
	private suspend fun moveFolderAndNewPath(
		folder			: Folder,
		targetFolder	: Folder,
		move 			: suspend (path: String, newPath: String) -> File?
	): ServerResponse
	{
		val folderPath 			= folder.path
		val targetFolderPath 	= targetFolder.path

		// return 404 if folder or target directory does not exist
		if (!storageService.exists(folderPath)
			||
			!storageService.exists(targetFolderPath)
		) return notFound().buildAndAwait()

		val newPath = "${targetFolderPath}/${folder.name}"

		val movedFile = move(folderPath, newPath)

		return if (movedFile != null)
			ok().bodyValueAndAwait(folder.apply { path = newPath })
		else
			status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}
}

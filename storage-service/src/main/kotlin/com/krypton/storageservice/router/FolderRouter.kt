package com.krypton.storageservice.router

import com.krypton.storageservice.model.FolderMoveData
import com.krypton.storageservice.model.response.FolderCreateResponse
import com.krypton.storageservice.service.storage.folder.StorageFolderService
import common.models.Folder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import java.io.File

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
class FolderHandler @Autowired constructor(storageFolderService: StorageFolderService)
{
	private val helper = FolderHandlerHelper(storageFolderService)

	/**
	 * Creates a new folder in storage on specified path
	 *
	 * @param request	incoming request with [Folder] body and "name" query param
	 * @return response with newly created folder data -> [FolderCreateResponse]
	 * */
	suspend fun create(request: ServerRequest): ServerResponse
	{
		val parentFolder 	= request.awaitBodyOrNull<Folder>()
		val name			= request.queryParam("name")

		return if (parentFolder != null && name.isPresent)
			helper.create(parentFolder, name.get())
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
}

/**
 * Helper class that handler logic of [FolderHandler], purpose of handler is
 * to get and validate the data, and helper will execute all the operations
 * */
class FolderHandlerHelper(private val storageFolderService: StorageFolderService)
{
	/**
	 * Create new folder
	 *
	 * @param parentFolder	directory where to create new folder
	 * @param name			new folder name
	 * @return [FolderCreateResponse] or internal server error status
	 * */
	suspend fun create(parentFolder: Folder, name: String): ServerResponse
	{
		// check if folder already exists
		if (File("${parentFolder.path}/$name").exists())
			return status(INTERNAL_SERVER_ERROR).bodyValueAndAwait("Folder already exist")

		val folder = storageFolderService.createFolder(parentFolder.path, name)

		return if (folder != null)
			ok().bodyValueAndAwait(FolderCreateResponse(folder.name, folder.path, folder.length()))
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
		val file = File(folder.path)

		if (!file.exists()) return notFound().buildAndAwait()

		val deleted = storageFolderService.delete(file)

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
		return moveFolderAndNewPath(folder, targetFolder, storageFolderService::move)
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
		return moveFolderAndNewPath(folder, targetFolder, storageFolderService::copy)
	}

	/**
	 * Rename folder
	 *
	 * @param folderModel	[Folder] model
	 * @param newName		new folder name
	 * @return [Folder] with updated name
	 * */
	suspend fun rename(folderModel: Folder, newName: String): ServerResponse
	{
		val folder = File(folderModel.path)

		if (!folder.exists()) return notFound().buildAndAwait()

		val newPath = "${folder.parent}/${newName}"

		val renamedFile = storageFolderService.move(folder, newPath)

		return if (renamedFile != null)
			ok().bodyValueAndAwait(folderModel.apply {
				name = newName
				path = newPath
			})
		else
			status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}

	/**
	 * Move operation of the folder, will check if folder and target
	 * directory are valid, well execute move operation
	 *
	 * @param folderModel			folder for move
	 * @param targetFolderModel		directory where to move folder
	 * @param move					move operation
	 * @return [Folder] with updated path or error code
	 * */
	private suspend fun moveFolderAndNewPath(
		folderModel: Folder,
		targetFolderModel: Folder,
		move : suspend (folder: File, newPath: String) -> File?
	): ServerResponse
	{
		val folder = File(folderModel.path)
		val targetFolder = File(targetFolderModel.path)
		// return 404 if folder or target directory does not exist
		if (!folder.exists() || !targetFolder.exists()) return notFound().buildAndAwait()

		val newPath = "${targetFolder.path}/${folder.name}"

		val movedFile = move(folder, newPath)

		return if (movedFile != null)
			ok().bodyValueAndAwait(folderModel.apply { path = newPath })
		else
			status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}
}
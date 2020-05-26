package com.krypton.storageservice.router

import com.krypton.storageservice.model.FileMoveData
import com.krypton.storageservice.model.response.FileUploadResponse
import com.krypton.storageservice.service.storage.file.StorageFileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import java.io.File
import common.models.File as FileModel
import common.models.Folder

/**
 * Router for handling requests related to files storage management
 * */
@Component
class FileRouter @Autowired constructor(handler: FileHandler)
{
	val router = coRouter {
		"/file".nest {
			POST("/upload", handler::save)
			DELETE("/delete", handler::delete)
			PUT("/move", handler::move)
			PUT("/copy", handler::copy)
			PUT("/rename", handler::rename)
		}
	}
}


/**
 * Handler for [FileRouter]
 *
 * Handles all incoming requests data, like json bodies, query params, ...
 * and filters invalid requests
 * */
@Component
class FileHandler @Autowired constructor(storageFileService: StorageFileService)
{
	private val helper = FileRouterHelper(storageFileService)

	/**
	 * Save new file to storage
	 *
	 * @param request	incoming request with file multipart and "path" query param
	 * @return newly saved file data
	 * */
	suspend fun save(request: ServerRequest): ServerResponse
	{
		val multipart	= request.awaitMultipartData()
		val path		= request.queryParam("path")

		// if file or path not present return bad request
		return if (multipart.isEmpty() || !multipart.containsKey("file") || path.isEmpty)
		{
			badRequest().buildAndAwait()
		} else
		{
			helper.save(multipart.getFirst("file") as FilePart, path.get())
		}
	}

	/**
	 * Delete file from storage
	 *
	 * @param request	incoming request with [FileModel] body
	 * @return http status code
	 * */
	suspend fun delete(request: ServerRequest): ServerResponse
	{
		val file = request.awaitBodyOrNull<FileModel>()

		return if (file != null)
			helper.delete(file)
		else
			badRequest().buildAndAwait()
	}

	/**
	 * Move file from on directory to another
	 *
	 * @param request	incoming request with [FileMoveData] body
	 * @return moved file with updated path [FileMoveData]
	 * */
	suspend fun move(request: ServerRequest) = moveOperation(request, helper::move)

	/**
	 * Copy file from one directory to another
	 *
	 * @param request	incoming request with [FileMoveData] body
	 * @return newly coped file [FileMoveData]
	 * */
	suspend fun copy(request: ServerRequest) = moveOperation(request, helper::copy)

	/**
	 * Rename file
	 *
	 * @param request	incoming request with [FileModel] body and "name" query param
	 * @return [FileModel] with updated name and path
	 * */
	suspend fun rename(request: ServerRequest): ServerResponse
	{
		val file 	= request.awaitBodyOrNull<FileModel>()
		val name 	= request.queryParam("name")

		return if (file != null && name.isPresent)
		{
			helper.rename(file, name.get())
		} else
		{
			badRequest().buildAndAwait()
		}
	}

	/**
	 * Move operation for files on storage, basically move or copy from one
	 * directory to another, will accept a callback for selected operation
	 *
	 * Main goal is to check if data for move is valid, if not valid will return
	 * bad request status and will not execute callback
	 *
	 * ex: moveOperation(request) { file, folder -> // do move }
	 *
	 * @param request	incoming request with [FileMoveData] body
	 * @param move		move operation callback with file and folder params
	 * @return server response with updated [FileModel] or error code
	 * */
	private suspend fun moveOperation(
		request: ServerRequest,
		move : suspend (file: FileModel, folder: Folder) -> ServerResponse
	): ServerResponse
	{
		val moveData = request.awaitBodyOrNull<FileMoveData>()

		return if (moveData == null)
		{
			badRequest().buildAndAwait()
		} else
		{
			move(moveData.file, moveData.folder)
		}
	}
}



/**
 * Helper class that handler logic of [FileHandler], purpose of handler is
 * to get and validate the data, and helper will execute all the operations
 * */
class FileRouterHelper(private val storageFileService: StorageFileService)
{
	/**
	 * Save file part to storage with given path
	 *
	 * @param filePart	part data file
	 * @param path		folder path for save
	 * @return [FileUploadResponse] body
	 * */
	suspend fun save(filePart: FilePart, path: String): ServerResponse
	{
		val file = File("${path}/${filePart.filename()}")
		// check if file already exist
		if (file.exists())
			return badRequest().bodyValueAndAwait("File already exist")
		// save file
		val newFile = storageFileService.saveFromFilePart(filePart, file)

		// check if file was created
		if (newFile?.exists() == true && newFile.length() > 0)
		{
			return ok().bodyValueAndAwait(FileUploadResponse(
				newFile.name,
				newFile.path,
				newFile.length(),
				newFile.extension
			))
		}
		return status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}

	/**
	 * Delete file from storage
	 *
	 * @param fileModel		[FileModel] I think -_-
	 * @return http status, 404, 505, or [ok]
	 * */
	suspend fun delete(fileModel: FileModel): ServerResponse
	{
		val file = File(fileModel.path)

		if (!file.exists()) return notFound().buildAndAwait()

		val deleted = storageFileService.delete(file)

		return if (deleted)
			ok().buildAndAwait()
		else
			status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}

	/**
	 * Move file to another directory
	 *
	 * @param fileModel		and again [FileModel] -_-
	 * @param folder		folder where to move file
	 * @return [FileModel] with updated path
	 * */
	suspend fun move(fileModel: FileModel, folder: Folder): ServerResponse
	{
		return moveFileAndNewPath(fileModel, folder, storageFileService::move)
	}

	/**
	 * Copy file to another directory
	 *
	 * @param fileModel		[FileModel]
	 * @param folder		folder where to copy file
	 * @return newly copied file [FileModel]
	 * */
	suspend fun copy(fileModel: FileModel, folder: Folder): ServerResponse
	{
		return moveFileAndNewPath(fileModel, folder, storageFileService::copy)
	}

	/**
	 * Rename file
	 *
	 * @param fileModel		[FileModel]
	 * @param newName		new file name
	 * @return file with updated name and path
	 * */
	suspend fun rename(fileModel: FileModel, newName : String): ServerResponse
	{
		val file	= File(fileModel.path)
		val newPath = "${file.parent}/$newName"

		if (!file.exists()) return notFound().buildAndAwait()

		storageFileService.move(file, newPath)

		return if (File(newPath).exists())
			ok().bodyValueAndAwait(fileModel.apply {
				name = newName
				path = newPath
			})
		else
			status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}

	/**
	 * Accepts file [FileModel] and folder [Folder] models for move operation,
	 * basically, we check if file and folder for move exists,
	 * otherwise return 404
	 *
	 * We create [File] entity and new path, and return em via callback,
	 * pretty clear if you ask me
	 *
	 * @param fileModel		[FileModel]
	 * @param folder		[Folder]
	 * @param move			suspend callback for move operation of choice
	 * @return [ok] if move operation succeeded or [status] with 505 if not
	 * */
	private suspend fun moveFileAndNewPath(
		fileModel: FileModel,
		folder: Folder,
		move : suspend (file: File, newPath: String) -> File?
	): ServerResponse
	{
		val file = File(fileModel.path)
		// check if file and folder for move exist
		if (!file.exists() && !File(folder.path).exists()) return notFound().buildAndAwait()

		val newPath = "${folder.path}/${fileModel.name}"

		val movedFile = move(file, newPath)

		return if (movedFile != null)
			ok().bodyValueAndAwait(fileModel.apply { path = newPath })
		else
			status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}
}

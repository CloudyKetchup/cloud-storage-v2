package com.krypton.storageservice.router.handler

import com.krypton.storageservice.model.FileMoveData
import com.krypton.storageservice.model.response.FileUploadResponse
import com.krypton.storageservice.router.FileRouter
import com.krypton.storageservice.service.storage.file.IStorageFileService
import common.models.File
import common.models.Folder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*

/**
 * Handler for [FileRouter]
 *
 * Handles all incoming requests data, like json bodies, query params, ...
 * and filters invalid requests
 * */
@Component
class FileHandler @Autowired constructor(private val helper: FileHandlerHelper)
{
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
	 * @param request	incoming request with [File] body
	 * @return http status code
	 * */
	suspend fun delete(request: ServerRequest): ServerResponse
	{
		val file = request.awaitBodyOrNull<File>()

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
	 * @param request	incoming request with [File] body and "name" query param
	 * @return [File] with updated name and path
	 * */
	suspend fun rename(request: ServerRequest): ServerResponse
	{
		val file 	= request.awaitBodyOrNull<File>()
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
	 * Download file
	 *
	 * @param request	incoming request with [File] body
	 * @return response with file byte array or http error status
	 * */
	suspend fun download(request: ServerRequest): ServerResponse
	{
		val file = request.awaitBodyOrNull<File>()

		return if (file != null)
			helper.download(file)
		else
			badRequest().buildAndAwait()
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
	 * @return server response with updated [File] or error code
	 * */
	private suspend fun moveOperation(
		request: ServerRequest,
		move : suspend (file: File, folder: Folder) -> ServerResponse
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
@Component
class FileHandlerHelper(private val storageFileService: IStorageFileService)
{
	/**
	 * Save file part to storage with given path
	 *
	 * @param filePart		part data file
	 * @param folderPath 	folder path for save
	 * @return [FileUploadResponse] body
	 * */
	suspend fun save(filePart: FilePart, folderPath: String): ServerResponse
	{
		val path = "${folderPath}/${filePart.filename()}"
		// check if file already exist
		if (storageFileService.exists(path))
			return badRequest().bodyValueAndAwait("File already exist")
		// save file
		val newFile = storageFileService.saveFromFilePart(filePart, path)

		// check if file was created
		if (newFile?.exists() == true && newFile.length() > 0)
		{
			return ok().bodyValueAndAwait(FileUploadResponse(
				newFile.name,
				path,
				newFile.length(),
				newFile.extension
			))
		}
		return status(HttpStatus.INTERNAL_SERVER_ERROR).buildAndAwait()
	}

	/**
	 * Delete file from storage
	 *
	 * @param file		[File]
	 * @return http status, 404, 505, or [ok]
	 * */
	suspend fun delete(file: File): ServerResponse
	{
		val path = file.path

		if (!storageFileService.exists(path)) return notFound().buildAndAwait()

		val deleted = storageFileService.delete(path)

		return if (deleted)
			ok().buildAndAwait()
		else
			status(HttpStatus.INTERNAL_SERVER_ERROR).buildAndAwait()
	}

	/**
	 * Move file to another directory
	 *
	 * @param file			and again [File] -_-
	 * @param folder		folder where to move file
	 * @return [File] with updated path
	 * */
	suspend fun move(file: File, folder: Folder): ServerResponse
	{
		return moveFileAndNewPath(file, folder, storageFileService::move)
	}

	/**
	 * Copy file to another directory
	 *
	 * @param file			[File]
	 * @param folder		folder where to copy file
	 * @return newly copied file [File]
	 * */
	suspend fun copy(file: File, folder: Folder): ServerResponse
	{
		return moveFileAndNewPath(file, folder, storageFileService::copy)
	}

	/**
	 * Rename file
	 *
	 * @param file			[File]
	 * @param newName		new file name
	 * @return file with updated name and path
	 * */
	suspend fun rename(file: File, newName : String): ServerResponse
	{
		val path	= file.path
		val fsFile	= java.io.File(path)
		val newPath = "${fsFile.parent}/$newName"

		if (!storageFileService.exists(path)) return notFound().buildAndAwait()

		storageFileService.move(path, newPath)

		return if (storageFileService.exists(newPath))
			ok().bodyValueAndAwait(file.apply {
				this.name = newName
				this.path = newPath
			})
		else
			status(HttpStatus.INTERNAL_SERVER_ERROR).buildAndAwait()
	}

	/**
	 * Download file
	 *
	 * @param file		[File]
	 * @return byte array response or not found
	 * */
	suspend fun download(file: File): ServerResponse
	{
		val fsFile = storageFileService.getFile(file.path)

		return if (fsFile != null)
			ok().bodyValueAndAwait(fsFile.readBytes())
		else
			notFound().buildAndAwait()
	}

	/**
	 * Accepts file [File] and folder [Folder] models for move operation,
	 * basically, we check if file and folder for move exists,
	 * otherwise return 404
	 *
	 * We create [File] entity and new path, and return em via callback,
	 * pretty clear if you ask me
	 *
	 * @param file			[File]
	 * @param folder		[Folder]
	 * @param move			suspend callback for move operation of choice
	 * @return [ok] if move operation succeeded or [status] with 505 if not
	 * */
	private suspend fun moveFileAndNewPath(
		file: File,
		folder: Folder,
		move : suspend (path: String, newPath: String) -> java.io.File?
	): ServerResponse
	{
		// check if file and folder for move exist
		if (!storageFileService.exists(file.path) || !storageFileService.exists(folder.path))
			return notFound().buildAndAwait()

		val newPath = "${folder.path}/${file.name}"

		val movedFile = move(file.path, newPath)

		return if (movedFile != null)
			ok().bodyValueAndAwait(file.apply { path = newPath })
		else
			status(HttpStatus.INTERNAL_SERVER_ERROR).buildAndAwait()
	}
}

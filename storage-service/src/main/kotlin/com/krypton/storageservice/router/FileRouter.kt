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

@Component
class FileRouter @Autowired constructor(storageFileService: StorageFileService)
{
	private val handler = FileHandler(storageFileService)

	val router = coRouter {
		"/file".nest {
			POST("/upload", handler::save)
			PUT("/move", handler::move)
			PUT("/copy", handler::copy)
			PUT("/rename", handler::rename)
		}
	}
}



class FileHandler(storageFileService: StorageFileService)
{
	private val helper = FileRouterHelper(storageFileService)

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

	suspend fun move(request: ServerRequest): ServerResponse
	{
		val moveData = request.awaitBodyOrNull<FileMoveData>()

		return if (moveData == null)
		{
			badRequest().buildAndAwait()
		} else
		{
			helper.move(moveData.file, moveData.folder)
		}
	}

	suspend fun copy(request: ServerRequest): ServerResponse
	{
		val moveData = request.awaitBodyOrNull<FileMoveData>()

		return if (moveData == null)
		{
			badRequest().buildAndAwait()
		} else
		{
			helper.copy(moveData.file, moveData.folder)
		}
	}

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
}




class FileRouterHelper(private val storageFileService: StorageFileService)
{
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

	suspend fun move(fileModel: FileModel, folder: Folder): ServerResponse
	{
		return moveFileAndNewPath(fileModel, folder) { file, newPath ->
			storageFileService.move(file, newPath)
		}
	}

	suspend fun copy(fileModel: FileModel, folder: Folder): ServerResponse
	{
		return moveFileAndNewPath(fileModel, folder) { file, newPath ->
			storageFileService.copy(file, newPath)
		}
	}

	suspend fun rename(fileModel: FileModel, newName : String): ServerResponse
	{
		val file	= File(fileModel.path)
		val newPath = "${file.parent}/$newName"

		if (!file.exists()) return notFound().buildAndAwait()

		storageFileService.move(file, newPath)

		return if (File(newPath).exists())
			ok().bodyValueAndAwait(fileModel.apply { path = newPath })
		else
			status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}

	private suspend fun moveFileAndNewPath(
		fileModel: FileModel,
		folder: Folder,
		move : suspend (file: File, newPath: String) -> Unit
	): ServerResponse
	{
		val file = File(fileModel.path)
		// check if file exist
		if (!file.exists()) return notFound().buildAndAwait()

		val newPath = "${folder.path}/${fileModel.name}"

		move(file, newPath)

		return if (File(newPath).exists())
			ok().bodyValueAndAwait(fileModel.apply { path = newPath })
		else
			status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}
}
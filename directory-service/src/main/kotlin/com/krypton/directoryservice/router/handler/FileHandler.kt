package com.krypton.directoryservice.router.handler

import com.krypton.directoryservice.client.file.IFileRepositoryClient
import com.krypton.directoryservice.client.file.IFileStorageClient
import com.krypton.directoryservice.model.FileMoveData
import com.krypton.directoryservice.model.FileUploadResponse
import common.models.File
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import java.text.SimpleDateFormat
import java.util.*

@Component
class FileHandler @Autowired constructor(private val helper: FileHandlerHelper)
{
	suspend fun upload(request: ServerRequest): ServerResponse
	{
		val multipart 	= request.awaitMultipartData()
		val folderId 	= request.queryParam("folderId")
		val path 		= request.queryParam("path")

		return if (multipart.containsKey("file") && folderId.isPresent && path.isPresent)
		{
			val map = multipart.toSingleValueMap()

			helper.save(map["file"] as FilePart, folderId.get(), path.get())
		} else
		{
			badRequest().buildAndAwait()
		}
	}

	suspend fun delete(request: ServerRequest): ServerResponse
	{
		val file = request.awaitBodyOrNull<File>()

		return if (file != null)
			helper.delete(file)
		else
			badRequest().buildAndAwait()
	}

	suspend fun move(request: ServerRequest): ServerResponse = move(request, helper::move)

	suspend fun copy(request: ServerRequest): ServerResponse = move(request, helper::copy)

	suspend fun rename(request: ServerRequest): ServerResponse
	{
		val file = request.awaitBodyOrNull<File>()
		val name = request.queryParam("name")

		return if (file != null && name.isPresent)
			helper.rename(file, name.get())
		else
			badRequest().buildAndAwait()
	}

	private suspend fun move(
		request: ServerRequest,
		move: suspend (moveData: FileMoveData) -> ServerResponse
	): ServerResponse
	{
		val moveData = request.awaitBodyOrNull<FileMoveData>()

		return if (moveData != null)
			move(moveData)
		else
			badRequest().buildAndAwait()
	}
}

@Component
class FileHandlerHelper @Autowired constructor(
	private val repository: IFileRepositoryClient,
	private val storage: IFileStorageClient
)
{
	suspend fun save(file: FilePart, folderId: String, path: String): ServerResponse
	{
		// save file to storage
		val storageResponse = storage.upload(file, path)
		// check if file saved to storage
		if (storageResponse.body == null)
			return status(HttpStatus.valueOf(storageResponse.status)).buildAndAwait()

		// save file to repository
		val repositoryResponse = repository.save(fileFromUpload(storageResponse.body, folderId))

		// check if file saved to repository
		return if (repositoryResponse.body == null)
			status(HttpStatus.valueOf(repositoryResponse.status)).buildAndAwait()
		else
			ok().bodyValueAndAwait(repositoryResponse)
	}

	suspend fun delete(file: File): ServerResponse
	{
		val storageResponse = storage.delete(file)

		if (storageResponse != HttpStatus.OK) return status(storageResponse).buildAndAwait()

		return status(repository.delete(file.id)).buildAndAwait()
	}

	suspend fun move(moveData: FileMoveData): ServerResponse
	{
		val storageResponse = storage.move(moveData)

		if (storageResponse.body == null)
			return status(HttpStatus.valueOf(storageResponse.status)).buildAndAwait()

		val repositoryResponse = repository.save(storageResponse.body)

		return if (repositoryResponse.body == null)
			status(HttpStatus.valueOf(repositoryResponse.status)).buildAndAwait()
		else
			ok().bodyValueAndAwait(repositoryResponse.body)
	}

	suspend fun copy(moveData: FileMoveData): ServerResponse
	{
		val storageResponse = storage.copy(moveData)

		if (storageResponse.body == null)
			return status(HttpStatus.valueOf(storageResponse.status)).buildAndAwait()

		val repositoryResponse = repository.save(storageResponse.body.apply {
			id = UUID.randomUUID().toString()
		})

		return if (repositoryResponse.body == null)
			status(HttpStatus.valueOf(repositoryResponse.status)).buildAndAwait()
		else
			ok().bodyValueAndAwait(repositoryResponse.body)
	}

	suspend fun rename(file: File, name: String): ServerResponse
	{
		val storageResponse = storage.rename(file, name)

		if (storageResponse.body == null)
			return status(HttpStatus.valueOf(storageResponse.status)).buildAndAwait()

		val repositoryResponse = repository.save(storageResponse.body)

		return if (repositoryResponse.body == null)
			status(HttpStatus.valueOf(repositoryResponse.status)).buildAndAwait()
		else
			ok().bodyValueAndAwait(repositoryResponse.body)
	}

	private fun fileFromUpload(file: FileUploadResponse, folderId: String): File
	{
		return File(
			id 			= UUID.randomUUID().toString(),
			name 		= file.name,
			path 		= file.path,
			folder 		= folderId,
			size 		= file.size.toInt(),
			dateCreated = SimpleDateFormat("dd/MM/yyyy:hh:mm").format(Date()),
			extension 	= file.extension
		)
	}
}

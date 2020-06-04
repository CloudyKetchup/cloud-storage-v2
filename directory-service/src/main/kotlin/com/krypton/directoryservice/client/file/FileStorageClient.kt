package com.krypton.directoryservice.client.file

import com.krypton.directoryservice.client.WebClientBodyResponse
import com.krypton.directoryservice.model.FileMoveData
import com.krypton.directoryservice.model.FileUploadResponse
import common.models.File
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Service
class FileStorageClient @Autowired constructor(private val storageClient: WebClient): IFileStorageClient
{
	private val uri = "/file"

	override suspend fun upload(formData: LinkedHashMap<String, Any>): WebClientBodyResponse<FileUploadResponse>
	{
		if (!formData.containsKey("path") || !formData.containsKey("file"))
			return WebClientBodyResponse(HttpStatus.BAD_REQUEST.value())

		return try
		{
			storageClient.post()
				.uri("$uri/upload")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.bodyValue(formData)
				.retrieve()
				.bodyToMono(FileUploadResponse::class.java)
				.map { WebClientBodyResponse(200, it) }
				.awaitSingle()
		} catch (e: WebClientResponseException)
		{
			WebClientBodyResponse(e.rawStatusCode)
		} catch (e: Exception)
		{
			WebClientBodyResponse(505)
		}
	}

	override suspend fun delete(body: File): HttpStatus
	{
		return try
		{
			storageClient.method(HttpMethod.DELETE)
				.uri("$uri/delete")
				.bodyValue(body)
				.retrieve()
				.toBodilessEntity()
				.map { it.statusCode }
				.awaitSingle()
		} catch (e: WebClientResponseException)
		{
			e.statusCode
		} catch (e: Exception)
		{
			HttpStatus.INTERNAL_SERVER_ERROR
		}
	}

	override suspend fun move(moveData: FileMoveData) = moveRequest(moveData, "$uri/move")

	override suspend fun copy(moveData: FileMoveData) = moveRequest(moveData, "$uri/copy")

	override suspend fun rename(body: File, name: String) : WebClientBodyResponse<File>
	{
		return try
		{
			storageClient.put()
				.uri("$uri/rename?name=${name}")
				.bodyValue(body)
				.retrieve()
				.bodyToMono(File::class.java)
				.map { WebClientBodyResponse(200, it) }
				.awaitSingle()
		} catch (e: WebClientResponseException)
		{
			WebClientBodyResponse(e.rawStatusCode)
		} catch (e: Exception)
		{
			WebClientBodyResponse(505)
		}
	}

	private suspend fun moveRequest(fileMoveData: FileMoveData, uri: String) : WebClientBodyResponse<File>
	{
		return try
		{
			storageClient.put()
				.uri(uri)
				.bodyValue(fileMoveData)
				.retrieve()
				.bodyToMono(File::class.java)
				.map { WebClientBodyResponse(200, it) }
				.awaitSingle()
		} catch (e: WebClientResponseException)
		{
			WebClientBodyResponse(e.rawStatusCode)
		} catch (e: Exception)
		{
			WebClientBodyResponse(505)
		}
	}
}

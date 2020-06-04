package com.krypton.directoryservice.client.folder

import com.krypton.directoryservice.client.WebClientBodyResponse
import com.krypton.directoryservice.model.FileMoveData
import com.krypton.directoryservice.model.FolderCreateResponse
import common.models.Folder
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Service
class FolderStorageClient @Autowired constructor(private val storageClient: WebClient): IFolderStorageClient
{
	private val uri = "/folder"

	override suspend fun create(parentFolder: Folder, name: String): WebClientBodyResponse<FolderCreateResponse>
	{
		return try
		{
			storageClient.post()
				.uri("$uri/create?name=${name}")
				.bodyValue(parentFolder)
				.retrieve()
				.bodyToMono(FolderCreateResponse::class.java)
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

	override suspend fun delete(body: Folder): HttpStatus
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

	override suspend fun rename(body: Folder, name: String): WebClientBodyResponse<Folder>
	{
		return try
		{
			storageClient.put()
				.uri("$uri/rename?name=${name}")
				.bodyValue(body)
				.retrieve()
				.bodyToMono(Folder::class.java)
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

	private suspend fun moveRequest(moveData: FileMoveData, uri: String): WebClientBodyResponse<Folder>
	{
		return try
		{
			storageClient.put()
				.uri(uri)
				.bodyValue(moveData)
				.retrieve()
				.bodyToMono(Folder::class.java)
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
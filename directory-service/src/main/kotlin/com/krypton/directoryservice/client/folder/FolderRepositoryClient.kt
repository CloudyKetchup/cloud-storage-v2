package com.krypton.directoryservice.client.folder

import com.krypton.directoryservice.client.WebClientBodyResponse
import common.models.Folder
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Service
class FolderRepositoryClient @Autowired constructor(private val repositoryClient: WebClient): IFolderRepositoryClient
{
	private val uri = "/folder"

	override suspend fun save(body: Folder): WebClientBodyResponse<Folder>
	{
		return try
		{
			repositoryClient.post()
				.uri("$uri/save")
				.header("Content-Type", "application/json")
				.bodyValue(body)
				.accept(MediaType.APPLICATION_JSON)
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

	override suspend fun update(folder: Folder): WebClientBodyResponse<Folder>
	{
		return try
		{
			repositoryClient.put()
				.uri("$uri/update")
				.bodyValue(folder)
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

	override suspend fun delete(id: String, recursively: Boolean?): HttpStatus
	{
		return try
		{
			repositoryClient.delete()
				.uri("$uri/delete?id=${id}&recursively=${recursively}")
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

	override suspend fun root(): WebClientBodyResponse<Folder>
	{
		return try
		{
			repositoryClient.get()
				.uri("$uri/root")
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

	override suspend fun all(folderId: String): WebClientBodyResponse<List<Folder>>
	{
		return try
		{
			val folders = repositoryClient.get()
				.uri("$uri/all?id=${folderId}")
				.retrieve()
				.bodyToFlux(Folder::class.java)
				.collectList()
				.awaitSingle()
			WebClientBodyResponse(200, folders)
		} catch (e: WebClientResponseException)
		{
			WebClientBodyResponse(e.rawStatusCode)
		} catch (e: Exception)
		{
			WebClientBodyResponse(505)
		}
	}

	override suspend fun find(id: String): WebClientBodyResponse<Folder>
	{
		return try
		{
			repositoryClient.get()
				.uri("$uri?id=${id}")
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
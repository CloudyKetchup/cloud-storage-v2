package com.krypton.directoryservice.client.file

import com.krypton.directoryservice.client.WebClientBodyResponse
import common.models.File
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Service
class FileRepositoryClient @Autowired constructor(private val repositoryClient: WebClient): IFileRepositoryClient
{
	private val uri	= "/file"

	override suspend fun save(body: File): WebClientBodyResponse<File>
	{
		return try
		{
			repositoryClient.post()
				.uri("$uri/save")
				.bodyValue(body)
				.retrieve()
				.bodyToMono(File::class.java)
				.map { WebClientBodyResponse(200, it) }
				.awaitSingle()
		} catch (e: WebClientResponseException)
		{
			e.printStackTrace()
			WebClientBodyResponse(e.rawStatusCode)
		} catch (e: Exception)
		{
			WebClientBodyResponse(500)
		}
	}

	override suspend fun saveAll(body: List<File>): WebClientBodyResponse<ArrayList<File>>
	{
		return try {
			repositoryClient.post()
				.uri("$uri/save/all")
				.bodyValue(body)
				.retrieve()
				.bodyToMono(arrayListOf<File>().javaClass)
				.map { WebClientBodyResponse(200, it) }
				.awaitSingle()
		} catch (e: WebClientResponseException) {
			WebClientBodyResponse(e.rawStatusCode)
		} catch (e: Exception) {
			WebClientBodyResponse(500)
		}
	}

	override suspend fun delete(id: String): HttpStatus
	{
		return try
		{
			repositoryClient.delete()
				.uri("$uri/delete?id=${id}")
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

	override suspend fun all(folderId: String): WebClientBodyResponse<List<File>>
	{
		return try
		{
			val files = repositoryClient.get()
				.uri("$uri/all?id=${folderId}")
				.retrieve()
				.bodyToFlux(File::class.java)
				.collectList()
				.awaitSingle()

			WebClientBodyResponse(200, files)
		} catch (e: WebClientResponseException)
		{
			WebClientBodyResponse(e.rawStatusCode)
		} catch (e: Exception)
		{
			WebClientBodyResponse(500)
		}
	}

	override suspend fun find(id: String): WebClientBodyResponse<File>
	{
		return try
		{
			repositoryClient.get()
				.uri("$uri/find?id=${id}")
				.retrieve()
				.bodyToMono(File::class.java)
				.map { WebClientBodyResponse(200, it) }
				.awaitSingle()
		} catch (e: WebClientResponseException)
		{
			WebClientBodyResponse(e.rawStatusCode)
		} catch (e: Exception)
		{
			WebClientBodyResponse(500)
		}
	}
}

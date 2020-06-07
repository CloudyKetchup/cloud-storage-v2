package com.krypton.directoryservice.configuration

import com.krypton.directoryservice.client.WebClientBodyResponse
import com.krypton.directoryservice.client.folder.IFolderRepositoryClient
import com.krypton.directoryservice.model.FolderResponse
import common.models.Folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.invoke
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.text.SimpleDateFormat
import java.util.*

@Configuration
class StartupMaintenance @Autowired constructor(
	storageClient: WebClient,
	private val repositoryClient: IFolderRepositoryClient,
	private val applicationContext: ApplicationContext
) : CommandLineRunner
{
	private var logger : Logger? = null

	private val storageMaintenanceClient = StorageMaintenanceClient(storageClient)

	private class StorageMaintenanceClient constructor(private val client: WebClient)
	{
		private val uri = "/folder"

		suspend fun getRoot(): WebClientBodyResponse<FolderResponse>
		{
			return try
			{
				client.get()
					.uri("$uri/root/find")
					.retrieve()
					.bodyToMono(FolderResponse::class.java)
					.map { WebClientBodyResponse(200, it) }
					.awaitSingle()
			} catch (e: WebClientResponseException)
			{
				WebClientBodyResponse(e.rawStatusCode)
			}
		}

		suspend fun createRoot(): WebClientBodyResponse<FolderResponse>
		{
			return try
			{
				client.post()
					.uri("$uri/root/create")
					.retrieve()
					.bodyToMono(FolderResponse::class.java)
					.map { WebClientBodyResponse(200, it) }
					.awaitSingle()
			} catch (e: WebClientResponseException)
			{
				WebClientBodyResponse(e.rawStatusCode)
			}
		}
	}

	override fun run(vararg args: String?) = runBlocking {
		val storageRootFolderOk = checkStorageRootFolder()
		val repositoryRootFolderOk = checkRepositoryRootFolder()

		if (!storageRootFolderOk)
		{
			logError("Storage Root Folder not found or cannot be created, check Storage Service")
			SpringApplication.exit(applicationContext)
		}

		if (!repositoryRootFolderOk)
		{
			logError("Repository Root Folder not found or cannot be created, check Storage Database Service")
			SpringApplication.exit(applicationContext)
		}
	}

	suspend fun checkStorageRootFolder(): Boolean
	{
		val storageRootResponse = storageMaintenanceClient.getRoot()

		return when(storageRootResponse.status)
		{
			404 ->
			{
				logInfo("Creating Root Folder in Storage")
				val rootCreateResponse = storageMaintenanceClient.createRoot()

				rootCreateResponse.body != null
			}
			200 -> true
			else -> false
		}
	}

	suspend fun checkRepositoryRootFolder(): Boolean
	{
		val repositoryRootResponse = repositoryClient.root()

		return if (repositoryRootResponse.status == 404)
		{
			val root = Folder(
				id = UUID.randomUUID().toString(),
				name = "root",
				path = "/root",
				folder = "",
				size = 64,
				dateCreated = SimpleDateFormat("dd/MM/yy  hh:mm").format(Date()),
				root = true
			)
			logInfo("Saving Root Folder to repository")
			val rootCreateResponse = repositoryClient.save(root)

			rootCreateResponse.body != null
		} else true
	}

	private fun logInfo(message: String)
	{
		if (logger == null) logger = LoggerFactory.getLogger(StartupMaintenance::class.java)

		logger!!.info(message)
	}

	private fun logError(message: String)
	{
		if (logger == null) logger = LoggerFactory.getLogger(StartupMaintenance::class.java)

		logger!!.error(message)
	}
}

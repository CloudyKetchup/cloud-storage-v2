package com.krypton.storageservice.service.storage.folder

import com.krypton.storageservice.config.Storage
import com.krypton.storageservice.model.FolderUsageStats
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/**
 * Service for managing storage state for folders only
 * */
@Service
class StorageFolderService @Autowired constructor(storage: Storage) : IStorageFolderService
{
	private val homeDir = storage.homeDir

	override suspend fun createFolder(path: String): File?
	{
		val file = File("${homeDir}/$path")

		file.mkdir()

		return if (file.exists()) file else null
	}

	override suspend fun delete(path: String): Boolean = try
	{
		val folder = File("${homeDir}/$path")

		if (!folder.exists()) throw FileNotFoundException()

		folder.deleteRecursively()
	} catch (e : IOException)
	{
		false
	}

	override suspend fun move(path: String, newPath: String): File? = try
	{
		val oldFolder 	= File("${homeDir}/$path")
		val folder		= File("${homeDir}/$newPath")

		if (!oldFolder.exists()) throw FileNotFoundException()
		// check if new folder already exist
		if (folder.exists()) throw IOException()

		val movedFile = Mono.fromCallable { Files.move(oldFolder.toPath(), Path.of(folder.path)) }
			.awaitSingle()
			.toFile()

		if (movedFile.exists())
		{
			movedFile
		} else
		{
			null
		}
	} catch (e : Exception)
	{
		null
	}

	override suspend fun copy(path: String, newPath: String): File? = try
	{
		val folder 	= File("${homeDir}/$path")
		val copy 	= File("${homeDir}/$newPath")

		if (copy.exists()) throw IOException("Folder already exists")

		if (folder.copyRecursively(copy, true))
		{
			copy
		} else null
	} catch (e : IOException)
	{
		null
	}

	override suspend fun exists(path: String): Boolean = File("${homeDir}/$path").exists()

	override suspend fun rootStats(path: String): FolderUsageStats?
	{
		val file = File("$homeDir/$path")
		val fs	 = File(homeDir)

		return if (file.exists())
		{
			val total = String.format("%.2f", fs.totalSpace.toDouble() / 1024 / 1024 / 1024).toDouble()
			val free = String.format("%.2f", fs.freeSpace.toDouble() / 1024 / 1024 / 1024).toDouble()

			FolderUsageStats(
				String.format("%.2f", total - free).toDouble(),
				free,
				total
			)
		} else null
	}
}

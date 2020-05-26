package com.krypton.storageservice.service.storage.folder

import com.krypton.storageservice.service.storage.IStorageService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.onErrorReturn
import reactor.kotlin.core.publisher.onErrorReturn
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/**
 * Service for managing storage state for folders only
 * */
@Service
class StorageFolderService : IStorageService
{
	override suspend fun delete(entity: File): Boolean = try
	{
		entity.deleteRecursively()
	} catch (e : IOException)
	{
		false
	}

	override suspend fun move(entity: File, newPath: String): File? = try
	{
		val file = File(newPath)

		if (file.exists()) throw IOException("Folder already exists")

		val movedFile = Mono.fromCallable { Files.move(entity.toPath(), Path.of(newPath)) }
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

	override suspend fun copy(entity: File, newPath: String): File? = try
	{
		val file = File(newPath)

		if (file.exists()) throw IOException("Folder already exists")

		if (entity.copyRecursively(file, true))
		{
			file
		} else null
	} catch (e : IOException)
	{
		null
	}

	suspend fun createFolder(path: String, name: String): File?
	{
		val file = File("$path/$name")

		file.mkdir()

		return if (file.exists()) file else null
	}
}
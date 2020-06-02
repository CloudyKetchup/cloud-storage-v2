package com.krypton.storageservice.service.storage.file

import com.krypton.storageservice.config.Storage
import com.krypton.storageservice.service.storage.folder.StorageFolderService
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

/**
 * Service for managing file storage state, yes a FILE, like document.txt, ..., not a folder.
 *
 * For folders use [StorageFolderService]
 * */
@Service
class StorageFileService @Autowired constructor(storage: Storage): IStorageFileService
{
	private val homeDir = storage.homeDir

	override suspend fun saveFromFilePart(filePart: FilePart, path: String): File?
	{
		val file = File("${homeDir}/$path")

		filePart.transferTo(file).awaitFirstOrNull()

		return if (file.exists()) file else null
	}

	override suspend fun delete(path: String): Boolean
	{
		val file = File("${homeDir}/$path")

		return file.delete()
	}

	override suspend fun move(path: String, newPath: String): File? = try
	{
		val file 	= File("${homeDir}/$path")
		val newFile = File("${homeDir}/$newPath")

		if (newFile.exists()) throw IOException("File already exists")

		if (file.renameTo(newFile))
		{
			newFile
		} else null
	} catch (e : IOException)
	{
		null
	}

	override suspend fun copy(path: String, newPath: String): File?
	{
		val file = File("${homeDir}/$path")
		val copy = File("${homeDir}/$newPath")

		if (copy.exists()) throw IOException("File already exists")

		val success = file.copyRecursively(copy, true)

		return if (success) copy else null
	}

	override suspend fun exists(path: String): Boolean = File("${homeDir}/$path").exists()
}
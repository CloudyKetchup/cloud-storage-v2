package com.krypton.storageservice.service.storage.file

import com.krypton.storageservice.service.storage.IStorageService
import com.krypton.storageservice.service.storage.folder.StorageFolderService
import kotlinx.coroutines.reactive.awaitFirstOrNull
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
class StorageFileService : IStorageService
{
	override suspend fun delete(entity: File): Boolean
	{
		return entity.delete()
	}

	override suspend fun move(entity: File, newPath: String): File? = try
	{
		val file = File(newPath)

		if (file.exists()) throw IOException("File already exists")

		if (entity.renameTo(file))
		{
			file
		} else null
	} catch (e : IOException)
	{
		null
	}

	override suspend fun copy(entity: File, newPath: String): File?
	{
		val file = File(newPath)

		if (file.exists()) throw IOException("File already exists")

		val success = entity.copyRecursively(file, true)

		return if (success) file else null
	}

	suspend fun saveFromFilePart(filePart: FilePart, file: File): File?
	{
		filePart.transferTo(file).awaitFirstOrNull()

		return if (file.exists()) file else null
	}
}
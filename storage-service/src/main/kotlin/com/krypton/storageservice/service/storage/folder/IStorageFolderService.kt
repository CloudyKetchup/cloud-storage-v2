package com.krypton.storageservice.service.storage.folder

import com.krypton.storageservice.model.FolderUsageStats
import com.krypton.storageservice.service.storage.IStorageService
import java.io.File

interface IStorageFolderService : IStorageService
{
	suspend fun getZipFile(path: String): File?

	suspend fun createFolder(path: String): File?

	suspend fun rootStats(path: String): FolderUsageStats?
}

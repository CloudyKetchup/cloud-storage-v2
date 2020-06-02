package com.krypton.storageservice.service.storage.folder

import com.krypton.storageservice.service.storage.IStorageService
import java.io.File

interface IStorageFolderService : IStorageService
{
	suspend fun createFolder(path: String): File?
}
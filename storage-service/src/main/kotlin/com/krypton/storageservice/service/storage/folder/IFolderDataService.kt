package com.krypton.storageservice.service.storage.folder

import com.krypton.storageservice.model.FileData

interface IFolderDataService
{
	fun getData(path: String): FileData?

	fun getFiles(path: String): List<FileData>?

	fun getFolders(path: String): List<FileData>?
}


package com.krypton.storageservice.service.storage.file

import com.krypton.storageservice.service.storage.IStorageService
import org.springframework.http.codec.multipart.FilePart
import java.io.File

interface IStorageFileService : IStorageService
{
	suspend fun getFile(path: String): File?

	suspend fun saveFromFilePart(filePart: FilePart, path: String): File?
}

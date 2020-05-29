package com.krypton.storagedatabaseservice.service.file

import com.krypton.storagedatabaseservice.service.IFileEntityService
import common.models.File

interface IFileService : IFileEntityService<File, String>
{
	suspend fun deleteByFolder(folderId: String)
}
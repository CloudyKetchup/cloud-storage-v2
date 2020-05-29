package com.krypton.storagedatabaseservice.service.folder

import com.krypton.storagedatabaseservice.service.IFileEntityService
import common.models.Folder

interface IFolderService : IFileEntityService<Folder, String>
{
	suspend fun getRootFolder(): Folder?

	suspend fun deleteRecursively(folderId: String, onDelete: suspend (folderId: String) -> Unit): Boolean
}

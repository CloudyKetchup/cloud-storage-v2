package com.krypton.directoryservice.client.folder

import com.krypton.directoryservice.client.IStorageClient
import com.krypton.directoryservice.client.WebClientBodyResponse
import com.krypton.directoryservice.model.FolderCreateResponse
import common.models.Folder

interface IFolderStorageClient : IStorageClient<Folder>
{
	suspend fun create(parentFolder: Folder, name: String): WebClientBodyResponse<FolderCreateResponse>
}
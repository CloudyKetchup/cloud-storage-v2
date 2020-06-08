package com.krypton.directoryservice.client.folder

import com.krypton.directoryservice.client.IStorageClient
import com.krypton.directoryservice.client.WebClientBodyResponse
import com.krypton.directoryservice.model.FolderResponse
import com.krypton.directoryservice.model.FolderMoveData
import common.models.Folder

interface IFolderStorageClient : IStorageClient<Folder>
{
	suspend fun create(parentFolder: Folder, name: String): WebClientBodyResponse<FolderResponse>

	suspend fun move(moveData: FolderMoveData): WebClientBodyResponse<Folder>

	suspend fun copy(moveData: FolderMoveData): WebClientBodyResponse<Folder>
}

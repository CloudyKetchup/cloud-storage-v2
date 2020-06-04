package com.krypton.directoryservice.client.folder

import com.krypton.directoryservice.client.IRepositoryClient
import com.krypton.directoryservice.client.WebClientBodyResponse
import common.models.Folder
import org.springframework.http.HttpStatus

interface IFolderRepositoryClient : IRepositoryClient<Folder>
{
	suspend fun update(folder: Folder): WebClientBodyResponse<Folder>

	suspend fun delete(id: String, recursively: Boolean? = false): HttpStatus

	suspend fun root(): WebClientBodyResponse<Folder>
}
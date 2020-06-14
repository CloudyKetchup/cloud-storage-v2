package com.krypton.directoryservice.client.file

import com.krypton.directoryservice.client.IStorageClient
import com.krypton.directoryservice.client.WebClientBodyResponse
import com.krypton.directoryservice.model.FileMoveData
import com.krypton.directoryservice.model.FileUploadResponse
import common.models.File
import org.springframework.http.codec.multipart.FilePart

interface IFileStorageClient : IStorageClient<File>
{
	suspend fun upload(
		file: FilePart,
		path: String
	): WebClientBodyResponse<FileUploadResponse>

	suspend fun move(moveData: FileMoveData): WebClientBodyResponse<File>

	suspend fun copy(moveData: FileMoveData): WebClientBodyResponse<File>
}

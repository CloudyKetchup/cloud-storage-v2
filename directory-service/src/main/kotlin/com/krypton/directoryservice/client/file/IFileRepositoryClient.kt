package com.krypton.directoryservice.client.file

import com.krypton.directoryservice.client.IRepositoryClient
import common.models.File
import org.springframework.http.HttpStatus

interface IFileRepositoryClient : IRepositoryClient<File>
{
	suspend fun delete(id: String): HttpStatus
}
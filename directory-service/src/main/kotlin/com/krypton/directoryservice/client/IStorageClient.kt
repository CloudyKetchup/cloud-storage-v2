package com.krypton.directoryservice.client

import com.krypton.directoryservice.model.FileMoveData
import org.springframework.http.HttpStatus

interface IStorageClient<T>
{
	suspend fun delete(body: T): HttpStatus

	suspend fun move(moveData: FileMoveData): WebClientBodyResponse<T>

	suspend fun copy(moveData: FileMoveData): WebClientBodyResponse<T>

	suspend fun rename(body: T, name: String): WebClientBodyResponse<T>
}
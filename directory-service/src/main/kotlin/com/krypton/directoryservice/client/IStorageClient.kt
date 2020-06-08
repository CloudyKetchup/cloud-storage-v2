package com.krypton.directoryservice.client

import org.springframework.http.HttpStatus

interface IStorageClient<T>
{
	suspend fun delete(body: T): HttpStatus

	suspend fun rename(body: T, name: String): WebClientBodyResponse<T>
}

package com.krypton.directoryservice.client

interface IRepositoryClient<T>
{
	suspend fun save(body: T): WebClientBodyResponse<T>

	suspend fun saveAll(body: List<T>): WebClientBodyResponse<ArrayList<T>>

	suspend fun all(folderId: String): WebClientBodyResponse<List<T>>

	suspend fun find(id: String): WebClientBodyResponse<T>
}

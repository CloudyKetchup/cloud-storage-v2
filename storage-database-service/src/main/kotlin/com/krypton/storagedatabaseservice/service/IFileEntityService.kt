package com.krypton.storagedatabaseservice.service

import kotlinx.coroutines.flow.Flow

interface IFileEntityService<E, ID> : IDaoService<E, ID>
{
	suspend fun findByFolder(folderId: ID) : Flow<E>

	suspend fun findByPath(path: String): E?
}
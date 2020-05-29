package com.krypton.storagedatabaseservice.service

interface IDaoService<E, ID>
{
	suspend fun save(entity: E): E?

	suspend fun findById(id: ID): E?

	suspend fun delete(e: E): Boolean

	suspend fun deleteById(id: ID): Boolean

	suspend fun exists(e: E): Boolean

	suspend fun existsById(id: ID): Boolean
}
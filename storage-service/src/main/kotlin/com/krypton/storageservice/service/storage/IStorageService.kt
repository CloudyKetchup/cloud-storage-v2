package com.krypton.storageservice.service.storage

import java.io.File

/**
 * Service for managing storage
 * */
interface IStorageService
{
	suspend fun delete(entity: File) : Boolean

	suspend fun move(entity: File, newPath: String) : File?

	suspend fun copy(entity: File, newPath: String) : File?
}
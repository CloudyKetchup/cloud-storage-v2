package com.krypton.storageservice.service.storage

import java.io.File

/**
 * Service for managing storage
 * */
interface IStorageService
{
	suspend fun delete(path: String) : Boolean

	suspend fun move(path: String, newPath: String) : File?

	suspend fun copy(path: String, newPath: String) : File?

	suspend fun exists(path: String): Boolean
}
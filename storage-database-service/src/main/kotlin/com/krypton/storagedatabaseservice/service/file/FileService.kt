package com.krypton.storagedatabaseservice.service.file

import com.krypton.storagedatabaseservice.repository.FileRepository
import common.models.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.asFlux
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.domain.Example
import org.springframework.stereotype.Service
import java.util.*

@Service
class FileService @Autowired constructor(private val repository: FileRepository) : IFileService
{
	override suspend fun findByFolder(folderId: String): Flow<File> = repository.findByFolder(folderId).asFlow()

	override suspend fun findByPath(path: String): File? = repository.findByPath(path).awaitFirstOrNull()

	override suspend fun findById(id: String): File? = repository.findById(id).awaitFirstOrNull()

	override suspend fun all(): Flow<File> = repository.findAll().asFlow()

	/**
	 * Save or update file to repository
	 *
	 * If file with same id was found, will update, otherwise will save like new
	 *
	 * @param entity	target file
	 * @return [File]
	 * */
	override suspend fun save(entity: File): File?
	{
		return if (findById(entity.id) == null)
			saveNew(entity)
		else
			update(entity)
	}

	override suspend fun delete(e: File): Boolean
	{
		repository.delete(e).awaitFirstOrNull()

		return !exists(e)
	}

	override suspend fun deleteById(id: String): Boolean
	{
		repository.deleteById(id).awaitFirstOrNull()

		return !existsById(id)
	}

	override suspend fun deleteByFolder(folderId: String)
	{
		repository.deleteAll(findByFolder(folderId).asFlux()).awaitFirstOrNull()
	}

	override suspend fun exists(e: File): Boolean = repository.exists(Example.of(e)).awaitSingle()

	override suspend fun existsById(id: String): Boolean = repository.existsById(id).awaitSingle()

	/**
	 * Save new file to repository
	 *
	 * @param file						[File] for save
	 * @throws DuplicateKeyException	if file with the same "path" value exists
	 * @return saved [File]
	 * */
	private suspend fun saveNew(file: File): File?
	{
		if (findByPath(file.path) != null)
			throw DuplicateKeyException("File with that 'path' value already exists")

		if (file.id.isBlank()) file.id = UUID.randomUUID().toString()

		return repository.save(file).awaitFirstOrNull()
	}

	private suspend fun update(file: File): File? = repository.save(file).awaitFirstOrNull()
}
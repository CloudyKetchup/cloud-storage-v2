package com.krypton.storagedatabaseservice.service.folder

import com.krypton.storagedatabaseservice.repository.FolderRepository
import common.models.Folder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.domain.Example
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FolderService @Autowired constructor(private val repository: FolderRepository): IFolderService
{
	override suspend fun getRootFolder(): Folder? = repository.getRootFolder(true).awaitFirstOrNull()

	override suspend fun findByFolder(folderId: String): Flow<Folder> = repository.findByFolder(folderId).asFlow()

	override suspend fun findByPath(path: String): Folder? = repository.findByPath(path).awaitFirstOrNull()

	override suspend fun save(entity: Folder): Folder?
	{
		return if (findById(entity.id) != null)
			update(entity)
		else
			saveNew(entity)
	}

	override suspend fun findById(id: String): Folder? = repository.findById(id).awaitFirstOrNull()

	override suspend fun delete(e: Folder): Boolean
	{
		repository.delete(e).awaitFirstOrNull()

		return !exists(e)
	}

	override suspend fun deleteById(id: String): Boolean
	{
		repository.deleteById(id).awaitFirstOrNull()

		return !existsById(id)
	}

	/**
	 * Deletes a folder and it's children recursively
	 *
	 * Basically, we get all that folder's child's, delete all content inside,
	 * then we delete folder itself plus calling onDelete callback.
	 * Here in onDelete we can for example delete files inside that folder as well
	 *
	 * @param folderId		obviously folder's id
	 * @param onDelete		callback on every child folder delete
	 * @return boolean on if this folder was deleted
	 * */
	@Transactional
	override suspend fun deleteRecursively(
		folderId: String,
		onDelete: suspend (folderId: String) -> Unit
	): Boolean
	{
		/** get all folders that have [folderId] as [Folder.folder] */
		findByFolder(folderId)
			.toList()
			// delete every folder and their children recursively as well
			.forEach {
				deleteRecursively(it.id, onDelete)	// run the same process for child folders
				delete(it)							// delete folder itself
				onDelete(it.id)						// onDelete callback
			}
		// folder itself
		val folder = findById(folderId)
		// delete files inside that folder
		onDelete(folderId)

		return if (folder != null) delete(folder) else false
	}

	override suspend fun exists(e: Folder): Boolean = repository.exists(Example.of(e)).awaitSingle()

	override suspend fun existsById(id: String): Boolean = repository.existsById(id).awaitSingle()

	private suspend fun saveNew(entity: Folder): Folder? = try
	{
		if (entity.root && getRootFolder() != null)
			throw DuplicateKeyException("Root folder already exists")
		else if (findByPath(entity.path) != null)
			throw DuplicateKeyException("Folder with that 'path' value already exits")

		repository.save(entity).awaitFirstOrNull()
	} catch (e : DuplicateKeyException)
	{
		e.printStackTrace()
		null
	}

	private suspend fun update(entity: Folder): Folder? = repository.save(entity).awaitFirstOrNull()
}
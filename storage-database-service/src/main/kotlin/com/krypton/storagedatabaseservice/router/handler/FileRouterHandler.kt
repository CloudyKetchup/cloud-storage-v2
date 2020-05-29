package com.krypton.storagedatabaseservice.router.handler

import com.krypton.storagedatabaseservice.service.file.IFileService
import common.models.File
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus.FOUND
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*

/**
 * File router handler
 * */
@Component
class FileHandler @Autowired constructor(private val helper: FileHandlerHelper)
{
	/**
	 * Save file to repository
	 *
	 * @param request	incoming request with [File] body
	 * @return save file
	 * */
	suspend fun save(request: ServerRequest): ServerResponse
	{
		val file = request.awaitBodyOrNull<File>()

		return if (file != null)
			helper.save(file)
		else
			badRequest().buildAndAwait()
	}

	/**
	 * Delete file from repository
	 *
	 * @param request	incoming request with file id
	 * */
	suspend fun delete(request: ServerRequest): ServerResponse = byIdAction(request, helper::delete)

	/**
	 * Get all files
	 *
	 * If "folderId" query param will be provided, will return all files
	 * related to that folder, otherwise, will just return the list of all files
	 * from the repository
	 *
	 * @param request	incoming request which may contain "folderId" query param
	 * @return list of [File]'s
	 * */
	suspend fun all(request: ServerRequest): ServerResponse
	{
		val folderId = request.queryParam("folderId")

		return if (folderId.isPresent)
			helper.findByFolder(folderId.get())
		else
			helper.all()
	}

	/**
	 * Find file by id
	 *
	 * @param request	incoming request with "id" query param
	 * @return [File] body or 404 not found status
	 * */
	suspend fun find(request: ServerRequest): ServerResponse = byIdAction(request, helper::find)

	/**
	 * An action based on [File] id
	 *
	 * Main goal is to validate id and perform the action if so
	 *
	 * @param request	incoming request with "id" query param
	 * @param action	action callback
	 * @return server response from [action] callback
	 * */
	private suspend fun byIdAction(
		request: ServerRequest,
		action: suspend (id: String) -> ServerResponse
	): ServerResponse
	{
		val id = request.queryParam("id")

		return if (id.isPresent)
			action(id.get())
		else
			badRequest().buildAndAwait()
	}
}

/**
 * Helper class for [FileHandler]
 * */
@Component
class FileHandlerHelper @Autowired constructor(private val fileService: IFileService)
{
	/**
	 * Save file to repository, may return 302 found status if
	 * a file with equal "path" value already exists
	 *
	 * @param file		file to save
	 * @return save [File] or status on error
	 * */
	suspend fun save(file: File): ServerResponse
	{
		val savedFile: File?

		try
		{
			savedFile = fileService.save(file)
		} catch (e : DuplicateKeyException)
		{
			return status(FOUND).bodyValueAndAwait(e.localizedMessage)
		}

		return if (savedFile != null)
			ok().bodyValueAndAwait(savedFile)
		else
			status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}

	/**
	 * Delete [File] by id
	 *
	 * @param id	file id
	 * @return status depending on success
	 * */
	suspend fun delete(id: String): ServerResponse
	{
		val deleted = fileService.deleteById(id)

		return if (deleted)
			ok().buildAndAwait()
		else
			status(INTERNAL_SERVER_ERROR).buildAndAwait()
	}

	/**
	 * Get all files from repository
	 *
	 * @return list of [File]'s
	 * */
	suspend fun all(): ServerResponse
	{
		val files = fileService.all().toList()

		return ok().bodyValueAndAwait(files)
	}

	/**
	 * Get all files related to provided folder id
	 *
	 * @param folderId		parent folder id
	 * @return
	 * */
	suspend fun findByFolder(folderId: String): ServerResponse
	{
		val files = fileService.findByFolder(folderId).toList()

		return ok().bodyValueAndAwait(files)
	}

	/**
	 * Find [File] by id
	 *
	 * @param id	file id
	 * @return [File] body or 404 not found status
	 * */
	suspend fun find(id: String): ServerResponse
	{
		val file = fileService.findById(id)

		return if (file != null)
			ok().bodyValueAndAwait(file)
		else
			notFound().buildAndAwait()
	}
}

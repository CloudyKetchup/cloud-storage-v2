package com.krypton.directoryservice.service.folder

import com.krypton.directoryservice.model.Directory
import common.models.File
import common.models.Folder
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.streams.toList

@Service
class FolderMoveOperationService
{
	private data class DirectoryWithFolder(
		val directory: Directory,
		val data: Folder
	)
	/**
	 * Create [File] and [Folder] models from [Directory] tree
	 *
	 * @param files			array where to push files
	 * @param folders		array where to push folders
	 * @param directory		directory tree
	 * @param folderId		current directory folder id
	 * */
	suspend fun modelsFromDirectory(
		files: ArrayList<File>,
		folders: ArrayList<Folder>,
		directory: Directory,
		folderId: String
	) : Unit = coroutineScope {
		val dateCreated = SimpleDateFormat("dd/MM/yy  hh:mm").format(Date())

		val fileModels 		= async { fileModelsFromDirectory(directory, folderId, dateCreated) }
		val folderModels 	= async { folderModelsFromDirectory(directory, folderId, dateCreated) }

		files.addAll(fileModels.await())
		folders.addAll(folderModels.await())

		folderModels.await().asFlow()
			.map { folder -> DirectoryWithFolder(
				directory 	= directory.folders.find { it.data.path == folder.path }!!,
				data 		= folder
			) }
			.collect { modelsFromDirectory(files, folders, it.directory, it.data.id) }
	}

	/**
	 * Create [File]'s from directory
	 *
	 * @param directory		directory with files
	 * @param folderId		directory folder id
	 * @param dateCreated	date when evey file was created
	 * @return [File] list
	 * */
	private suspend fun fileModelsFromDirectory(
		directory: Directory,
		folderId: String,
		dateCreated: String
	) = directory.files
		.parallelStream()
		.map { File(
			id 			= UUID.randomUUID().toString(),
			name 		= it.name,
			path 		= it.path,
			folder 		= folderId,
			size		= it.size.toInt(),
			dateCreated = dateCreated,
			extension 	= it.extension ?: ""
		) }
		.toList()

	/**
	 * Create [Folder]'s from directory
	 *
	 * @param directory		directory with folder
	 * @param folderId		directory folder id
	 * @param dateCreated	date when evey file was created
	 * @return [Folder] list
	 * */
	private suspend fun folderModelsFromDirectory(
		directory: Directory,
		folderId: String,
		dateCreated: String
	) = directory.folders
		.parallelStream()
		.map { Folder(
			id			= UUID.randomUUID().toString(),
			name 		= it.data.name,
			path 		= it.data.path,
			folder 		= folderId,
			size 		= it.data.size.toInt(),
			dateCreated = dateCreated,
			root 		= false
		) }
		.toList()
}

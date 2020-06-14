package com.krypton.storageservice.service.storage.folder

import com.krypton.storageservice.config.Storage
import com.krypton.storageservice.model.FileData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import kotlin.reflect.KFunction1
import kotlin.streams.toList

/**
 * Service for handling data content of the folder
 * */
@Service
class FolderDataService @Autowired constructor(storage: Storage) : IFolderDataService
{
	private val homeDir = storage.homeDir

	/**
	 * Get [FileData] from path
	 *
	 * @param path	folder path, should not be full path, ex: "/root/folder1/nested_folder"
	 * @return folder as [FileData] or null if folder not found by path
	 * */
	override fun getData(path: String): FileData?
	{
		val folder = File("${homeDir}/$path")

		return if (folder.exists())
			FileData(
				folder.name,
				folder.path.removePrefix(homeDir),
				folder.length(),
				folder.parent.removePrefix(homeDir)
			)
		else null
	}

	/**
	 * Get files of a folder by path
	 *
	 * @param path 	folder path
	 * @return list of [FileData] or null if folder does not exist
	 * */
	override fun getFiles(path: String): List<FileData>? = getContent(path, File::isFile)

	/**
	 * Get folders inside a folder by path
	 *
	 * @param path 	folder path
	 * @return list of [FileData] or null if folder does not exist
	 * */
	override fun getFolders(path: String): List<FileData>? = getContent(path, File::isDirectory)

	/**
	 * Get items inside a folder, will use a comparator to filter only specific items
	 *
	 * @param path			folder path
	 * @param comparator	comparator for [File] items
	 * @return list of [FileData] items
	 * */
	private fun getContent(path: String, comparator: KFunction1<File, Boolean>): List<FileData>?
	{
		val files = File("${homeDir}/$path").listFiles()

		return if (!files.isNullOrEmpty())
		{
			files.toList()
				.parallelStream()
				.filter { !it.name.startsWith(".") } // ignore files starting with dot
				.filter { comparator.invoke(it) }			// choose only selected items
				.map {
					FileData(
						it.name,
						it.path.removePrefix(homeDir),
						it.length(),
						path,
						it.extension
					)
				}
				.toList()
		} else null
	}
}

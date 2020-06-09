package com.krypton.storageservice.service.storage.folder

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FolderDataServiceTests @Autowired constructor(private val dataService: FolderDataService)
{
	@Test
	fun shouldGetData()
	{
		val path = "/root/tests_folder"

		val data = dataService.getData(path)

		println(data)
		assert(data != null)
	}

	@Test
	fun shouldFetchFile()
	{
		val path = "/root/tests_folder/folder_with_files"

		val files = dataService.getFiles(path)

		println(files)
		assert(files != null)
	}

	@Test
	fun shouldFetchFolders()
	{
		val path = "/root/tests_folder/folder_with_folders"

		val folders = dataService.getFolders(path)

		println(folders)
		assert(folders != null)
	}
}

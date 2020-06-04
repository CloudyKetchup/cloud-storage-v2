package com.krypton.directoryservice.client.folder

import common.models.Folder
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import java.util.*

@SpringBootTest
class FolderRepositoryClientTests @Autowired constructor(private val folderRepositoryClient: IFolderRepositoryClient)
{
	val folder = Folder(
		"6663fc36-9d71-49bc-bd58-79dbfc7607c1",
		"test3",
		"/root",
		"126e6628-5f3f-4ca8-8382-b4a18e2da5d8",
		64,
		Date().time.toString(),
		true
	)

	@Test
	fun shouldSave() = runBlocking {
		val response = folderRepositoryClient.save(folder)

		System.err.println(response)
		assert(response.body != null)
	}

	@Test
	fun shouldUpdate() = runBlocking {
		val response = folderRepositoryClient.update(folder.copy().apply { name = "test5" })

		System.err.println(response)
		assert(response.body != null && response.body!!.name != folder.name)
	}

	@Test
	fun shouldDelete() = runBlocking {
		val response = folderRepositoryClient.delete(folder.id)

		System.err.println(response)
		assert(response == HttpStatus.OK)
	}

	@Test
	fun shouldDeleteRecursively() = runBlocking {
		val response = folderRepositoryClient.delete("126e6628-5f3f-4ca8-8382-b4a18e2da5d8", true)

		System.err.println(response)
		assert(response == HttpStatus.OK)
	}

	@Test
	fun shouldFailFetchingRoot() = runBlocking {
		val response = folderRepositoryClient.root()

		System.err.println(response)
		assert(response.body == null)
	}

	@Test
	fun shouldFetchRoot() = runBlocking {
		val response = folderRepositoryClient.root()

		System.err.println(response)
		assert(response.body != null)
	}

	@Test
	fun shouldFetchAll() = runBlocking {
		val response = folderRepositoryClient.all("6663fc36-9d71-49bc-bd58-79dbfc7607c1")

		System.err.println(response)
		assert(response.body != null)

		response.body!!.forEach { System.err.println(it) }
	}

	@Test
	fun shouldFetchById() = runBlocking {
		val response = folderRepositoryClient.find(folder.id)

		System.err.println(response)
		assert(response.body != null)
	}
}
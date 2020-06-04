package com.krypton.directoryservice.client.file

import common.models.File
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import java.util.*

@SpringBootTest
class FileRepositoryClientTests @Autowired constructor(private val repositoryClient: IFileRepositoryClient)
{
	@Test
	fun shouldSave() = runBlocking {
		val body = File(
			id = UUID.randomUUID().toString(),
			name = "testFile.jpg",
			path = "/root/test1/testFile.jpg",
			folder = "6663fc36-9d71-49bc-bd58-79dbfc7607c1",
			size = 64,
			dateCreated = Date().time.toString(),
			extension = "jpg"
		)

		val response = repositoryClient.save(body)

		System.err.println(response.body)
		assert(response.body != null)
	}

	@Test
	fun shouldDeleteFile() = runBlocking {
		val response = repositoryClient.delete("0e41f1a8-1f51-45bd-b12a-91797d10f6ae")

		System.err.println(response)
		assert(response == HttpStatus.OK)
	}

	@Test
	fun shouldFetchAllFiles() = runBlocking {
		val response = repositoryClient.all("7e9ed441-c916-4326-b748-680c7658d9b5")

		System.err.println(response)
		assert(!response.body.isNullOrEmpty())
	}

	@Test
	fun shouldFindById() = runBlocking {
		val response = repositoryClient.find("68ac70e1-432d-40b9-bda8-9518f83dc252")

		System.err.println(response)
		assert(response.body != null)
	}
}
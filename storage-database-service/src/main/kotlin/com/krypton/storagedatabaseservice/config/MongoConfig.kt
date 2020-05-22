package com.krypton.storagedatabaseservice.config

import com.krypton.storagedatabaseservice.repository.FileRepository
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import com.krypton.storagedatabaseservice.repository.FolderRepository

@Configuration
@EnableReactiveMongoRepositories(basePackageClasses = [FolderRepository::class, FileRepository::class])
class MongoConfig : AbstractReactiveMongoConfiguration()
{
	override fun autoIndexCreation() : Boolean = true

	override fun reactiveMongoClient() : MongoClient = MongoClients.create()

	override fun getDatabaseName() : String = "cloud-storage"
}

package com.krypton.storagedatabaseservice.repository

import common.models.Folder
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface FolderRepository : FileEntityRepository<Folder, String>
{
	@Query("{ 'root' : ?0 }")
	fun getRootFolder(root: Boolean) : Mono<Folder>
}

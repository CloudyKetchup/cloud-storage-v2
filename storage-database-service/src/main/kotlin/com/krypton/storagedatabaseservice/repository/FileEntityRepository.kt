package com.krypton.storagedatabaseservice.repository

import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.repository.NoRepositoryBean
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@NoRepositoryBean
interface FileEntityRepository<E, T> : ReactiveMongoRepository<E, T>
{
  @Query("{ 'folder' : ?0 }")
  fun findByFolder(folderId : String) : Flux<E>

  @Query("{ 'path' : ?0 }")
  fun findByPath(path: String): Mono<E>
}
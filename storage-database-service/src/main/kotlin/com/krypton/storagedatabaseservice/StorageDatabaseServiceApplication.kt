package com.krypton.storagedatabaseservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class StorageDatabaseServiceApplication

fun main(args: Array<String>) {
	runApplication<StorageDatabaseServiceApplication>(*args)
}

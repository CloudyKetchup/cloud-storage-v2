package com.krypton.storageservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class StorageServiceApplication

fun main(args: Array<String>) {
	runApplication<StorageServiceApplication>(*args)
}

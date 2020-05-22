package com.krypton.directoryservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class DirectoryServiceApplication

fun main(args: Array<String>) {
	runApplication<DirectoryServiceApplication>(*args)
}

package com.krypton.storageservice.config

import com.krypton.storageservice.router.FileRouter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RoutingConfig
{
	@Bean
	fun fileStorageRouting(fileRouter: FileRouter) = fileRouter.router
}
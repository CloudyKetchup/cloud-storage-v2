package com.krypton.storagedatabaseservice.config

import com.krypton.storagedatabaseservice.router.FileRouter
import com.krypton.storagedatabaseservice.router.FolderRouter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RoutingConfig
{
	@Bean
	fun fileRouterBean(fileRouter: FileRouter) = fileRouter.router

	@Bean
	fun folderRouterBean(folderRouter: FolderRouter) = folderRouter.router
}
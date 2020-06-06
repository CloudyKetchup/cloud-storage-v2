package com.krypton.directoryservice.configuration

import com.krypton.directoryservice.router.FileRouter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebConfig
{
	@Bean
	fun fileRouting(fileRouter: FileRouter) = fileRouter.router
}

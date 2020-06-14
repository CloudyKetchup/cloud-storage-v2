package com.krypton.directoryservice.configuration

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableWebFlux
class WebClientConfiguration
{
	@Bean
	@LoadBalanced
	fun loadBalancedWebClientBuilder(): WebClient.Builder = WebClient.builder()

	@Bean
	fun storageClient(webClientBuilder: WebClient.Builder): WebClient
	{
		return webClientBuilder.baseUrl("http://storage-service:8300").build()
	}

	@Bean
	fun repositoryClient(webClientBuilder: WebClient.Builder): WebClient
	{
		return webClientBuilder.baseUrl("http://storage-database-service:8100").build()
	}
}
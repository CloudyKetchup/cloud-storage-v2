package com.krypton.directoryservice.bean

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableWebFlux
class WebClientBuilder
{
	@Bean
	@LoadBalanced
	fun loadBalancedWebClientBuilder() : WebClient.Builder = WebClient.builder()
}
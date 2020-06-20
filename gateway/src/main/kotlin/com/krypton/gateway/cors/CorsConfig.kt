package com.krypton.gateway.cors

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.cors.reactive.CorsUtils.isCorsRequest
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Configuration
@EnableWebFlux
class CorsConfiguration
{
	@Bean
	fun corsFilter() : WebFilter
	{
		return WebFilter { ctx: ServerWebExchange, chain: WebFilterChain ->
			val request = ctx.request
			if (isCorsRequest(request)) {
				val response = ctx.response
				response.headers.apply {
					add("Access-Control-Allow-Origin", "*")
					add("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS")
					add("Access-Control-Max-Age", "3600")
					add("Access-Control-Allow-Headers", "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN")
				}
				if (request.method == HttpMethod.OPTIONS)
				{
					response.statusCode = HttpStatus.OK

					Mono.empty<Unit>()
				}
			}
			chain.filter(ctx)
		}
	}
}

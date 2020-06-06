package com.krypton.directoryservice.router

import com.krypton.directoryservice.router.handler.FileHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.coRouter

@Component
class FileRouter @Autowired constructor(handler: FileHandler)
{
	val router = coRouter {
		"/file".nest {
			POST("/upload", handler::upload)
			DELETE("/delete", handler::delete)
			PUT("/move", handler::move)
			PUT("/copy", handler::copy)
			PUT("/rename", handler::rename)
		}
	}
}

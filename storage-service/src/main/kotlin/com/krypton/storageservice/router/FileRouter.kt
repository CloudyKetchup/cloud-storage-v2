package com.krypton.storageservice.router

import com.krypton.storageservice.router.handler.FileHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

/**
 * Router for handling requests related to files storage management
 * */
@Component
class FileRouter @Autowired constructor(handler: FileHandler)
{
	val router = coRouter {
		"/file".nest {
			POST("/upload", handler::save)
			DELETE("/delete", handler::delete)
			PUT("/move", handler::move)
			PUT("/copy", handler::copy)
			PUT("/rename", handler::rename)
			GET("/download", handler::download)
		}
	}
}

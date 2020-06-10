package com.krypton.storagedatabaseservice.router

import com.krypton.storagedatabaseservice.router.handler.FileHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.coRouter

@Component
class FileRouter @Autowired constructor(private val handler: FileHandler)
{
	val router = coRouter {
		"/file".nest {
			"/save".nest {
				POST("/", handler::save)
				POST("/all", handler::saveAll)
			}
			DELETE("/delete", handler::delete)
			GET("/all", handler::all)
			GET("/find", handler::find)
		}
	}
}


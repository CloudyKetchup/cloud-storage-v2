package com.krypton.storagedatabaseservice.router

import com.krypton.storagedatabaseservice.router.handler.FolderHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.coRouter

@Component
class FolderRouter @Autowired constructor(private val handler: FolderHandler)
{
	val router = coRouter {
		"/folder".nest {
			POST("/save", handler::save)
			PUT("/update", handler::update)
			DELETE("/delete", handler::delete)
			GET("/root") { handler.root() }
			GET("/all", handler::all)
			GET("/", handler::find)
		}
	}
}
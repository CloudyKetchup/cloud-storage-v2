package com.krypton.directoryservice.router

import com.krypton.directoryservice.router.handler.FolderHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.coRouter

@Component
class FolderRouter @Autowired constructor(handler: FolderHandler)
{
	val router = coRouter {
		"/folder".nest {
			POST("/create", handler::create)
			DELETE("/delete", handler::delete)
			PUT("/move", handler::move)
			PUT("/copy", handler::copy)
			GET("/root") { handler.root() }
		}
	}
}

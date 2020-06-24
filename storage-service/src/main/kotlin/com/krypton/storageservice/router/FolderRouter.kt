package com.krypton.storageservice.router

import com.krypton.storageservice.router.handler.FolderHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

/**
 * Router for handling requests related to folders storage management
 * */
@Component
class FolderRouter @Autowired constructor(handler: FolderHandler)
{
	val router = coRouter {
		"/folder".nest {
			POST("/create", handler::create)
			DELETE("/delete", handler::delete)
			PUT("/move", handler::move)
			PUT("/copy", handler::copy)
			PUT("/rename", handler::rename)
			GET("/tree", handler::getTree)
			GET("/download", handler::download)
			"/root".nest {
				GET("/stats", handler::rootStats)
				GET("/find") { handler.findRoot() }
				POST("/create") { handler.createRoot() }
			}
		}
	}
}



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
			"/save".nest {
				POST("/", handler::save)
				POST("/all", handler::saveAll)
			}
			"/update".nest {
				PUT("/", handler::update)
				PUT("/child-paths", handler::updateChildPaths)
			}
			DELETE("/delete", handler::delete)
			GET("/root") { handler.root() }
			GET("/all", handler::all)
			GET("/", handler::find)
			GET("/tree", handler::getTree)
			GET("/previous/folders", handler::getPreviousFolders)
		}
	}
}

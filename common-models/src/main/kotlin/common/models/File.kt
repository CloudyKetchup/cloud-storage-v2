package common.models

import java.util.UUID

data class File(
	var id          : String = UUID.randomUUID().toString(),
	var name        : String,
	var path        : String,
	var folder      : String,  // folder id
	var size        : Int,
	var dateCreated : String,
	var extension   : String
)

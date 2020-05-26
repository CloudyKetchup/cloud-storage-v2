package com.krypton.storageservice.model.response

data class FolderCreateResponse(
	val name : String,
	val path : String,
	val size : Long
)
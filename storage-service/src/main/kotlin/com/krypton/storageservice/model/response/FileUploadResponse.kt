package com.krypton.storageservice.model.response

data class FileUploadResponse(
	val name: String,
	val path: String,
	val size: Long,
	val extension: String
)
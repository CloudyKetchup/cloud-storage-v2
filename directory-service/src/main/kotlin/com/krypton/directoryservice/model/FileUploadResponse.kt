package com.krypton.directoryservice.model

data class FileUploadResponse(
	val name: String,
	val path: String,
	val size: Long,
	val extension: String
)
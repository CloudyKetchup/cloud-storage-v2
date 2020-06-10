package com.krypton.directoryservice.model

data class FileData(
	val name 		: String,
	val path 		: String,
	val size 		: Long,
	val parent		: String,
	val extension 	: String? = null
)

data class Directory(
	val data		: FileData,
	val folders		: ArrayList<Directory>,
	val files 		: ArrayList<FileData>
)

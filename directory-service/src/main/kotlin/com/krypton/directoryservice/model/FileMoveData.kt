package com.krypton.directoryservice.model

import common.models.File
import common.models.Folder

data class FileMoveData(
	val file 	: File,
	val folder 	: Folder
)
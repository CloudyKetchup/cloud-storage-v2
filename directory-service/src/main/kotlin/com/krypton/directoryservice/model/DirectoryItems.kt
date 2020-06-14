package com.krypton.directoryservice.model

import common.models.File
import common.models.Folder

data class DirectoryItems(
	val files 	: List<File>,
	val folders	: List<Folder>
)

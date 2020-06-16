package com.krypton.storagedatabaseservice.model

import common.models.File
import common.models.Folder

data class DirectoryTree(
	val data	: Folder,
	val folders	: ArrayList<DirectoryTree>,
	val files	: ArrayList<File>
)

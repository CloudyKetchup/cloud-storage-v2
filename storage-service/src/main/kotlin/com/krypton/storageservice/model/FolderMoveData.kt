package com.krypton.storageservice.model

import common.models.Folder

data class FolderMoveData(
	val folder			: Folder,
	val targetFolder	: Folder
)
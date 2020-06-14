package com.krypton.directoryservice.model

data class FolderUsageStats(
	val used: Long,
	val free: Long,
	val total: Long
)

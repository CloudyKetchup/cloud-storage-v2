package com.krypton.storageservice.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import java.io.File

/**
 * Startup configuration for storage
 * */
@Configuration
class StartupStorage @Autowired constructor(private val storage: Storage): CommandLineRunner
{
	override fun run(vararg args: String?)
	{
		val homeDir = File(storage.homeDir)

		if (!homeDir.exists()) homeDir.mkdirs()
	}
}
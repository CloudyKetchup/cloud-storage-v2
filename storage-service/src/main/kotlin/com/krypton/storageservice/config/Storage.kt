package com.krypton.storageservice.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class Storage @Autowired constructor(environment : Environment)
{
	private val activeProfiles = environment.activeProfiles.asList()

	val homeDir = if (activeProfiles.contains("container"))
		"/cloud-storage"
	else
		"${System.getProperty("user.home")}/cloud-storage"
}
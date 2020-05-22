package com.krypton.storagedatabaseservice.repository

import common.models.File
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : FileEntityRepository<File, String>

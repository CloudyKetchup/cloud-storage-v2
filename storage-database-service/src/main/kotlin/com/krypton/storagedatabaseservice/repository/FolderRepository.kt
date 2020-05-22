package com.krypton.storagedatabaseservice.repository

import common.models.Folder
import org.springframework.stereotype.Repository

@Repository
interface FolderRepository : FileEntityRepository<Folder, String>

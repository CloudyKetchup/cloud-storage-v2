package common.models

import java.util.UUID

data class File(
  val id          : String = UUID.randomUUID().toString(),
  val name        : String,
  val path        : String,
  val folder      : String,  // folder id
  val size        : Int,
  val dateCreated : String,
  val extension   : String
)
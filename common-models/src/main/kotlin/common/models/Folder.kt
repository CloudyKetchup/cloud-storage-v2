package common.models

import java.util.UUID

data class Folder(
  val id          : String = UUID.randomUUID().toString(),
  var name        : String,
  var path        : String,
  var folder      : String,    // folder id
  var size        : Int,
  val dateCreated : String,
  val root		  : Boolean
)
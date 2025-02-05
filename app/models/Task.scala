package models

import play.api.libs.json._
import reactivemongo.api.bson.{BSONDocumentHandler, BSONObjectID, Macros}
import reactivemongo.play.json.compat._
import bson2json._

case class Task(
  _id: BSONObjectID,
  task: String,
  isCompleted: Boolean
)

trait TaskBson {
  implicit val taskBson: BSONDocumentHandler[Task] = Macros.handler[Task]
}

object Task {
  implicit val taskFormat: OFormat[Task] = Json.format[Task]
}

case class TaskDTO(
  task: String,
  isCompleted: Boolean
)

object TaskDTO {
  implicit val taskDTOFormat: OFormat[TaskDTO] = Json.format[TaskDTO]
}
package models

import play.api.libs.json._

case class Task(
  task: String,
  isCompleted: Boolean
)

object Task {
  implicit val taskReads: Reads[Task] = Json.reads[Task]
  implicit val taskFormat: OFormat[Task] = Json.format[Task]
  implicit val taskWrites: Writes[Task] = Json.writes[Task]
}

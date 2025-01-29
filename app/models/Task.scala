package models

import play.api.libs.json.{OFormat, Json}

case class Task(
  id: Option[String],
  isDeleted: Boolean
)

object Task {
  implicit val taskFormat: OFormat[Task] = Json.format[Task]
}

package models

import play.api.libs.json.{Json, OFormat}

case class Task(
  id: Long,
  task: String,
  completed: Boolean
)

trait TaskFormat {
  implicit val taskFormat: OFormat[Task] = Json.format[Task]
}

object Task extends TaskFormat
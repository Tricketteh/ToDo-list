package models

import play.api.libs.json.{Json, OFormat}

case class TaskDTO(
  task: String,
  isCompleted: Boolean
)

trait TaskJson {
  implicit val taskDTOFormat: OFormat[TaskDTO] = Json.format[TaskDTO]
}

object TaskDTO extends TaskJson
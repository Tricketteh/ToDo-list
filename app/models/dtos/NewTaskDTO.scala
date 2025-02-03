package models.dtos

import play.api.libs.json.{Json, OFormat, Reads, Writes}

case class NewTaskDTO(
  title: String
)

object NewTaskDTO {
  implicit val newTaskDTOFormat: OFormat[NewTaskDTO] = Json.format[NewTaskDTO]
  implicit val newTaskDTOWrites: Writes[NewTaskDTO] = Json.writes[NewTaskDTO]
  implicit val newTaskDTOReads: Reads[NewTaskDTO] = Json.reads[NewTaskDTO]
}

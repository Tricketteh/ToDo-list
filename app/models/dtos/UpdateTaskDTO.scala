package models.dtos

import play.api.libs.json.{Json, OFormat, Reads, Writes}

case class UpdateTaskDTO(
  title: String,
  isCompleted: Option[Boolean]
)

object UpdateTaskDTO {
  implicit val updateTaskDTOFormat: OFormat[UpdateTaskDTO] = Json.format[UpdateTaskDTO]
  implicit val updateTaskDTOWrites: Writes[UpdateTaskDTO] = Json.writes[UpdateTaskDTO]
  implicit val updateTaskDTOReads: Reads[UpdateTaskDTO] = Json.reads[UpdateTaskDTO]
}

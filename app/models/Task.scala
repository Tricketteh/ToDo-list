package models

import play.api.libs.json._
import reactivemongo.api.bson.BSONObjectID
import utils.BSONObjectIDFormats._

case class Task(
  _id: Option[BSONObjectID],
  task: String,
  isCompleted: Boolean
)

object Task {
  implicit val taskReads: Reads[Task] = Json.reads[Task]
  implicit val taskWrites: Writes[Task] = Json.writes[Task]
  implicit val taskFormat: OFormat[Task] = Json.format[Task]
}

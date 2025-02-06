package models

import reactivemongo.api.bson.{BSONDocumentHandler, BSONObjectID, Macros}

case class Task(
  _id: BSONObjectID,
  task: String,
  isCompleted: Boolean
)

trait TaskBson {
  implicit val taskBson: BSONDocumentHandler[Task] = Macros.handler[Task]
}

object Task extends TaskBson
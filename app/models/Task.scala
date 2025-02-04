package models

import scala.util.Try

import play.api.libs.json._
import reactivemongo.api.bson.{BSONDocument, BSONDocumentHandler, BSONDocumentReader, BSONDocumentWriter, BSONObjectID, MacroOptions, Macros}
import reactivemongo.play.json.compat.bson2json._

case class Task(
  _id: Option[BSONObjectID],
  task: String,
  isCompleted: Boolean
)

object Task {
  implicit val bsonObjectIDReads: Reads[BSONObjectID] = Reads[BSONObjectID] { json =>
    json.validate[String].flatMap { str =>
      BSONObjectID.parse(str).fold(
        _ => JsError("Invalid BSONObjectID"),
        objId => JsSuccess(objId)
      )
    }
  }

  implicit val bsonObjectIDWrites: Writes[BSONObjectID] = Writes[BSONObjectID] { objId =>
    JsString(objId.stringify)
  }

  implicit val taskBSONHandler: BSONDocumentHandler[Task] = Macros.handlerOpts[Task, MacroOptions.Default]
  implicit val bsonObjectIDFormat: Format[BSONObjectID] = Format(bsonObjectIDReads, bsonObjectIDWrites)

  implicit val taskReads: Reads[Task] = Json.reads[Task]
  implicit val taskWrites: Writes[Task] = Json.writes[Task]
  implicit val taskFormat: OFormat[Task] = Json.format[Task]

  implicit object TaskBSONReader extends BSONDocumentReader[Task] {
    override def readDocument(bson: BSONDocument): Try[Task] = Try {
      Task(
        bson.getAsOpt[BSONObjectID]("_id"),
        bson.getAsOpt[String]("task").getOrElse(""),
        bson.getAsOpt[Boolean]("isCompleted").getOrElse(false)
      )
    }

  }

  implicit object TaskBSONWriter extends BSONDocumentWriter[Task] {
    override def writeTry(task: Task): Try[BSONDocument] = Try {
      BSONDocument(
        "_id" -> task._id,
        "task" -> task.task,
        "isCompleted" -> task.isCompleted
      )
    }
  }
}
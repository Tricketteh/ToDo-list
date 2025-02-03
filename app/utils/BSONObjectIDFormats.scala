package utils

import scala.util.{Failure, Success, Try}

import play.api.libs.json._
import reactivemongo.api.bson.BSONObjectID

object BSONObjectIDFormats {
  implicit val bsonObjectIDReads: Reads[BSONObjectID] = {
    case JsString(str) =>
      BSONObjectID.parse(str) match {
        case Success(id) => JsSuccess(id)
        case Failure(_) => JsError("Invalid BSONObjectID format")
      }
    case _ => JsError("Expected a string for BSONObjectID")
  }

  implicit val bsonObjectIDWrites: Writes[BSONObjectID] = new Writes[BSONObjectID] {
    def writes(id: BSONObjectID): JsValue = JsString(id.stringify)
  }

  implicit val bsonObjectIDOptionReads: Reads[Option[BSONObjectID]] = Reads.optionWithNull(bsonObjectIDReads)
  implicit val bsonObjectIDOptionWrites: Writes[Option[BSONObjectID]] = Writes.optionWithNull(bsonObjectIDWrites)

  implicit val bsonObjectIDFormat: Format[BSONObjectID] = Format(bsonObjectIDReads, bsonObjectIDWrites)
}
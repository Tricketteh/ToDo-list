package daos

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

import models.Task
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor
import reactivemongo.api.bson.{BSONDocument, BSONObjectID}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.play.json.compat.bson2json._
import reactivemongo.play.json.compat.json2bson._

class TaskDAO @Inject()(
  implicit ex: ExecutionContext,
  reactiveMongoApi: ReactiveMongoApi
) {
  private val collection: Future[BSONCollection] = reactiveMongoApi.database.map(db => db.collection("tasks"))

  def findAll(): Future[List[Task]] = {
    collection.flatMap(
      _.find(BSONDocument())
        .cursor[Task]()
        .collect[List](100, Cursor.FailOnError[List[Task]]())
    )
  }

  def create(task: Task): Future[WriteResult] = {
    collection.flatMap(_.insert(ordered = false).one(task))
  }

  def update(id: BSONObjectID, update: BSONDocument): Future[Boolean] = {
    val selector = BSONDocument("_id" -> id)
    collection.flatMap(_.update.one(selector, update).map(_.nModified > 0))
  }

  def delete(id: BSONObjectID): Future[Boolean] = {
    val selector = BSONDocument("_id" -> id)
    collection.flatMap(_.delete.one(selector).map(_.n > 0))
  }

}

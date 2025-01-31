package daos

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

import models.Task
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor
import reactivemongo.api.ReadPreference.Primary
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.play.json.compat.bson2json._
import reactivemongo.play.json.compat.json2bson._

class TaskDAO @Inject()(
  implicit ex: ExecutionContext,
  reactiveMongoApi: ReactiveMongoApi
) {
  private def collection: Future[BSONCollection] = reactiveMongoApi.database.map(db => db.collection("tasks"))

  def findAll(): Future[Seq[Task]] = {
    collection.flatMap(
      _.find(BSONDocument(), Option.empty[Task])
        .cursor[Task](Primary)
        .collect[Seq](100, Cursor.FailOnError[Seq[Task]]())
    )
  }

  def createTask(task: Task): Future[WriteResult] = {
    collection.flatMap(_.insert(ordered = false).one(task))
  }

  def updateTask(taskName: String, task: Task): Future[WriteResult] = {
    collection.flatMap(_.update(ordered = false).one(BSONDocument("task" -> taskName), task))
  }

  def deleteTask(taskName: String): Future[WriteResult] = {
    collection.flatMap(_.delete.one(BSONDocument("task" -> taskName)))
  }

}

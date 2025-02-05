package services

import javax.inject._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import daos.TaskDAO
import models.{Task, TaskDTO}
import reactivemongo.api.bson.{BSONDocument, BSONObjectID}

@Singleton
class TaskService @Inject()(taskDAO: TaskDAO)(implicit ec: ExecutionContext) {

  def createTask(dto: TaskDTO): Future[Task] = {
    val taskCreate = Task(BSONObjectID.generate(), dto.task, dto.isCompleted)
    taskDAO.create(taskCreate).map(_ => taskCreate)
  }

  def getAllTasks: Future[List[Task]] = {
    taskDAO.findAll()
  }

  def updateTask(id: String, dto: TaskDTO): Future[Option[Task]] = {
    BSONObjectID.parse(id) match {
      case Success(objectId) =>
        val update = BSONDocument(
          "$set" -> BSONDocument(
            "task" -> dto.task,
            "isCompleted" -> dto.isCompleted
          )
        )
        taskDAO.update(objectId, update).map {
          case true => Some(Task(objectId, dto.task, dto.isCompleted))
          case false => None
        }
      case Failure(_) => Future.successful(None)
    }
  }

  def deleteTask(id: String): Future[Boolean] = {
    BSONObjectID.parse(id) match {
      case Success(objectId) => taskDAO.delete(objectId)
      case Failure(_) => Future.successful(false)
    }
  }

}

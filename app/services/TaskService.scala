package services

import javax.inject._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import daos.TaskDAO
import models.{Task, TaskDTO}
import reactivemongo.api.bson.{BSONDocument, BSONObjectID}

@Singleton
class TaskService @Inject()(taskDAO: TaskDAO)(implicit ec: ExecutionContext) {

  def createTask(dto: TaskDTO): Future[TaskDTO] = {
    val taskCreate = Task(BSONObjectID.generate(), dto.task, dto.isCompleted)
    taskDAO.create(taskCreate).map(_ => TaskDTO(taskCreate.task, taskCreate.isCompleted))
  }

  def getAllTasks(completedFilter: Option[Boolean]): Future[List[TaskDTO]] = {
    taskDAO.findAll(completedFilter).map(tasks => tasks.map(task => TaskDTO(task.task, task.isCompleted)))
  }

  def getTaskByID(id: String): Future[Option[TaskDTO]] = {
    BSONObjectID.parse(id) match {
      case Success(objectID) =>
        taskDAO.findById(objectID).map {
          case Some(task) => Some(TaskDTO(task.task, task.isCompleted))
          case None => None
        }
      case Failure(_) => Future.successful(None)
    }
  }

  def updateTask(id: String, dto: TaskDTO): Future[Option[TaskDTO]] = {
    BSONObjectID.parse(id) match {
      case Success(objectId) =>
        val update = BSONDocument(
          "$set" -> BSONDocument(
            "task" -> dto.task,
            "isCompleted" -> dto.isCompleted
          )
        )
        taskDAO.update(objectId, update).map {
          case true => Some(TaskDTO(dto.task, dto.isCompleted))
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

  def deleteCompletedTasks(): Future[Boolean] = {
    taskDAO.deleteCompleted()
  }

}

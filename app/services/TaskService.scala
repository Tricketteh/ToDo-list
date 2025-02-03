package services

import javax.inject._

import scala.concurrent.{ExecutionContext, Future}

import daos.TaskDAO
import models.Task
import models.dtos.{NewTaskDTO, UpdateTaskDTO}
import reactivemongo.api.bson.BSONObjectID
import reactivemongo.api.commands.WriteResult

@Singleton
class TaskService @Inject()(taskDAO: TaskDAO)(implicit ec: ExecutionContext) {

  def createTask(dto: NewTaskDTO): Future[WriteResult] = {
    val task = Task(None, dto.title, isCompleted = false)
    taskDAO.createTask(task)
  }

  def getAllTasks: Future[Seq[Task]] = {
    taskDAO.findAll()
  }

  def updateTask(id: BSONObjectID, dto: UpdateTaskDTO): Future[WriteResult] = {
    taskDAO.findAll().flatMap { tasks =>
      tasks.find(task => task._id.contains(id)) match {
        case Some(_) =>
          taskDAO.updateTask(id, dto)
      }
    }
  }

  def deleteTask(id: BSONObjectID): Future[WriteResult] = {
    taskDAO.findAll().flatMap { tasks =>
      tasks.find(task => task._id.contains(id)) match {
        case Some(_) => taskDAO.deleteTask(id)
      }
    }
  }

}

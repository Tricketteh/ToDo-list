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

  def createTask(task: Task): Future[WriteResult] = {
    val taskCreate = Task(None, task.task, task.isCompleted)
    taskDAO.create(taskCreate)
  }

  def getAllTasks: Future[Seq[Task]] = {
    taskDAO.findAll()
  }

  def updateTask(id: BSONObjectID, dto: UpdateTaskDTO): Future[WriteResult] = {
    taskDAO.findAll().flatMap { tasks =>
      tasks.find(task => task._id.contains(id)) match {
        case Some(_) =>
          taskDAO.update(id, dto)
      }
    }
  }

  def deleteTask(id: BSONObjectID): Future[WriteResult] = {
    taskDAO.findAll().flatMap { tasks =>
      tasks.find(task => task._id.contains(id)) match {
        case Some(_) => taskDAO.delete(id)
      }
    }
  }

}

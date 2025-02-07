package services

import daos.TaskDAO
import models.{Task, TaskDTO}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import reactivemongo.api.bson.BSONObjectID
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.concurrent.ScalaFutures

class TaskServiceSpec extends PlaySpec with MockitoSugar with ScalaFutures {

  "TaskServiceSpec" should {
    "return all tasks as DTOs" in {
      val tasks = List(
        Task(
          BSONObjectID.generate(),
          "first task",
          isCompleted = false
        ),
        Task(
          BSONObjectID.generate(),
          "second task",
          isCompleted = true
        ),
        Task(
          BSONObjectID.generate(),
          "third task",
          isCompleted = true
        )
      )
      val dao = mock[TaskDAO]
      val service = new TaskService(dao)
      when(dao.findAll()).thenReturn(Future.successful(tasks))
      val result = service.getAllTasks
      result.map { tasks =>
        tasks.mustBe(TaskDTO)
      }
    }

    "delete all completed tasks and return true" in {
      val dao = mock[TaskDAO]
      val service = new TaskService(dao)
      when(dao.deleteCompleted()).thenReturn(Future.successful(true))
      val result = service.deleteCompletedTasks()
      whenReady(result) { res =>
        res mustBe true
      }
    }

    "return task by id" in {

    }

    "update task by id" in {

    }

    "create a new task" in {

    }

    "delete task by id and return true" in {
      val dao = mock[TaskDAO]
      val service = new TaskService(dao)
      val id: BSONObjectID = BSONObjectID.generate()
      when(dao.delete(any())).thenReturn(Future.successful(true))
    }
  }
}

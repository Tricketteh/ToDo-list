package services

import daos.TaskDAO
import models.{Task, TaskDTO}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import reactivemongo.api.bson.{BSONDocument, BSONObjectID}
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers._
import reactivemongo.api.commands.WriteResult

class TaskServiceSpec extends PlaySpec with MockitoSugar with ScalaFutures {

  val mockTaskDAO: TaskDAO = mock[TaskDAO]
  val taskService = new TaskService(mockTaskDAO)

  "TaskService" should {

    "create a new task" in {
      val taskDTO = TaskDTO("New Task", isCompleted = false)
      val task = Task(BSONObjectID.generate(), taskDTO.task, taskDTO.isCompleted)

      val mockWriteResult = mock[WriteResult]
      when(mockWriteResult.writeErrors).thenReturn(Seq())

      when(mockTaskDAO.create(any[Task])).thenReturn(Future.successful(mockWriteResult))

      whenReady(taskService.createTask(taskDTO)) { result =>
        result shouldBe taskDTO
        verify(mockTaskDAO, times(1)).create(any[Task])
      }
    }

    "return all tasks when no filter is applied" in {
      val tasks = List(
        Task(BSONObjectID.generate(), "Task 1", isCompleted = false),
        Task(BSONObjectID.generate(), "Task 2", isCompleted = true)
      )

      when(mockTaskDAO.findAll(None)).thenReturn(Future.successful(tasks))

      whenReady(taskService.getAllTasks(None)) { result =>
        result should have size 2
        result.map(_.task) should contain allElementsOf List("Task 1", "Task 2")
        verify(mockTaskDAO, times(1)).findAll(None)
      }
    }

    "return only completed tasks when filter is applied" in {
      val tasks = List(Task(BSONObjectID.generate(), "Completed Task", isCompleted = true))

      when(mockTaskDAO.findAll(Some(true))).thenReturn(Future.successful(tasks))

      whenReady(taskService.getAllTasks(Some(true))) { result =>
        result should have size 1
        result.head.isCompleted shouldBe true
        verify(mockTaskDAO, times(1)).findAll(Some(true))
      }
    }

    "return task by ID if it exists" in {
      val id = BSONObjectID.generate()
      val task = Task(id, "Task 1", isCompleted = false)

      when(mockTaskDAO.findById(id)).thenReturn(Future.successful(Some(task)))

      whenReady(taskService.getTaskByID(id.stringify)) { result =>
        result shouldBe Some(TaskDTO("Task 1", isCompleted = false))
        verify(mockTaskDAO, times(1)).findById(id)
      }
    }

    "return None if task by ID does not exist" in {
      val id = BSONObjectID.generate()

      when(mockTaskDAO.findById(id)).thenReturn(Future.successful(None))

      whenReady(taskService.getTaskByID(id.stringify)) { result =>
        result shouldBe None
        verify(mockTaskDAO, times(1)).findById(id)
      }
    }

    "update task successfully" in {
      val id = BSONObjectID.generate()
      val taskDTO = TaskDTO("Updated Task", isCompleted = true)
      val updateDoc = BSONDocument(
        "$set" -> BSONDocument(
          "task" -> taskDTO.task,
          "isCompleted" -> taskDTO.isCompleted
        )
      )

      when(mockTaskDAO.update(id, updateDoc)).thenReturn(Future.successful(true))

      whenReady(taskService.updateTask(id.stringify, taskDTO)) { result =>
        result shouldBe Some(taskDTO)
        verify(mockTaskDAO, times(1)).update(id, updateDoc)
      }
    }

    "return None when updating a non-existent task" in {
      val id = BSONObjectID.generate()
      val taskDTO = TaskDTO("Updated Task", isCompleted = true)
      val updateDoc = BSONDocument(
        "$set" -> BSONDocument(
          "task" -> taskDTO.task,
          "isCompleted" -> taskDTO.isCompleted
        )
      )

      when(mockTaskDAO.update(id, updateDoc)).thenReturn(Future.successful(false))

      whenReady(taskService.updateTask(id.stringify, taskDTO)) { result =>
        result shouldBe None
        verify(mockTaskDAO, times(1)).update(id, updateDoc)
      }
    }

    "delete task successfully" in {
      val id = BSONObjectID.generate()

      when(mockTaskDAO.delete(id)).thenReturn(Future.successful(true))

      whenReady(taskService.deleteTask(id.stringify)) { result =>
        result shouldBe true
        verify(mockTaskDAO, times(1)).delete(id)
      }
    }

    "return false when deleting a non-existent task" in {
      val id = BSONObjectID.generate()

      when(mockTaskDAO.delete(id)).thenReturn(Future.successful(false))

      whenReady(taskService.deleteTask(id.stringify)) { result =>
        result shouldBe false
        verify(mockTaskDAO, times(1)).delete(id)
      }
    }

    "delete completed tasks successfully" in {
      when(mockTaskDAO.deleteCompleted()).thenReturn(Future.successful(true))

      whenReady(taskService.deleteCompletedTasks()) { result =>
        result shouldBe true
        verify(mockTaskDAO, times(1)).deleteCompleted()
      }
    }
  }
}

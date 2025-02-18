package controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

import models.TaskDTO
import org.apache.pekko.stream.Materializer
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{NOT_FOUND, NO_CONTENT, OK}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.{DELETE, GET, contentAsJson, defaultAwaitTimeout, status, stubControllerComponents}
import services.TaskService

class TaskControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite with ScalaFutures {

  implicit lazy val materializer: Materializer = app.materializer

  val mockTaskService: TaskService = mock[services.TaskService]
  val controller = new TaskController(mockTaskService, stubControllerComponents())

  "TaskController" should {

    "return all tasks when no query parameter is provided" in {
      val tasks = List(TaskDTO("Task 1", isCompleted = false), TaskDTO("Task 2", isCompleted = true))
      when(mockTaskService.getAllTasks(None)).thenReturn(Future.successful(tasks))

      val request = FakeRequest(GET, "/tasks")
      val result = controller.getAllTasks()(request)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(tasks)
    }

    "return filtered tasks when query parameter is provided" in {
      val tasks = List(TaskDTO("Completed Task", isCompleted = true))
      when(mockTaskService.getAllTasks(Some(true))).thenReturn(Future.successful(tasks))

      val request = FakeRequest(GET, "/tasks?completed=true")
      val result = controller.getAllTasks()(request)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(tasks)
    }

    "return task by ID if found" in {
      val task = TaskDTO("Test Task", isCompleted = false)
      when(mockTaskService.getTaskByID("validId")).thenReturn(Future.successful(Some(task)))

      val request = FakeRequest(GET, "/tasks/validId")
      val result = controller.getTaskById("validId")(request)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(task)
    }

    "return 404 if task by ID is not found" in {
      when(mockTaskService.getTaskByID("invalidId")).thenReturn(Future.successful(None))

      val request = FakeRequest(GET, "/tasks/invalidId")
      val result = controller.getTaskById("invalidId")(request)

      status(result) mustBe NOT_FOUND
    }

    "delete an existing task" in {
      when(mockTaskService.deleteTask("taskId")).thenReturn(Future.successful(true))

      val request = FakeRequest(DELETE, "/tasks/taskId")
      val result = controller.deleteTask("taskId")(request)

      status(result) mustBe NO_CONTENT
    }

    "return 404 when deleting a non-existing task" in {
      when(mockTaskService.deleteTask("taskId")).thenReturn(Future.successful(false))

      val request = FakeRequest(DELETE, "/tasks/taskId")
      val result = controller.deleteTask("taskId")(request)

      status(result) mustBe NOT_FOUND
    }

    "delete all completed tasks" in {
      when(mockTaskService.deleteCompletedTasks()).thenReturn(Future.successful(true))

      val request = FakeRequest(DELETE, "/tasks/completed")
      val result = controller.deleteCompletedTasks()(request)

      status(result) mustBe NO_CONTENT
    }

  }

}

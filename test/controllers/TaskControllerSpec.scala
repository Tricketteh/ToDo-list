package controllers

import scala.concurrent.{ExecutionContext, Future}

import models.TaskDTO
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Results
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers.baseApplicationBuilder.injector
import play.api.test.Helpers.{contentType, defaultAwaitTimeout, status}
import play.test.WithApplication
import services.TaskService

class TaskControllerSpec extends PlaySpec with MockitoSugar with Results with ScalaFutures {

  lazy val taskService = mock[TaskService]
  implicit lazy val executionContext = injector.instanceOf[ExecutionContext]
  val controller = new TaskController(taskService, Helpers.stubControllerComponents())

  "TaskController GET" should {
    "return a list of users" in new WithApplication(){
      val result = controller.getAllTasks().apply(FakeRequest())
      status(result) mustBe Ok
      contentType(result) mustBe Some("application/Json")
    }
  }

  "TaskController POST" should {
    "create a new task" in new WithApplication() {

    }
  }

}

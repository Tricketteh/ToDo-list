package controllers

import javax.inject._

import scala.concurrent.{ExecutionContext, Future}

import models._
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.TaskService

@Singleton
class TaskController @Inject()(
  val taskService: TaskService,
  val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext) extends BaseController {

  def getAllTasks: Action[AnyContent] = Action.async { request =>
    val completedFilter: Option[Boolean] = request.getQueryString("completed").map(_.toBoolean)
    taskService.getAllTasks(completedFilter).map {
      tasks =>
        Ok(Json.toJson(tasks)
        )
    }
  }

  def getTaskById(id: String): Action[AnyContent] = Action.async {
    taskService.getTaskByID(id).map {
      case Some(task) => Ok(Json.toJson(task))
      case None => NotFound(Json.obj("error" -> s"Task not found with id $id"))
    }
  }

  def createTask(): Action[JsValue] = Action.async(parse.json) { implicit request => {
    request.body.validate[TaskDTO].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> errors.mkString(",")))),
      dto => taskService.createTask(dto).map(task => Created(Json.toJson(task)))
    )
  }
  }

  def updateTask(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[TaskDTO].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> errors.mkString(",")))),
      dto => taskService.updateTask(id, dto).map {
        case Some(updatedTask) => Ok(Json.toJson(updatedTask))
        case None => NotFound(Json.obj("error" -> "Task not found"))
      }
    )
  }

  def deleteTask(id: String): Action[AnyContent] = Action.async {
    taskService.deleteTask(id).map {
      case true => NoContent
      case false => NotFound(Json.obj("error" -> "Task not found"))
    }
  }

  def deleteCompletedTasks(): Action[AnyContent] = Action.async {
    taskService.deleteCompletedTasks().map {
      case true => NoContent
      case false => NotFound(Json.obj("error" -> "No completed tasks found"))
    }
  }

}

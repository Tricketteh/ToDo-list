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
  implicit ec: ExecutionContext,
  val taskService: TaskService,
  val controllerComponents: ControllerComponents
) extends BaseController {

  def getAllTasks: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    taskService.getAllTasks.map {
      tasks =>
        Ok(Json.toJson(tasks)
        )
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

}

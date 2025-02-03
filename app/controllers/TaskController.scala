package controllers

import javax.inject._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import models.dtos.{NewTaskDTO, UpdateTaskDTO}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import reactivemongo.api.bson.BSONObjectID
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

  def createTask(): Action[JsValue] = Action.async(controllerComponents.parsers.json) { implicit request => {
    request.body.validate[NewTaskDTO].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      task => taskService.createTask(task).map {
        _ => Created(Json.toJson(task))
      }
    )
  }
  }

  def update(id: String): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[UpdateTaskDTO] match {
      case JsSuccess(dto, _) =>
        BSONObjectID.parse(id) match {
          case Success(objectId) =>
            taskService.updateTask(objectId, dto).map(_ => Ok("Task updated successfully"))
          case Failure(_) =>
            Future.successful(BadRequest("Invalid BSONObjectID format"))
        }
      case JsError(_) =>
        Future.successful(BadRequest("Invalid format"))
    }
  }

  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    BSONObjectID.parse(id) match {
      case Success(objectId) =>
        taskService.deleteTask(objectId).map { writeResult =>
          if (writeResult.n > 0) Ok(s"Task with ID $objectId deleted")
          else NotFound("Task not found")
        }
      case Failure(_) =>
        Future.successful(BadRequest("Invalid BSONObjectID format"))
    }
  }

}

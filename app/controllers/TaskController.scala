package controllers

import javax.inject._

import scala.concurrent.{ExecutionContext, Future}

import daos.TaskDAO
import models.Task
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc._

@Singleton
class TaskController @Inject()(
  implicit ec: ExecutionContext,
  val taskDAO: TaskDAO,
  val controllerComponents: ControllerComponents
) extends BaseController {

  def findAll(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    taskDAO.findAll().map {
      tasks =>
        Ok(Json.toJson(tasks)
        )
    }
  }

  def create(): Action[JsValue] = Action.async(controllerComponents.parsers.json) { implicit request => {
    request.body.validate[Task].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      task => taskDAO.createTask(task).map {
        _ => Created(Json.toJson(task))
      }
    )

  }
  }


}

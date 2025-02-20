package daos

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import models.Task
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.CanBeQueryCondition._

@Singleton
class TaskDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private class TasksTable(tag: Tag) extends Table[Task](tag, "tasks") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def completed = column[Boolean]("completed")
    def * = (id, title, completed).mapTo[Task]
  }

  private val tasks = TableQuery[TasksTable]

  def findAll(completedFilter: Option[Boolean]): Future[Seq[Task]] = {
    db.run(
      tasks
        .filterOpt(completedFilter)((task, completed) => task.completed === completed.bind)
        .result)
  }

  def findById(id: Long): Future[Option[Task]] = {

  }

  def create(task: Task): Future[Long] = {
  }

  def update(id: Long, update: taskDTO): Future[Boolean] = {
  }

  def delete(id: Long): Future[Boolean] = {
  }

  def deleteCompleted(): Future[Boolean] = {
  }

}

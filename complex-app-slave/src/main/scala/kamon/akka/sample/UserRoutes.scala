package kamon.akka.sample

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{ get, post }
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import kamon.akka.sample.UserRegistryActor.{ ActionPerformed, CreateUser, GetUsers }

import scala.concurrent.Future
import scala.concurrent.duration._

//#user-routes-class
trait UserRoutes extends JsonSupport {
  //#user-routes-class

  // we leave these abstract, since they will be provided by the App
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[UserRoutes])

  // other dependencies that UserRoutes use
  def userRegistry: ActorRef

  def remotelyDeployedUSerRegistry: ActorRef

  def remoteUserRegistry: ActorRef
  //def remoteLBUserRegistry: ActorRef

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  //#all-routes
  //#users-get-post
  //#users-get-delete
  lazy val userRoutes: Route =
    pathPrefix("local") {
      pathPrefix("users") {
        concat(
          //#users-get-delete
          pathEnd {
            concat(
              get {
                val users: Future[Users] =
                  (userRegistry ? GetUsers).mapTo[Users]
                complete(users)
              },
              post {
                entity(as[User]) { user =>
                  val userCreated: Future[ActionPerformed] =
                    (userRegistry ? CreateUser(user)).mapTo[ActionPerformed]
                  onSuccess(userCreated) { performed =>
                    log.info("Created user [{}]: {}", user.name, performed.description)
                    complete((StatusCodes.Created, performed))
                  }
                }
              }
            )
          }
        )
        //#users-get-delete
      }
    } ~
      pathPrefix("remote") {
        pathPrefix("existing") {
          pathPrefix("users") {
            concat(
              //#users-get-delete
              pathEnd {
                concat(
                  get {
                    val users: Future[Users] =
                      (remoteUserRegistry ? GetUsers).mapTo[Users]
                    complete(users)
                  },
                  post {
                    entity(as[User]) { user =>
                      val userCreated: Future[ActionPerformed] =
                        (remoteUserRegistry ? CreateUser(user)).mapTo[ActionPerformed]
                      onSuccess(userCreated) { performed =>
                        log.info("Created user [{}]: {}", user.name, performed.description)
                        complete((StatusCodes.Created, performed))
                      }
                    }
                  }
                )
              }
            )
            //#users-get-delete
          }
        } ~
          pathPrefix("deploy") {
            pathPrefix("users") {
              concat(
                //#users-get-delete
                pathEnd {
                  concat(
                    get {
                      val users: Future[Users] =
                        (remotelyDeployedUSerRegistry ? GetUsers).mapTo[Users]
                      complete(users)
                    },
                    post {
                      entity(as[User]) { user =>
                        val userCreated: Future[ActionPerformed] =
                          (remotelyDeployedUSerRegistry ? CreateUser(user)).mapTo[ActionPerformed]
                        onSuccess(userCreated) { performed =>
                          log.info("Created user [{}]: {}", user.name, performed.description)
                          complete((StatusCodes.Created, performed))
                        }
                      }
                    }
                  )
                }
              )
              //#users-get-delete
            }
          } ~
          pathPrefix("lb") {
            pathPrefix("users") {
              concat(
                //#users-get-delete
                pathEnd {
                  concat(
                    get {
                      val users: Future[Users] =
                        (remotelyDeployedUSerRegistry ? GetUsers).mapTo[Users]
                      complete(users)
                    },
                    post {
                      entity(as[User]) { user =>
                        val userCreated: Future[ActionPerformed] =
                          (remotelyDeployedUSerRegistry ? CreateUser(user)).mapTo[ActionPerformed]
                        onSuccess(userCreated) { performed =>
                          log.info("Created user [{}]: {}", user.name, performed.description)
                          complete((StatusCodes.Created, performed))
                        }
                      }
                    }
                  )
                }
              )
              //#users-get-delete
            }
          }
      }

  //#all-routes
}

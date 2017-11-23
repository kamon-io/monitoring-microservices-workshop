package com.lightbend.akka.http.sample

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.Http

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives.{ pathPrefix, _ }
import akka.http.scaladsl.model.{ HttpRequest, StatusCodes }
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path

import scala.concurrent.{ Await, Future }
import com.lightbend.akka.http.sample.UserRegistryActor._
import akka.pattern.ask
import akka.stream.{ ActorMaterializer, ActorMaterializerSettings }
import akka.util.Timeout

import scala.util.{ Failure, Success }

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
    pathPrefix("client") {
      pathPrefix("ok") {
        pathEnd {
          get {
            val res = Http(system).singleRequest(HttpRequest(uri = "http://localhost:8080/local/users"))(ActorMaterializer(ActorMaterializerSettings(system)))
            onComplete(res) {
              case Success(value) => complete(s"The result was $value")
              case Failure(ex) => complete("err")
            }
          }
        }
      } ~ pathPrefix("remote") {
        pathEnd {
          get {
            val res = Http(system).singleRequest(HttpRequest(uri = "http://localhost:8080/remote/existing/users"))(ActorMaterializer(ActorMaterializerSettings(system)))
            onComplete(res) {
              case Success(value) => complete(s"The result was $value")
              case Failure(ex) => complete("err")
            }
          }
        }
      } ~ pathPrefix("error") {
        pathEnd {
          concat(
            get {
              val res = Http(system).singleRequest(HttpRequest(uri = "http://localhost:8080/error"))(ActorMaterializer(ActorMaterializerSettings(system)))
              onComplete(res) {
                case Success(value) => complete(s"The result was $value")
                case Failure(ex) => complete("err")
              }
            }
          )
        }
      }
    } ~
      pathPrefix("error") {
        pathEnd {
          concat(
            get {
              throw new RuntimeException("runtime error")
              complete("ok")
            }
          )
        }
        //#users-get-delete
      } ~
      pathPrefix("local") {
        pathPrefix("users") {
          concat(
            pathEnd {
              get {
                val users: Future[Users] =
                  (userRegistry ? GetUsers).mapTo[Users]
                complete(users)
              }
            }
          )
        }
      } ~
      pathPrefix("remote") {
        pathPrefix("existing") {
          pathPrefix("users") {
            concat(
              pathEnd {
                get {
                  val users: Future[Users] =
                    (remoteUserRegistry ? GetUsers).mapTo[Users]
                  complete(users)
                }
              }
            )
          }
        } ~
          pathPrefix("deploy") {
            pathPrefix("users") {
              concat(
                pathEnd {
                  get {
                    val users: Future[Users] =
                      (remotelyDeployedUSerRegistry ? GetUsers).mapTo[Users]
                    complete(users)
                  }
                }
              )
            }
          } ~
          pathPrefix("lb") {
            pathPrefix("users") {
              concat(
                pathEnd {
                  get {
                    val users: Future[Users] =
                      (remotelyDeployedUSerRegistry ? GetUsers).mapTo[Users]
                    complete(users)
                  }
                }
              )
            }
          }
      }

}

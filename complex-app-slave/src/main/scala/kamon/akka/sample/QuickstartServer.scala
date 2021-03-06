package kamon.akka.sample

import akka.actor.{ ActorRef, ActorSystem, AddressFromURIString, Deploy, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import akka.remote.RemoteScope
import akka.stream.ActorMaterializer
import kamon.Kamon
import kamon.jaeger.Jaeger
import kamon.kamino.{ KaminoReporter, KaminoTracingReporter }
import kamon.system.SystemMetrics
import kamon.zipkin.ZipkinReporter

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.io.StdIn

//#main-class
object QuickstartServer extends App with UserRoutes {

  Kamon.addReporter(new KaminoReporter)
  Kamon.addReporter(new KaminoTracingReporter)
  Kamon.addReporter(new ZipkinReporter)
  Kamon.addReporter(new Jaeger)

  SystemMetrics.startCollecting()
  implicit val system: ActorSystem = ActorSystem("demo2")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val remoteAddress = AddressFromURIString("akka.tcp://demo1@127.0.0.1:2552")

  val userRegistry: ActorRef = system.actorOf(UserRegistryActor.props, "traced-userRegistryActor")

  lazy val remotelyDeployedUSerRegistry = system.actorOf(Props[UserRegistryActor].withDeploy(Deploy(scope = RemoteScope(remoteAddress))), "traced-remotelyDeployedUserRegistry")

  lazy val remoteUserRegistry = Await.result(system.actorSelection("akka.tcp://demo2@10.0.0.1:2552/user/traced-userRegistryActor").resolveOne(3 seconds), 4 seconds)

  //lazy val remoteLBUserRegistry = Await.result(system.actorSelection("akka.tcp://demo2@10.0.0.1:2552/user/traced-lb-userRegistryActor").resolveOne(3 seconds), 4 seconds)

  /*  val routeeRepos = (1 to 5) map { i =>
    ActorRefRoutee(system.actorOf(UserRegistryActor.props, s"traced-routed-userRegistryActor-$i"))
  }

  val a = Router(RoundRobinRoutingLogic(), routeeRepos.toVector)*/

  //TODO
  //start

  lazy val routes: Route = userRoutes

  val serverBindingFuture: Future[ServerBinding] = Http().bindAndHandle(routes, "localhost", 8081)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

  StdIn.readLine()

  serverBindingFuture
    .flatMap(_.unbind())
    .onComplete { done =>
      done.failed.map { ex => log.error(ex, "Failed unbinding") }
      system.terminate()
    }

}

//local registry
//remote registry
//loadBalanced remote registry
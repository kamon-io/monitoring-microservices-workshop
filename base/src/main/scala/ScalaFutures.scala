import akka.actor.{Actor, ActorSystem, Props}
import kamon.Kamon
import kamon.context.Key

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


object ScalaFutures extends App {
  val UserID = Key.local[String]("user-id", null)
  val system = ActorSystem("test-system")

  val actor = system.actorOf(Props[Whatever], "my-whatever-actor")


  val number = Kamon.withContextKey(UserID, "ivan") {
    println("Context Before the Future: " + Kamon.currentContext().get(UserID))

    Future {
      println("Thread in Step One: " + Thread.currentThread().getName)
      println("Context: " + Kamon.currentContext().get(UserID))
      "123"
    }.map(text => {
      println("Thread in Step Two: " + Thread.currentThread().getName)
      println("Context: " + Kamon.currentContext().get(UserID))
      actor ! "hello"
      text.toInt
    }).map(n => {
      println("Thread in Step Three: " + Thread.currentThread().getName)
      println("Context: " + Kamon.currentContext().get(UserID))
      println("The number is: " + n)
    })
  }


  Await.result(number, Duration.Inf)

}

class Whatever extends Actor {
  override def receive: Receive = {
    case anything =>
      println("Got something with context: " + Kamon.currentContext().get(ScalaFutures.UserID))
  }
}

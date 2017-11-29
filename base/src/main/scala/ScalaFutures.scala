import akka.actor.{Actor, ActorSystem, Props}
import kamon.Kamon
import kamon.context.Key
import kamon.jaeger.Jaeger
import kamon.prometheus.PrometheusReporter
import kamon.system.SystemMetrics
import kamon.trace.Span
import org.slf4j.LoggerFactory

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


object ScalaFutures extends App {
  val logger = LoggerFactory.getLogger("ScalaFutures")
  SystemMetrics.startCollecting()
  Kamon.addReporter(new PrometheusReporter)
  Kamon.addReporter(new Jaeger)

  val system = ActorSystem("test-system")
  val actor = system.actorOf(Props[Whatever], "my-whatever-actor")
  val actor2 = system.actorOf(Props[Whatever], "my-whatever-ble")


  val number = Kamon.withSpan(Kamon.buildSpan("operationAwesomeness").start(), finishSpan = true) {
    logger.info("Context Before the Future: " + Kamon.currentContext().get(Span.ContextKey))

    Future {
      logger.info("Thread in Step One: " + Thread.currentThread().getName)
      logger.info("Context: " + Kamon.currentContext().get(Span.ContextKey))
      "123"
    }.map(text => {
      logger.info("Thread in Step Two: " + Thread.currentThread().getName)
      logger.info("Context: " + Kamon.currentContext().get(Span.ContextKey))
      actor ! "hello"
      actor ! "hello"
      actor ! "hello"
      text.toInt
    }).map(n => {
      logger.info("Thread in Step Three: " + Thread.currentThread().getName)
      logger.info("Context: " + Kamon.currentContext().get(Span.ContextKey))
      logger.info("The number is: " + n)
    })


  }


  Await.result(number, Duration.Inf)

}

class Whatever extends Actor {
  val logger = LoggerFactory.getLogger("Whatever")
  val other = context.actorOf(Props[Other], "the-other")
  override def receive: Receive = {
    case anything =>
      logger.info("Got something with context: " + Kamon.currentContext().get(Span.ContextKey))
      other ! anything
      other ! anything
      other ! anything
      other ! anything
  }
}

class Other extends Actor {
  val logger = LoggerFactory.getLogger("Other")
  override def receive: Receive = {
    case buuuuh =>
      Kamon.currentContext().get(Span.ContextKey).tag("value", "awesome")

      logger.info("On The Other actor")
  }
}

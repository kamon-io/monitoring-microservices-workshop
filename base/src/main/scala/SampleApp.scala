import com.typesafe.config.Config
import kamon.{Kamon, MetricReporter}
import kamon.context.{Context, Key}
import kamon.jaeger.Jaeger
import kamon.metric.{MeasurementUnit, TickSnapshot}

object SampleApp extends App {
  Kamon.addReporter(new MyReporter())
  Kamon.addReporter(new Jaeger)

  val counter = Kamon.counter("my-counter").increment()
  val gauge = Kamon.gauge("my-gauge")
  val histogram = Kamon.histogram("my-histogram")
  val mmCounter = Kamon.minMaxCounter("my-min-max-counter",
    MeasurementUnit.time.nanoseconds).increment()


  while(true) {
    val span = Kamon.buildSpan("GetSomeCoolData")
      .start()
      .tag("user", "Ivan")
    Thread.sleep(300)
    span.finish()
  }


  val myLocalKey = Key.local[String]("test", null)
  val myBroadcastKey = Key.broadcast[Option[String]]("test", None)
  val myStringBroadcasy = Key.broadcastString("whatever")



  val context = Context.Empty.withKey(myLocalKey, "Test")
  val localKey = context.get(myLocalKey)
  Kamon.currentContext().get(myLocalKey)











}

class MyReporter extends MetricReporter {
  override def reportTickSnapshot(snapshot: TickSnapshot): Unit = {
    snapshot.metrics.counters.foreach(m => {
      println(m.name)
    })
  }

  override def start(): Unit = {}

  override def stop(): Unit = {}

  override def reconfigure(config: Config): Unit = {}
}




























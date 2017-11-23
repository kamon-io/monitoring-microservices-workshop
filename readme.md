

- Introduction to Kamon
  - A bit about the origins
  - What's different between Kamon and other libraries?
  - Exploring the Kamon APIs
    - Metrics
      - Instrument types suported by Kamon (Counters, Gauges, Histograms and MinMaxCounters)
      - Creating and refining metrics.
      - Recoding metrics
    - Tracing
      - Small intro to tracing
      - Differences between this Kamon and other tracers
      - Creating and manipulating Spans
    - Context
      - What is it? what can you use it for?
      - Local and Remote keys
      - Goals of Context Propagation
      - Use case and example with the tracer
  - Creating Metrics and Span Reporters.
  - Basic Configuration Settings
  - Putting it all together in a simple application with a custom Span and Metrics Reporter.

- Start getting things for free!
  - Context Propagation in Action: Scala and Akka Actor
  - Logging with a TraceID
  - Actor, Dispatchers, Routers and Group Metrics
  - JVM and Host Metrics
  - Getting Akka HTTP
  - Reporting Traces





Useful Notes:

Starting the Prometheus Container:

```
docker run -d --net=host --name=prometheus -v /home/ivantopo/projects/monitoring-microservices-with-kamon/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus -config.file=/etc/prometheus/prometheus.yml -storage.local.path=/prometheus
```

Reconfigure the Prometheus Container:

```
docker kill --signal=HUP prometheus
```

Starting the Jaeger Container

```
docker run -d --rm --net=host --name jaeger jaegertracing/all-in-one:latest
```
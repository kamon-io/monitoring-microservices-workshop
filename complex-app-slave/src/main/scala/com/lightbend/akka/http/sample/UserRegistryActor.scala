package com.lightbend.akka.http.sample

import akka.actor.{ Actor, ActorLogging, Props }
import kamon.Kamon
import kamon.trace.Span

//#user-case-classes
final case class User(name: String, age: Int, countryOfResidence: String)
final case class Users(users: Seq[User])
//#user-case-classes

object UserRegistryActor {
  final case class ActionPerformed(description: String)
  final case object GetUsers
  final case object DeleteUsers
  final case class CreateUser(user: User)
  final case class GetUser(name: String)
  final case class DeleteUser(name: String)

  def props: Props = Props[UserRegistryActor]
}

class UserRegistryActor extends Actor with ActorLogging {
  import UserRegistryActor._

  var users = Set.empty[User]

  def addUser(user: User) = {
    val span = Kamon.buildSpan("inserting-users-service")
      .withTag("time", System.currentTimeMillis())
      .withTag("user", user.name)
      .start()
    Kamon.withContext(Kamon.currentContext().withKey(Span.ContextKey, span)) {
      println("adding user")
      users += user

      DBService.insert

    }
    span.finish()
  }

  def getUsers: Users = {
    val span = Kamon.buildSpan("getting-users-service")
      .withTag("time", System.currentTimeMillis())
      .withTag("userCount", users.size)
      .start()
    Kamon.withContext(Kamon.currentContext().withKey(Span.ContextKey, span)) {
      println("fetching users")
      DBService.insert
      DBService.query
      DBService.delete
    }
    span.finish()

    Users(users.toSeq)
  }

  def deleteUsers: Users = {
    val span = Kamon.buildSpan("deleting-users-service")
      .withTag("time", System.currentTimeMillis())
      .withTag("userCount", users.size)
      .start()
    Kamon.withContext(Kamon.currentContext().withKey(Span.ContextKey, span)) {
      println("fetching users")
      DBService.delete
    }
    span.finish()

    Users(users.toSeq)
  }

  def receive: Receive = {
    case GetUsers =>
      sender() ! getUsers
    case CreateUser(user) =>
      addUser(user)
      sender() ! ActionPerformed(s"User ${user.name} created.")
    case DeleteUsers =>
      deleteUsers
      sender() ! ActionPerformed(s"Users deleted.")
  }
}


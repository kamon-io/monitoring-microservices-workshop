package com.lightbend.akka.http.sample

import java.sql.{ Connection, PreparedStatement, SQLException, Statement }
import java.sql.DriverManager

object DBService {

  private val DB_DRIVER = "org.h2.Driver"
  private val DB_CONNECTION = "jdbc:h2:~/test"
  private val DB_USER = ""
  private val DB_PASSWORD = ""

  import org.h2.tools.DeleteDbFiles

  DeleteDbFiles.execute("~", "test", true)

  val conn = getDBConnection
  conn.setAutoCommit(false)
  val createPreparedStatement = conn.prepareStatement("CREATE TABLE PERSON(id int primary key, name varchar(255))")
  createPreparedStatement.executeUpdate
  createPreparedStatement.close()

  def query: Unit = {
    val connection = getDBConnection
    var selectPreparedStatement: PreparedStatement = null
    val SelectQuery = "select * from PERSON"
    try {
      selectPreparedStatement = connection.prepareStatement(SelectQuery)
      val rs = selectPreparedStatement.executeQuery
      System.out.println("H2 Database inserted through PreparedStatement")
      while ({
        rs.next
      }) System.out.println("Id " + rs.getInt("id") + " Name " + rs.getString("name"))
      selectPreparedStatement.close()
      connection.commit
    } catch {
      case e: Throwable =>
        println("Exception Message " + e.getLocalizedMessage)
      case e: Exception =>
        e.printStackTrace()
    } finally connection.close
  }

  def insert: Unit = {
    val connection = getDBConnection
    var insertPreparedStatement: PreparedStatement = null
    val InsertQuery = "INSERT INTO PERSON" + "(id, name) values" + "(?,?)"
    try {
      insertPreparedStatement = connection.prepareStatement(InsertQuery)
      insertPreparedStatement.setInt(1, 1)
      insertPreparedStatement.setString(2, "Jose")
      insertPreparedStatement.executeUpdate
      insertPreparedStatement.close()
      connection.commit
    } catch {
      case e: Throwable =>
        println("Exception Message " + e.getLocalizedMessage)
      case e: Exception =>
        e.printStackTrace()
    } finally connection.close
  }

  def delete: Unit = {
    val connection = getDBConnection
    var insertPreparedStatement: PreparedStatement = null
    val InsertQuery = "DELETE FROM PERSON"
    try {
      insertPreparedStatement = connection.prepareStatement(InsertQuery)
      insertPreparedStatement.executeUpdate()
      insertPreparedStatement.close()
      connection.commit
    } catch {
      case e: Throwable =>
        println("Exception Message " + e.getLocalizedMessage)
      case e: Exception =>
        e.printStackTrace()
    } finally connection.close
  }

  private def getDBConnection: Connection = {
    var dbConnection: Connection = null
    try
      Class.forName(DB_DRIVER)
    catch {
      case e: ClassNotFoundException =>
        System.out.println(e.getMessage)
    }
    try {
      dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD)
      return dbConnection
    } catch {
      case e: Throwable => println(e.getMessage)
    }
    dbConnection
  }
}

package com.agiledon.scala.wheel.datasource

import java.sql.{DriverManager, Connection}
import scala.util.{Failure, Success, Try}
import com.typesafe.config.ConfigFactory

trait DataSource {
  def getConnection(): Connection
}

object DataSource {

  //set default data source to mysql
  implicit object MySql extends MySqlDataSource

}

class MySqlDataSource extends DataSource {
  private val host = loadProperty("mysql.host")
  private val port = loadProperty("mysql.port")
  private val db = loadProperty("mysql.db")

  val url = s"jdbc:mysql://$host:$port/$db"
  val driver = loadProperty("mysql.driver")
  val username = loadProperty("mysql.user")
  val password = loadProperty("mysql.password")

  def loadProperty(name: String): String = {
    val conf = ConfigFactory.load()
    conf.getString(name)
  }

  def getConnection(): Connection = {
    Class.forName(driver)
    Try(DriverManager.getConnection(url, username, password)) match {
      case Success(result) => result
      case Failure(ex) => throw ex
    }
  }
}

package com.agiledon.scala.wheel

import java.sql.{DriverManager, Connection}
import scala.util.{Failure, Success, Try}

trait DataSource {
  def getConnection(): Connection
}

object DataSource {
  //set default data source to mysql
  implicit object MySql extends MySqlDataSource
}

class MySqlDataSource extends DataSource {
  val url = "jdbc:mysql://localhost:3306/wheel"
  val driver = "com.mysql.jdbc.Driver"
  val username = "root"
  val password = "root"

  def getConnection(): Connection = {
      Class.forName(driver)
      Try(DriverManager.getConnection(url, username, password)) match {
        case Success(result) => result
        case Failure(ex) => throw ex
      }
  }
}

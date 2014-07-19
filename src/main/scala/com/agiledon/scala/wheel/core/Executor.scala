package com.agiledon.scala.wheel.core

import java.sql.{SQLException, Statement, ResultSet, Connection}
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import com.agiledon.scala.wheel.datasource.DataSource

object Executor {

  //default: ResultSet => List[List[String]]
  implicit val Converter: ResultSet => List[List[String]] = source => {
    var rows: List[List[String]] = List()
    val columnCount = source.getMetaData.getColumnCount

    while (source.next()) {
      val row = (1 to columnCount).map(source.getString).toList
      rows = row :: rows
    }

    rows.reverse
  }

  abstract class Converter[T] extends (ResultSet => T)

  trait QueryExecutor {
    this: Sql =>

    def query[T](implicit dataSource: DataSource, converter: ResultSet => T): Option[T] = {
      val conn = dataSource.getConnection()
      try {
        query[T](conn)(converter)
      } finally {
        if (conn != null) conn.close()
      }
    }

    //for transaction, don't need close connection
    def query[T](conn: Connection)(implicit converter: ResultSet => T): Option[T] = {
      executeQuery(conn, sqlStatement, converter) match {
        case Right(result) => Some(result)
        case Left(_) => None
      }
    }

    //async method
    //suggest using onComplete or (onSuccess, onFailure) callback(s) to handle the result
    def asyncQuery[T](implicit dataSource: DataSource, converter: ResultSet => T): Future[Option[T]] = {
      Future {
        query[T](dataSource, converter)
      }
    }
  }

  trait CommandExecutor {
    this: Sql =>

    def execute(implicit dataSource: DataSource): Boolean = {
      val conn = dataSource.getConnection()
      try {
        execute(conn)
      } finally {
        if (conn != null) conn.close()
      }
    }

    //for transaction, don't need close connection
    def execute(conn: Connection): Boolean = {
      executeCommand(conn, sqlStatement) match {
        case Right(result) => result
        case Left(_) => false
      }
    }

    //async method
    //suggest using onComplete or (onSuccess, onFailure) callback(s) to handle the result
    def asyncExecute(implicit dataSource: DataSource): Future[Boolean] = {
      Future {
        execute(dataSource)
      }
    }
  }

  trait BatchCommandExecutor {
    this: Sql with CommandExecutor =>

    //for transaction
    def batchExecute(conn: Connection): Array[Int] = {
      executeWith(conn) {
        stmt =>
          stmt.addBatch(sqlStatement)
          stmt.executeBatch()
      } match {
        case Right(result) => result
        case Left(_) => Array()
      }
    }
  }

  private def executeQuery[T](conn: Connection, sql: String, converter: ResultSet => T): Either[SQLException, T] = {
    var stmt: Statement = null
    var rs: ResultSet = null
    try {
      stmt = conn.createStatement()
      rs = stmt.executeQuery(sql)
      Right(converter(rs))
    } catch {
      case e: SQLException => {
        e.printStackTrace()
        Left(e)
      }
    } finally {
      try {
        if (rs != null) rs.close()
        if (stmt != null) stmt.close()
      } catch {
        case e: SQLException => e.printStackTrace()
      }
    }
  }

  private def executeCommand(conn: Connection, sql: String): Either[SQLException, Boolean] = {
    executeWith(conn) {
      stmt =>
        stmt.executeUpdate(sql) > 0
    }
  }

  private def executeWith[T](conn: Connection)(f: Statement => T): Either[SQLException, T] = {
    var stmt: Statement = null
    try {
      stmt = conn.createStatement()
      Right(f(stmt))
    } catch {
      case e: SQLException => {
        e.printStackTrace()
        Left(e)
      }
    } finally {
      try {
        if (stmt != null) stmt.close()
      } catch {
        case e: SQLException => e.printStackTrace()
      }
    }
  }
}



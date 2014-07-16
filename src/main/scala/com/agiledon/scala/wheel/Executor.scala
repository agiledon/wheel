package com.agiledon.scala.wheel

import java.sql.{SQLException, Statement, ResultSet, Connection}
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

object Executor {

  trait QueryExecutor {
    this: Sql =>

    //default: ResultSet => List[List[String]]
    implicit val converter: ResultSet => List[List[String]] = rs => {
      var rows: List[List[String]] = List()
      val columnCount = rs.getMetaData.getColumnCount

      while (rs.next()) {
        val row = (1 to columnCount).map(rs.getString).toList
        rows = row :: rows
      }

      rows.reverse
    }

    def query[T](implicit dataSource: DataSource, converter: ResultSet => T): Option[T] = {
      val conn = dataSource.getConnection()
      try {
        query[T](conn, converter)
      } finally {
        if (conn != null) conn.close()
      }
    }

    //for transaction, don't need close connection
    def query[T](implicit conn: Connection, converter: ResultSet => T): Option[T] = {
      executeQuery(conn) match {
        case Right(result) => Some(converter(result))
        case Left(_) => None
      }
    }

    //async method
    //suggest using onComplete or (onSuccess, onFailure) callback(s) to handle the result
    def queryAsync[T](implicit dataSource: DataSource, converter: ResultSet => T): Future[Option[T]] = {
      Future {
        query[T](dataSource, converter)
      }
    }

    private def executeQuery(conn: Connection): Either[SQLException, ResultSet] = {
      var stmt: Statement = null
      var rs: ResultSet = null
      try {
        stmt = conn.createStatement()
        rs = stmt.executeQuery(sqlStatement)
        Right(rs)
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
      executeCommand(conn) match {
        case Right(result) => result
        case Left(_) => false
      }
    }

    //async method
    //suggest using onComplete or (onSuccess, onFailure) callback(s) to handle the result
    def executeAsync(implicit dataSource: DataSource): Future[Boolean] = {
      Future {
        execute(dataSource)
      }
    }

    private def executeCommand(conn: Connection): Either[SQLException, Boolean] = {
      var stmt: Statement = null
      try {
        stmt = conn.createStatement()
        Right(stmt.execute(sqlStatement))
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

  trait BatchCommandExecutor {
    this: Sql with CommandExecutor =>

    def batchExecute(implicit dataSource: DataSource): String = {
      List("begin transaction", execute(dataSource), "commit").mkString("\n")
    }
  }

}



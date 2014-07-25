package com.agiledon.scala.wheel.core

import java.sql.{SQLException, Statement, ResultSet, Connection}
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import com.agiledon.scala.wheel.datasource.DataSource
import com.agiledon.scala.wheel.core.WrappedResultSet.wrapResultSet
import com.agiledon.scala.wheel.LogSupport

object Executor extends LogSupport {

  trait QueryExecutor {
    this: Sql =>

    def query(implicit dataSource: DataSource): Table = {
      val conn = dataSource.getConnection()
      try {
        query(conn)
      } finally {
        if (conn != null) conn.close()
      }
    }

    //for transaction, don't need close connection
    def query(conn: Connection): Table = {
      executeQuery(conn, sqlStatement)
    }

    //async method
    //suggest using onComplete or (onSuccess, onFailure) callback(s) to handle the result
    def asyncQuery(implicit dataSource: DataSource): Future[Table] = {
      Future {
        query(dataSource)
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

  private def executeQuery(conn: Connection, sql: String): Table = {
    var stmt: Statement = null
    var rs: ResultSet = null
    try {
      stmt = conn.createStatement()
      rs = stmt.executeQuery(sql)
      DataTable(rs.rows)
    } catch {
      case e: SQLException => {
        log.error(e.getMessage)
        e.printStackTrace()
        NullTable
      }
    } finally {
      try {
        if (rs != null) rs.close()
        if (stmt != null) stmt.close()
      } catch {
        case e: SQLException => {
          log.error(e.getMessage)
          e.printStackTrace()
        }
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
        log.debug(e.getMessage)
        Left(e)
      }
    } finally {
      try {
        if (stmt != null) stmt.close()
      } catch {
        case e: SQLException => {
          log.error(e.getMessage)
          e.printStackTrace()
        }
      }
    }
  }
}



package com.agiledon.scala.wheel.core

import java.sql.{SQLException, Connection}
import com.agiledon.scala.wheel.datasource.DataSource

trait TransactionScope {
  def using(f: Connection => Unit)(implicit dataSource: DataSource) {
    val conn = dataSource.getConnection()
    try {
      conn.setAutoCommit(false)
      f(conn)
      conn.commit()
    } catch {
      case ex: Exception => {
        conn.rollback()
      }
    } finally {
      if (conn != null) conn.close()
    }
  }
}

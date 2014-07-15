package com.agiledon.scala.wheel

import java.sql.{SQLException, Connection}

trait TransactionScope {
  def using(f: Connection => Unit)(implicit dataSource: DataSource) {
    val conn = dataSource.getConnection()
    try {
      conn.setAutoCommit(false)
      f(conn)
      conn.commit()
    } catch {
      case ex: SQLException => {
        conn.rollback()
      }
    } finally {
      if (conn != null) conn.close()
    }
  }
}

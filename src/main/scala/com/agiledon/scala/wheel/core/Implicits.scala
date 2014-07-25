package com.agiledon.scala.wheel.core

import java.sql.ResultSet

object Implicits {
  implicit def sql(sqlStatement: String):Sql = new Sql(sqlStatement)

  implicit def resultSet(rs: ResultSet) = WrappedResultSet(rs)
}

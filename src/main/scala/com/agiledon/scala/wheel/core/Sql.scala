package com.agiledon.scala.wheel.core

import Executor.{BatchCommandExecutor, CommandExecutor, QueryExecutor}

class Sql(val sqlStatement: String) extends QueryExecutor with CommandExecutor with BatchCommandExecutor {
  def this(sqls: String*) {
    this(sqls.mkString(SQL_SPLITTER))
  }

}

object Sql {
  def apply(sqlStatement: String) = new Sql(sqlStatement)
  def apply(sqlStatement: String*) = new Sql(sqlStatement:_*)
}


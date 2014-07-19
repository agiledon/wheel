package com.agiledon.scala.wheel.core

import Executor.{BatchCommandExecutor, CommandExecutor, QueryExecutor}

class Sql(val sqlStatement: String) extends QueryExecutor with CommandExecutor with BatchCommandExecutor {
  def this(sqls: Sql*) {
    this(sqls.map(_.sqlStatement).mkString("\n"))
  }

}

object Sql {
  def apply(sqlStatement: String) = new Sql(sqlStatement)
  def apply(sqls: Sql*) = new Sql(sqls.map(_.sqlStatement).mkString("\n"))
}


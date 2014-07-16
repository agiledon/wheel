package com.agiledon.scala.wheel

import com.agiledon.scala.wheel.Executor.{BatchCommandExecutor, CommandExecutor, QueryExecutor}

sealed trait SqlStatement

case class SqlPart(sqlClause: String) extends SqlStatement

class Sql(val sqlStatement: String) extends SqlStatement with QueryExecutor with CommandExecutor with BatchCommandExecutor {
  def this(sqls: Sql*) {
    this(sqls.map(_.sqlStatement).mkString("\n"))
  }

}

object Sql {
  def apply(sqlStatement: String) = new Sql(sqlStatement)
  def apply(sqls: Sql*) = new Sql(sqls.map(_.sqlStatement).mkString("\n"))
}


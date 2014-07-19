package com.agiledon.scala.wheel.syntax

import com.agiledon.scala.wheel.core.Sql
import com.agiledon.scala.wheel._
import com.agiledon.scala.wheel.syntax.SqlPart

sealed trait SqlStatement

case class SqlPart(sqlClause: String) extends SqlStatement

class SqlGenerator(paras: Parameters) {
  def sql(generators: SqlPartGenerator*): Sql = {
    generateSql(generators.map(g => g(paras)): _*)
  }

  private def generateSql(parts: SqlPart*): Sql = {
    new Sql(parts.map(_.sqlClause).mkString(" "))
  }
}

object SqlGenerator {
  def apply(paras: Parameters): SqlGenerator = new SqlGenerator(paras)
}

package com.agiledon.scala.wheel


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

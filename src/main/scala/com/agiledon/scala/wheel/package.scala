package com.agiledon.scala

package object wheel {
  type Parameters = Map[String, Part]
  type SqlPartGenerator = Parameters => SqlPart

  val selectPartGenerator: SqlPartGenerator = e => {
    SqlPart(s"select ${e.getPart("columns")} from ${e.getPart("tablename")}")
  }

  val insertPartGenerator: SqlPartGenerator = e => {
    SqlPart(s"insert into ${e.getPart("tablename")}")
  }

  val wherePartGenerator: SqlPartGenerator = e => {
    SqlPart(s"where ${e.getPart("condition")}")
  }

  implicit class PartMapUtil(paras: Parameters) {
    def getPart(key: String): Part = {
      paras.get(key) match {
        case Some(x) => x
        case None => throw new SqlParametersException(s"can not find parameter by key [$key]")
      }
    }
  }
}

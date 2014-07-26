package com.agiledon.scala.wheel.core

import java.sql.ResultSet

object Implicits {
  implicit def sql(sqlStatement: String):Sql = new Sql(sqlStatement)

  implicit class WrappedResultSet(private[this] val rs: ResultSet) {
    private val handlers: Map[String, (Int, ResultSet) => Any] = Map(
      "VARCHAR" -> ((no, resultSet) => resultSet.getString(no)),
      "INT" -> ((no, resultSet) => resultSet.getInt(no)),
      "BOOLEAN" -> ((no, resultSet) => resultSet.getBoolean(no))
    )

    def rows: List[Row] = {
      var rows = List[Row]()
      val metadata = rs.getMetaData

      def getColumnValue(columnNo: Int, resultSet: ResultSet): Any = {
        handlers.get(metadata.getColumnTypeName(columnNo)) match {
          case Some(handler) => handler(columnNo, resultSet)
          case None => resultSet.getString(columnNo)
        }
      }

      while (rs.next()) {
        val row: Row = DataRow((1 to metadata.getColumnCount)
          .map(x => Cell(metadata.getColumnName(x), getColumnValue(x, rs)))
          .toList)
        rows = row :: rows
      }
      rows.reverse
    }
  }
}

package com.agiledon.scala.wheel.core

import java.sql.ResultSet
import scala.collection.GenTraversableOnce

trait Row[+A]

case class DataRow[A](elements: A*) extends Row[A]

case object NullRow extends Row[Nothing]

trait Table[+A] {
  def firstRow: Row[A] = this match {
    case DataTable(rows) => rows.head
    case _ => NullRow
  }

  def firstRowOption: Option[Row[A]] = this match {
    case DataTable(rows) => Some(rows.head)
    case _ => None
  }

  def lastRow: Row[A] = this match {
    case DataTable(rows) => rows.last
    case _ => NullRow
  }

  def lastRowOption: Option[Row[A]] = this match {
    case DataTable(rows) => Some(rows.last)
    case _ => None
  }

  def foreach[B](f: Row[A] => B) {
    this match {
      case DataTable(rows) => rows.foreach(f)
      case _ =>
    }
  }
}

case class DataTable[A](rows: List[Row[A]]) extends Table[A]

case object NullTable extends Table[Nothing]

class WrappedResultSet(private[this] val rs: ResultSet) {
  private val handlers: Map[String, (Int, ResultSet) => Any] = Map(
    "VARCHAR" -> ((no, resultSet) => resultSet.getString(no)),
    "INT" -> ((no, resultSet) => resultSet.getInt(no)),
    "BOOLEAN" -> ((no, resultSet) => resultSet.getBoolean(no))
  )

  private val table: List[Map[String, Any]] = {
    var rows = List[Map[String, Any]]()
    val metadata = rs.getMetaData
    val columnCount = metadata.getColumnCount

    def getColumnValue(columnNo: Int, resultSet: ResultSet): Any = {
      handlers.get(metadata.getColumnTypeName(columnNo)) match {
        case Some(handler) => handler(columnNo, resultSet)
        case None => resultSet.getString(columnNo)
      }
    }

    while (rs.next()) {
      val row: Map[String, Any] = (1 to columnCount).map(x => (metadata.getColumnName(x), getColumnValue(x, rs))).toMap
      rows = row :: rows
    }
    rows.reverse
  }

  def head: Map[String, Any] = table.head

  def tail: List[Map[String, Any]] = table.tail

  def foreach[B](f: Map[String, Any] => B) = table.foreach(f)

  def map[B](f: Map[String, Any] => B) = table.map(f)

  def flatMap[B](f: Map[String, Any] => GenTraversableOnce[B]) = table.flatMap(f)

  def take(n: Int) = table.take(n)
}

class NullWrappedResultSet extends WrappedResultSet(null)

object WrappedResultSet {
  def apply(rs: ResultSet) = new WrappedResultSet(rs)
}

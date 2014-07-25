package com.agiledon.scala.wheel.core

import java.sql.ResultSet

private[core] case class Cell(name: String, value: Any)

trait Row {
  def cells: List[Any] = this match {
    case DataRow(c) => c.map(_.value)
    case _ => Nil
  }

  def cell(columnName: String): Option[Any] = this match {
    case DataRow(c) => c.find(x => x.name == columnName).map(_.value)
    case _ => None
  }
}

case class DataRow(c: List[Cell]) extends Row

case object NullRow extends Row

trait Table {
  def head: Row = this match {
    case DataTable(rows) => rows.head
    case _ => NullRow
  }

  def headOption: Option[Row] = this match {
    case DataTable(rows) => rows.headOption
    case _ => None
  }

  def tail: Table = this match {
    case DataTable(rows) => DataTable(rows.tail)
    case _ => NullTable
  }

  def last: Row = this match {
    case DataTable(rows) => rows.last
    case _ => NullRow
  }

  def lastOption: Option[Row] = this match {
    case DataTable(rows) => lastOption
    case _ => None
  }

  def foreach[B](f: Row => B) {
    this match {
      case DataTable(rows) => rows.foreach(f)
      case _ =>
    }
  }

  //row no is begin with 1
  def row(rowNo: Int): Row = take(rowNo).last

  def first(f: Row => Boolean): Option[Row] = takeWhile(f).headOption

  def filter(f: Row => Boolean): Table = this match {
    case DataTable(rows) => DataTable(rows.filter(f))
    case _ => NullTable
  }

  def take(count: Int): Table = this match {
    case DataTable(rows) => DataTable(rows.take(count))
    case _ => NullTable
  }

  def takeWhile(f: Row => Boolean): Table = this match {
    case DataTable(rows) => DataTable(rows.takeWhile(f))
    case _ => NullTable
  }

  def drop(count: Int): Table = this match {
    case DataTable(rows) => DataTable(rows.drop(count))
    case _ => NullTable
  }

  def dropWhile(f: Row => Boolean): Table = this match {
    case DataTable(rows) => DataTable(rows.dropWhile(f))
    case _ => NullTable
  }
}

case class DataTable(rows: List[Row]) extends Table

case object NullTable extends Table

class WrappedResultSet(private[this] val rs: ResultSet) {
  private val handlers: Map[String, (Int, ResultSet) => Any] = Map(
    "VARCHAR" -> ((no, resultSet) => resultSet.getString(no)),
    "INT" -> ((no, resultSet) => resultSet.getInt(no)),
    "BOOLEAN" -> ((no, resultSet) => resultSet.getBoolean(no))
  )
  
  lazy val rows: List[Row] = {
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

object WrappedResultSet {
  def apply(rs: ResultSet) = new WrappedResultSet(rs)
}



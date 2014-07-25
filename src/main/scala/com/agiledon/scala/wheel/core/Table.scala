package com.agiledon.scala.wheel.core

import java.sql.ResultSet
import scala.collection.GenTraversableOnce

trait Row[+A]

case class DataRow[A](elements: A*) extends Row[A]

case object NullRow extends Row[Nothing]

trait Table[+A] {
  def head: Row[A] = this match {
    case DataTable(rows) => rows.head
    case _ => NullRow
  }

  def headOption: Option[Row[A]] = this match {
    case DataTable(rows) => rows.headOption
    case _ => None
  }

  def tail: Table[A] = this match {
    case DataTable(rows) => DataTable(rows.tail)
    case _ => NullTable
  }

  def last: Row[A] = this match {
    case DataTable(rows) => rows.last
    case _ => NullRow
  }

  def lastOption: Option[Row[A]] = this match {
    case DataTable(rows) => lastOption
    case _ => None
  }

  def foreach[B](f: Row[A] => B) {
    this match {
      case DataTable(rows) => rows.foreach(f)
      case _ =>
    }
  }

  //row no is begin with 1
  def row(rowNo: Int): Row[A] = take(rowNo).last

  def first(f: Row[A] => Boolean): Option[Row[A]] = takeWhile(f).headOption

  def filter(f: Row[A] => Boolean): Table[A] = this match {
    case DataTable(rows) => DataTable(rows.filter(f))
    case _ => NullTable
  }

  def take(count: Int): Table[A] = this match {
    case DataTable(rows) => DataTable(rows.take(count))
    case _ => NullTable
  }

  def takeWhile(f: Row[A] => Boolean): Table[A] = this match {
    case DataTable(rows) => DataTable(rows.takeWhile(f))
    case _ => NullTable
  }

  def drop(count: Int): Table[A] = this match {
    case DataTable(rows) => DataTable(rows.drop(count))
    case _ => NullTable
  }

  def dropWhile(f: Row[A] => Boolean): Table[A] = this match {
    case DataTable(rows) => DataTable(rows.dropWhile(f))
    case _ => NullTable
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

package com.agiledon.scala.wheel.core

import scala.collection.SeqView

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

  def find(f: Row => Boolean): Option[Row] = this match {
    case DataTable(rows) => rows.find(f)
    case _ => None
  }

  def filter(f: Row => Boolean): Table = this match {
    case DataTable(rows) => DataTable(rows.filter(f))
    case _ => NullTable
  }

  def map[B](f: Row => B): List[B] = this match {
    case DataTable(rows) => rows.map(f)
    case _ => Nil
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

  def length: Int = this match {
    case DataTable(rows) => rows.length
    case _ => 0
  }

  def count(f: Row => Boolean): Int = filter(f).length

  def view: SeqView[Row, List[Row]] = this match {
    case DataTable(rows) => rows.view
    case NullTable => Nil.view
  }
}

case class DataTable(rows: List[Row]) extends Table

case object NullTable extends Table



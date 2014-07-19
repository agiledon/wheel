package com.agiledon.scala.wheel.syntax

sealed trait Part

case class Columns(name: String*) extends Part {
  override def toString() = name.mkString(", ")
}
case class TableName(name: String) extends Part {
  override def toString() = name
}
case class Condition(expression: String) extends Part {
  override def toString = expression
}

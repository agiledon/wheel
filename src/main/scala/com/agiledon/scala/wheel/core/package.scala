package com.agiledon.scala.wheel

package object core {
  implicit def SqlConverter(sqlStatement: String):Sql = new Sql(sqlStatement)
}

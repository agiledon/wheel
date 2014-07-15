package com.agiledon.scala.wheel

import java.sql.Connection

trait DataSource {
  def getConnection(): Connection
}

object DataSourcePrefs {
}

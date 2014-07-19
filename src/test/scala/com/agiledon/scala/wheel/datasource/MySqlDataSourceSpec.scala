package com.agiledon.scala.wheel.datasource

import org.scalatest.{ShouldMatchers, FlatSpec}

class MySqlDataSourceSpec extends FlatSpec with ShouldMatchers {
  it should "load properties for mysql data source" in {
    val source = new MySqlDataSource
    source.url  should be("jdbc:mysql://localhost:3306/wheel")
    source.driver should be("com.mysql.jdbc.Driver")
    source.username should be("root")
    source.password should be("root")
  }
}

package com.agiledon.scala.wheel.syntax

import org.scalatest.{ShouldMatchers, FlatSpec}

class SqlGeneratorSpec extends FlatSpec with ShouldMatchers {

  it should "generate select statement" in {
    val generators = Seq(selectPartGenerator, wherePartGenerator)
    val paras:Parameters = Map("columns" -> Columns("name", "age"),
                                "tablename" -> TableName("customer"),
                                "condition" -> Condition("name = 'zhangyi'"))

    val sql = SqlGenerator(paras).sql(generators: _*)
    sql.sqlStatement should be("select name, age from customer where name = 'zhangyi'")
  }

  it should "generate insert statement" in {
    val generators = Seq(insertPartGenerator)
    val entityMap:Parameters = Map("columns" -> Columns("name", "age"),
                                "tablename" -> TableName("customer"),
                                "condition" -> Condition("name = 'zhangyi'"))

    val sql = SqlGenerator(entityMap).sql(generators: _*)
    sql.sqlStatement should be("insert into customer")
  }

}

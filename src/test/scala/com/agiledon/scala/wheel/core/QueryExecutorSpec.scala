package com.agiledon.scala.wheel.core

import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import com.agiledon.scala.wheel.core.Implicits.sql
import com.agiledon.scala.wheel.IntSpec

class QueryExecutorSpec extends IntSpec {
  before {
    Sql("delete from customer").execute
    Sql("insert into customer values ('zhangyi', 'chengdu high tech zone', '13098981111')"
    , "insert into customer values ('bruce zhang', 'chongqing shapingba', '13098982222')"
    , "insert into customer values ('agiledon', 'beijing dongchen', '13098983333')"
    ).batchExecute
  }

  it should "query WrappedResultSet from customer using Sql object" in {
    val result = Sql("select * from customer").query
    assertQueryResult(result)
  }

  it should "query one record from customer directly" in {
    val result = "select * from customer".query
    assertQueryResult(result)
  }

  it should "query one record from customer async" in {
    val result = "select * from customer".asyncQuery
    result.onSuccess {
      case result => {
        assertQueryResult(result)
      }
    }

    Thread.sleep(50)
  }

  it should "generate view from query result for big data scenario" in {
    val result = "select * from customer".query
    val v = result.view
    val phones = v.filter(_.cell("name").get.toString.contains("zhang")).map(_.cell("phone").get).mkString("|")
    phones should be("13098981111|13098982222")
  }

  def assertQueryResult(result: Table) {
    result.head.cells.mkString("|") should be("zhangyi|chengdu high tech zone|13098981111")
    result.head.cell("name").getOrElse("not found") should be("zhangyi")

    result.last.cells.mkString("|") should be("agiledon|beijing dongchen|13098983333")
    result.last.cell("name").getOrElse("not found") should be("agiledon")

    result.first(_.cell("phone").get == "13098982222")
    result.filter(_.cell("name").get.toString.contains("zhang") ).length should be(2)
  }
}

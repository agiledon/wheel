package com.agiledon.scala.wheel.core

import org.scalatest.{ShouldMatchers, BeforeAndAfter, FlatSpec}
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

class QueryExecutorSpec extends FlatSpec with BeforeAndAfter with ShouldMatchers {
  before {
    Sql("delete from customer").execute
    Sql("insert into customer values ('zhangyi', 'chengdu high tech zone', '13098989999')").execute
  }

  it should "query WrappedResultSet from customer using Sql object" in {
    val result = Sql("select * from customer").query
    result.head.getOrElse("name", "zhangyi") should be("zhangyi")
  }

  it should "query one record from customer directly" in {
    val result = "select * from customer".query
    result.head.getOrElse("name", "zhangyi") should be("zhangyi")
  }

  it should "query one record from customer async" in {
    val result = "select * from customer".asyncQuery
    result.onSuccess {
      case result => {
        result.head.getOrElse("name", "zhangyi") should be("zhangyi")
      }
    }

    Thread.sleep(50)
  }
}

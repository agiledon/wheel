package com.agiledon.scala.wheel.core

import org.scalatest.{ShouldMatchers, BeforeAndAfter, FlatSpec}
import Executor.Converter
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

class QueryExecutorSpec extends FlatSpec with BeforeAndAfter with ShouldMatchers {
  before {
    Sql("delete from customer").execute
    Sql("insert into customer values ('zhangyi', 'chengdu high tech zone', '13098989999')").execute
  }

  it should "query one record from customer using Sql object" in {
    val result = Sql("select * from customer").query[List[List[String]]]
    result match {
      case Some(x) => {
        x.size should be(1)
        x.head.mkString("|") should be("zhangyi|chengdu high tech zone|13098989999")
      }
      case None =>
    }
  }

  it should "query WrappedResultSet from customer using Sql object" in {
    val result = Sql("select * from customer").query1
    result.head.getOrElse("name", "zhangyi") should be("zhangyi")
  }

  it should "query one record from customer directly" in {
    val result = "select * from customer".query[List[List[String]]]
    result match {
      case Some(x) => {
        x.size should be(1)
        x.head.mkString("|") should be("zhangyi|chengdu high tech zone|13098989999")
      }
      case None =>
    }
  }

  it should "query one record from customer async" in {
    val result = "select * from customer".asyncQuery[List[List[String]]]
    result.onSuccess {
      case result => {
        result.getOrElse(List()).size should be(1)
        result.getOrElse(List()).head.mkString("|") should be("zhangyi|chengdu high tech zone|13098989999")
      }
    }

    Thread.sleep(50)
  }
}

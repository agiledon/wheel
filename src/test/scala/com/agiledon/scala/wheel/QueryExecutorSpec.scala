package com.agiledon.scala.wheel

import org.scalatest.{ShouldMatchers, BeforeAndAfter, FlatSpec}
import Executor.Converter

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
}

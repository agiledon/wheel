package com.agiledon.scala.wheel

import org.scalatest.{BeforeAndAfter, FlatSpec}

class CommandExecutorSpec extends FlatSpec with BeforeAndAfter {
  before {
    Sql("delete from customer").execute
  }

  it should "insert one record to customer using Sql object" in {
    Sql("insert into customer values ('zhangyi', 'chengdu high tech zone', '13098989999')").execute
  }

  it should "insert one record to customer directly" in {
    "insert into customer values ('zhangyi', 'chengdu high tech zone', '13098989999')".execute
  }

}

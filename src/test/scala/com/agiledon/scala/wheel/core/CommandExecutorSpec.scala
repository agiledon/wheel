package com.agiledon.scala.wheel.core

import scala.concurrent.ExecutionContext.Implicits.global
import com.agiledon.scala.wheel.core.Implicits.sql
import com.agiledon.scala.wheel.IntSpec

class CommandExecutorSpec extends IntSpec {
  before {
    Sql("delete from customer").execute
  }

  it should "insert one record to customer using Sql object" in {
    Sql("insert into customer values ('zhangyi', 'chengdu high tech zone', '13098989999')").execute should be(true)
  }

  it should "insert one record to customer directly" in {
    "insert into customer values ('zhangyi', 'chengdu high tech zone', '13098989999')".execute should be(true)
  }

  it should "insert one record to customer async" in {
    val future = "insert into customer values ('zhangyi', 'chengdu high tech zone', '13098989999')".asyncExecute
    future.onSuccess {
      case result => result should be(true)
    }

    Thread.sleep(50)
  }

}

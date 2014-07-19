package com.agiledon.scala.wheel.core

import org.scalatest.{BeforeAndAfter, FlatSpec}

class TransactionScopeSpec extends FlatSpec with TransactionScope with BeforeAndAfter {
  before {
    Sql("delete from customer").execute
    Sql("insert into customer values ('zhangyi', 'chengdu high tech zone', '13098989999')").execute
  }

  it should "execute command in the transaction scope" in {
    using { conn =>
      Sql("delete from customer").execute(conn)
      Sql("insert into customer values ('agiledon', 'shenzhen guangdong province', '13098989999')").execute(conn)
      throw new Exception
    }
  }

}

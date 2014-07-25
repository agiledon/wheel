package com.agiledon.scala.wheel.core

import com.agiledon.scala.wheel.IntSpec

class TransactionScopeSpec extends IntSpec with TransactionScope {
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

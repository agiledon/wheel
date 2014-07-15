package com.agiledon.scala.wheel

import org.scalatest.FlatSpec

class TransactionScopeSpec extends FlatSpec with TransactionScope {

  it should "execute command in the transaction scope" in {
//    using { conn =>
//      println("begin")
//      Sql("delete * from customer").execute(conn)
//      Sql("insert customer values").execute(conn)
//      println("end")
//    }
  }

}

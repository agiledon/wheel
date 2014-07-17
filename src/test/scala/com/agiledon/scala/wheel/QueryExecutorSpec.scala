package com.agiledon.scala.wheel

import org.scalatest.{BeforeAndAfter, FlatSpec}
import java.sql.ResultSet

class QueryExecutorSpec extends FlatSpec with BeforeAndAfter {
  before {
    Sql("delete from customer").execute
    Sql("insert into customer values ('zhangyi', 'chengdu high tech zone', '13098989999')").execute
  }


  implicit val converter: ResultSet => List[List[String]] = rs => {
    var rows: List[List[String]] = List()
    val columnCount = rs.getMetaData.getColumnCount

    while (rs.next()) {
      val row = (1 to columnCount).map(rs.getString).toList
      rows = row :: rows
    }

    rows.reverse
  }

  it should "query one record from customer" in {
//    val result = Sql("select * from customer").query()
  }
}

package com.agiledon.scala.wheel

import java.sql.Connection

object Executor {
  trait QueryExecutor {
    this: Sql =>

    def query(implicit dataSource:DataSource): List[String] = {
      println(dataSource.getConnection())
      List(s"$sqlStatement")
    }

    def queryIn(implicit conn:Connection): List[String] = {
      List(s"$sqlStatement")
    }
  }

  trait CommandExecutor {
    this: Sql =>

    def execute(implicit dataSource:DataSource): String = {
      sqlStatement
    }

    def execute(conn:Connection): String = {
      sqlStatement
    }
  }

  trait BatchCommandExecutor {
    this: Sql with CommandExecutor =>

    def batchExecute(implicit dataSource:DataSource): String = {
      List("begin transaction", execute(dataSource), "commit").mkString("\n")
    }
  }
}



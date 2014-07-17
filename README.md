wheel
=====

Scala Wrapper for Java JDBC

More convinient to exeucte sql command in scala language


```scala
implicit val mysql = new MySqlDataSource

//execute query
Sql("select * from customer).query

//execute command
Sql("delete from customer).execute

//execute command in transaction
using { conn => 
  Sql("delete from customer").execute
  Sql("Insert into customer values(...)").execute
}

//support async execution
val f = Sql("select * from bigtable").queryAsync

f.onComplete {
  case Success(result) => println(result)
  case Failure(e) => e.printStackTrace
}
```

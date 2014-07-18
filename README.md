wheel
=====

Scala Wrapper for Java JDBC

More convinient to exeucte sql command in scala language


```scala
//execute query
Sql("select * from customer").query

//execute command
Sql("delete from customer").execute
```

Or use String directly
```scala
"select * from customer".query

"delete from customer".execute
```

You can execute command in the transaction scope:

```scala
//first, you need extend TransactionScope trait
class YourClient extends Transaction...

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

Currentyly, I just implement the data source to support MySql, you can config the data source in the application.conf file:
```
mysql {
    host      =     localhost
    port      =     3306
    db        =     wheel
    user      =     root
    password  =     root
    driver    =     com.mysql.jdbc.Driver
}
```

If you want to run tests, you need create the table "customer":
```
create table customer (name VARCHAR(20), address VARCHAR(100), phone VARCHAR(20))
```
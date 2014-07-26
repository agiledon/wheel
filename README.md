wheel
=====

Scala Wrapper for Java JDBC

More convinient to exeucte sql command in scala language

## Query Table
You can pass the sql statement to Sql object to query the data table, or use sql statement with String type directly:

```scala
//execute query
val result = Sql("select * from customer").query

import com.agiledon.scala.wheel.core.Implicits.sql

val result = "select * from customer".query
```

The return result is the Table object which is customize type. It exposes head, tail, last, take, drop, filter, find, map and more methods. For example:
``` scala
val result = "select * from customer".query

//first row
val firstRow: Row = result.head

val firstRowOption: Option[Row] = result.headOption

//first row matches predication
val foundedRow: Option[Row] = result.find(_.cell("phone").get == "13098982222")

//filter
val filtered: Table = result.filter(_.cell("name").get.toString.contains("zhang"))
```

Row means the each record of the table, and it expose two methods:
```scala
val eachRecord: List[Any] = result.head.cells

//cell method accept column name, return Option
val phone: String = result.head.cell("phone").get
```

Even, you can invoke map method to do object-relation mapping, for example:
``` scala
case class Customer(name: String, address: String, phone: String)

val result = "select * from customer".query
val customers = result.map(r => Customer(r.cell("name").get.toString,
                                         r.cell("address").get.toString,
                                         r.cell("phone").get.toString ))

val customer = customers.head
customer.name should be("zhangyi")
```

For querying big data from database, it provide view method:
``` scala
val result = "select * from customer".query
val v = result.view.map(r => Customer(r.cell("name").get.toString,
                                      r.cell("address").get.toString,
                                      r.cell("phone").get.toString ))
                   .filter(_.name.contains("zhang"))
                   .force
```

## Insert, Delete, Update Table

```scala
//execute command
Sql("delete from customer").execute

"select * from customer".query

"delete from customer".execute
```

You can execute multi statements in batch way:
```scala
Sql("insert into customer values ('zhangyi', 'chengdu high tech zone', '13098981111')",
    "insert into customer values ('bruce zhang', 'chongqing shapingba', '13098982222')",
    "insert into customer values ('agiledon', 'beijing dongchen', '13098983333')"
    ).batchExecute
```

You can execute command in the transaction scope. But at first, you must extend TransactionScope trait:

```scala
class YourClient extends Transaction...

//execute command in transaction
using { conn => 
  Sql("delete from customer").execute(conn)
  Sql("Insert into customer values(...)").execute(conn)
}
```

Notice: You must pass the conn(variable) to execute method explicitly.

## Async Execution

For long time execution, you can invoke async methods. They will return Future object, so that you can use callback:

``` scala
val f = Sql("select * from bigtable").asnycQuery

f.onSuccess {
  case result => println(result.head.cell("name"))
}
```

Also, you can invoke asyncExecute to execute command async.

## Configure of Data Source

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

## Sample DB

If you want to run tests, you need create the table "customer":
```
create table customer (name VARCHAR(20), address VARCHAR(100), phone VARCHAR(20))
```
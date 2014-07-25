package com.agiledon.scala.wheel

import org.scalatest._

trait UnitSpec extends FlatSpec with ShouldMatchers

trait IntSpec extends UnitSpec with BeforeAndAfter

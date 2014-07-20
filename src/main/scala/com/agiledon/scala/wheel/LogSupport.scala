package com.agiledon.scala.wheel

import org.slf4j.LoggerFactory

private[wheel] trait LogSupport {
  protected val log = LoggerFactory.getLogger(this.getClass)
}

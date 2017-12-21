package org.http4s.scalajsexample

import utest._

object TutorialTest extends TestSuite {

  def tests = Tests {
    'HelloWorld {
      assert("Hello World" == "Hello World")
    }
  }
}

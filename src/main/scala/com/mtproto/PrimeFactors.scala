package com.mtproto

import java.util

object PrimeFactors {
  //http://www.vogella.com/tutorials/JavaAlgorithmsPrimeFactorization/article.html
  def calculate(numbers: Long): util.List[Long] = {
    var n = numbers
    val factors = new util.ArrayList[Long]
    var i = 2
    while ( {
      i <= n / i
    }) {
      while ( {
        n % i == 0
      }) {
        factors.add(i)
        n /= i
      }

      {
        i += 1;
        i - 1
      }
    }
    if (n > 1) factors.add(n)
    factors
  }

  def main(args: Array[String]): Unit = {
    System.out.println("Primefactors of 44")
    import scala.collection.JavaConversions._
    for (integer <- calculate(44)) {
      System.out.println(integer)
    }
    System.out.println("Primefactors of 3")
    import scala.collection.JavaConversions._
    for (integer <- calculate(3)) {
      System.out.println(integer)
    }
    System.out.println("Primefactors of 32")
    import scala.collection.JavaConversions._
    for (integer <- calculate(32)) {
      System.out.println(integer)
    }
  }
}

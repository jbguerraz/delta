package org.apache.spark.sql.delta.expressions

import org.apache.spark.benchmark.{Benchmark, BenchmarkBase}
import org.apache.spark.sql.catalyst.expressions.Expression
import org.apache.spark.sql.catalyst.dsl.expressions._
import org.apache.spark.sql.catalyst.{CatalystTypeConverters, InternalRow}

/**
 * Benchmark to measure performance for interleave bits.
 * To run this benchmark:
 * {{{
 *   build/sbt "core/test:runMain org.apache.spark.sql.delta.expressions.InterleaveBitsBenchmark"
 * }}}
 */
object InterleaveBitsBenchmark extends BenchmarkBase {

  private val numRows = 1 * 1000 * 1000

  private def randomInt(numColumns: Int): Seq[Array[Int]] = {
    (1 to numRows).map { l =>
      val arr = new Array[Int](numColumns)
      (0 until numColumns).foreach(col => arr(col) = l)
      arr
    }
  }

  private def createExpression(numColumns: Int): Expression = {
    val inputs = (0 until numColumns).map { i =>
      $"c_$i".int.at(i)
    }
    InterleaveBits(inputs)
  }

  protected def create_row(values: Any*): InternalRow = {
    InternalRow.fromSeq(values.map(CatalystTypeConverters.convertToCatalyst))
  }

  override def runBenchmarkSuite(mainArgs: Array[String]): Unit = {
    val benchmark =
      new Benchmark(s"$numRows rows interleave bits benchmark", numRows, output = output)
    benchmark.addCase("1 int columns benchmark", 3) { _ =>
      val interleaveBits = createExpression(1)
      randomInt(1).foreach { input =>
        interleaveBits.eval(create_row(input: _*))
      }
    }

    benchmark.addCase("2 int columns benchmark", 3) { _ =>
      val interleaveBits = createExpression(2)
      randomInt(2).foreach { input =>
        interleaveBits.eval(create_row(input: _*))
      }
    }

    benchmark.addCase("3 int columns benchmark", 3) { _ =>
      val interleaveBits = createExpression(3)
      randomInt(3).foreach { input =>
        interleaveBits.eval(create_row(input: _*))
      }
    }

    benchmark.addCase("4 int columns benchmark", 3) { _ =>
      val interleaveBits = createExpression(4)
      randomInt(4).foreach { input =>
        interleaveBits.eval(create_row(input: _*))
      }
    }
    benchmark.run()
  }
}
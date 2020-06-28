package com.ga

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

/**
 * @author zelei.fan
 * @date 2019/12/3 9:38
 * @description
 */
object TestRdd {

  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf()
    val spark: SparkSession = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()
    val df: DataFrame = spark.read.parquet("hdfs://nist22:9000/test/parquet")
    val rdd = df.rdd
    rdd.map((row: Row) => Row(row.getAs[String]("country"), row.getAs[String]("time")))count()
    rdd.mapPartitions((rows: Iterator[Row]) => {
      val res = for (row <- rows) yield {Row(row.getAs[String]("country"), row.getAs[String]("time"))}
      res
    }).count()
  }

}

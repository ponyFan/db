package com.ga

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.storage.StorageLevel

/**
 * @author zelei.fan
 * @date 2019/11/25 16:05
 * @description
 *
 * spark-submit
 * --master yarn  //提交方式: local, spark://192.168.9.22:7077(该方式是standalone模式，需要先启动spark), yarn
 * --deploy-mode client
 * --name testjob //提交的job名称
 * --jars /root/fzl/spark-yarn-jar/spark-streaming-kafka-0-10_2.11-2.4.3.jar,/root/fzl/spark-yarn-jar/kafka-clients-0.10.2.0.jar //本地jar包，包含在driver和executor的环境变量中
 * --driver-memory 2018m 	//driver内存大小，一般1~2G即可
 * --driver-cores 2		//driver核数
 * --num-executors 8 		//总的executor数量，看资源多少而定
 * --executor-cores 1  	//每个executor核数，一般2~4核
 * --executor-memory 1024m  //每个executor的内存，一般2~4G
 * --conf spark.default.parallelism=24  		//设置每个stage的task数量，官网建议设置数量为num-executors*executor-cores的2~3倍
 * --conf spark.storage.memoryFraction=0.7  	//设置rdd持久化数据在executor内存中所占的比列，默认是0.6，超过的话就会写磁盘，可以适当调高以减少磁盘io
 * --conf spark.shuffle.memoryFraction=0.5		//设置shuffle后做聚合时能够使用executor的内存比例，默认0.2，超过的话会写磁盘，shuffle操作过多的话建议降低持久化的内存占比（即减少spark.storage.memoryFraction），而提高shuffle操作的内存占比
 * --conf spark.shuffle.file.buffer=64k		//设置缓冲区大小，默认32k，将数据写磁盘前，会先写入缓冲区，缓冲区写满后会溢写到磁盘，内存足够的情况下可以适当调高参数，从而减少磁盘io
 * --conf spark.reducer.maxSizeInFlight=100m	//设置shuffle read task的buffer缓冲大小，决定了每次能够拉取多少数据，默认48m
 * --conf spark.shuffle.io.maxRetries=30		//设置shuffle read task从shuffle write task所在节点拉取数据重试次数，默认3，可能存在网络原因或者GC导致拉取失败，超过次数则作业执行失败，可尽量调大
 * --conf spark.shuffle.io.retryWait=60s		//设置每次重试拉取数据等待的时间间隔，默认5s，可以适当调大
 * --class com.test.Test /root/fzl/test-spark-1.0-SNAPSHOT.jar test003
 */
object TestSpark {

  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf()
    val spark: SparkSession = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()
    val df: DataFrame = spark.read.parquet("hdfs://nist22:9000/test/parquet")
    /* parquet文件表结构如下
    +-----+-------------+----------------------------+--------+----------+
    |   id|         name|                     address| country|      time|
    +-----+-------------+----------------------------+--------+----------+
    |    4|徐, 陆 and 姜|     Apt. 555 段中心75828...|  河南省|1574824725|
    |    1|程, 雷 and 赖|     Suite 747 沈栋7844号...|  陕西省|1574824725|
    +-----+-------------+----------------------------+--------+----------+
    */
    df.show()

    /*********************transformation算子***************************/
    val rdd = df.rdd
    rdd.cache()
    /*原始结构：Array([4,徐, 陆 and 姜,Apt. 555 段中心75828号, 成徽市, 吉 054544,河南省,1574824725], [1,程, 雷 and 赖,Suite 747 沈栋7844号, 吉林市, 宁 998946,陕西省,1574824725]]*/
    rdd.collect()
    /*map，对每个元素做转换，返回的可以时原来结构，也可以是新的结构*/
    /*转换后的结构：Array([河南省,1574824725], [陕西省,1574824725]]*/
    val mapRdd = rdd.map(row => {Row(row.getAs[String]("country"), row.getAs[String]("time"))}).collect()

    /*filter，过滤算子，过滤出 country==江苏省 的记录*/
    /*Array([26,方, 邓 and 蔡,Apt. 739 陈中心34号, 南都市, 辽 920724,江苏省,1574824727], [679,孙, 丁 and 龚,Apt. 713 范路66号, 衡阳市, 辽 783168,江苏省,1574825091]....]*/
    val filterRdd = rdd.filter(_.getAs("country")=="江苏省").collect()

    /*mapPartitions，作用和map一样，但是底层实现上不同，性能比map高，因为mapPartitions每次取到的是一个partition的数据集合，而map则是取的每条记录*/
    /*转换后的结构：Array([河南省,1574824725], [陕西省,1574824725]]*/
    rdd.mapPartitions((rows: Iterator[Row]) => {
      /*yield作用类似缓存，将遍历的结果存放到一个集合中*/
      val res = for (row <- rows) yield {Row(row.getAs[String]("country"), row.getAs[String]("time"))}
      res
    }).collect()

    /*sample，随机采样，参数：是否放回，采样比列，随机数生成器的种子*/
    rdd.sample(false, 0.5, 1).collect()

    /*union，两个rdd的并集，不去重*/
    val unionRdd: Array[Row] = mapRdd.union(filterRdd)

    /*distinct，去重算子，有shuffule操作，有参数的是设置partition数*/
    rdd.distinct()
    rdd.distinct(10)

    /*聚合类算子，要聚合的话需要先将数据转化成<k,v>的格式，如下*/
    /*Array((1574824725,4), (1574824725,1), (1574824725,9), (1574824725,0), (1574824725,6), (1574824725,33), (1574824725,92062)]*/
    val aggRdd: RDD[(String, String)] = rdd.map((row: Row) => (row.getAs[String]("time"), row.getAs[String]("id")))
    aggRdd.cache()

    /*groupByKey，按照key做聚合操作，主要用于分组，同一个key的数据放在一起，形成集合*/
    /*Array((1574825152,CompactBuffer(162, 1208)),(1574824793,CompactBuffer(27, 18, 299, 78, 76243))*/
    val groupRdd: RDD[(String, Iterable[String])] = aggRdd.groupByKey(10)

    /*reduceByKey，根据key聚合，并且将value做操作（加减乘除之类的），与groupbykey的结果类似，但是结果时聚合后的*/
    /*Array((1574825081,1458108), (1574825318,1046769), (1574824919,1265532), (1574825438,1418003))*/
    val reduceRdd: Array[(String, String)] = aggRdd.reduceByKey((str: String, str1: String) => (str.toLong + str1.toLong).toString).collect()
    df.rdd.keyBy(_.getAs[String]("country")).countByKey().map((tuple: (String, Long)) => tuple._2)
    /*sortByKey，按key进行排序*/
    val sortRdd: RDD[(String, String)] = aggRdd.sortByKey(true)

    /*join，两个<k,v>和<k,w>类型的数据，通过join后返回(k,(v,w))*/
    val joinRdd: RDD[(String, (String, String))] = aggRdd.join(aggRdd)
    val agg1: RDD[(String, Row)] = rdd.filter(_.getAs("country")=="江苏省").keyBy((row: Row) => row.getAs[String]("id"))
    val agg2: RDD[(String, Row)] = rdd.filter(_.getAs("country")=="安徽省").keyBy((row: Row) => row.getAs[String]("id"))
    agg1.join(agg2).collect()
    agg1.fullOuterJoin(agg2).collect()
    agg1.leftOuterJoin(agg2).collect()

    /*cogroup，将两个rdd根据相同key，把value合并，返回值<k,(v1,v2)>*/
    val cogroupRdd: RDD[(String, (Iterable[String], Iterable[String]))] = aggRdd.cogroup(sortRdd)

    /*cartesian，两个rdd的笛卡儿积*/
    val carRdd: RDD[((String, String), (String, String))] = sortRdd.cartesian(aggRdd)

    /*coaRdd，用于减少分区，一般用于过滤之后，过滤后可以将部分分区合并减少分区数*/
    val coaRdd: RDD[(String, String)] = aggRdd.coalesce(2)

    /*repartition，重分区*/
    val repartRdd: RDD[(String, String)] = aggRdd.repartition(10)



    /*********************action算子***************************/
    /*reduce算子，对元素进行操作*/
    val reduce: Long = rdd.map(_.getAs[Long]("id")).reduce((l: Long, l1: Long) => l+l1)

    /*collect，将executor端的数据收集到driver端，返回格式是array*/
    rdd.collect()

    /*count，计数，统计总数*/
    rdd.count()

    /*first，返回第一条数据*/
    rdd.first()

    /*takeSample，采样，采两条*/
    rdd.takeSample(true, 2, 1)

    /*返回前n个元素的数组*/
    rdd.take(10)

    /*保存成文件*/
    rdd.saveAsTextFile("d://test")

    /*循环每个分区*/
    rdd.foreachPartition((rows: Iterator[Row]) => print(rows))

    /*循环每条记录*/
    rdd.foreach((row: Row) => print(row))
  }
}

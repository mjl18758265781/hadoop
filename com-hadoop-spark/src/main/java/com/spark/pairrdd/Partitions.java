package com.spark.pairrdd;

import com.google.common.base.Optional;
import org.apache.spark.Partitioner;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 查看ＲＤＤ的分区
 */
public class Partitions
{
//    public static void main(String[] args)
//    {
//        init();
//    }

    private static void init()
    {
        SparkConf conf = new SparkConf();
        conf.setAppName("Partitions");
        conf.setMaster("local");

        JavaSparkContext jsc = new JavaSparkContext(conf);
        jsc.addJar("/home/titanic/soft/Work_Intellij/20151106/com-hadoop-spark/target/com-hadoop-spark-1.0-SNAPSHOT.jar");

        //最后的参数１０，并行的的分区，应该是分配１０个任务
        JavaRDD<String> dataRDD = jsc.textFile("file:///D:\\soft\\spark\\spark-1.5.1-bin-hadoop2.6\\spark-1.5.1-bin-hadoop2.6\\README.md", 10);

        //查看ＲＤＤ分区数
        int dataRDDPartitions = dataRDD.partitions().size();
        System.out.println(dataRDDPartitions);

        org.apache.spark.api.java.Optional<Partitioner> optional = dataRDD.partitioner();

        if(optional.isPresent())
        {
            System.out.println(optional.get().toString());
        }

        JavaPairRDD<String,Integer> toPairRDD = dataRDD.flatMapToPair(new PairFlatMapFunction<String, String, Integer>()
        {
            public Iterator<Tuple2<String, Integer>> call(String s) throws Exception
            {
                String[] tempStr = s.split(" ");
                List<Tuple2<String, Integer>> list =new  ArrayList<Tuple2<String, Integer>>();
                for(String str : tempStr)
                {
                    Tuple2 t = new Tuple2(str,1);
                    list.add(t);
                }
                return list.iterator();
            }
        });
        Map<String,Integer> map = toPairRDD.collectAsMap();
        System.out.println(map);

        //返回一组Partition对象
        int x = toPairRDD.partitions().size();
        System.out.println(x);

        //此操作是降低RDD的分区数量，即降低并行的任务数量.不是很确定
        JavaPairRDD<String,Integer> coalesceRDD = toPairRDD.coalesce(5);
        int c = coalesceRDD.partitions().size();
        System.out.println(c);

        //按照父分区的迭代器，逐个计算分区p的元素
//        toPairRDD.iterator(toPairRDD.partitions().get(0),jsc);

    }


}

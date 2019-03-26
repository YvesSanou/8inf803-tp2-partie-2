import java.util
import java.util.ArrayList

import org.apache.spark.{SparkConf, SparkContext}

class Program {

  def run(): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("combat");
    val sc = new SparkContext(conf);
    val array=init();
    val MonstersRDD=sc.parallelize(array);
    MonstersRDD.map(m=>sendMessages(m));
    val a=MonstersRDD.collect();
    var b=0;
  }

  def sendMessages(monstre: Monstre): Monstre =
  {
    monstre;
  }

  def init(): Array[Monstre] = {
    val m1 = new Monstre("m1");
    val m2 = new Monstre("m2");
    val m3 = new Monstre("m3");
    val result=Array(m1,m2,m3);
    result;
  }
}

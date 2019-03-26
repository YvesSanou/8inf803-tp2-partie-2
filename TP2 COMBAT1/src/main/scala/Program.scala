import java.awt.Point
import java.util
import java.util.ArrayList

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

class Program extends Serializable {

  def run(): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("combat");
    val sc = new SparkContext(conf);
    val monsters=init();
    val monstersRDD=sc.parallelize(monsters);
    val positions=monstersRDD.flatMap(m=>sendPositions(m,monsters)).groupByKey().map(findClosest).collect();
    val monstersRDD2=monstersRDD.map(m=>initializeGraph(m,positions));
    val a=monstersRDD2.collect();
    var b=0;
  }


  def initializeGraph(monstre:Monstre,positions:Array[(Monstre,Monstre)]):Monstre={
    for(i<- 0 until positions.size)
      {
        if(monstre.name.equals(positions(i)._1.name))
          {
            monstre.closestMonster=positions(i)._2;
          }
      }
    monstre;
  }

  def sendPositions(monstre: Monstre,monstres:List[Monstre]): List[(Monstre,Monstre)] =
  {
    var result=new ListBuffer[(Monstre,Monstre)];
    for(m<-monstres)
    {
      if(!m.name.equals(monstre.name)) {
        result.append((m, monstre));
      }
    }
    result.toList;
  }

  def findClosest(item:(Monstre,Iterable[Monstre])): (Monstre,Monstre) =
  {
    val monsters=item._2.toList;
    var closest=new Monstre("",null);
    var distance=1000000000.0;
    for(i<- 0 until monsters.size)
    {
      var temp=monsters(i);
      if(!item._1.name.equals(temp.name)) {
        var pA = new Point(item._1.position(0), item._1.position(1));
        var pB = new Point(temp.position(0), temp.position(1));
        if (distance > pA.distance(pB)) {
          closest = temp;
          distance=pA.distance(pB);
        }
      }
    }
    (item._1,closest);
  }

  def init(): List[Monstre] = {
    var pos=new Array[Integer](2);
    pos(0)=10;
    pos(1)=5;
    val m1 = new Monstre("m1",pos.clone());
    pos(0)=0;
    pos(1)=5;
    val m2 = new Monstre("m2",pos.clone());
    pos(0)=0;
    pos(1)=10;
    val m3 = new Monstre("m3",pos.clone());

    val result=Array(m1,m2,m3);
    result.toList;
  }
}

import java.awt.Point
import java.util
import java.util.ArrayList
import Armes.Attaque
import scala.collection.JavaConverters._

import Monstres._
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

class Program extends Serializable {

  def run(): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("combat")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
    var monsters = init()
    var boucle = true
    var i=1
    var monstersRDD = sc.parallelize(monsters);
    while (boucle) {
      Console.println("Tour "+i)
      var tab=sc.broadcast(monsters);
      monstersRDD=monstersRDD.map(m=>initializeGraph(m,tab))
      val messages = monstersRDD.flatMap(m => sendMessages(m)).groupByKey()
      if (messages.count() == 0)
        boucle = false
      else {
        val newMonsters = messages.map(mess => processMessages(mess,tab)).collect().toList
         monstersRDD = monstersRDD.flatMap(m=>updateGraph(m,newMonsters))
        Ui.Instance().printMonsters(null)
        monsters.foreach{u =>
          Ui.Instance().printMonsters(u);
        }
        i+=1
      }
    }
    Console.println("Combat terminé")
    Console.println("Vainqueur: "+monsters(0).name)

  }


  def initializeGraph(monstre: Monstre, tab: Broadcast[List[Monstre]]): Monstre = {
    var e=new ListBuffer[Monstre];
    var a=new ListBuffer[Monstre];
    for(m<-tab.value)
      {
        if(m.alive && monstre.id!=m.id)
          {
            if(monstre.faction==m.faction)
              a.append(m);
            else
              e.append(m);
          }
      }
    monstre.setAllies(a.toList);
    monstre.setEnemies(e.toList);
    monstre;
  }

  /*def sendPositions(monstre: Monstre, monstres: List[Monstre]): List[(Monstre, Monstre)] = {
    var result = new ListBuffer[(Monstre, Monstre)]
    for (m <- monstres) {
      if (!m.name.equals(monstre.name)) {
        result.append((m, monstre))
      }
    }
    var a=result.toList
    a
  }

  def findClosest(item: (Monstre, Iterable[Monstre])): (Monstre, (Monstre,Monstre)) = {
    val monsters = item._2.toList
    var closestFoe:Monstre=null;
    var closestAlly:Monstre=null;
    var distanceFoe = 1000000000.0;
    var distanceAlly = 1000000000.0;
    for (i <- 0 until monsters.size) {
      var temp = monsters(i);
      if (!item._1.name.equals(temp.name)) {
        /*var pA = new Point(item._1.position(0), item._1.position(1));
        var pB = new Point(temp.position(0), temp.position(1));
        var distance=pA.distance(pB);*/
        var distance=item._1.calculateDistance(temp);
        if(item._1.faction==temp.faction)
          {
            if (distanceAlly > distance) {
              closestAlly = temp;
              distanceAlly = distance;
            }
          }
        else
          {
            if (distanceFoe > distance) {
              closestFoe = temp;
              distanceFoe = distance;
            }
          }

      }
    }
    (item._1,( closestFoe,closestAlly));
  }*/

  def sendMessages(monstre: Monstre): List[(Int, (String, Monstre))] = {
    var res=new ListBuffer[(Int, (String, Monstre))];
    res.appendAll(monstre.intelligence());
    res.toList;
  }

  def processMessages(item: (Int, Iterable[(String, Monstre)]),tab:Broadcast[List[Monstre]]): Monstre = {
    var monstres=tab.value;
    var monstre_id = item._1;
    var messages = item._2.toList;
    var monstre: Monstre = null;
    for (i <- 0 until monstres.size) {
      if (monstre_id == monstres(i).id) {
        monstre = monstres(i);
      }
    }
    for (m <- messages) {
      val action = m._1;
      action match {
        case Messages.SEFAITATTAQUER => {
          if(monstre.alive) {
            monstre = m._2.MeleeAttack(monstre);
          }
        }
        case Messages.SEFAITATTAQUERADISTANCE => {
          if(monstre.alive) {
            monstre = m._2.RangeAttack(monstre);
          }
        }
        case Messages.REGENERATION => {
          monstre.asInstanceOf[Solar].regeneration();
        }
        case Messages.VOLER => {
          monstre.asInstanceOf[Solar].voler();
        }
        case Messages.SEDEPLACERVERS => {
          monstre.seDeplacerVers(m._2);
        }
      }
    }
    monstre;
  }

  def updateGraph(monstre: Monstre, newMonstres: List[Monstre]): List[Monstre] = {

    var res = new ListBuffer[Monstre];
      var item = monstre;
      for (m <- newMonstres) {
        if (m.name.equals(item.name))
          item = m;
      }
    if(item.alive)
      res.append(item);
    res.toList;
  }

  def init(): List[Monstre] = {
    var result=new ListBuffer[Monstre];
    var id_count=0;
    val solar = new Solar(id_count,1,"Solar");
    id_count+=1;
    var worgRiders=new ListBuffer[Monstre];
    for(i<-0 until 8)
      {
        var m=new WorgRider(id_count,2,"Worg Rider "+(i+1));
        worgRiders.append(m);
        id_count+=1;
      }
    var barbares=new ListBuffer[Monstre];
    for(i<-0 until 3)
    {
      var m=new Barbare(id_count,2,"Barbare "+(i+1));
      barbares.append(m);
      id_count+=1;
    }
    var warlord=new BrutalWarlord(id_count,2,"Brutal Warlord");
    result.append(solar);
    result.appendAll(worgRiders);
    result.appendAll(barbares);
    result.append(warlord);
    result.toList;
  }
}

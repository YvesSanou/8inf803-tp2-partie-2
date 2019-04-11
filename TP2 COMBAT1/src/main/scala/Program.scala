import java.awt.Point
import java.util
import java.util.ArrayList

import Armes.Attaque
import Monstres._
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
    while (boucle) {
      Console.println("Tour "+i)
      val monstersRDD = sc.parallelize(monsters)
      val tab=sc.broadcast(monsters);
      val messages = monstersRDD.flatMap(m => sendMessages(m)).groupByKey()
      if (messages.count() == 0)
        boucle = false
      else {
        val newMonsters = messages.map(mess => processMessages(mess,monsters)).collect().toList
        val monstersRDD2 = monstersRDD.flatMap(m=>updateGraph(m,newMonsters))
        monsters = monstersRDD2.collect().toList
        i+=1
        var a = 0
      }
    }
    Console.println("Combat terminé")
    Console.println("Vainqueur: "+monsters(0))

  }


  /*def initializeGraph(monstre: Monstre, positions: Array[(Monstre, (Monstre,Monstre))]): Monstre = {
    for (i <- 0 until positions.size) {
      if (monstre.name.equals(positions(i)._1.name)) {
        monstre.closestFoe = positions(i)._2._1
        monstre.closestAlly=positions(i)._2._2
      }
    }
    if(positions.size==0)
      {
        monstre.closestFoe=null
        monstre.closestAlly=null
      }
    monstre
  }

  def sendPositions(monstre: Monstre, monstres: List[Monstre]): List[(Monstre, Monstre)] = {
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

  def processMessages(item: (Int, Iterable[(String, Monstre)]),monstres:List[Monstre]): Monstre = {
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
          monstre = m._2.MeleeAttack(monstre);
        }
        case Messages.SEFAITATTAQUERADISTANCE => {
          monstre = m._2.RangeAttack(monstre);
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
    val solar = new Solar(id_count);
    id_count+=1;
    var worgRiders=new ListBuffer[Monstre];
    for(i<-0 until 8)
      {
        var m=new WorgRider(id_count);
        m.setEnemy(solar);
        worgRiders.append(m);
        id_count+=1;
      }
    var barbares=new ListBuffer[Monstre];
    for(i<-0 until 3)
    {
      var m=new Barbare(id_count);
      m.setEnemy(solar);
      barbares.append(m);
      id_count+=1;
    }
    var warlord=new BrutalWarlord(id_count);

    var enemies=new ListBuffer[Monstre];
    enemies.append(warlord);
    enemies.appendAll(worgRiders);
    enemies.appendAll(barbares);
    solar.setEnemies(enemies.toList);

    result.append(solar);
    result.appendAll(worgRiders);
    result.appendAll(barbares);
    result.append(warlord);
    result.toList;
  }
}

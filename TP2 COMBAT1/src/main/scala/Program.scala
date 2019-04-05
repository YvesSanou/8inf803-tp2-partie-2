import java.awt.Point
import java.util
import java.util.ArrayList

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
      val positions = monstersRDD.flatMap(m => sendPositions(m, monsters)).groupByKey().map(findClosest).collect()
      val monstersRDD2 = monstersRDD.map(m => initializeGraph(m, positions))
      var a=monstersRDD2.collect()
      val messages = monstersRDD2.flatMap(m => sendMessages(m)).groupByKey()
      if (messages.count() == 0)
        boucle = false
      else {
        val newMonsters = messages.map(mess => processMessages(mess)).collect()
        val monstersRDD3 = monstersRDD2.flatMap((m => updateGraph(m, newMonsters.toList)))
        monsters = monstersRDD3.collect().toList
        i+=1
        var a = 0
      }

    }

    Console.println("Combat terminé")
    Console.println("Vainqueur: "+monsters(0).name)

    var b = 0
  }


  def initializeGraph(monstre: Monstre, positions: Array[(Monstre, (Monstre,Monstre))]): Monstre = {
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
        var pA = new Point(item._1.position(0), item._1.position(1));
        var pB = new Point(temp.position(0), temp.position(1));
        var distance=pA.distance(pB);
        if(item._1.faction==temp.faction)
          {
            if (distanceAlly > distance) {
              closestAlly = temp;
              distanceAlly = pA.distance(pB);
            }
          }
        else
          {
            if (distanceFoe > distance) {
              closestFoe = temp;
              distanceFoe = pA.distance(pB);
            }
          }

      }
    }
    (item._1,( closestFoe,closestAlly));
  }

  def sendMessages(monstre: Monstre): List[(Monstre, (String, String))] = {
    var res=new ListBuffer[(Monstre, (String, String))];
    var decision = monstre.intelligence();
    var action = decision._2;
    var emetteur = decision._1;
    action match {
      case "attack" => res.append((emetteur.closestFoe, ("attack", emetteur.name)))
      case "moveToAttack" => res.append((emetteur, ("move", emetteur.closestFoe.name)))
      case "moveToHeal" => res.append((emetteur, ("move", emetteur.closestAlly.name)))
      case "heal" => {res.append((emetteur.closestAlly, ("heal", emetteur.name)));
                      res.append((emetteur, ("potionUsed", "")))}
      case "noAction"=>{}
    }
    res.toList;
  }

  def processMessages(item: (Monstre, Iterable[(String, String)])): Monstre = {
    var monstre = item._1;
    var messages = item._2.toList;
    for (m <- messages) {
      var action = m._1;
      action match {
        case "attack" => {
          monstre.takeHit();
          Console.println(m._2 + " attaque " + monstre.name);
          if (!monstre.alive)
            {
            Console.println(monstre.name + " a ete tué");
            }
        }
        case "move" => {
          var target:Monstre=null;
          var text:String=null;
          if(monstre.closestFoe.name.equals(m._2)) {
            target = monstre.closestFoe;
            text=" se deplace vers l'enemi ";
          }
          else {
            target = monstre.closestAlly;
            text=" se deplace vers l'allié "
          }
          //monstre.moveTo(target.position);
          Console.println(monstre.name + text + m._2);
        }
        case "heal" => {
          monstre.beHealed();
          Console.println(monstre.name + " est soigné par " + m._2);
        }
        case "potionUsed" => {
          monstre.consumePotion();
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
    var pos = new Array[Integer](2);
    pos(0) = 10;
    pos(1) = 5;
    val m1 = new Monstre("m1",1,Array(new Attaque("greatSword", Array(35, 30, 25, 20), "3d6+18", 10)))
    pos(0) = 0;
    pos(1) = 5;
    val m2 = new Monstre("m2",1,Array(new Attaque("greatSword", Array(35, 30, 25, 20), "3d6+18", 10)))
    pos(0) = 0;
    pos(1) = 10;
    val m3 = new Monstre("m3",2,Array(new Attaque("greatSword", Array(35, 30, 25, 20), "3d6+18", 10)))
    pos(0) = 20;
    pos(1) = 10;
    val m4 = new Monstre("m4",2,Array(new Attaque("greatSword", Array(35, 30, 25, 20), "3d6+18", 10)))
    pos(0) = 20;
    pos(1) = 15;
    val m5 = new Monstre("m5",2,Array(new Attaque("greatSword", Array(35, 30, 25, 20), "3d6+18", 10)))
    val result = Array(m1, m2, m3,m4,m5);
    result.toList;
  }
}

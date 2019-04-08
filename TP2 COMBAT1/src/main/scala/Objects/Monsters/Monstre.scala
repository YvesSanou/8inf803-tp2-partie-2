import java.awt.Point
import java.lang.Integer

import scala.math.pow
import scala.util.Random

class Monstre(nameVal: String, fc: Integer, attacks: Array[Attaque]) extends Serializable {


  val name = nameVal
  var hp= 0
  var speed = 0
  val armor= 0;
  val faction = fc
  var alive = true;
  val seuil = 5;
  var hit = 0;
  var attackslist = attacks
  var healingPotions = 2;
  var position=generateRandomPosition();
  var closestFoe: Monstre = null;
  var closestAlly: Monstre = null;

  def attaque(monstre:  Monstre): Monstre = {
    System.out.println(name + " attaque")

    return monstre
  }

  def prendreDegats(): Unit = {
    System.out.println(name + " a recu des degats")
  }

  def generateRandomPosition(): Array[Double] = {

    val min=0;
    val max=100;
    var rd = new Random()
    var res = new Array[Double](2)
    res(0) = rd.nextDouble()*(max-min)+min;
    res(1) = rd.nextDouble()*(max-min)+min;
    res
  }

  /*override def toString():String=
  {
    "{name:"+name+",id:1}";
  }*/

  def intelligence(): (Monstre, String) = {
    var res: (Monstre, String) = null
      /*if (closestAlly != null && closestAlly.hit > 0 && healingPotions > 0) {
          var a = new Point(position(0), position(1));
          var b = new Point(closestFoe.position(0), closestFoe.position(1));
          var distance = a.distance(b);
          if (distance < rayonAction) {;
             return (this, heal());
          }
          else {
            return (this, move(// mettre le monstre vers lequel on se deplace));
          }
      }*/

      /*if (closestFoe != null) {
        var a = new Point(position(0), position(1))
        var b = new Point(closestFoe.position(0), closestFoe.position(1))
        var distance = a.distance(b)
        if (distance < rayonAction) {
          return (this, "attack",closestFoe)
        }
        else {
          return (this, "move",closestFoe)
        }
      }
    else
        return (this,noAction(),null)*/
    return(this,heal());
  }

  def attack(m: Monstre):  Monstre = {

    //choisir arme et attaquer
    return m
  }

  def move(p: Monstre): Monstre = {
    val d = calculateDistance(p)

    val cosTeta = (p.position(0) - position(0)) / d
    val sinTeta = (p.position(1) - position(1)) / d

    if (d - speed < 5) {
      val newSpeed = d - 5
      position(0) += (newSpeed * cosTeta);
      position(1) += (newSpeed * sinTeta);

    } else {
      position(0) += speed * cosTeta;
      position(1) += speed * sinTeta;
    }
    this
  }
  def calculateDistance(m: Monstre): Double = {
    return  Math.sqrt(Math.pow(m.position(0)-this.position(0),2)+Math.pow(m.position(1)-this.position(1),2));
  }

  def heal(): String = {
    return "heal"
  }

  def noAction(): String = {
    return "noAction"
  }



  def beHealed(): Unit = {
    hit -= 1
  }

  def consumePotion(): Unit = {
    healingPotions -= 1
  }
}
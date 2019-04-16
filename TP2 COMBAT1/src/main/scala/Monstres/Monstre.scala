package Monstres

import java.awt.Point
import java.lang.Integer

import Armes.{Attaque, GreatSword}

import scala.math.pow
import scala.util.Random

abstract class Monstre(idval: Int, nameval: String, hpval: Int, armorval: Int, walk_speedval: Int, meleeval: Attaque,rangeval:Attaque) extends Serializable {
  val id = idval;
  val name = nameval;
  var hp = hpval;
  var armor = armorval;
  val melee = meleeval;
  val range=rangeval;
  val walk_speed = walk_speedval;
  var alive = true;
  var position = generateRandomPosition();



  /*****************************MESSAGES *************************************************************************/
  def seDeplacerVersMessage(monstre: Monstre): (Int, (String, Monstre)) = {
    Console.println(name + "se deplace vers");
    return (this.id, (Messages.SEDEPLACERVERS, monstre));
  }

  def attaqueMeleeMessage(monstre: Monstre): (Int, (String, Monstre)) = {
    Console.println(this.name + " attaque  " + monstre.name);
    return (monstre.id, (Messages.SEFAITATTAQUER, this));
  }

  def attaqueDistanceMessage(monstre:Monstre):(Int,(String,Monstre))=
  {
    Console.println(this.name + " attaque a distance " + monstre.name);
    return (monstre.id,(Messages.SEFAITATTAQUERADISTANCE,this));
  }

  /*************************************************************************************************************/


  /****************************************ACTIONS*************************************************************/
  def prendreDegats(degats: Int): Unit = {
    hp -= degats;
    if (hp <= 0) {
      alive = false;
    }
  }

  def beHealed(soins: Int): Unit = {
    hp += soins;
  }

  def attackable(attaque: Attaque, monstre: Monstre): Boolean = {
    var distance = this.calculateDistance(monstre);
    if (distance <= attaque._range) {
      return true;
    }
    else {
      return false
    }
  }

  def intelligence(): List[(Int, (String, Monstre))];

  def MeleeAttack(monstre: Monstre): Monstre = {
    attack(this.melee,monstre);
  }

  def RangeAttack(monstre: Monstre): Monstre = {
    attack(this.range,monstre);
  }

  def attack(attaque: Attaque, monstre: Monstre): Monstre = {
    var degats = attaque.attacks(monstre);
    monstre.prendreDegats(degats);
    return monstre;
  }

  def seDeplacerVers(monstre:Monstre): Unit =
  {
    moveTowards(monstre,walk_speed);
  }

  /**********************************************************************************************************/


  /***************************************DEPLACEMENTS**************************************************/
  def generateRandomPosition(): Array[Double] = {

    val min = 0;
    val max = 100;
    var rd = new Random()
    var res = new Array[Double](2)
    res(0) = rd.nextDouble() * (max - min) + min;
    res(1) = rd.nextDouble() * (max - min) + min;
    res
  }

  def moveTowards(p: Monstre, speed: Int): Unit = {
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
  }

  def move(speed: Int): Unit = {
    /*val d = calculateDistance(p)

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
    this*/
  }

  def calculateDistance(m: Monstre): Double = {
    return Math.sqrt(Math.pow(m.position(0) - this.position(0), 2) + Math.pow(m.position(1) - this.position(1), 2));
  }

  def closest(monsters: List[Monstre]): Monstre = {
    var result = monsters(0);
    var distance = calculateDistance(result);
    for (i <- 1 until monsters.size) {
      var temp = monsters(i);
      var tempDistance = calculateDistance(temp);
      if (tempDistance < distance) {
        result = temp;
        distance = tempDistance;
      }
    }
    result;
  }
  /**************************************************************************************************************/
}
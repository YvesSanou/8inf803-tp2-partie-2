import java.awt.Point

import scala.util.Random

class Monstre(nameVal: String, pos: Array[Integer], fc: Integer) extends Serializable {


  val name = nameVal;
  val rayonAction = 5;
  val faction = fc
  var alive = true;
  val seuil = 5;
  var hit = 0;
  var healingPotions = 2;
  //var position=generateRandomPosition();
  var position = pos;
  var closestFoe: Monstre = null;
  var closestAlly: Monstre = null;

  def attaque(): Unit = {
    System.out.println(name + " attaque");
  }

  def prendreDegats(): Unit = {
    System.out.println(name + " a recu des degats");
  }

  def generateRandomPosition(): Array[Integer] = {

    var rd = new Random();
    var res = new Array[Integer](2);
    res(0) = rd.nextInt(100);
    res(1) = rd.nextInt(100);
    res;
  }

  /*override def toString():String=
  {
    "{name:"+name+",id:1}";
  }*/

  def intelligence(): (Monstre, String) = {
    var res: (Monstre, String) = null;
      if (closestAlly != null && closestAlly.hit > 0 && healingPotions > 0) {
          var a = new Point(position(0), position(1));
          var b = new Point(closestFoe.position(0), closestFoe.position(1));
          var distance = a.distance(b);
          if (distance < rayonAction) {;
             return (this, heal());
          }
          else {
            return (this, moveToHeal());
          }
      }
      else if (closestFoe != null) {
        var a = new Point(position(0), position(1));
        var b = new Point(closestFoe.position(0), closestFoe.position(1));
        var distance = a.distance(b);
        if (distance < rayonAction) {
          return (this, attack());
        }
        else {
          return (this, moveToAttack());
        }
      }
    else
        return (this,noAction())
  }

  def attack(): String = {
    return ("attack");
  }

  def moveToAttack(): String = {
    return "moveToAttack";
  }

  def moveToHeal(): String = {
    return "moveToHeal";
  }

  def heal(): String = {
    return "heal";
  }

  def noAction(): String = {
    return "noAction";
  }

  def takeHit(): Unit = {
    hit += 1;
    if (hit == seuil) {
      alive = false;
    }
  }

  def moveTowards(newPos: Array[Integer]): Unit = {
    position = newPos;
   /* var dx=(Double)(newPos(0)-position(0));
    var dy=(Double)(newPos(1)-position(1));
    var angle=Math.atan2(dx,dy);*/

  }

  def beHealed(): Unit = {
    hit -= 1;
  }

  def consumePotion(): Unit = {
    healingPotions -= 1;
  }
}
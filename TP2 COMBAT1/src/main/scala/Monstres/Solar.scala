package Monstres

import java.util.Random

import Armes.{Arc, Attaque, GreatSword}

import scala.collection.mutable.ListBuffer

class Solar(id: Int) extends Monstre(id, "Solar", 363, 44, 50, new GreatSword(), new Arc()) {

  val fly_speed = 150;
  val regen = 15;
  val actionsPerTour = 2;
  val surroundedCount = 3;
  val choice = 0.6
  var enemies: List[Monstre]=null


  def setEnemies(monstres:List[Monstre]):Unit={
    enemies=monstres;
  }


  def volerMessage(): (Int, (String, Monstre)) = {
    Console.println(name + "s'envole");
    return (this.id, (Messages.VOLER, null));
  }

  def regenerationMessage(): (Int, (String, Monstre)) = {
    Console.println(name + "se regenere");
    return (this.id, (Messages.REGENERATION, null));
  }

  override def intelligence(): List[(Int, (String, Monstre))] = {
    var result = new ListBuffer[(Int, (String, Monstre))];
    if (surrounded()) {
      result.append(volerMessage());
    }
    else {
      var target: Monstre = null;
      var monstres = meleeAttackPossible();
      if (monstres.size != 0) {
        target = chooseMonsterToAttack(monstres);
        result.append(attaqueMeleeMessage(target));
      }
      else {
        monstres = rangeAttackPossible();
        if (monstres.size != 0) {
          var rand = new Random().nextDouble();
          if (rand > choice) {
            target = chooseMonsterToAttack(monstres);
            result.append(attaqueDistanceMessage(target));
          }
          else {
            target = closest(this.enemies);
            result.append(seDeplacerVersMessage(target));
          }
        }
        else {
          target = closest(this.enemies);
          result.append(seDeplacerVersMessage(target));
        }

      }
    }
    return result.toList;
  }

  def surrounded(): Boolean = {
    var count = 0
    for (m <- enemies) {
      if (m.attackable(m.melee, this)) {
        count += 1;
      }
    }
    if (count > surroundedCount)
      return true;
    else
      return false;
  }

  def meleeAttackPossible(): List[Monstre] = {
    var result = new ListBuffer[Monstre];
    for (m <- enemies) {
      if (attackable(melee, m))
        result.append(m);
    }
    result.toList;
  }

  def rangeAttackPossible(): List[Monstre] = {
    var result = new ListBuffer[Monstre];
    for (m <- enemies) {
      if (attackable(range, m))
        result.append(m);
    }
    result.toList;
  }


  def chooseMonsterToAttack(monstres: List[Monstre]): Monstre = {
    var i = new Random().nextInt(monstres.size);
    return monstres(i);
  }

  def regeneration(): Unit = {
    hp += regen;
  }
  def voler(): Unit = {
    move(fly_speed);
  }

}



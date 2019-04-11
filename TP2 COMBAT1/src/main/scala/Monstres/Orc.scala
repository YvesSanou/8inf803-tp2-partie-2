package Monstres

import java.util.Random

import Armes.{Attaque, BattleAxe}

import scala.collection.mutable.ListBuffer

abstract class Orc(id: Int, nameval: String, hpval: Int, armorval: Int, walk_speedval: Int, meleeval: Attaque,rangeval:Attaque) extends Monstre(id, nameval, hpval, armorval, walk_speedval, meleeval,rangeval) {

  var allies: List[Monstre]=null;
  var enemy:Monstre=null;
  val choice = 0.6

  def setAllies(monstres:List[Monstre]):Unit={
    allies=monstres;
  }

  def setEnemy(monstre:Monstre):Unit={
    enemy=monstre;
  }

  override def intelligence(): List[(Int, (String, Monstre))] = {
    var result = new ListBuffer[(Int, (String, Monstre))];
    if (attackable(melee, enemy)) {
      result.append(attaqueMeleeMessage(enemy));
    }
    else if (attackable(range, enemy)) {
      var rand = new Random().nextDouble();
      if (rand > choice)
        result.append(attaqueDistanceMessage(enemy));
      else
        result.append(seDeplacerVersMessage(enemy));
    }
    else {
      result.append(seDeplacerVersMessage(enemy));
    }
    result.toList;
  }
}

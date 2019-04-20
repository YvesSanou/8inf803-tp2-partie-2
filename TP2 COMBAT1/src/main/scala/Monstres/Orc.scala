package Monstres

import java.util.Random

import Armes.{Attaque, BattleAxe}

import scala.collection.mutable.ListBuffer

abstract class Orc(id: Int, faction: Int, nameval: String, hpval: Int, armorval: Int, walk_speedval: Int, meleeval: Attaque, rangeval: Attaque) extends Monstre(id, faction, nameval, hpval, armorval, walk_speedval, meleeval, rangeval) {

  val choice = 0.6

  override def intelligence(): List[(Int, (String, Monstre))] = {
    var result = new ListBuffer[(Int, (String, Monstre))];
    if (enemies.size != 0) {
      var enemy = enemies(0);
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
    }
    result.toList;
  }
}

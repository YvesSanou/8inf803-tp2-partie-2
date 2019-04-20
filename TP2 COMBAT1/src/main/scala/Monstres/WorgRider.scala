package Monstres

import Armes.{BattleAxe, Shortbow}

class WorgRider(id: Int,faction:Int,name:String) extends Orc(id,faction,name,13,4,20,new BattleAxe(),new Shortbow()){

}

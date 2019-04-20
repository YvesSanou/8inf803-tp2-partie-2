package Monstres

import Armes.{DoubleAxe, MwkCompositeLongBow}

class Barbare(id: Int,faction:Int,name:String) extends Orc(id,faction,name,142,10,30,new DoubleAxe,new MwkCompositeLongBow()) {

}

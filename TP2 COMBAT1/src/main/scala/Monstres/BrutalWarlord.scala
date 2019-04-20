package Monstres

import Armes.{MwkThrowingAxe, ViciousFlail}

import scala.collection.mutable.ListBuffer

class BrutalWarlord (id: Int,faction:Int,name:String) extends Orc(id,faction,name,141,10,30,new ViciousFlail(),new MwkThrowingAxe()) {

}

import java.awt.Point

import scala.util.Random

class Monstre(nameVal:String,pos:Array[Integer]) extends Serializable
{


  val name=nameVal;
  //var position=generateRandomPosition();
  var position=pos;
  var closestMonster:Monstre=null;

  def attaque(): Unit =
  {
    System.out.println(name+" attaque");
  }

  def prendreDegats(): Unit =
  {
    System.out.println(name+" a recu des degats");
  }

   def generateRandomPosition():Array[Integer]={

     var rd=new Random();
     var res=new Array[Integer](2);
     res(0)=rd.nextInt(100);
     res(1)=rd.nextInt(100);
     res;
   }

  /*override def toString():String=
  {
    "{name:"+name+",id:1}";
  }*/
}
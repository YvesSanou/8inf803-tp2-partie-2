class Monstre(nameVal:String) extends Serializable
{
  val name=nameVal;

  def attaque(): Unit =
  {
    System.out.println(name+" attaque");
  }

  def prendreDegats(): Unit =
  {
    System.out.println(name+" a recu des degats");
  }



  /*override def toString():String=
  {
    "{name:"+name+",id:1}";
  }*/
}
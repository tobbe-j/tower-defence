package towers

import main._
import enemies._
import scala.collection.mutable.Buffer

class BasicTower(location: Location) extends Tower(location) {
  
  val cost = 50
  val damage = 100
  val speed = 20
  private var coolDown = speed
  val range = 2
  val imageID = "sprites/towers/towerDefense_tile249.png"
  
  
  def attack(enemies: Buffer[Enemy]): Option[Projectile] = {
    if (coolDown == 0) {
    if (target.isDefined) {
      coolDown = speed
      if (location.distanceTo(target.get.location) <= range) {
        target.foreach(_.takeDamage(damage))
        val targetLocation = target.get.location
        this.getTarget(enemies)
        Option(new Projectile(targetLocation))
      } else {
        this.getTarget(enemies)
        None
      }
    } else {
      this.getTarget(enemies)
      None
    }
  } else {
    coolDown -= 1
    this.getTarget(enemies)
    None
  }
  }
  
}
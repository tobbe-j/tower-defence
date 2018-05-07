package towers

import main._
import enemies._
import scala.collection.mutable.Buffer

class BombTower(location: Location) extends Tower(location) {
  
  val cost = 100
  val damage = 200
  val speed = 40
  private var coolDown = speed
  val range = 1
  val imageID = "sprites/towers/towerDefense_tile250.png"
  
  
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
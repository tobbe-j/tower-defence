package towers

import processing.core.PImage
import enemies._
import main._
import scala.collection.mutable.Buffer

abstract class Tower(val location: Location) {
  
  val cost: Int

  val damage: Int
  
  val speed: Double
  
  val range: Int
  
  val imageID: String

  var target: Option[Enemy] = None
  
  def getTarget(enemies: Buffer[Enemy]) = {
    if (!enemies.isEmpty) {
      //println(enemies.length)
      target = Option(enemies.map(x => location.distanceTo(x.location) )
                             .zip(enemies)
                             .minBy(_._1)
                             ._2)
    } else {
      //println("target None")
      target = None
    }
  }
  
  def attack(enemies: Buffer[Enemy]): Option[Projectile]
  
  
}
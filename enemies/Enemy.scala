package enemies

import processing.core.PImage
import main._
import map._

abstract class Enemy(var location: Location, delay: Int) {
  
  val speed: Int
  var reward: Int
  var health: Int
  var direction: Int = 0
  var coolDown = speed + delay

  
  val spriteID: String
  
  def takeDamage(amount: Int) = {
    health -= amount
  }
  
  def kill() = {
    health = 0
    reward = 0
  }
  
  def move(map: Map): Boolean
  
}
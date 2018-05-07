package enemies

import map._
import main._

class NormalEnemy(loc: Location, del: Int) extends Enemy(loc, del) {

  val speed = 30
  var reward = 10
  var health = 1000

  val spriteID = "sprites/enemies/towerDefense_tile245.png"

  def move(map: Map): Boolean = {
    if (health <= 0) {
      false
    } else {
      if (coolDown == 0) {
        coolDown = speed
        if (map.getTile(location.infront(direction)) == Path || 
            map.getTile(location.infront(direction)) == SpawnPoint) {
          location = location.infront(direction)
        } else if (map.getTile(location.infront((direction + 1) % 4)) == Path || 
                   map.getTile(location.infront((direction + 1) % 4)) == SpawnPoint) {
          direction = (direction + 1) % 4
          location = location.infront(direction)
        } else if (map.getTile(location.infront((4 + direction - 1) % 4)) == Path || 
                   map.getTile(location.infront((4 + direction - 1) % 4)) == SpawnPoint) {
          direction = (4 + direction - 1) % 4
          location = location.infront(direction)
        }
      } else {
        coolDown -= 1
      }
      true
    }
  }
}
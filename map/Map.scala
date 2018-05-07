package map

import towers._
import main._

class Map(mapFile: Array[Array[Char]]) {
  
  val heigth = mapFile.length
  val width = mapFile(0).length
  
  private val tiles: Array[Array[MapTile]] = mapFile.map( _.map { x => 
    x match {
      case '0' => new BuildableTile
      case '#' => Path
      case '@' => SpawnPoint
      case _ => UnbuildableTile
    }
  })

  def spawnPoint = {
    val a = for {
      y <- 0 until heigth
      x <- 0 until width
      if (tiles(y)(x) == SpawnPoint)
    } yield new Location(y, x)
    a.head
  }
  
  def target = {
    val a = for {
      y <- 0 until heigth
      x <- 0 until width
      if (tiles(y)(x) == SpawnPoint)
    } yield new Location(y, x)
    a.last
  }
  
  def getTile(cords: Location): MapTile = tiles(cords.y)(cords.x)
  
  def buildTower(tower: Tower, cords: Location) = {
    if (tiles(cords.y)(cords.x).isInstanceOf[BuildableTile]) {
      tiles(cords.y)(cords.x).asInstanceOf[BuildableTile].tower = Option(tower)
    }  
  }
  
  def removeTower(cords: Location) = {
    if(tiles(cords.y)(cords.x).isInstanceOf[BuildableTile] &&
        tiles(cords.y)(cords.x).asInstanceOf[BuildableTile].tower.isDefined) {
      tiles(cords.y)(cords.x).asInstanceOf[BuildableTile].tower = None
    }
  }
  
  
  
}
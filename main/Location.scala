package main

class Location(val y: Int, val x: Int) {
  
  def distanceTo(other: Location): Int = {
    math.sqrt(math.pow(this.y-other.y,2) + 
              math.pow(this.x-other.x,2)).toInt
  }
  
  def infront(dir: Int): Location = {
    dir match {
      case 0 => new Location(y+1,x)
      case 1 => new Location(y,x+1)
      case 2 => new Location(y-1,x)
      case 3 => new Location(y,x-1)
    }
  }
  
  override def toString: String = {
    "Location: " + x + " " + y
  }
  
}
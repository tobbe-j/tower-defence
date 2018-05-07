package map

import towers._

abstract class MapTile

class BuildableTile extends MapTile {
  var tower: Option[Tower] = None
}

object UnbuildableTile extends MapTile

object Path extends MapTile

object SpawnPoint extends MapTile
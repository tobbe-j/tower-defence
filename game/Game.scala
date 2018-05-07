package game

import main._
import map._
import enemies._
import towers._
import scala.collection.mutable.Buffer

class Game(difficulty: Difficulty, mapFile: Array[Array[Char]]) {

  val map: Map = new Map(mapFile)
  private var money: Int = difficulty.money
  private var lives: Int = difficulty.lives
  private var points: Int = 0
  private var wave = 1
  val enemies: Buffer[Enemy] = Buffer()
  val towers: Buffer[Tower] = Buffer()

  def getPoints = points
  def getWave = wave
  def getMoney = money
  def getLives = lives

  def generateWave() = {
    val spawn = map.spawnPoint
    for (i <- 0 until wave / 5) {
      println("Strong ENEMY!!!")
      enemies += new StrongEnemy(spawn, wave * difficulty.grade * 30 + 50)
    }
    for (i <- 0 until wave * difficulty.grade) {
      enemies += new NormalEnemy(spawn, i * 30 + 10)
    }
    wave += 1
  }

  def buyTower(tower: Tower, location: Location): Boolean = {
    if (map.getTile(location).isInstanceOf[BuildableTile] &&
      !map.getTile(location).asInstanceOf[BuildableTile].tower.isDefined &&
      money >= tower.cost) {
      money = money - tower.cost
      map.buildTower(tower, location)
      towers += tower
      true
    } else {
      false
    }
  }
  def sellTower(location: Location): Boolean = {
    if (map.getTile(location).isInstanceOf[BuildableTile] &&
      map.getTile(location).asInstanceOf[BuildableTile].tower.isDefined) {
      money = money + map.getTile(location).asInstanceOf[BuildableTile].tower.get.cost / 2
      towers -= map.getTile(location).asInstanceOf[BuildableTile].tower.get
      map.removeTower(location)
      true
    } else {
      false
    }
  }

  def loseLife() = {
    val target = map.target
    val finishedEnemies = enemies.filter { x =>
      x.location.x == target.x &&
        x.location.y == target.y
    }
    if (finishedEnemies.nonEmpty) {
      finishedEnemies.foreach(_.kill())
      lives -= finishedEnemies.length
    }
  }

  def removeKilled() = {
    val dead = enemies.filter(_.health <= 0)
    points += dead.map(_.reward).sum
    money += dead.map(_.reward).sum
    dead.foreach { x =>
      enemies -= x
    }
  }

  def chooseTower(nr: Int, loc: Location): Option[Tower] = {
    nr match {
      case 0 => Option(new BasicTower(loc))
      case 1 => Option(new BombTower(loc))
      case 2 => Option(new LongRangeTower(loc))
      case _ => None
    }
  }

}
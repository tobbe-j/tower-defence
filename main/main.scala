

package main

import game._
import map._
import enemies._
import towers._

import processing.core.PApplet
import processing.core.PImage
import java.io.IOException

class TowerDefence extends PApplet {
  val srcDirPath = System.getProperty("user.dir") + "/src/"

  //setup stuff
  val maps = ParseMap.getMaps(srcDirPath + "maps/")
  var towerSprites = Map[String, PImage]()
  var enemySprites = Map[String, PImage]()
  var projectileSprite = Array[PImage]()
  val mapFile = ParseMap.parse(maps(0))
  var difficulty: Difficulty = new Easy
  var current = new Game(difficulty, mapFile)

  //variables
  val mapMenuSpace = 700 / maps.length
  var selectedSquare = new Location(0, 0)
  var menuOpen = true
  var optionsOpen = false
  var waveComing = false

  def mapHeight = current.map.heigth
  def mapWidth = current.map.width

  override def setup() {
    frameRate(30)
    textSize(40)
    background(255)
    try {
      towerSprites = ParseMap.loadTowers(srcDirPath +
        "sprites/towers/",
        loadImage)
      enemySprites = ParseMap.loadEnemies(srcDirPath +
        "sprites/enemies/",
        loadImage)
      projectileSprite = Array(loadImage(srcDirPath + "sprites/bang-explosion.png"))
    } catch {
      case e: IOException => {
        fill(255, 0, 0)
        rect(0, 0, 1000, 700)
        fill(255, 255, 255)
        text("Error loading sprites, press R to reset gam and try again", 400, 350)
      }
    }
    menuOpen = true
    optionsOpen = false
    waveComing = false
    drawMenu()
    text("Welcome to...\n" +
      "Tower Defence!\n\n" +
      "Choose difficulty first\n" +
      "and then map to begin.\nGood Luck!", 260, 200)
  }

  override def settings() {
    size(1000, 700)
  }

  override def draw() {
    try {
      if (waveComing) {
        current.enemies.foreach { x =>
          drawTile(x.location)
          if (x.move(current.map)) {
            drawEnemy(x)
          }
        }
        current.loseLife()
        drawTile(current.map.target)
        if (!optionsOpen) drawGameMenu()
        current.removeKilled()
        current.towers.foreach { x =>
          x.attack(current.enemies).foreach(drawProjectile)
        }
        if (current.enemies.isEmpty) {
          drawGameBoard()
          drawNewWave()
        }
      }
      if (current.getLives <= 0 && !menuOpen) drawLoss()
    } catch {
      case _: Throwable => {
        fill(255, 0, 0)
        rect(0, 0, 1000, 700)
        fill(255, 255, 255)
        text("Error attacking or moving, press r to reset game", 400, 350)
      }
    }
  }

  override def keyPressed() {
    if (key == 'a') {
      drawGameMenu()
    } else if (key == 'r') {
      this.setup()
    }
  }

  override def mouseClicked() {
    try {
      if (menuOpen) {
        mouseSelectMenu()
      } else {
        mouseSelectGame()
      }
    } catch {
      case _: Throwable => {
        fill(255, 0, 0)
        rect(0, 0, 1000, 700)
        fill(255, 255, 255)
        text("Error clicking around options or menu, press r to reset game", 400, 350)
      }
    }
  }

  private def mouseSelectGame() = {
    val (squareX, squareY) = (mouseX / 50 - 1, (mouseY - 100) / 50 - 1)
    //Open menu and quit game
    if (mouseX > 800 && mouseY < 100) {
      waveComing = false
      drawLoss()

      //start wave
    } else if (mouseX < 200 && mouseY < 100 && !menuOpen && !waveComing) {
      fill(200, 200, 200)
      rect(0, 0, 200, 100)
      fill(0, 0, 0)
      text("Incoming", 20, 65)
      drawMessage("Wave: " + current.getWave)
      current.generateWave()
      waveComing = true

      //Select square from map
    } else if (squareX <= mapWidth - 1 && squareY <= mapHeight - 1 && squareX * squareY >= 0) {
      selectedSquare = new Location(squareY, squareX)
      drawOptionMenu(squareX, squareY)

      //buy tower
    } else if (mouseX > 210 && mouseX < 700 && mouseY < 200) {
      val towerNr = (mouseX - 210) / 50
      val tower = current.chooseTower(towerNr, selectedSquare)
      if (tower.isDefined && current.buyTower(tower.get, selectedSquare)) {
        drawMessage("Successfully bought a new tower for " + tower.get.cost + "!")
        drawTower(current.towers.last)
        drawGameMenu()
        //Close options menu
      } else {
        if (!current.map.getTile(selectedSquare).isInstanceOf[BuildableTile]) {
          drawMessage("Please select a dark square to build a tower")
        } else {
          var cost = 0
          if (tower.isDefined) cost = tower.get.cost
          drawMessage("Do you have enough money? That tower costs " + cost)
        }
        drawGameMenu()
      }

      //Sell tower
    } else if (mouseX > 700 && mouseX < 800 && mouseY < 200) {
      if (current.sellTower(selectedSquare)) {
        drawMessage("selling")
        fill(80, 80, 80)
        rect((selectedSquare.x + 1) * 50, (selectedSquare.y + 1) * 50 + 100, 50, 50)
        drawGameMenu()
        //Close options menu
      } else {
        drawMessage("could not sell tower at " + selectedSquare.x + " " + selectedSquare.y)
        drawGameMenu()
      }
    }
  }

  private def mouseSelectMenu() = {
    if (mouseX > 780 && mouseX < 980 && mouseY > 225 && mouseY < 280) {
      difficulty = new Hard
      drawDif(difficulty)
    } else if (mouseX > 780 && mouseX < 980 && mouseY > 465 && mouseY < 510) {
      difficulty = new Easy
      drawDif(difficulty)
    } else if (mouseX < 250) {
      val chosenMapNr = math.min((mouseY) / mapMenuSpace, maps.length - 1)
      current = new Game(difficulty, ParseMap.parse(maps(chosenMapNr)))
      drawGameBoard()
      drawGameMenu()
      menuOpen = false
    }
  }

  private def drawGameBoard() = {
    fill(255, 255, 255)
    rect(0, 100, 1000, 700)
    fill(200, 200, 200)
    stroke(0)
    rect(0, 0, 200, 100)
    rect(800, 0, 200, 100)
    fill(0, 0, 0)
    text("Next", 50, 65)
    text("Menu", 850, 65)

    for (
      y <- 0 until mapHeight;
      x <- 0 until mapWidth
    ) {
      drawTile(new Location(y, x))
    }
    current.enemies.foreach(drawEnemy)
    current.towers.foreach(drawTower)
    fill(0, 0, 0)
  }

  private def drawOptionMenu(x: Int, y: Int) = {
    fill(200, 200, 200)
    rect(200, 0, 600, 100)
    fill(0, 0, 0)
    var counter = 0
    for (t <- towerSprites.values) {
      image(t, 210 + counter * 50, 30)
      counter += 1
    }
    text("Sell", 710, 50)
    optionsOpen = true
  }

  private def drawGameMenu() = {
    fill(200, 200, 200)
    rect(200, 0, 600, 100)
    fill(0, 0, 0)
    text("Money: ", 230, 50)
    text(current.getMoney, 250, 90)
    text("Points: ", 420, 50)
    text(current.getPoints, 450, 90)
    text("Lives: ", 630, 50)
    text(current.getLives, 650, 90)
    optionsOpen = false
  }

  private def drawMenu() = {
    fill(255, 255, 255)
    rect(0, 0, 250, 700)
    rect(750, 0, 250, 700)
    drawDif(difficulty)
    fill(0, 0, 0)
    textSize(30)
    text("Choose map:", 10, 50)
    text("Difficulty:", 760, 50)
    maps.foreach { x =>
      val mapName = x.reverse.takeWhile(_ != '/').reverse.dropRight(4)
      text(mapName, 60, (maps.indexOf(x) + 1) * mapMenuSpace - mapMenuSpace / 2)
    }
    textSize(40)
  }

  private def drawDif(dif: Difficulty) = {
    textSize(30)
    if (dif.dif == "Hard") {
      fill(255, 255, 255)
      rect(780, 465, 200, 55)
      fill(255, 0, 0)
      rect(780, 225, 200, 55)
    } else {
      fill(255, 255, 255)
      rect(780, 225, 200, 55)
      fill(0, 255, 0)
      rect(780, 465, 200, 55)
    }
    fill(0, 0, 0)
    text("Hard", 835, 265)
    text("Easy", 835, 505)
    textSize(40)
  }

  private def drawMessage(message: String) = {
    fill(255, 255, 255)
    rect(200, 100, 600, 50)
    fill(0, 0, 0)
    textSize(20)
    text(message, 210, 130)
    textSize(40)
  }

  private def drawNewWave() = {
    fill(200, 200, 200)
    rect(0, 0, 200, 100)
    fill(0, 0, 0)
    text("Next", 50, 65)
    waveComing = false
  }

  private def drawLoss() = {
    difficulty = new Easy
    fill(255, 255, 255)
    rect(0, 0, 1000, 800)
    fill(0, 0, 0)
    current.enemies.clear()
    drawMenu()
    menuOpen = true
    waveComing = false
    optionsOpen = false
    text("You Lost\nPlay Again?\nScore: " + current.getPoints, 420, 300)

  }

  private def drawTower(tower: Tower) = {
    image(towerSprites(srcDirPath + tower.imageID),
      (tower.location.x + 1) * 50,
      (tower.location.y + 1) * 50 + 100)
  }

  private def drawEnemy(enemy: Enemy) = {
    image(enemySprites(srcDirPath + enemy.spriteID),
      (enemy.location.x + 1) * 50,
      (enemy.location.y + 1) * 50 + 100)
  }

  private def drawProjectile(pro: Projectile) = {
    image(projectileSprite.head, (pro.target.x + 1) * 50, (pro.target.y + 1) * 50 + 100)
  }

  private def drawTile(square: Location) = {
    current.map.getTile(square) match {
      case SpawnPoint =>
        fill(200, 200, 200)
        rect((square.x + 1) * 50, (square.y + 1) * 50 + 100, 50, 50)
        fill(255, 0, 0)
        ellipse((square.x + 1) * 50 + 25, (square.y + 1) * 50 + 125, 50, 50)
      case Path =>
        fill(0, 255, 0)
        rect((square.x + 1) * 50, (square.y + 1) * 50 + 100, 50, 50)
      case UnbuildableTile =>
        fill(200, 200, 200)
        rect((square.x + 1) * 50, (square.y + 1) * 50 + 100, 50, 50)
      case _ =>
        fill(80, 80, 80)
        rect((square.x + 1) * 50, (square.y + 1) * 50 + 100, 50, 50)
    }
  }
}

object TowerDefence extends App {
  PApplet.main("main.TowerDefence")
}

package game

abstract class Difficulty {
  val dif: String
  val grade: Int
  val money: Int
  val lives: Int
}

case class Hard() extends Difficulty {
  val dif = "Hard"
  val grade = 5
  val money = 400
  val lives = 5
}

case class Easy() extends Difficulty {
  val dif = "Easy"
  val grade = 1
  val money = 150
  val lives = 20
}
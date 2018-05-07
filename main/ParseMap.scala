package main

import scala.io.Source
import scala.collection.mutable.Buffer
import java.io.FileNotFoundException
import java.io.IOException
import java.io.File
import processing.core.PImage

object ParseMap {
  def parse(dir: String): Array[Array[Char]] = {
    val res = Buffer[Array[Char]]()
    try {
      val bufferedSource = Source.fromFile(dir)
      for (line <- bufferedSource.getLines()) {
        res += line.toCharArray()
      }
      bufferedSource.close()
    } catch {
      case e: FileNotFoundException => println("file not found " + dir)
      case e: IOException => println("IO exception")
    }
    res.toArray
  }
  
  def getMaps(dir: String): Array[String] = {
    val d = new File(dir)
    if (d.exists() && d.isDirectory()) {
      d.listFiles().filter(_.isFile).map(_.getPath)
    } else {
      Array[String]()
    }
  }
  
  def loadTowers(dir: String, loadImage: String => PImage): Map[String, PImage] = {
    val d = new File(dir)
    val imagePaths = if (d.exists() && d.isDirectory()) {
      d.listFiles().filter(_.isFile).map(_.getPath)
    } else {
      Array[String]()
    }
    imagePaths.map(x => (x, loadImage(x))).toMap
  }
  
  def loadEnemies(dir: String, loadImage: String => PImage): Map[String, PImage] = {
    val d = new File(dir)
    val imagePaths = if (d.exists() && d.isDirectory()) {
      d.listFiles().filter(_.isFile).map(_.getPath)
    } else {
      Array[String]()
    }
    imagePaths.map(x => (x, loadImage(x))).toMap
  }
  
    
}
  
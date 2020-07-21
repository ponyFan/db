package com.ga

class Student {

  var id: Int

  def get(id: Int): Option[Student]
}

object User {
  def get(id: Int): Option[Student]
}
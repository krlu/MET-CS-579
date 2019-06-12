package org.bu.metcs579

import org.bu.metcs579.Parser.Entity

/*
  //[a/each] __________ [has][one/many] ___________ [[with][one/many] ___________ ]
  //[a/each] __________ [can/] [[ ___________ [one/many/a] ____________[with/for]/has][a/one/many/] ________
 */
object MainExperiment {

  def main(args: Array[String]): Unit = {
    test1()
  }

  private val handler: DBHandler = {
    val host = "localhost"
    val port = 5432
    val dbName = "postgres"
    new DBHandler(host, port, dbName)
  }

  def test1(): Unit = {
    val sentence =
      """a team has a name, location, and many players.
         a player has a first name, last name, and middle name.
         each player has many stints with many teams"""
    val tables = Parser.parseParagraph(sentence)
    tables.foreach(println)
    handler.createTables(tables)
  }

  def test2(): Unit ={
    val sentence = "each player has many stints with many teams"
    val tokens = sentence.split("has")
    val entity1Name = tokens(0).replace("each", "").trim
    val relationData = tokens(1).split("with").map(s => s.replace("many", "").trim).map(EnglishNoun.singularOf)
    val relationTableName = relationData(0)
    val entity2Name = relationData(1)
    val fields = List(
      "id" -> "serial not null primary key",
      "created_at" -> "timestamp not null",
      s"${entity1Name}_id" -> s"integer references $entity1Name(id)",
      s"${entity2Name}_id" -> s"integer references $entity2Name(id)"
    )
    val table = Entity(relationTableName, fields, List.empty)
    handler.createTable(table)
  }

}

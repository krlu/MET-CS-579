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
    val tableNames = List("stint", "team", "player")
    handler.deleteTables(tableNames)
    val sentence =
      """a team has a name, location.
         a player has a first name, last name, and middle name.
         each player has many stints with many teams.
         each stint has a start time and end time"""
    val tables = Parser.parseParagraph(sentence)
    val combinedTables = tables.map(_.name).toSet.map{ name: String =>
      val tablesWithName: Seq[Entity] = tables.filter(_.name == name)
      tablesWithName.foldLeft(Entity(name, List.empty, List.empty))((x: Entity, y: Entity) => x + y)
    }.toList
    combinedTables.foreach(println)
    handler.createTables(combinedTables)
  }
}
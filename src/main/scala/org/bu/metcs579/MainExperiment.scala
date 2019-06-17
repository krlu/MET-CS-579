package org.bu.metcs579

import org.bu.metcs579.Parser.{Entity, Relation}

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
      """a team has a name, location and many players.
         a player has a first name, last name, and middle name.
         each player has many stints with many teams.
         each stint has a start time and end time"""
    val tables = Parser.parseParagraph(sentence)
    val uniqueNames = tables.map(_.name).toSet
    val combinedTables: List[Entity] = uniqueNames.map{ name: String =>
      val tablesWithName: Seq[Entity] = tables.filter(e => e.name == name)
      tablesWithName.foldLeft(Entity(name, List.empty, List.empty))((x: Entity, y: Entity) => x + y)
    }.toList
    var finalTables: List[Entity] = List.empty[Entity]
    combinedTables.foreach{ table =>
      if(!finalTables.exists(e => table.fields.forall(f => e.fields.contains(f)))){
        finalTables = finalTables :+ table
      }
    }
    val finalTablesWithFilteredRelations = finalTables.map{ table: Entity =>
      val filteredRelations = table.relations.filter{ relation: Relation =>
        !finalTables.exists(e => relation.fields.forall(f => e.fields.contains(f)))
      }
      table.copy(relations = filteredRelations)
    }

    handler.createTables(finalTablesWithFilteredRelations)
  }
}
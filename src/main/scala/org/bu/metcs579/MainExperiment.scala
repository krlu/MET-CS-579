package org.bu.metcs579

object MainExperiment {
  def main(args: Array[String]): Unit = {
    val sentence = "a team has a name, location, and many players. a player has a first name, last name, and middle name"
    val tables = Parser.parseParagraph(sentence)
    val host = "localhost"
    val port = 5432
    val dbName = "postgres"
    val handler = new DBHandler(host, port, dbName)
    tables.filter(_.relations.isEmpty).foreach{ table =>
      handler.createTable(table.name, table.fields)
    }
    tables.filter(_.relations.nonEmpty).foreach{ table =>
      handler.createTable(table.name, table.fields)
      table.relations.foreach{relation =>
        handler.createTable(relation.tableName, relation.fields)
      }
    }
  }
}

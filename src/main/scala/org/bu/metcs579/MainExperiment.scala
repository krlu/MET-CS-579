package org.bu.metcs579

object MainExperiment {
  def main(args: Array[String]): Unit = {
    val sentence = "a Person has a first name, last name, and middle name"
    val (tableName, allFields) = Parser.parse(sentence)
    val host = "localhost"
    val port = 5432
    val dbName = "postgres"
    val handler = new DBHandler(host, port, dbName)
    handler.createTable(tableName, allFields)
  }
}

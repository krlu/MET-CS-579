package org.bu.metcs579
/*
  //[a/each] __________ [has][one/many] ___________ [[with][one/many] ___________ ]
  //[a/each] __________ [can/] [[ ___________ [one/many/a] ____________[with/for]/has][a/one/many/] ________
 */
object MainExperiment {

  def main(args: Array[String]): Unit = {

    val sentence1 = """a team has a name, location and many players.
         a player has a first name, last name, and middle name.
         each player has many stints with many teams.
         each stint has a start time and end time"""

    val sentence2 = "a groceryStore has many kinds of food. Each food can be of one type, produce, meat, or drink." +
        "Each food has an expiration, name, description, and price." +
        "Each drink has a volume." +
        "Each meat has protein_count and cooked attributes." +
        "Each produce has a vitamins_count attribute." +
        "A customer can purchase many food."

//    test(sentence1)
    test(sentence2)
  }

  private val handler: DBHandler = {
    val host = "localhost"
    val port = 5432
    val dbName = "postgres"
    new DBHandler(host, port, dbName)
  }

  def test(sentence: String): Unit = {
    val tables = Parser.parseParagraph(sentence)
    val (depEntities, indepEntities) = tables.partition(e => e.fields.exists(x => x._2.contains("references")))
    handler.createTables(indepEntities)
    handler.createTables(depEntities)
  }
}
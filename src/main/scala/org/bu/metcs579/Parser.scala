package org.bu.metcs579

object Parser {

  def parse(inputDescription: String): Entity = {
    val split1 = inputDescription.split("has")
    val tableName = split1(0).replace("a ", "").replace("the ", "").trim
    val split2 = split1(1).split(",")
    val parsedFields = split2.toList.filter(s => !s.contains("many")).map{ s =>
      val field = s.replace("a ", "").replace("the ", "").replace("and", "").trim.replace(" ", "_")
      field -> "varchar(64)"
    }
    val defaultFields = List("id" -> "serial not null primary key", "created_at" -> "timestamp not null")
    val allFields = defaultFields ++ parsedFields
    val relations = split2.toList.filter(s => s.contains("many")).map{ s =>
      val filteredWord = s.replace("many", "").replace("the ", "").replace("and", "").trim.replace(" ", "_")
      val finalWord = EnglishNoun.singularOf(filteredWord)
      val relationFields = defaultFields ++ List(tableName, finalWord).map{s => (s"${s}_id", s"integer references $s(id)")}
      Relation(tableName, finalWord, relationFields)
    }
    Entity(tableName, allFields, relations)
  }

  def parseParagraph(inputDescription: String): List[Entity] = {
    inputDescription.split("\\.").map { s =>
      parse(s)
    }.toList
  }

  case class Entity(name: String, fields: List[(String, String)], relations: List[Relation])
  case class Relation(e1Name: String, e2Name: String, fields: List[(String, String)]){
    def tableName = s"${e1Name}_${e2Name}_rel"
  }
}

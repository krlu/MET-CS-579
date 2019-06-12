package org.bu.metcs579

object Parser {

  def parse(sentence: String): Entity = {
    if(sentence.contains("has") && sentence.contains("with"))
      parseRelationshipEntity(sentence)
    else
      parseConcreteEntity(sentence)
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

  private def parseRelationshipEntity(sentence: String): Entity = {
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
    Entity(relationTableName, fields, List.empty)
  }

  private def parseConcreteEntity(sentence: String): Entity = {
    val split1 = sentence.split("has")
    val tableName = filterWords(split1(0)).trim
    val split2 = splitByWords(split1(1), ",", "and")
    val parsedFields = split2.toList.filter(s => !s.contains("many")).map{ s =>
      val field = filterWords(s).trim.replace(" ", "_")
      field -> "varchar(64)"
    }
    val defaultFields = List("id" -> "serial not null primary key", "created_at" -> "timestamp not null")
    val allFields = defaultFields ++ parsedFields
    val relations = split2.toList.filter(s => s.contains("many")).map{ s =>
      val filteredWord = filterWords(s).trim.replace(" ", "_")
      val finalWord = EnglishNoun.singularOf(filteredWord)
      val relationFields = defaultFields ++ List(tableName, finalWord).map{s => (s"${s}_id", s"integer references $s(id)")}
      Relation(tableName, finalWord, relationFields)
    }
    Entity(tableName, allFields, relations)
  }



  private def splitByWords(sentence: String, delimiters: String*): Array[String] = {
    var temp = sentence.split(delimiters.head)
    delimiters.drop(1).foreach{ delimiter =>
      temp = temp.flatMap(_.split(delimiter)).filter(s => s.trim != "")
    }
    temp
  }

  private def filterWords(inputString: String): String = {
    var stringToReturn = inputString
    wordsToFilterOut.foreach{ word =>
      stringToReturn = stringToReturn.replace(word, "")
    }
    stringToReturn
  }
  private val wordsToFilterOut = Array("a ", "each", "the", "and", "many")
//  private val hardEntityVerbs = Array("has", "contains", "belongs", "belongs to")
}

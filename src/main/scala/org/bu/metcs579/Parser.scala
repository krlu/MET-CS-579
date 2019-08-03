package org.bu.metcs579

import java.io.FileInputStream

import opennlp.tools.postag.{POSModel, POSTaggerME}

object Parser {

  private val modelIn = new FileInputStream("en-pos-perceptron.bin")
  private val model = new POSModel(modelIn)
  private val tagger = new POSTaggerME(model)

  def parse(sentence: String): List[Entity] = {
    if(sentence.contains(" has ") && sentence.contains("with"))
      List(parseRelationshipEntity(sentence))
    else if(sentence.contains("type"))
      parseTypeEntity(sentence)
    else
      List(parseConcreteEntity(sentence))
  }

  def parseParagraph(inputDescription: String): List[Entity] = {
    val tables: Seq[Entity] = inputDescription.split("\\.").map { s =>
      parse(s)
    }.toList.flatten

    val uniqueNames = tables.map(_.name).toSet

    // find and remove duplicate entities by name
    val combinedTables: List[Entity] = uniqueNames.map{ name: String =>
      val tablesWithName: Seq[Entity] = tables.filter(e => e.name == name)
      tablesWithName.foldLeft(Entity(name, List.empty, List.empty))((x: Entity, y: Entity) => x + y)
    }.toList

    // find and remove duplicate entities by fields
//    var finalTables: List[Entity] = List.empty[Entity]
//    combinedTables.foreach{ table =>
//      if(!finalTables.exists(e => table.fields.forall(f => e.fields.contains(f)))){
//        finalTables = finalTables :+ table
//      }
//    }
    // find and remove relations with same fields as pre-existing entity
    val finalTablesWithFilteredRelations = combinedTables.map{ table: Entity =>
      val filteredRelations = table.relations.filter{ relation: Relation =>
        !combinedTables.exists(e => relation.fields.forall(f => e.fields.contains(f)))
      }
      table.copy(relations = filteredRelations)
    }
    finalTablesWithFilteredRelations
  }

  case class Entity(name: String, fields: List[(String, String)], relations: List[Relation]){
    def + (other: Entity): Entity = {
      if(other.name == this.name){
        val combinedFields = (this.fields ++ other.fields).distinct
        val combinedRelations = (this.relations ++ other.relations).distinct
        Entity(name, combinedFields, combinedRelations)
      }
      else this
    }
  }
  case class Relation(e1Name: String, e2Name: String, fields: List[(String, String)]){
    def tableName = s"${e1Name}_${e2Name}_rel"
  }

  private def parseRelationshipEntity(sentence: String): Entity = {
    val tokens = sentence.split(" has ")
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

  private def parseTypeEntity(sentence: String): List[Entity] = {
    val verbs = findVerbs(sentence)
    val sentenceWithoutMainVerbs = filterWords(sentence, verbs)
    val tokens = filterWords(sentenceWithoutMainVerbs).split(",").map(_.trim)
    val types = tokens.drop(1)
    // find parent type
    val words = tokens(0).replace("type", "").split(" ")
    val results = tagger.tag(tokens(0).replace("type", "").split(" "))
    val parentType = (results zip words).find(_._1 == "NN") match {
      case Some(pair) => pair._2
      case None => throw new IllegalArgumentException(s"Need parent type defined for ${types.toList.mkString(",")}")
    }
    types.toList.map{ name =>
      val fields = List(
        "created_at" -> "timestamp not null",
        s"${parentType}_id" -> s"integer references food(id)"
      )
      Entity(name, fields, List())
    }
  }

  private def parseConcreteEntity(sentence: String): Entity = {
    val verbs = findVerbs(sentence)
    val sentenceWithoutMainVerbs = filterWords(sentence, verbs)
    val split1 =
      if(sentenceWithoutMainVerbs.contains(" has ")) sentenceWithoutMainVerbs.split(" has ")
      else if(sentenceWithoutMainVerbs.contains(" can ")) sentenceWithoutMainVerbs.split(" can ")
      else throw new IllegalArgumentException("need verbs has or can to create concrete entity")
    val tableName = filterWords(split1(0)).trim
//    println(split1.toList, split1.size)
    val split2 = splitByWords(split1(1), ",", "and")
    val parsedFields = split2.toList.filter(s => !s.contains("many")).map{ s =>
      val field = filterWords(s).trim.replace(" ", "_")
      field -> "varchar(64)"
    }
    println(split2.toList)
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

  private def findVerbs(sentence: String): List[String] = {
    val splitSentence = sentence.split(" ")
    val results = tagger.tag(splitSentence)
    (splitSentence zip results).toList.filter(r => r._2 == "VB" || r._2 == "VBN").map(_._1)
  }

  private def splitByWords(sentence: String, delimiters: String*): Array[String] = {
    var temp = sentence.split(delimiters.head)
    delimiters.drop(1).foreach{ delimiter =>
      temp = temp.flatMap(_.split(delimiter)).filter(s => s.trim != "")
    }
    temp
  }

  private def filterWords(inputString: String, filterWords: List[String] = wordsToFilterOut): String = {
    var stringToReturn = inputString
    filterWords.foreach{ word =>
      stringToReturn = stringToReturn.toLowerCase.replace(word, "")
    }
    stringToReturn
  }
  private val wordsToFilterOut = List("a ", "each ", "the ", "and ", "many ", " or ", " can ", " be ", "attribute")

}

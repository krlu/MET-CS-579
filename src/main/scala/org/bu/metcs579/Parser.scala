package org.bu.metcs579

object Parser{

  def parse(inputDescription: String): (String, List[(String, String)]) = {
    val sentence = "a Person has a first name, last name, and middle name"
    val split1 = sentence.split("has")
    val tableName = split1(0).replace("a ", "").replace("the ", "")
    val split2 = split1(1).split(",")
    val parsedFields = split2.toList.map{ s =>
      val field = s.replace("a ", "").replace("the ", "").replace("and", "").trim.replace(" ", "_")
      field -> "varchar(64)"
    }
    val allFields = List("id" -> "serial not null primary key", "created_at" -> "timestamp not null") ++ parsedFields
    (tableName, allFields)
  }
}

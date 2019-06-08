package org.bu.metcs579
import scalikejdbc._

object Parser{

  def main(args: Array[String]): Unit = {
    Class.forName("org.postgresql.Driver")
    val host = "localhost"
    val port = 5432
    val db = "postgres"
    val url = s"jdbc:postgresql://$host:$port/$db"
    ConnectionPool.singleton(url, "postgres", "12345")

    // ad-hoc session provider on the REPL
    implicit val session: AutoSession = AutoSession

    sql"""create table members (id serial not null primary key, name varchar(64), created_at timestamp not null)"""
      .execute().apply()
  }
  def parse(inputDescription: String): Unit ={

  }
}

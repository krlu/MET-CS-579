package org.bu.metcs579

import java.sql.{Connection, DriverManager, Statement}

class DBHandler(host: String, port: Int, dbName: String) {

  private val driverName = "org.postgresql.Driver"
  Class.forName(driverName)
  val url = s"jdbc:postgresql://$host:$port/$dbName"
  val c: Connection = DriverManager.getConnection(url, "postgres", "12345")

  def createTable(name: String, fields: Map[String, String]): Unit = {
    val stmt: Statement = c.createStatement
    val fieldsConstruction = fields.map{ case (k,v) => s"$k $v"}.mkString(",")
    val sql = s"create table $name ($fieldsConstruction)"
    stmt.executeUpdate(sql)
    stmt.close()
  }

  def deleteTable(name: String): Unit = {
    val stmt: Statement = c.createStatement
    val sql = s"drop table $name"
    stmt.executeUpdate(sql)
    stmt.close()
  }
}

object DBHandler{
  def main(args: Array[String]): Unit = {
    val host = "localhost"
    val port = 5432
    val dbName = "postgres"
    val handler = new DBHandler(host, port, dbName)

    val tableName = "members"
    val fields = Map(
      "id" -> "serial not null primary key",
      "first_name" -> "varchar(64)",
      "last_name" -> "varchar(64)",
      "created_at" -> "timestamp not null"
    )
    handler.createTable(tableName, fields)
//    handler.deleteTable(tableName)
  }
}

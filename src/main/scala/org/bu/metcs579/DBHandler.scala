package org.bu.metcs579

import java.sql.{Connection, DriverManager, Statement}
import java.io.File
import com.typesafe.config.{ Config, ConfigFactory }


class DBHandler(host: String, port: Int, dbName: String) {
  val config: Config = ConfigFactory.parseFile(new File("src/main/resources/db.conf"))
  val userName: String = config.getString("userName")
  val password: String = config.getString("password")

  private val driverName = "org.postgresql.Driver"
  Class.forName(driverName)
  val url = s"jdbc:postgresql://$host:$port/$dbName"
  val c: Connection = DriverManager.getConnection(url, userName, password)

  def createTable(name: String, fields: List[(String, String)]): Unit = {
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
package org.bu.metcs579

import java.io.File
import java.sql.{Connection, DriverManager, Statement}

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.log4j.{BasicConfigurator, Level, Logger}
import org.bu.metcs579.Parser.{Entity, Relation}


class DBHandler(host: String, port: Int, dbName: String, level: Level = Level.INFO) {

  BasicConfigurator.configure()
  val log: Logger = Logger.getLogger(getClass.getName)
  log.setLevel(level)

  val config: Config = ConfigFactory.parseFile(new File("src/main/resources/db.conf"))
  val userName: String = config.getString("userName")
  val password: String = config.getString("password")

  private val driverName = "org.postgresql.Driver"
  Class.forName(driverName)
  val url = s"jdbc:postgresql://$host:$port/$dbName"
  val c: Connection = DriverManager.getConnection(url, userName, password)

  def createTables(entities: List[Entity]): Unit = {
    entities.foreach(createTable)
    entities.flatMap(_.relations).foreach(createTable)
  }

  def createTable(entity: Entity): Unit = createTable(entity.name, entity.fields)
  def createTable(relation: Relation): Unit = createTable(relation.tableName, relation.fields)
  def createTable(name: String, fields: List[(String, String)]): Unit = {
    val stmt: Statement = c.createStatement
    val fieldsConstruction = fields.map{ case (k,v) => s"$k $v"}.mkString(",")
    val sql = s"create table $name ($fieldsConstruction)"
    stmt.executeUpdate(sql)
    log.info(s"created table $name")
    stmt.close()
  }

  def deleteTable(name: String): Unit = {
    val meta = c.getMetaData
    val res = meta.getTables(null, null, name, Array[String]("TABLE"))
    val tableExists = res.next()
    if(tableExists) {
      val stmt: Statement = c.createStatement
      val sql = s"drop table $name"
      stmt.executeUpdate(sql)
      stmt.close()
    }
    else log.warn(s"Tried to drop table '$name' but table does not exist")
  }

  def deleteTables(names: List[String]): Unit = names.foreach(deleteTable)
}
package org.bu.metcs579

import java.sql.{Connection, DriverManager, Statement}
import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.log4j.{BasicConfigurator, Level, Logger}
import org.bu.metcs579.Parser.Entity


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

  def createTables(entities: List[Entity]): Unit ={
    entities.foreach{ table =>
      createTable(table.name, table.fields)
    }
    entities.filter(_.relations.nonEmpty).foreach{ table =>
      table.relations.foreach{relation =>
        createTable(relation.tableName, relation.fields)
      }
    }
  }

  def createTable(entity: Entity): Unit = {
    createTable(entity.name, entity.fields)
  }

  def createTable(name: String, fields: List[(String, String)]): Unit = {
    val stmt: Statement = c.createStatement
    val fieldsConstruction = fields.map{ case (k,v) => s"$k $v"}.mkString(",")
    val sql = s"create table $name ($fieldsConstruction)"
    stmt.executeUpdate(sql)
    log.info(s"created table $name")
    stmt.close()
  }

  def deleteTable(name: String): Unit = {
    val stmt: Statement = c.createStatement
    val sql = s"drop table $name"
    stmt.executeUpdate(sql)
    stmt.close()
  }

  def deleteTables(names: List[String]): Unit = names.foreach(deleteTable)
}
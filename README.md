# MET-CS-579

## Requirements 
- Java 8
- Scala 2.12.8
- sbt 1.2.8 
- PostgreSQL 11.3  

## Setup 

Download the repository, navigate to the root directory and run:  
>  sbt clean compile 

This will build all java and scala code and download necessary dependencies\
After everything is built.create a file called `db.conf` in the `src/main/resources` directory and add the following: 
```
userName=[your postgreSQL user name]
password=[your postgreSQL password] 
``` 
This will enable the program to access your local database


## Run Experiment 

Navigate to `src/main/scala/org/bu/metcs579` and run the main method in `MainExperiment`

The default input to this main method is the following sentence: 

> "a Person has a first name, last name, and middle name"

If everything is running correctly, you should have successfully parsed the above sentence 
and created an empty `Person` table to your default `postgres` database. 
To check open the psql console on command line and type 

> \dt


| Schema  |      name       | type  |  owner   |
| ------- | --------------- | ----- | -------- |
| public  |     player      | table | postgres |
| public  |      team       | table | postgres |
| public  | player_team_rel | table | postgres |

> select * from player; 

| id  | created_at | first_name| last_name | middle_name |
| --- | ---------- | --------- | --------- | ----------- |

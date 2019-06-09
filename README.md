# MET-CS-579

## Requirements 
- Java 8+ 
- Scala 2.12.8
- PostgreSQL 11.3  

## Setup 

Download the repository, navigate to the root directory run:  
>  sbt clean compile 

This will build all java and scala code and download necessary dependencies\
After everything is built, create a file called `db.conf` in the `src/main/resources` directory and add the following: 
```
userName=[your postgreSQL user name]
password=[your postgreSQL password] 
``` 

## Run Experiment 

Navigate to `src/main/scala/org/bu/metcs579` and run the main method in `MainExperiment`

The default input to this main method is the following sentence: 

> "a Person has a first name, last name, and middle name"

If everything is running correctly, you should have successfully parsed the above sentence 
and created an empty `Person` table to your default `postgres` database. 
To check open the psql console on command line and type 

> \dt

And you should see:   

| Schema  |  name  | type |  owner   |
| ------- | ------ | ---- | -------- |
| public  | person | table| postgres |

> select * from person 

And you should see: 

| id  | created_at | first_name| last_name | middle_name |
| --- | ---------- | --------- | --------- | ----------- |

> \
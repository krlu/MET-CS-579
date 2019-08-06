-- scenario 1
CREATE TABLE Team(
    PRIMARY KEY (ID),
    Found_Date DATETIME,
    Location VARCHAR(255)
);

CREATE TABLE Player(
    PRIMARY KEY (ID),
    Height int,
    Weight int,
    Date_Of_Birth DATETIME,
);

CREATE TABLE Stint(
    PRIMARY KEY (ID),
    FOREIGN Key (Player_Id) REFERENCES Player(ID),
    FOREIGN Key (Team_Id) REFERENCES Team(ID),
    Start_Date DATETIME,
    End_Date DATETIME,
);


-- scenario 2
CREATE TABLE Food_Item(
    PRIMARY KEY (ID),
    Expiration_Date DATETIME,
    Name varchar(20),
    Price_Per_Unit FLOAT(5),
    Description VARCHAR(255),
    Is_Drink BOOLEAN,
    Is_Meat BOOLEAN,
    Is_Produce, BOOLEAN
);

CREATE TABLE Produce(
    FOREIGN Key (Food_Id) REFERENCES Food_Item(ID),
    Vitamin_Count INT
);

CREATE TABLE Drink(
    FOREIGN Key (Food_Id) REFERENCES Food_Item(ID),
    Volume Int
);

CREATE TABLE Meat(
    FOREIGN Key (Food_Id) REFERENCES Food_Item(ID),
    Protein_Count Int
    Cooked BOOLEAN
);
CREATE TABLE Customer(
    PRIMARY KEY (ID),
    First_Name VARCHAR(255),
    Last_Name VARCHAR(255)
);

CREATE TABLE Purchase(
    PRIMARY KEY (ID),
    FOREIGN Key (Customer_Id) REFERENCES Customer(ID),
    FOREIGN Key (Food_Id) REFERENCES Food_Item(ID),
    Purchase_Date DATETIME
);
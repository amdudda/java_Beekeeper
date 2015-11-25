package com.amdudda;

import java.sql.*;

/**
 * Created by amdudda on 11/24/15.
 */
public class Database {
    // this class creates the Beekeeper database, creates the database tables, and
    // populates the database with some data.

    // adapted from MovieRatings code presented in class
    private static String DB_CONNECTION_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "Beekeeper";
    private static final String USER = "amdudda";
    private static final String PASS = "tanuki";

    public final static String BEEHIVE_TABLE_NAME = "Beehive";
    public final static String PK_COLUMN = "id";                   //Primary key column. Each movie will have a unique ID.
    //A primary key is needed to allow updates to the database on modifications to ResultSet
    public final static String LOCATION_COLUMN = "hive_location";

    public final static String HONEY_TABLE_NAME = "HoneyData";
    public final static String DATE_COLLECTED_COLUMN = "date_collected";
    public final static String WEIGHT_COLUMN = "weight";
    public final static String BEEHIVE_FK_COLUMN = "beehive_id";  // foreign key linking hives with their locations

    private static Connection conn = Main.conn;
    private static Statement statement = Main.statement;
    private static ResultSet rs = Main.rs;

    // also some static values to refer to hive IDs since we're only working with 4 hives:
    protected final static int NORTHFIELD = 1;
    protected final static int SOUTHMEADOW = 2;
    protected final static int EASTLEA = 3;
    protected final static int BOGGYMARSH = 4;

    // no constructor; this is really just a code repository for database creation.

    // A method to set up the database for the rest of the project
    public static void createDatabase() {
        createSchema();
        createTables();
        populateData();
    }


    // A method to create the schema
    private static void createSchema() {
        try {
            conn = DriverManager.getConnection(DB_CONNECTION_URL, USER, PASS);
            statement = conn.createStatement();
            String createDB = "CREATE SCHEMA " + DB_NAME;
            statement.executeUpdate(createDB);
            System.out.println("Created database " + DB_NAME);
            statement.close();
            conn.close();
        } catch (SQLException sqle) {
            if (sqle.getSQLState().startsWith("HY")) {
                // database already exists
                System.out.printf("Database \"%s\" already exists.\n", DB_NAME);
            } else {
                System.out.println("Unable to create database " + DB_NAME);
                System.out.println("Sql State code = " + sqle.getSQLState());
                System.out.println(sqle);
            }
        }
    }

    // A method to create the database tables
    private static void createTables() {

        // set up connection
        openConnStatement();

        // create beehive table
        String sqlToRun = "CREATE TABLE " + BEEHIVE_TABLE_NAME + "(" +
                PK_COLUMN + " INT NOT NULL AUTO_INCREMENT, " +
                LOCATION_COLUMN + " VARCHAR(50)," +
                "PRIMARY KEY (" + PK_COLUMN + " ))";
        createTable(sqlToRun,BEEHIVE_TABLE_NAME);


        // create honey table
        sqlToRun = "CREATE TABLE " + HONEY_TABLE_NAME + "(" +
                PK_COLUMN + " INT NOT NULL AUTO_INCREMENT, " +
                DATE_COLLECTED_COLUMN + " DATE, " +
                WEIGHT_COLUMN + " DECIMAL(5,2), " +
                BEEHIVE_FK_COLUMN + " INT, " +
                "PRIMARY KEY (" + PK_COLUMN + "), " +
                "FOREIGN KEY (" + BEEHIVE_FK_COLUMN + ") REFERENCES " +
                BEEHIVE_TABLE_NAME + "(" + PK_COLUMN + "))";
        createTable(sqlToRun, HONEY_TABLE_NAME);


        // try closing the connections
        String flag = "statement or connection";
        try {
            statement.close();
            flag = "statement";
            conn.close();
            flag = "connection";
        } catch (SQLException sqle) {
            System.out.println("Unable to close " + flag);
            System.out.println(sqle);
        }
    }

    // create a table based on a string and a table name
    private static void createTable(String sql, String table_name) {
        try {
            statement.executeUpdate(sql);
            System.out.println("Created table " + table_name);
        } catch (SQLException sqle) {
            if (sqle.getSQLState().startsWith("42")) {
                System.out.printf("Table \"%s\" already exists.\n", table_name);
            } else {
                System.out.println("Unable to create table " + table_name);
                System.out.println("Sql State code = " + sqle.getSQLState());
                System.out.println(sqle);
            }
        }
    }

    // A method to populate the database with data
    private static void populateData() {
        // create some records for the Beehive table
        openConnStatement();

        // generate beehive data
        createBeehiveData();

        // generate some honey data
        createHoneyData();

        // try closing the connections
        String flag = "statement or connection";
        try {
            statement.close();
            flag = "statement";
            conn.close();
            flag = "connection";
        } catch (SQLException sqle) {
            System.out.println("Unable to close " + flag);
            System.out.println(sqle);
        }
    }

    private static void createBeehiveData() {
        // inserts some beehive data into database.
        try {
            String sqlToRun = "INSERT INTO " + BEEHIVE_TABLE_NAME + " VALUES " +
                    "(1,\"North Field\"), (2,\"South Meadow\"), (3,\"East Lea\"), (4,\"Hopelessly Boggy Marsh\")";
            statement.executeUpdate(sqlToRun);
        } catch (SQLException sqle) {
            System.out.println("Unable to insert beehive records.\n" + sqle);
        }
    }

    private static void createHoneyData() {
        // generates some honey data to populate the database
        // nb: data columns after key are date, weight, and beehive number.
        addHoneyData("2014-08-01",25.74,NORTHFIELD);
        addHoneyData("2014-10-01",37.6,NORTHFIELD);
        addHoneyData("2014-08-01",25.74,EASTLEA);
        addHoneyData("2014-10-01",32.19,EASTLEA);
        addHoneyData("2015-08-01",13.75,NORTHFIELD);
        addHoneyData("2015-10-01",99.52,SOUTHMEADOW);
        addHoneyData("2015-08-01",77.74,SOUTHMEADOW);
        addHoneyData("2015-10-01",16.19,EASTLEA);
        addHoneyData("2013-08-01",22.1,NORTHFIELD);
        addHoneyData("2013-10-01",18.95,NORTHFIELD);
        addHoneyData("2013-08-01",45.62,EASTLEA);
        addHoneyData("2013-10-01",17.11,EASTLEA);
        addHoneyData("2012-08-01",65.02,NORTHFIELD);
        addHoneyData("2012-10-01",78.13,SOUTHMEADOW);
    }

    protected static void addHoneyData(String date, double weight, int HiveID) {
        // adds data to Honey table
        String sqlToRun = "";
        int next_avail_id = -1;
        // this makes sure we don't reuse numbers from previous tests -- adapted the sql statement from the one at
        //  http://stackoverflow.com/questions/1405393/finding-the-next-available-id-in-mysql
        try {
            sqlToRun = "SELECT Auto_increment AS NextID FROM information_schema.tables WHERE table_name='" + HONEY_TABLE_NAME + "'";
            rs = statement.executeQuery(sqlToRun);
            rs.next();  // move to the first (hopefully only) record
            next_avail_id = rs.getInt("NextID");
            rs.close();
        } catch (SQLException sqle) {
            System.out.println(sqle);
        }

        // if the result set didn't return a positive integer, something broke, and we need to return to the caller.
        if (next_avail_id <=0 ) {
            System.out.println("Invalid record ID returned, moving to next record.");
            return;
        }

        //then insert the record
        try {
            sqlToRun = "INSERT INTO " + HONEY_TABLE_NAME + " VALUES ( ?, ?, ?, ? )";
            PreparedStatement psInsert = conn.prepareStatement(sqlToRun);

            psInsert.setInt(1, next_avail_id);
            psInsert.setDate(2, Date.valueOf(date));
            psInsert.setDouble(3,weight);
            psInsert.setInt(4,HiveID);

            psInsert.executeUpdate();
            psInsert.close();
        } catch (SQLException sqle) {
            System.out.println("Unable to insert honey record.\n" + sqle);
            System.out.println(sqlToRun);
        }
    }

    private static void openConnStatement() {
        // establishes a connection to the database.
        try {
            conn = DriverManager.getConnection(DB_CONNECTION_URL + DB_NAME, USER, PASS);
            statement = conn.createStatement();
        } catch (SQLException sqle) {
            System.out.println("Unable to open connection or create statement.");
            System.out.println(sqle);
        }
    }
}

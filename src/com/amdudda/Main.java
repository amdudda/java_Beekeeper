package com.amdudda;

import java.sql.*;

public class Main {

    public static void main(String[] args) {
	// write your code here

        // this creates the schema 'Beekeeper', sets up the tables, and adds some test data to the database.
        //Database.createDatabase();

        // display the main GUI form
        HarvestManager hm = new HarvestManager();

        // let's make really truly sure our connections are closed
        try {
            if (Database.statement != null) Database.statement.close();
            if (Database.conn != null) Database.conn.close();
            if (Database.rs != null) Database.rs.close();
        } catch (SQLException sqle) {
            System.out.println(sqle);
        }
    }


}

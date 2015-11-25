package com.amdudda;

import java.sql.*;

public class Main {

    // TODO: do these belong in Database object, or should I leave these here to avoid typing "Database.statement" all the time?
    static Statement statement = null;
    static Connection conn = null;
    static ResultSet rs = null;

    public static void main(String[] args) {
	// write your code here

        // this creates the schema 'Beekeeper', sets up the tables, and adds some test data to the database.
        //Database.createDatabase();


        // let's make really truly sure our connections are closed
        try {
            if (statement != null) statement.close();
            if (conn != null) conn.close();
            if (rs != null) rs.close();
        } catch (SQLException sqle) {
            System.out.println(sqle);
        }
    }


}

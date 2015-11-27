package com.amdudda;

import javax.xml.crypto.Data;

/**
 * Created by amdudda on 11/24/15.
 */
public class Queries {

    // no constructor, this just stores a bunch of SQL strings
    // these queries are not parameterized because they don't require user input to generate the sql statement.
    // TODO: assignment specifies to use parameterized queries.  need to think how and where to implement.

    protected static String getAllHiveData() {
        return "SELECT " + Database.HONEY_TABLE_NAME + "." + Database.PK_COLUMN + ", " +
                Database.DATE_COLLECTED_COLUMN + ", " +
                Database.WEIGHT_COLUMN + ", " +
                Database.LOCATION_COLUMN + " " +
                "FROM " + Database.HONEY_TABLE_NAME + ", " + Database.BEEHIVE_TABLE_NAME + " " +
                "WHERE " + Database.HONEY_TABLE_NAME + "." + Database.BEEHIVE_FK_COLUMN + " = " +
                Database.BEEHIVE_TABLE_NAME + "." + Database.PK_COLUMN + " " +
                "ORDER BY " + Database.DATE_COLLECTED_COLUMN + " DESC";
    }

    protected static String getTotalWeightOfAllHoneyForYear() {
        // gets the total amount of honey
        // parameterized b/c of user input.
        return "SELECT SUM(weight) AS TotalCollected FROM " + Database.HONEY_TABLE_NAME + " WHERE YEAR(" +
                Database.DATE_COLLECTED_COLUMN + ") = ?";
    }

    protected static String getTotalHoneyFromHive() {
        // DONE: This one should be parameterized.=
        // gets the total amount of honey from a specific hive
        return "SELECT SUM(weight) AS TotalWeight FROM " + Database.HONEY_TABLE_NAME +
                " WHERE " + Database.BEEHIVE_FK_COLUMN + " = ? ";

    }

    protected static String getBestYearWithWeightFromHive() {
        // TODO: This one should be parameterized.
        // gets the best year and that year's total honey for one particular hive
        // the SELECT TOP syntax doesn't seem to work with a group by clause in mySQL,
        // but LIMIT seems to do the same thing.
        return "SELECT YEAR(" + Database.DATE_COLLECTED_COLUMN + ") AS YearCollected," +
                " SUM(" + Database.WEIGHT_COLUMN + ") AS TotalWeight FROM " + Database.HONEY_TABLE_NAME +
                " WHERE " + Database.BEEHIVE_FK_COLUMN +
                " = ? GROUP BY YearCollected ORDER BY TotalWeight DESC LIMIT 1;";
    }

    protected static String getHiveLocations() {
        return "SELECT " + Database.LOCATION_COLUMN + " FROM " + Database.BEEHIVE_TABLE_NAME;
    }

    protected static String getAnnualTotalsInRankOrder() {
        // returns a list of yearly totals in descending order of total annual production
        return "SELECT YEAR(" + Database.DATE_COLLECTED_COLUMN + ") AS year_collected, " +
                " SUM(weight) AS " + Database.WEIGHT_COLUMN + " FROM  " + Database.HONEY_TABLE_NAME +
                " GROUP BY year_collected" +
                " ORDER BY total_weight DESC";
    }
}

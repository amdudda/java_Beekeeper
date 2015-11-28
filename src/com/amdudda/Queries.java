package com.amdudda;

import javax.xml.crypto.Data;

/**
 * Created by amdudda on 11/24/15.
 */
public class Queries {

    // no constructor, this just stores a bunch of SQL strings
    // these queries are not parameterized because they don't require user input to generate the sql statement.

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
        // DONE: This one should be parameterized.
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
        // this syntax returns a list of yearly totals in descending order of total annual production
        return "SELECT \"n/a\" as Record_id, YEAR(" + Database.DATE_COLLECTED_COLUMN + ") AS year_collected, " +
                " SUM(weight) AS " + Database.WEIGHT_COLUMN + ", \"All Hives\" as location FROM  " + Database.HONEY_TABLE_NAME +
                " GROUP BY year_collected" +
                " ORDER BY " + Database.WEIGHT_COLUMN + " DESC";
    }

    protected static String getMostProductiveHive() {
        // this syntax return data for the most productive hive
        return "SELECT SUM(weight) AS total_harvest," + Database.LOCATION_COLUMN +
                " FROM " + Database.HONEY_TABLE_NAME + ", " + Database.BEEHIVE_TABLE_NAME +
                " WHERE " + Database.BEEHIVE_FK_COLUMN + " = " + Database.BEEHIVE_TABLE_NAME + "." + Database.PK_COLUMN +
                " GROUP BY " + Database.LOCATION_COLUMN +
                " ORDER BY total_harvest DESC " +
                " LIMIT 1;";
    }

    protected static String getLeastProductiveHive() {
        // this syntax returns data for the least productive hive
        return "SELECT SUM(weight) AS total_harvest," + Database.LOCATION_COLUMN +
                " FROM " + Database.HONEY_TABLE_NAME + ", " + Database.BEEHIVE_TABLE_NAME +
                " WHERE" + Database.BEEHIVE_FK_COLUMN + " = " + Database.BEEHIVE_TABLE_NAME + "." + Database.PK_COLUMN +
                " GROUP BY " + Database.LOCATION_COLUMN +
                " ORDER BY total_harvest " +
                " LIMIT 1;";
    }

    protected static String getCurrentVsPreviousYearProduction() {
        // this syntax is a very very long-winded query that returns current year's production vs previous year's production
        // for every hive.  Handles nulls gracefully.  Extremely annoying because my mySQL version doesn't support full joins.  :(
        return "SELECT hive_location, current_year, previous_year,  " +
                "IF(current_year IS NULL,0,current_year) - IF(previous_year IS NULL,0,previous_year) AS difference  " +
                "FROM " +
                "(SELECT CONVERT(harvest_2015,DECIMAL(5,2)) as current_year, CONVERT(harvest_2014,DECIMAL(5,2)) AS previous_year, bid15 as beehive_id FROM  " +
                "(SELECT sum(weight) AS harvest_2015, Year(date_collected) AS yc, beehive_id AS bid15 FROM HoneyData " +
                "GROUP BY beehive_id, yc " +
                "HAVING yc = Year(now())) AS tbl2015 " +
                "LEFT JOIN " +
                "(SELECT sum(weight) as harvest_2014, Year(date_collected) AS yc, beehive_id AS bid14 FROM HoneyData " +
                "GROUP BY beehive_id, yc " +
                "HAVING yc = Year(now())-1) AS tbl2014 " +
                "ON tbl2015.bid15 = tbl2014.bid14 " +
                "UNION DISTINCT " +
                "SELECT harvest_2015, harvest_2014, bid15 as beehive_id FROM  " +
                "(SELECT sum(weight) AS harvest_2015, Year(date_collected) AS yc, beehive_id AS bid15 FROM HoneyData " +
                "GROUP BY beehive_id, yc " +
                "HAVING yc = 2015) AS tbl2015  " +
                "LEFT JOIN " +
                "(SELECT sum(weight) as harvest_2014, Year(date_collected) AS yc, beehive_id AS bid14 FROM HoneyData " +
                "GROUP BY beehive_id, yc " +
                "HAVING yc = 2014) AS tbl2014 " +
                "ON tbl2015.bid15 = tbl2014.bid14) AS tblUnion " +
                "RIGHT JOIN " +
                "Beehive " +
                "ON beehive_id = Beehive.id " +
                "ORDER BY difference DESC;";
    }
}

package com.amdudda;

/**
 * Created by amdudda on 11/24/15.
 */
public class Queries {

    // no constructor, this just stores a bunch of SQL strings

    protected String getTotalWeightOfAllHoney() {
        // gets the total amount of honey
        return "SELECT SUM(weight) AS TotalWeight FROM " + Database.HONEY_TABLE_NAME;
    }

    protected String getTotalHoneyFromHive(int hiveNum) {
        // gets the total amount of honey from a specific hive
        return "SELECT SUM(weight) AS TotalWeight FROM " + Database.HONEY_TABLE_NAME +
                "WHERE " + Database.BEEHIVE_FK_COLUMN + " = " + hiveNum;

    }

    protected String getBestYearWithWeightFromHive(int hiveNum) {
        // gets the best year and total honey for one particular hive
        // the SELECT TOP syntax doesn't seem to work with a group by clause in mySQL,
        // but LIMIT seems to do the same thing.
        return "SELECT YEAR(" + Database.DATE_COLLECTED_COLUMN + ") AS YearCollected, " +
                "SUM(" + Database.WEIGHT_COLUMN + ") AS TotalWeight FROM " + Database.HONEY_TABLE_NAME +
                "WHERE beehive_id = " + hiveNum +
                " GROUP BY YearCollected ORDER BY TotalWeight DESC LIMIT 1;";
    }
}

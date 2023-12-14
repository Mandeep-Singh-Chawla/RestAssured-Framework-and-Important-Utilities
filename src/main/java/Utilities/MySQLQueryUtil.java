package Utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




/**
 * @see DBconnectUtil
 */
public class MySQLQueryUtil {

    private static Logger logger = LoggerFactory.getLogger(MySQLQueryUtil.class);
    private static PropertyFactory DBpropertyFactory = Propertyfile.getDBProperty();

    /**
     * Executes a select query and returns the result set.
     *
     * @param selectQuery the select query to execute
     * @return the result set of the executed query
     * @throws SQLException if a database access error occurs or the SQL syntax is invalid
     */
    public static ResultSet executeSelectQueryGetResultset(String selectQuery) throws SQLException {

        logger.info("Select Query: " + selectQuery);
        Statement statement = null;
        ResultSet rs = null;

        try {
//        	statement = DatabaseUtil.getInstance(DBpropertyFactory).getConnection().createStatement();
            statement = DBconnectUtil.conn.createStatement();
            rs = statement.executeQuery(selectQuery);
        } catch (Exception e) {
            if (statement != null) {
                statement.close();
            }
        }
        return rs;
    }

    /**
     * Executes a select query on a MySQL database and returns the result as a string.
     * Uses the default delimiter of "," to separate the columns in the result.
     *
     * @param selectQuery the select query to execute
     * @return the result of the select query as a string
     */
    public static String executeSelectQuery(String selectQuery) {
        return executeSelectQuery(selectQuery, ",");
    }

    /**
     * Executes a select query and returns the result set as a string.
     * @param selectQuery the select query to execute
     * @param seprator the separator to use between columns in the result set
     * @return the result set as a string, or null if an exception occurs
     */
    public static String executeSelectQuery(String selectQuery, String seprator) {
        logInfo("Query : " + selectQuery);
        ResultSet resultSet;
        StringBuilder builder = new StringBuilder();
        try {
            resultSet = executeSelectQueryGetResultset(selectQuery);
            int columnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                for (int i = 0; i < columnCount;) {
                    builder.append(resultSet.getString(i + 1));
                    if (++i < columnCount)
                        builder.append(seprator);
                }
                if (!resultSet.isLast())
                    builder.append("##");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        String resultSetAsString = builder.toString();
        logger.info("DB output: " + resultSetAsString);
        logInfo("Query output : " + resultSetAsString);
        return resultSetAsString;
    }

    /**
     * Executes a select query without logging and returns the result set as a string.
     * @param selectQuery the select query to execute
     * @return the result set as a string, or null if an exception occurs
     */
    public static String executeSelectQueryWoLogs(String selectQuery) {
        ResultSet resultSet;
        StringBuilder builder = new StringBuilder();
        try {
            resultSet = executeSelectQueryGetResultset(selectQuery);
            int columnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                for (int i = 0; i < columnCount;) {
                    builder.append(resultSet.getString(i + 1));
                    if (++i < columnCount)
                        builder.append(",");
                }
                if (!resultSet.isLast())
                    builder.append("##");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        String resultSetAsString = builder.toString();
        return resultSetAsString;
    }

    /**
     * Executes a select query in a connection pool and returns the result set as a string.
     * If the result set is empty or null, it retries the query up to 5 times with a 2 second delay between each retry.
     *
     * @param selectQuery the select query to execute
     * @return the result set as a string
     * @throws SQLException if a database access error occurs
     * @throws InterruptedException if the thread is interrupted while sleeping
     */
    public static String executeSelectQueryInPool(String selectQuery) throws SQLException, InterruptedException {
        String resultSetAsString = null;
        boolean flag = false;
        logInfo("Query  : " + selectQuery);
        try {
            for (int i = 0; i < 5; i++) {
                System.out.println("Iteration : " + i);
                resultSetAsString = executeSelectQuery(selectQuery);
                System.out.println("resultSetAsString : " + resultSetAsString);
                if (resultSetAsString.isEmpty() || resultSetAsString == null
                        || resultSetAsString.equalsIgnoreCase("null")) {
                    System.out.println("Condition not matched");
                    Thread.sleep(2000);
                } else {
                    flag = true;
                    System.out.println("Condition matched");
                    break;
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        logInfo("Query output : " + resultSetAsString);
        Assert.assertTrue(flag, "DB output : " + resultSetAsString);
        return resultSetAsString;
    }

    public static void logInfo(String description) {
        ExtentReportUtil.logInfo(description);
    }

    /**
     * Executes the given select query in a connection pool and waits for the result to be non-empty.
     * If the result is empty, it retries the query up to 10 times with a 2 second delay between each attempt.
     *
     * @param selectQuery the select query to execute
     * @throws SQLException if there is an error executing the query
     * @throws InterruptedException if the thread is interrupted while waiting for the result
     */
    public static void executeSelectQueryInPool2(String selectQuery) throws SQLException, InterruptedException {
        String resultSetAsString = null;
        try {
            for (int i = 0; i < 10; i++) {
                System.out.println("Iteration : " + i);
                resultSetAsString = executeSelectQueryWoLogs(selectQuery);
                System.out.println("resultSetAsString : " + resultSetAsString);
                if (resultSetAsString.isEmpty() || resultSetAsString == null
                        || resultSetAsString.equalsIgnoreCase("null")) {
                    System.out.println("Condition not matched");
                    Thread.sleep(2000);
                } else {
                    System.out.println("Condition matched");
                    break;
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes the given select query in a connection pool and waits for the result to be non-empty for a maximum of 30 iterations, with a specified time delay between each iteration.
     *
     * @param selectQuery the select query to be executed
     * @param timeInSeconds the time delay between each iteration, in seconds
     * @return the result of the select query as a string
     * @throws SQLException if a database access error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public static String executeSelectQueryInPool(String selectQuery, int timeInSeconds)
            throws SQLException, InterruptedException {
        String resultSetAsString = null;
        boolean flag = false;
        logInfo("Query  : " + selectQuery);
        try {
            for (int i = 0; i < 30; i++) {
                System.out.println("Iteration : " + i);
                resultSetAsString = executeSelectQuery(selectQuery);
                System.out.println("resultSetAsString : " + resultSetAsString);
                if (resultSetAsString.isEmpty() || resultSetAsString == null
                        || resultSetAsString.equalsIgnoreCase("null")) {
                    System.out.println("Condition not matched");
                    Thread.sleep(timeInSeconds * 1000);

                } else {
                    flag = true;
                    System.out.println("Condition matched");
                    break;
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        logInfo("Query output : " + resultSetAsString);
        Assert.assertTrue(flag, "DB output : " + resultSetAsString);
        return resultSetAsString;
    }

    /**
     * Executes a select query in a connection pool and waits for the result to match a certain condition.
     *
     * @param selectQuery the select query to execute
     * @param timeInSeconds the time to wait between iterations in seconds
     * @return the result set as a string if the condition is matched, otherwise null
     * @throws SQLException if a database access error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public static String executeSelectQueryInPoolForCount(String selectQuery, int timeInSeconds)
            throws SQLException, InterruptedException {
        String resultSetAsString = null;
        logInfo("Query  : " + selectQuery);
        try {
            for (int i = 0; i < 30; i++) {
                System.out.println("Iteration : " + i);
                resultSetAsString = executeSelectQuery(selectQuery);
                System.out.println("resultSetAsString : " + resultSetAsString);
                if (resultSetAsString.isEmpty() || resultSetAsString == null
                        || resultSetAsString.equalsIgnoreCase("null") || resultSetAsString.equals("0")) {
                    System.out.println("Condition not matched");
                    Thread.sleep(timeInSeconds * 1000);

                } else {
                    System.out.println("Condition matched");
                    break;
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        logInfo("Query output : " + resultSetAsString);
        return resultSetAsString;
    }

    /**
     * Executes a select query in a connection pool for a specified number of iterations and waits for a specified time between each iteration.
     * @param selectQuery the select query to execute
     * @param timeInSeconds the time to wait between each iteration in seconds
     * @param iterations the number of iterations to perform
     * @return the result set as a string
     * @throws SQLException if a database access error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public static String executeSelectQueryInPoolForCount(String selectQuery, int timeInSeconds, int iterations)
            throws SQLException, InterruptedException {
        String resultSetAsString = null;
        logInfo("Query  : " + selectQuery);
        try {
            for (int i = 0; i < iterations; i++) {
                System.out.println("Iteration : " + i);
                resultSetAsString = executeSelectQuery(selectQuery);
                System.out.println("resultSetAsString : " + resultSetAsString);
                if (resultSetAsString.isEmpty() || resultSetAsString == null
                        || resultSetAsString.equalsIgnoreCase("null") || resultSetAsString.equals("0")) {
                    System.out.println("Condition not matched");
                    Thread.sleep(timeInSeconds * 1000);

                } else {
                    System.out.println("Condition matched");
                    break;
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        logInfo("Query output : " + resultSetAsString);
        return resultSetAsString;
    }

    /*
     * public static String executeSelectQuery(String selectQuery) { ResultSet
     * resultSet; StringBuilder builder = new StringBuilder(); try { resultSet =
     * executeSelectQueryGetResultset(selectQuery); int columnCount =
     * resultSet.getMetaData().getColumnCount(); ResultSetMetaData metaData =
     * resultSet.getMetaData(); while (resultSet.next()) { for (int i = 1; i <
     * columnCount; i++) { String value = resultSet.getString(i); if
     * ("status".equalsIgnoreCase(metaData.getColumnName(i)) &&
     * !"CLOSED".equalsIgnoreCase(value)) continue;
     * builder.append(value).append(","); } if (builder.length() > 0)
     * builder.substring(0, builder.length() - 1); if (!resultSet.isLast())
     * builder.append("##"); } } catch (SQLException e) { e.printStackTrace();
     * return null; } catch (NullPointerException e) { e.printStackTrace(); return
     * null; } String resultSetAsString = builder.toString();
     * logger.info("DB output: " + resultSetAsString); return resultSetAsString; }
     */

    /**
     * Separate the data, row wise and column wise as per given parameters
     *
     * @param selectQuery
     * @param rowSeparator
     * @param columnSeparator
     * @return
     * @throws SQLException
     */
    public static String executeSelectQuery(String selectQuery, String columnSeparator, String rowSeparator)
            throws SQLException {
        logInfo("Query  : " + selectQuery);

        ResultSet resultSet = executeSelectQueryGetResultset(selectQuery);
        StringBuilder builder = new StringBuilder();
        int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            for (int i = 0; i < columnCount;) {
                builder.append(resultSet.getString(i + 1));
                if (++i < columnCount && columnSeparator != null)
                    builder.append(columnSeparator);
            }
            if (rowSeparator != null)
                builder.append(rowSeparator);
        }
        String resultSetAsString = builder.toString();
        logInfo("Query  output : " + resultSetAsString);
        return resultSetAsString;

    }

    /**
     * Executes an update query on the connected MySQL database.
     *
     * @param updateQuery the update query to be executed
     * @throws SQLException if a database access error occurs or the query fails
     */
    public static void executeUpdateQuery(String updateQuery) throws SQLException {
        logInfo("Query Update : " + updateQuery);

        Statement statement = null;
        executeSelectQueryGetResultset(updateQuery);
        try {
            statement = DBconnectUtil.connection.createStatement();
            int count = statement.executeUpdate(updateQuery);
            logInfo(count + " Row(0) effected");
        } catch (Exception e) {

            if (statement != null) {
                statement.close();
            }

            logInfo(e.getMessage());
        }
    }

    /**
     * Executes a select query and returns the result as a list of maps.
     * Each map represents a row in the result set, with column names as keys and column values as values.
     * @param selectQuery the select query to execute
     * @return a list of maps representing the result set, or null if an exception occurs
     */
    public static List<Map<String, String>> executeSelectQueryReturnsListOfMap(String selectQuery) {
        logInfo("Query : " + selectQuery);
        ResultSet resultSet;
        StringBuilder builder = new StringBuilder();
        List<Map<String, String>> listOfMap = new ArrayList<>();
        try {
            try {
                executeSelectQueryInPool2(selectQuery);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resultSet = executeSelectQueryGetResultset(selectQuery);

            java.sql.ResultSetMetaData meta = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, String> map = new HashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String key = meta.getColumnName(i);
                    String value = resultSet.getString(key);
                    map.put(key, value);
                }
                listOfMap.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        logger.info("DB output: " + listOfMap);
        logInfo("Query output : " + listOfMap);
        return listOfMap;
    }

    /**
     * Executes a SELECT query and returns the result set as a list of lists of strings.
     *
     * @param selectQuery the SELECT query to execute
     * @return a list of lists of strings representing the result set, or null if an error occurs
     */
    public static List<List<String>> executeSelectQueryReturnsListOfList(String selectQuery) {
        logInfo("Query : " + selectQuery);
        ResultSet resultSet;
        StringBuilder builder = new StringBuilder();
        List<List<String>> listOfList = new ArrayList<>();
        try {
            resultSet = executeSelectQueryGetResultset(selectQuery);

            java.sql.ResultSetMetaData meta = resultSet.getMetaData();
            while (resultSet.next()) {
                List<String> rowList = new ArrayList<String>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String key = meta.getColumnName(i);
                    String value = resultSet.getString(key);
                    rowList.add(value);
                }
                listOfList.add(rowList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        logger.info("DB output: " + listOfList);
        logInfo("Query output : " + listOfList);
        return listOfList;
    }

    /**
     * Executes a select query and returns the result set as a list of strings.
     *
     * @param selectQuery the select query to execute
     * @return a list of strings representing the rows returned by the query
     */
    public static List<String> executeSelectQueryReturnsList(String selectQuery) {
        ResultSet resultSet;
        List<String> rowList = new ArrayList<>();
        try {
            resultSet = executeSelectQueryGetResultset(selectQuery);

            java.sql.ResultSetMetaData meta = resultSet.getMetaData();
            while (resultSet.next()) {
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String key = meta.getColumnName(i);
                    String value = resultSet.getString(key);
                    rowList.add(value);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        logInfo("Query output : " + rowList);
        return rowList;
    }

    /**
     * Executes a select query and returns the result as a map of column names and values.
     * @param selectQuery the select query to execute
     * @return a map of column names and values representing the result of the select query
     */
    public static Map<String, String> executeSelectQueryReturnsMap(String selectQuery) {
        logInfo("Query : " + selectQuery);
        ResultSet resultSet;
        Map<String, String> map = new HashMap<String, String>();
        try {
            try {
                executeSelectQueryInPool2(selectQuery);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resultSet = executeSelectQueryGetResultset(selectQuery);

            java.sql.ResultSetMetaData meta = resultSet.getMetaData();
            while (resultSet.next()) {
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String key = meta.getColumnName(i);
                    String value = resultSet.getString(key);
                    map.put(key, value);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        logInfo("Query output : " + map);
        return map;
    }

    /**
     * Executes a select query and returns the result as a map of key-value pairs.
     * @param selectQuery the select query to be executed
     * @return a map of key-value pairs representing the result of the select query
     */
    public static Map<String, String> executeSelectQueryMapValues(String selectQuery) {
        logInfo("Query : " + selectQuery);
        ResultSet resultSet;
//        StringBuilder builder = new StringBuilder();
        Map<String, String> map = new HashMap<String, String>();
        try {
            try {
                executeSelectQueryInPool2(selectQuery);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resultSet = executeSelectQueryGetResultset(selectQuery);

            while (resultSet.next()) {
                String key = resultSet.getString(1);
                String value = resultSet.getString(2);
                map.put(key, value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        logInfo("Query output : " + map);
        return map;
    }

    /**
     * Executes a select query and returns null if no results are found.
     *
     * @param selectQuery the select query to execute
     * @return the result of the select query, or null if no results are found
     */
    public static String executeSelectQueryfornull(String selectQuery) {
        return executeSelectQueryfornull(selectQuery, ",");
    }

    /**
     * Executes a select query and returns the result set as a string with a specified separator.
     *
     * @param selectQuery the select query to be executed
     * @param seprator the separator to be used in the result set string
     * @return the result set as a string with the specified separator, or null if there was an error
     */
    public static String executeSelectQueryfornull(String selectQuery, String seprator) {
        logInfo("Query : " + selectQuery);
        ResultSet resultSet;
        StringBuilder builder = new StringBuilder();
        try {
            resultSet = executeSelectQueryGetResultset(selectQuery);
            if(resultSet!=null)
            {
                int columnCount = resultSet.getMetaData().getColumnCount();
                while (resultSet.next()) {
                    for (int i = 0; i < columnCount;) {
                        builder.append(resultSet.getString(i + 1));
                        if (++i < columnCount)
                            builder.append(seprator);
                    }
                    if (!resultSet.isLast())
                        builder.append("##");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        String resultSetAsString = builder.toString();
        logger.info("DB output: " + resultSetAsString);
        logInfo("Query output : " + resultSetAsString);
        return resultSetAsString;
    }
}
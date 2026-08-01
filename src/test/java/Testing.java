import Utilities.*;
import com.jcraft.jsch.Session;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

public class Testing extends BaseTest {

    String sshHost, sshUser, sshPassword;
    private PropertyFactory propertyFactory;

    private String testDataDir() {
        return System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                + File.separator + "resources" + File.separator + "testdata";
    }

    private boolean hasUsableDbConnection() {
        try {
            return DBconnectUtil.conn != null && !DBconnectUtil.conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPlaceholder(String value) {
        return value == null || value.trim().isEmpty()
                || value.contains("${")
                || "IP".equalsIgnoreCase(value)
                || "user".equalsIgnoreCase(value)
                || "passwd".equalsIgnoreCase(value);
    }

    @Test(description = "Test ConnectToServer Utility")
    public void test1() throws Exception {
        propertyFactory = Propertyfile.getDBProperty();
        sshHost = propertyFactory.getProperty("SSH_Host");
        sshUser = propertyFactory.getProperty("SSH_UserName");
        sshPassword = propertyFactory.getProperty("SSH_Password");

        if (isPlaceholder(sshHost) || isPlaceholder(sshUser) || isPlaceholder(sshPassword)) {
            throw new SkipException("SSH credentials are not configured in Connection.properties");
        }

        ConnectToServer cts = new ConnectToServer();
        Session session = cts.createServerSession(sshUser, sshHost, sshPassword);
        Assert.assertNotNull(session, "SSH session should be created");
        Assert.assertTrue(cts.isSessionConnected(session), "SSH session should be connected");
        cts.disconnectToServer(session);
    }

    @Test(description = "Demo code to read properties from file")
    public void test2() throws Exception {
        propertyFactory = Propertyfile.getDBProperty();
        Assert.assertNotNull(propertyFactory, "PropertyFactory should load Connection.properties");

        sshHost = propertyFactory.getProperty("SSH_Host");
        sshUser = propertyFactory.getProperty("SSH_UserName");
        sshPassword = propertyFactory.getProperty("SSH_Password");

        Assert.assertNotNull(sshHost, "SSH_Host property should exist");
        Assert.assertNotNull(sshUser, "SSH_UserName property should exist");
        Assert.assertNotNull(sshPassword, "SSH_Password property should exist");

        System.out.println("sshHost:" + sshHost);
        System.out.println("sshUser:" + sshUser);
        System.out.println("sshPassword:" + sshPassword);
    }

    @Test(description = "Demo code for RestAssured Utility")
    public void test3() throws Exception {
        RestAssuredUtil request = new RestAssuredUtil("https://automationexercise.com");
        String response = request.getRequest("/api/productsList", 200);
        Assert.assertNotNull(response, "API response should not be null");
        Assert.assertTrue(response.contains("products"), "Response should contain products");

        System.out.println("Response: " + response);
        String obj = new JSONObject(response).getJSONArray("products").getJSONObject(1).getString("name");
        System.out.println("Name: " + obj);
        JSONArray obj1 = new JSONObject(response).getJSONArray("products");
        Assert.assertTrue(obj1.length() > 0, "Products list should not be empty");
        for (int i = 0; i < obj1.length(); i++) {
            JSONObject obj2 = obj1.getJSONObject(i);
            System.out.println("Name: " + obj2.getString("name"));
        }
    }

    @Test(description = "Demo code to read data from CSV")
    public void test4() throws Exception {
        String csvPath = testDataDir() + File.separator + "testing.csv";
        Assert.assertTrue(new File(csvPath).exists(), "Sample CSV should exist at " + csvPath);

        CSVReadUtil reader = new CSVReadUtil(csvPath);
        List<String[]> csvtest = reader.getEntriesAsList();
        Assert.assertTrue(csvtest.size() > 1, "CSV should contain header and at least one data row");
        for (int i = 0; i < csvtest.size(); i++) {
            System.out.println(csvtest.get(i)[0]);
        }
        reader.closeReader();
    }

    @Test(description = "Demo code to write data on CSV")
    public void test5() throws Exception {
        File outDir = new File(testDataDir());
        if (!outDir.exists()) {
            Assert.assertTrue(outDir.mkdirs(), "Unable to create testdata directory");
        }

        String csvPath = testDataDir() + File.separator + "testing5.csv";
        String[] data = {"a", "b", "c"};
        CSVWriteUtil write = new CSVWriteUtil(csvPath);
        write.addLineToCSV(data);
        write.closeWriter();

        File written = new File(csvPath);
        Assert.assertTrue(written.exists(), "CSV file should be created");
        Assert.assertTrue(written.length() > 0, "CSV file should not be empty");
    }

    @Test(description = "Demo code to execute query on DB")
    public void test6() throws Exception {
        if (!hasUsableDbConnection()) {
            throw new SkipException("Database connection is not available");
        }

        int ID = 1;
        DBconnectUtil.initConnection(DBpropertyFactory);
        String query = "SELECT 1 AS id";
        String DbData = MySQLQueryUtil.executeSelectQuery(query);
        DBconnectUtil.closeConnection();
        System.out.println("DbData : " + DbData);

        Assert.assertNotNull(DbData, "Query result should not be null");
        for (String row : DbData.split("##")) {
            if (row.trim().isEmpty()) {
                continue;
            }
            System.out.println("Barcode  : " + row.split(",")[0]);
        }
    }
}

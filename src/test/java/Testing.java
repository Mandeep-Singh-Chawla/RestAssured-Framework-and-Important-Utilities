import Utilities.*;
import com.jcraft.jsch.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Properties;
import org.testng.annotations.Test;

public class Testing extends BaseTest {

    String sshHost, sshUser, sshPassword;
    private PropertyFactory DBpropertyFactory;


    @Test(description = "Test ConnectToServer Utility")
    public void test1() throws Exception {

        ConnectToServer cts = new ConnectToServer();
        Session session = cts.createServerSession("user", "IP", "passwd");
        System.out.println(ConnectToServer.copyFileFromServer(session, "/file_23-08-2022 13:00:00.csv", "/Users/mandeepsinghchawla/Documents/"));
        cts.disconnectToServer(session);
    }


    @Test(description = "Demo code to read properties from file")
    public void test2() throws Exception {

        DBpropertyFactory = Propertyfile.getDBProperty();
        sshHost = DBpropertyFactory.getProperty("SSH_Host");
        sshUser = DBpropertyFactory.getProperty("SSH_UserName");
        sshPassword = DBpropertyFactory.getProperty("SSH_Password");
        System.out.println("sshHost:" + sshHost);
        System.out.println("sshUser:" + sshUser);
        System.out.println("sshPassword:" + sshPassword);

    }

    @Test(description = " Demo code for RestAssured Utility")
    public void test3() throws Exception {

        RestAssuredUtil request;
        request = new RestAssuredUtil("https://automationexercise.com");
        String response = request.getRequest("/api/productsList", 200);
        System.out.println("Response: " + response);
        String obj = new JSONObject(response).getJSONArray("products").getJSONObject(1).getString("name");
        System.out.println("Name: " + obj);
        JSONArray obj1 = new JSONObject(response).getJSONArray("products");
        for (int i = 0; i < obj1.length(); i++) {
            JSONObject obj2 = obj1.getJSONObject(i);
            System.out.println("Name: " + obj2.getString("name"));

        }
    }

        @Test(description = "Demo code to read data from CSV")
        public void test4() throws Exception {

            CSVReadUtil reader = new CSVReadUtil("/Users/mandeepsinghchawla/Downloads/testing.csv");
            List<String[]> csvtest = reader.getEntriesAsList();
            for (int i = 0; i < csvtest.size(); i++) {
                System.out.println(csvtest.get(i)[0]);

            }
            reader.closeReader();

        }

        @Test(description = "Demo code to write data on CSV")
            public void test5() throws Exception {

            String Data[] = {"a", "b", "c"};
            CSVWriteUtil write = new CSVWriteUtil("/Users/mandeepsinghchawla/Downloads/testing5.csv");

//            write.addAllToCSV(csvtest);
            write.addLineToCSV(Data);
            write.closeWriter();
        }


        @Test(description = "Demo code to execute query on DB")
        public void test6() throws Exception {

        int ID = 1;
        DBconnectUtil.initConnection(DBpropertyFactory);
            String query = "dbquery" + ID + "';";
        String DbData = MySQLQueryUtil.executeSelectQuery(query);
        DBconnectUtil.closeConnection();
        System.out.println("DbData : " + DbData);

        for (String row : DbData.split("##")) {
            System.out.println("Barcode  : "+row.split(",")[0]);
    }




    }
}

package Utilities;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DBconnectUtil {

    private final static Logger log = LogManager.getLogger(DBconnectUtil.class.getName());
    public static Connection connection = null;
    private static Session session = null;
    public static MongoClient mongoClient = null;
    public static MongoDatabase mongodb;

    private static String MongoHost = "127.0.0.1";
    private static int mongoLPort = 27020;
    private static int mongoRPort = 27017;
    private static String mongoUser;
    private static String mongoPassword;

    private static String mongoRHost;
    private static String mongoDatabase;
    private static String mongoDBname;
    private static final int sshPort = 22;
    private static int LPort = 3460;
    private static int RPort = 3306;
    private static String mysqlHost;
    private static String mysqlUser;
    private static String mysqlPassword;
    private static String databaseName;
    private static String sshUserName;
    private static String sshHost;
    private static String sshPassword;
    private static String RHost;
    private static DBconnectUtil instance;
    public static Connection conn;

    static String privateKey = "./src/main/resources/id_rsa";

    private DBconnectUtil(PropertyFactory propertyFactory) {
        try {
            this.conn=initConnection1(propertyFactory);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return this.conn;
    }

    /**
     * Returns an instance of the DatabaseUtil class using the provided PropertyFactory.
     * If an instance already exists and its connection is closed, a new instance is created.
     *
     * @param propertyFactory the PropertyFactory to use for creating the DatabaseUtil instance
     * @return the DatabaseUtil instance
     * @throws SQLException if there is an error creating the DatabaseUtil instance
     */
    public static DBconnectUtil getInstance(PropertyFactory propertyFactory) throws SQLException {
        if(instance==null) {
            instance =  new DBconnectUtil(propertyFactory);
        } else if(instance.getConnection().isClosed()) {
            instance =  new DBconnectUtil(propertyFactory);
        }

        return instance;
    }

    public static void initConnection(PropertyFactory propertyFactory) throws SQLException {

//		if (connection == null) {
//			closeConnection();
//		}
//			sshHost = propertyFactory.getProperty("SSH_Host");
//			if (sshHost != null && !sshHost.isEmpty()) {
//				sshUserName = propertyFactory.getProperty("SSH_UserName");
//				sshPassword = propertyFactory.getProperty("SSH_Password");
//				RHost = propertyFactory.getProperty("DB_IP");
//				mysqlHost = "127.0.0.1";
//				JSch jsch = new JSch();
//				try {
//					session = jsch.getSession(sshUserName, sshHost, sshPort);
//					session.setConfig("StrictHostKeyChecking", "No");
//					session.setPassword(sshPassword);
//					session.connect(60000);
//					session.setPortForwardingL(LPort, RHost, RPort);
//					log.debug(session.getPortForwardingL()[0]);
//					log.debug("ssh " + sshUserName + "@" + sshHost + ":" + sshPort + "? " + session.isConnected());
//				} catch (JSchException e1) {
//					e1.printStackTrace();
//					return;
//				}
//			} else {
//				mysqlHost = propertyFactory.getProperty("DB_IP");
//				LPort = 3310;
//			}
//			createDatabaseConnection(propertyFactory);
    }

    /**
     * Initializes a database connection using the given PropertyFactory object.
     * If SSH_Host is not null or empty, it creates an SSH tunnel to the database server and forwards the connection through it.
     * Otherwise, it connects directly to the database server.
     * @param propertyFactory the PropertyFactory object containing the necessary properties for database connection
     * @return a Connection object representing the database connection
     * @throws SQLException if a database access error occurs
     * @throws IOException if an I/O error occurs
     */
    public Connection initConnection1(PropertyFactory propertyFactory) throws SQLException, IOException {

        sshHost = propertyFactory.getProperty("SSH_Host");
        RPort = Integer.parseInt(propertyFactory.getProperty("DB_PORT"));
        if (sshHost != null && !sshHost.isEmpty()) {
            sshUserName = propertyFactory.getProperty("SSH_UserName");
            sshPassword = propertyFactory.getProperty("SSH_Password");
            RHost = propertyFactory.getProperty("DB_IP");
            mysqlHost = "127.0.0.1";
            JSch jsch = new JSch();
            try {
                session = jsch.getSession(sshUserName, sshHost, sshPort);
                session.setConfig("StrictHostKeyChecking", "No");
                //jsch.addIdentity(privateKey);
                session.setPassword(sshPassword);
                session.connect(60000);
//				ServerSocket s = create(new int[] { 3462, 3463, 3464, 3465, 3466, 3467, 3468, 3469 });
//				System.out.println("listening on port: " + s.getLocalPort());
//				LPort = s.getLocalPort();
                session.setPortForwardingL(LPort, RHost, RPort);
                log.debug(session.getPortForwardingL()[0]);
                log.debug("ssh " + sshUserName + "@" + sshHost + ":" + sshPort + "? " + session.isConnected());
            } catch (JSchException e1) {
                e1.printStackTrace();
            }
        } else {
            mysqlHost = propertyFactory.getProperty("DB_IP");
            LPort = 3306;
        }
        return createDatabaseConnection(propertyFactory);
    }

    /**
     * Creates a server socket on the first available port from the given array of ports.
     *
     * @param ports an array of integers representing the ports to try
     * @return a ServerSocket object on the first available port
     * @throws IOException if no free port is found in the given range
     */
    public static ServerSocket create(int[] ports) throws IOException {
        for (int port : ports) {
            try {
                return new ServerSocket(port);
            } catch (IOException ex) {
                continue; // try next port
            }
        }

        // if the program gets here, no port in the range was found
        throw new IOException("no free port found");
    }

    /**
     * Initializes a database connection using the given PropertyFactory and database name.
     * If a connection already exists, it is closed before initializing a new one.
     * If SSH host is provided, it creates a session and sets up port forwarding.
     *
     * @param propertyFactory the PropertyFactory object containing the necessary properties for database connection
     * @param dbName the name of the database to connect to
     * @throws SQLException if there is an error in creating the database connection
     */
    public static void initConnection(PropertyFactory propertyFactory, String dbName) throws SQLException {

        if (connection != null) {
            closeConnection();
        }
        sshHost = propertyFactory.getProperty("SSH_Host");
        RPort = Integer.parseInt(propertyFactory.getProperty("DB_PORT"));
        if (sshHost != null && !sshHost.isEmpty()) {
            sshUserName = propertyFactory.getProperty("SSH_UserName");
            sshPassword = propertyFactory.getProperty("SSH_Password");
            RHost = propertyFactory.getProperty("DB_IP");
            mysqlHost = "127.0.0.1";
            JSch jsch = new JSch();
            try {
                session = jsch.getSession(sshUserName, sshHost, sshPort);
                session.setConfig("StrictHostKeyChecking", "No");
                session.setPassword(sshPassword);
                session.connect(60000);
                session.setPortForwardingL(LPort, RHost, RPort);
                log.debug(session.getPortForwardingL()[0]);
                log.debug("ssh " + sshUserName + "@" + sshHost + ":" + sshPort + "? " + session.isConnected());
            } catch (JSchException e1) {
                e1.printStackTrace();
                return;
            }
        } else {
            mysqlHost = propertyFactory.getProperty("DB_IP");
            LPort = 3306;
        }
        createDatabaseConnection(propertyFactory, dbName);
    }

    /**
     * Initializes a database connection using the provided database URL, database name, database user, and database password.
     * If a connection already exists, it is closed before initializing a new connection.
     * If an SSH host is provided, a secure connection is established using the SSH host, username, and password.
     * Otherwise, a connection is established directly to the database URL.
     * @param propertyFactory the property factory used to retrieve SSH host, username, password, and database port
     * @param dbUrl the URL of the database
     * @param dbName the name of the database
     * @param dbuser the username for the database
     * @param dbPassword the password for the database
     * @throws SQLException if a database access error occurs
     */
    public static void initConnection(PropertyFactory propertyFactory, String dbUrl, String dbName, String dbuser,
                                      String dbPassword) throws SQLException {

        if (connection != null) {
            closeConnection();
        }

        sshHost = propertyFactory.getProperty("SSH_Host");
        RPort = Integer.parseInt(propertyFactory.getProperty("DB_PORT"));
        if (sshHost != null && !sshHost.isEmpty()) {
            sshUserName = propertyFactory.getProperty("SSH_UserName");
            sshPassword = propertyFactory.getProperty("SSH_Password");
            RHost = dbUrl;
            mysqlHost = "127.0.0.1";
            JSch jsch = new JSch();
            try {
                session = jsch.getSession(sshUserName, sshHost, sshPort);
                session.setConfig("StrictHostKeyChecking", "No");
                session.setPassword(sshPassword);
                session.connect(60000);
                session.setPortForwardingL(LPort, RHost, RPort);
                log.debug(session.getPortForwardingL()[0]);
                log.debug("ssh " + sshUserName + "@" + sshHost + ":" + sshPort + "? " + session.isConnected());
            } catch (JSchException e1) {
                e1.printStackTrace();
                return;
            }
        } else {
            mysqlHost = dbUrl;
            LPort = 3310;
        }
        createDatabaseConnection(dbName, dbuser, dbPassword);
    }

    /**
     * Creates a database connection using the provided PropertyFactory object.
     * @param propertyFactory the PropertyFactory object containing the necessary properties for the connection
     * @return a Connection object representing the established database connection
     * @throws SQLException if there is an error connecting to the database
     */
    private static Connection createDatabaseConnection(PropertyFactory propertyFactory) throws SQLException {
        mysqlUser = propertyFactory.getProperty("DB_USER_ID");
        mysqlPassword = propertyFactory.getProperty("DB_PASSWORD");
        databaseName = propertyFactory.getProperty("DB_NAME");
        String url = "jdbc:mysql://" + mysqlHost + ":" + LPort + "/" + databaseName;
        Properties properties = new Properties();
        properties.setProperty("user", mysqlUser);
        properties.setProperty("password", mysqlPassword);
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        System.out.println("url : "+url + " properties: "+properties);
        connection = DriverManager.getConnection(url, properties);
//		DataSource dataSource = getDataSource(propertyFactory);
//        connection = dataSource.getConnection();
        log.info("JDBC Connection to Database host " + url + " established? " + !connection.isClosed());

        return connection;
    }

    /**
     * This method creates a database connection using the given PropertyFactory and database name.
     * It retrieves the database user ID and password from the PropertyFactory and uses them to establish a connection.
     * The connection properties are set to disable SSL and enable auto-reconnect.
     *
     * @param propertyFactory The PropertyFactory object used to retrieve the database user ID and password.
     * @param dbName The name of the database to connect to.
     * @throws SQLException If there is an error establishing the database connection.
     */
    private static void createDatabaseConnection(PropertyFactory propertyFactory, String dbName) throws SQLException {
        mysqlUser = propertyFactory.getProperty("DB_USER_ID");
        mysqlPassword = propertyFactory.getProperty("DB_PASSWORD");
//    databaseName   = propertyFactory.getProperty("DB_NAME");
        String url = "jdbc:mysql://" + mysqlHost + ":" + LPort + "/" + dbName;
        Properties properties = new Properties();
        properties.setProperty("user", mysqlUser);
        properties.setProperty("password", mysqlPassword);
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");

        connection = DriverManager.getConnection(url, properties);
        log.info("JDBC Connection to Database host " + url + " established? " + !connection.isClosed());
    }

    /**
     * Creates a database connection with the given database name, username and password.
     * @param dbName the name of the database to connect to
     * @param dbUser the username to use for the database connection
     * @param dbPassword the password to use for the database connection
     * @throws SQLException if there is an error connecting to the database
     */
    private static void createDatabaseConnection(String dbName, String dbUser, String dbPassword) throws SQLException {
        mysqlUser = dbUser;
        mysqlPassword = dbPassword;
        String url = "jdbc:mysql://" + mysqlHost + ":" + LPort + "/" + dbName;
        Properties properties = new Properties();
        properties.setProperty("user", mysqlUser);
        properties.setProperty("password", mysqlPassword);
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");

        connection = DriverManager.getConnection(url, properties);
        log.info("JDBC Connection to Database host " + url + " established? " + !connection.isClosed());
    }

    public static void closeConnection() {
//        try {
//            if (connection != null) {
//                connection.close();
//            }
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
//        if(session!=null && session.isConnected()) {
//            session.disconnect();
//        }
    }

    /**
     * Closes the database connection and session.
     * If the connection is not null, it will be closed.
     * If the session is not null and connected, it will be disconnected.
     */
    public static void closeConnection1() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    /**
     * Initializes a MongoDB connection using the provided PropertyFactory and password.
     * If a connection already exists, it is closed before initializing a new one.
     *
     * @param propertyFactory The PropertyFactory containing the necessary properties for the connection.
     * @param password The password to use for the SSH connection.
     * @throws UnknownHostException If the host is unknown.
     */
    public static void initMongoDBConnection(PropertyFactory propertyFactory, String password)
            throws UnknownHostException {
        if (mongoClient != null) {
            closeMongoConnection();
        }
        sshUserName = propertyFactory.getProperty("SSH_UserName");
        sshHost = propertyFactory.getProperty("SSH_Host");
        // sshPassword = propertyFactory.getProperty( "SSH_Password" );
        sshPassword = password;
        mongoRHost = propertyFactory.getProperty("Mongo_DB_IP");
        mongoRPort = Integer.parseInt(propertyFactory.getProperty("Mongo_DB_PORT"));

        JSch jsch = new JSch();
        try {
            session = jsch.getSession(sshUserName, sshHost, sshPort);
            session.setConfig("StrictHostKeyChecking", "No");
            session.setPassword(sshPassword);
            session.connect(60000);
            session.setPortForwardingL(mongoLPort, mongoRHost, mongoRPort);
            log.debug(session.getPortForwardingL()[0]);
            log.debug("ssh " + sshUserName + "@" + sshHost + ":" + sshPort + "? " + session.isConnected());
        } catch (JSchException e) {
            e.printStackTrace();
            return;
        }
        mongoDatabase = propertyFactory.getProperty("Mongo_DB_NAME");
        mongoUser = propertyFactory.getProperty("Mongo_DB_USER_ID");
        mongoPassword = propertyFactory.getProperty("Mongo_DB_PASSWORD");
        String url = "mongodb://" + MongoHost + ":" + mongoLPort;
        mongoClient = new MongoClient(new MongoClientURI(url));
        mongodb = mongoClient.getDatabase(mongoDatabase);

        log.info("Mongo Connection to Mongo host " + url + " established? " + mongoClient.toString());
    }

    /*
     * public static void mongoDBConnection(PropertyFactory
     * propertyFactory) throws UnknownHostException {
     *
     * if (mongoClient != null) { closeMongoConnection(); } sshUserName =
     * propertyFactory.getProperty( "SSH_UserName" ); sshHost =
     * propertyFactory.getProperty( "SSH_Host" ); sshPassword =
     * propertyFactory.getProperty( "SSH_Password" ); mongoRHost =
     * propertyFactory.getProperty( "Mongo_DB_IP" ); mongoRPort = Integer.parseInt(
     * propertyFactory.getProperty( "Mongo_DB_PORT" ) );
     *
     * JSch jsch = new JSch(); try { session = jsch.getSession(sshUserName, sshHost,
     * sshPort); session.setConfig("StrictHostKeyChecking", "No");
     * session.setPassword(sshPassword); session.connect(60000);
     * session.setPortForwardingL(mongoLPort, mongoRHost, mongoRPort);
     * log.debug(session.getPortForwardingL()[0]);
     * log.debug("ssh "+sshUserName+"@"+sshHost+":"+sshPort+"? "+session.isConnected
     * ()); } catch (JSchException e) { e.printStackTrace(); return; }
     *
     *
     *
     * Block<Document> printBlock = new Block<Document>() {
     *
     * @Override public void apply(final Document document) {
     * System.out.println(document.toJson()); } };
     *
     * String mongoHost = "localhost"; String mongoPort = "27017"; String user =
     * "csmean"; String password = "Sdefsrte&dgsfdge4359dTc8767hjf&";
     *
     * String mongoCollection = "sms"; String mongoDatabase = "mean-dev";
     *
     * MongoClientURI uri = new
     * MongoClientURI("mongodb://"+user+":"+password+"@"+mongoHost+":"+mongoPort);
     * System.out.println(uri.toString()); mongoClient = new MongoClient(uri);
     * mongodb = mongoClient.getDatabase(mongoDatabase); MongoCollection<Document>
     * collection = mongodb.getCollection(mongoCollection);
     * collection.find().forEach(printBlock); log.info(
     * "Mongo Connection to Mongo database "+mongoDatabase+" established? " +
     * mongoClient.toString());
     *
     * }
     */

    /*
     ********************
     */

    /**
     * Initializes a connection to a MongoDB database using the provided PropertyFactory object.
     * If a connection already exists, it is closed before initializing a new one.
     * @param propertyFactory the PropertyFactory object containing the necessary properties for the connection
     * @throws UnknownHostException if the host is unknown
     */
    public static void initMongoIDDBConnection(PropertyFactory propertyFactory) throws UnknownHostException {

        if (mongoClient != null) {
            closeMongoConnection();
        }
        sshUserName = propertyFactory.getProperty("SSH_UserName");
        sshHost = propertyFactory.getProperty("SSH_Host");
        sshPassword = propertyFactory.getProperty("SSH_Password");
        mongoDatabase = propertyFactory.getProperty("Mongo_DB_DATABASE_NAME");
        mongoDBname = propertyFactory.getProperty("Mongo_DB_DATABASE_NAME");
        mongoRHost = propertyFactory.getProperty("Mongo_DB_IP");
        mongoRPort = Integer.parseInt(propertyFactory.getProperty("Mongo_DB_PORT"));

        JSch jsch = new JSch();
        try {
            session = jsch.getSession(sshUserName, sshHost, sshPort);
            session.setConfig("StrictHostKeyChecking", "No");
            session.setPassword(sshPassword);
            session.connect(60000);
            session.setPortForwardingL(mongoLPort, mongoRHost, mongoRPort);
            log.info(session.getPortForwardingL()[0]);
            log.info("ssh " + sshUserName + "@" + sshHost + ":" + sshPort + "? " + session.isConnected());
        } catch (JSchException e) {
            e.printStackTrace();
            return;
        }
        List<ServerAddress> seeds = new ArrayList<ServerAddress>();
        seeds.add(new ServerAddress(MongoHost, mongoLPort));
        mongoUser = propertyFactory.getProperty("Mongo_DB_ID_USER_ID");
        mongoPassword = propertyFactory.getProperty("Mongo_DB_ID_PASSWORD");
        MongoCredential credential = MongoCredential.createCredential(mongoUser, mongoDBname,
                mongoPassword.toCharArray());
        MongoClientOptions options = new MongoClientOptions.Builder().build();
//        mongoClient = new MongoClient(seeds, credential, options);

        if (mongoDatabase != null) {
            mongodb = mongoClient.getDatabase(mongoDBname);
        }
    }

    /**
     * Initializes a MongoDB connection with the given host name, port, database name, user ID, and password.
     *
     * @param propertyFactory The property factory to use for retrieving SSH connection information.
     * @param Mongo_DB_DATABASE_NAME The name of the MongoDB database to connect to.
     * @param Mongo_DB_IP The IP address of the MongoDB server.
     * @param Mongo_DB_PORT The port number of the MongoDB server.
     * @param Mongo_DB_ID_USER_ID The user ID to use for authentication.
     * @param Mongo_DB_ID_USER_Password The password to use for authentication.
     * @throws UnknownHostException If the MongoDB server cannot be reached.
     */
    public static void initMongoIDDBConnectionWithHostName(PropertyFactory propertyFactory,
                                                           String Mongo_DB_DATABASE_NAME, String Mongo_DB_IP, int Mongo_DB_PORT, String Mongo_DB_ID_USER_ID,
                                                           String Mongo_DB_ID_USER_Password) throws UnknownHostException {

        if (mongoClient != null) {
            closeMongoConnection();
        }
        sshUserName = propertyFactory.getProperty("SSH_UserName");
        sshHost = propertyFactory.getProperty("SSH_Host");
        sshPassword = propertyFactory.getProperty("SSH_Password");
        mongoDatabase = Mongo_DB_DATABASE_NAME;
        mongoDBname = Mongo_DB_DATABASE_NAME;
        mongoRHost = Mongo_DB_IP;
        mongoRPort = Mongo_DB_PORT;

        JSch jsch = new JSch();
        try {
            session = jsch.getSession(sshUserName, sshHost, sshPort);
            session.setConfig("StrictHostKeyChecking", "No");
            session.setPassword(sshPassword);
            session.connect(60000);
            session.setPortForwardingL(mongoLPort, mongoRHost, mongoRPort);
            log.info(session.getPortForwardingL()[0]);
            log.info("ssh " + sshUserName + "@" + sshHost + ":" + sshPort + "? " + session.isConnected());
        } catch (JSchException e) {
            e.printStackTrace();
            return;
        }

        List<ServerAddress> seeds = new ArrayList<ServerAddress>();
        seeds.add(new ServerAddress(MongoHost, mongoLPort));
        mongoUser = Mongo_DB_ID_USER_ID;
        mongoPassword = Mongo_DB_ID_USER_Password;
        MongoCredential credential = MongoCredential.createCredential(mongoUser, "admin", mongoPassword.toCharArray());
        MongoClientOptions options = new MongoClientOptions.Builder().build();
        mongoClient = new MongoClient(seeds, Arrays.asList(credential), options);

        if (mongoDatabase != null) {
            mongodb = mongoClient.getDatabase(mongoDBname);
        }
    }

    /**
     * Initializes a MongoDB connection with the given host name, database name, IP address, remote and local ports, user ID and password.
     * If a connection already exists, it is closed before initializing a new one.
     * @param propertyFactory an instance of PropertyFactory
     * @param Mongo_DB_DATABASE_NAME the name of the MongoDB database
     * @param Mongo_DB_IP the IP address of the MongoDB database
     * @param Mongo_R_PORT the remote port of the MongoDB database
     * @param Mongo_L_PORT the local port of the MongoDB database
     * @param Mongo_DB_ID_USER_ID the user ID for the MongoDB database
     * @param Mongo_DB_ID_USER_Password the password for the MongoDB database
     * @throws UnknownHostException if the host is unknown
     */
    public static void initMongoIDDBConnectionWithHostName(PropertyFactory propertyFactory,
                                                           String Mongo_DB_DATABASE_NAME, String Mongo_DB_IP, int Mongo_R_PORT, int Mongo_L_PORT,
                                                           String Mongo_DB_ID_USER_ID, String Mongo_DB_ID_USER_Password) throws UnknownHostException {

        if (mongoClient != null) {
            closeMongoConnection();
        }
        sshUserName = propertyFactory.getProperty("SSH_UserName");
        sshHost = propertyFactory.getProperty("SSH_Host");
        sshPassword = propertyFactory.getProperty("SSH_Password");
        mongoDatabase = Mongo_DB_DATABASE_NAME;
        mongoDBname = Mongo_DB_DATABASE_NAME;
        mongoRHost = Mongo_DB_IP;
        mongoRPort = Mongo_R_PORT;

        JSch jsch = new JSch();
        try {
            session = jsch.getSession(sshUserName, sshHost, sshPort);
            session.setConfig("StrictHostKeyChecking", "No");
            session.setPassword(sshPassword);
            session.connect(60000);
            session.setPortForwardingL(Mongo_L_PORT, mongoRHost, mongoRPort);
            log.info(session.getPortForwardingL()[0]);
            log.info("ssh " + sshUserName + "@" + sshHost + ":" + sshPort + "? " + session.isConnected());

        } catch (JSchException e) {
            e.printStackTrace();
            return;
        }
        List<ServerAddress> seeds = new ArrayList<ServerAddress>();
        seeds.add(new ServerAddress(MongoHost, Mongo_L_PORT));
        mongoUser = Mongo_DB_ID_USER_ID;
        mongoPassword = Mongo_DB_ID_USER_Password;
        MongoCredential credential = MongoCredential.createCredential(mongoUser, "admin", mongoPassword.toCharArray());
        MongoClientOptions options = new MongoClientOptions.Builder().build();
//        mongoClient = new MongoClient(seeds, credential, options);

        if (mongoDatabase != null) {
            mongodb = mongoClient.getDatabase(mongoDBname);
        }
    }

    /*
     ********************
     */

    /**
     * Closes the MongoDB connection and session.
     * If the session is not null, it disconnects the session and prints "session connection closed!!".
     * If the session is null, it prints "session is already closed!!".
     * If the mongoClient is not null, it closes the mongoClient and prints "DB connection closed!!".
     * If the mongoClient is null, it prints "Connection is already closed!!".
     * If an exception occurs, it prints the stack trace.
     * @throws Exception if an error occurs while closing the connection or session.
     */
    public static void closeMongoConnection() {
        try {
            if (session != null) {
                session.disconnect();
                System.out.println("session connection closed!!");
            } else {
                System.out.println("session is already closed!!");
            }
            Thread.sleep(2000);
            if (mongoClient != null) {
                mongoClient.close();
                System.out.println("DB connection closed!!");
            } else {
                System.out.println("Connection is already closed!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes a connection to a MongoDB database using the provided PropertyFactory object.
     * If a connection already exists, it is closed before a new connection is established.
     *
     * @param propertyFactory the PropertyFactory object containing the necessary properties for establishing the connection
     * @throws UnknownHostException if the host is unknown
     */
    public static void initMongoIDDBConnectionMongoCollection(PropertyFactory propertyFactory)
            throws UnknownHostException {

        if (mongoClient != null) {
            closeMongoConnection();
        }
        sshUserName = propertyFactory.getProperty("SSH_UserName");
        sshHost = propertyFactory.getProperty("SSH_Host");
        sshPassword = propertyFactory.getProperty("SSH_Password");
        mongoDatabase = propertyFactory.getProperty("Mongo_DB_DATABASE_NAME");
        mongoDBname = propertyFactory.getProperty("Mongo_DB_DATABASE_NAME");
        mongoRHost = propertyFactory.getProperty("Mongo_DB_IP");
        mongoRPort = Integer.parseInt(propertyFactory.getProperty("Mongo_DB_PORT"));

        JSch jsch = new JSch();
        try {
            session = jsch.getSession(sshUserName, sshHost, sshPort);
            session.setConfig("StrictHostKeyChecking", "No");
            session.setPassword(sshPassword);
            session.connect(60000);
            session.setPortForwardingL(mongoLPort, mongoRHost, mongoRPort);
            log.info(session.getPortForwardingL()[0]);
            log.info("ssh " + sshUserName + "@" + sshHost + ":" + sshPort + "? " + session.isConnected());
        } catch (JSchException e) {
            e.printStackTrace();
            return;
        }
        List<ServerAddress> seeds = new ArrayList<ServerAddress>();
        seeds.add(new ServerAddress(MongoHost, mongoLPort));
        mongoUser = propertyFactory.getProperty("Mongo_DB_USER_ID");
        mongoPassword = propertyFactory.getProperty("Mongo_DB_PASSWORD");
        mongoDatabase = propertyFactory.getProperty("Mongo_DB_NAME");
        MongoCredential credential = MongoCredential.createCredential(mongoUser, mongoDBname,
                mongoPassword.toCharArray());
        MongoClientOptions options = new MongoClientOptions.Builder().build();
        //        mongoClient = new MongoClient(seeds, credential, options);

        if (mongoDatabase != null) {
            mongodb = mongoClient.getDatabase(mongoDatabase);
        }

    }

    /**
     * Initializes a MongoDB connection with the specified database name and collection.
     * @param propertyFactory an instance of PropertyFactory to retrieve necessary properties.
     * @param mongoDatabaseName the name of the MongoDB database to connect to.
     * @throws UnknownHostException if the host is unknown.
     */
    public static void initMongoIDDBConnectionWithCollection(PropertyFactory propertyFactory, String mongoDatabaseName)
            throws UnknownHostException {

        if (mongoClient != null) {
            closeMongoConnection();
        }
        sshUserName = propertyFactory.getProperty("SSH_UserName");
        sshHost = propertyFactory.getProperty("SSH_Host");
        sshPassword = propertyFactory.getProperty("SSH_Password");
        mongoDatabase = propertyFactory.getProperty("Mongo_DB_DATABASE_NAME");
        mongoDBname = propertyFactory.getProperty("Mongo_DB_DATABASE_NAME");
        mongoRHost = propertyFactory.getProperty("Mongo_DB_IP");
        mongoRPort = Integer.parseInt(propertyFactory.getProperty("Mongo_DB_PORT"));

        JSch jsch = new JSch();
        try {
            session = jsch.getSession(sshUserName, sshHost, sshPort);
            session.setConfig("StrictHostKeyChecking", "No");
            session.setPassword(sshPassword);
            session.connect(60000);
            session.setPortForwardingL(mongoLPort, mongoRHost, mongoRPort);
            log.info(session.getPortForwardingL()[0]);
            log.info("ssh " + sshUserName + "@" + sshHost + ":" + sshPort + "? " + session.isConnected());
        } catch (JSchException e) {
            e.printStackTrace();
            return;
        }
        List<ServerAddress> seeds = new ArrayList<ServerAddress>();
        seeds.add(new ServerAddress(MongoHost, mongoLPort));
        mongoUser = propertyFactory.getProperty("Mongo_DB_USER_ID");
        mongoPassword = propertyFactory.getProperty("Mongo_DB_PASSWORD");
        mongoDatabase = mongoDatabaseName;
        MongoCredential credential = MongoCredential.createCredential(mongoUser, mongoDBname,
                mongoPassword.toCharArray());
        MongoClientOptions options = new MongoClientOptions.Builder().build();
//        mongoClient = new MongoClient(seeds, credential, options);

        if (mongoDatabase != null) {
            mongodb = mongoClient.getDatabase(mongoDatabase);
        }

    }

    /**
     * Initializes a database connection with custom details.
     *
     * @param mySQLDBname the name of the MySQL database
     * @param MysqlUSer the username for the MySQL database
     * @param MySqlpwd the password for the MySQL database
     * @param mysqlDb the name of the MySQL database
     * @param propertyFactory the PropertyFactory object used to retrieve properties
     * @throws SQLException if a database access error occurs
     */
    public static void initConnectionWithCustomDBDeatils(String mySQLDBname, String MysqlUSer, String MySqlpwd,
                                                         String mysqlDb, PropertyFactory propertyFactory) throws SQLException {

        if (connection != null) {
            closeConnection();
        }
        sshHost = propertyFactory.getProperty("SSH_Host");
        if (sshHost != null && !sshHost.isEmpty()) {
            sshUserName = propertyFactory.getProperty("SSH_UserName");
            sshPassword = propertyFactory.getProperty("SSH_Password");
            RHost = mySQLDBname;
            mysqlHost = "127.0.0.1";
            JSch jsch = new JSch();
            try {
                session = jsch.getSession(sshUserName, sshHost, sshPort);
                session.setConfig("StrictHostKeyChecking", "No");
                session.setPassword(sshPassword);
                session.connect(60000);
                session.setPortForwardingL(LPort, RHost, RPort);
                log.debug(session.getPortForwardingL()[0]);
                log.debug("ssh " + sshUserName + "@" + sshHost + ":" + sshPort + "? " + session.isConnected());
            } catch (JSchException e1) {
                e1.printStackTrace();
                return;
            }
        } else {
            mysqlHost = propertyFactory.getProperty("DB_IP");
            LPort = 3310;
        }

        createDatabaseConnectionWithCustomdetails(MysqlUSer, MySqlpwd, mysqlDb);
    }

    /**
     * This method creates a database connection with custom details such as MySQL user, MySQL password, and MySQL database name.
     * @param MysqlUSer The MySQL user to connect to the database.
     * @param MySqlpwd The MySQL password to connect to the database.
     * @param mysqlDb The MySQL database name to connect to.
     * @throws SQLException If there is an error in creating the database connection.
     */
    private static void createDatabaseConnectionWithCustomdetails(String MysqlUSer, String MySqlpwd, String mysqlDb)
            throws SQLException {
        mysqlUser = MysqlUSer;
        mysqlPassword = MySqlpwd;
        databaseName = mysqlDb;
        String url = "jdbc:mysql://" + mysqlHost + ":" + LPort + "/" + databaseName;
        Properties properties = new Properties();
        properties.setProperty("user", mysqlUser);
        properties.setProperty("password", mysqlPassword);
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");

        connection = DriverManager.getConnection(url, properties);
        log.info("JDBC Connection to Database host " + url + " established? " + !connection.isClosed());
    }
}

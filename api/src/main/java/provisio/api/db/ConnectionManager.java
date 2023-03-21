package provisio.api.db;

import provisio.api.ApiApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    public static Connection getConnection() throws ClassNotFoundException {

        //connection details, easily modifiable
        final String dbURL = "jdbc:mysql://104.237.154.92/provisio?allowPublicKeyRetrieval=true&useSSL=false";
        final String dbUserName = ApiApplication.getDbUsername(); //loaded each time with startup
        final String dbPassword = ApiApplication.getDbPassword(); //loaded each time with startup

        //load jdbc driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        try {
            return DriverManager.getConnection(dbURL, dbUserName, dbPassword);
        } catch (SQLException e) {
            System.out.println("******************************");
            System.out.println("INVALID CREDENTIALS OR CONNECTION ERROR");
            System.out.println("******************************");
            throw new RuntimeException(e);
        }

    }

}

package provisio.api.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    /**
     *
     * @return a preconfigured connection object for the Provisio project's database.
     * @throws ClassNotFoundException when the JDBC driver is not in your classpath.
     */
    public static Connection getConnection() throws ClassNotFoundException {

        //connection details, easily modifiable
        final String dbURL = "jdbc:mysql://mysql.railway.internal/provisio";
        final String dbUserName = System.getenv("PROVISIO_USERNAME");
        final String dbPassword = System.getenv("PROVISIO_PASSWORD");

        //load jdbc driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        try {
            return DriverManager.getConnection(dbURL, dbUserName, dbPassword);
        } catch (SQLException e) {
            System.out.println("***************************************");
            System.out.println("INVALID CREDENTIALS OR CONNECTION ERROR");
            System.out.println("***************************************");
            throw new RuntimeException(e);
        }

    }

}

package provisio.api.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    public static Connection getConnection() throws ClassNotFoundException, SQLException {

        //connection details, easily modifiable
        final String dbURL = "jdbc:mysql://localhost/provisio";
        final String dbUserName = "provisio_user";
        final String dbPassword = "capstone";

        //load jdbc driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(dbURL, dbUserName, dbPassword);

    }

}

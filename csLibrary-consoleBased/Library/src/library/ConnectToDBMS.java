package library;
import java.sql.*;

// DB connection (TEST)
public class ConnectToDBMS {

	public static void main(String[] args) {
		String URL = "jdbc:mysql://127.0.0.1:3306/library";
        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Class Not Found : " + e.getMessage());
        }
        try {
            con = DriverManager.getConnection(URL, "Haebin", "Noh28899@@");
            System.out.println("Successfully connected to DBMS!");
            con.close();
        } catch (SQLException e) {
            System.err.println("SQL Error : " + e.getMessage());
        }
	}
}

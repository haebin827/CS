package library;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// DB connection
public class Dao {
	public Connection connectDB() {
		Connection con = null;
        String url = "jdbc:mysql://127.0.0.1:3306/library";
        String user = "Haebin";
        String passwd = "Noh28899@@";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, passwd);
        } catch (ClassNotFoundException e) {
            System.err.print("ClassNotFoundException: ");
            System.err.println(e.getMessage());
            return null;
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
        return con;       
	}
}

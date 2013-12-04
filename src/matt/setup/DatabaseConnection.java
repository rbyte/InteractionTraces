package matt.setup;

import java.sql.*;

import matt.parameters.Params;
import matt.parameters.Params.DatabaseAccessSetup;

public class DatabaseConnection {
	
	private static final boolean silent = true;
	
	private String url;
	private String user;
	private String pass;
	private String databaseName;
	
	java.sql.Connection con;
	
	public DatabaseConnection() throws ClassNotFoundException, SQLException {
		this(Params.databaseAccessSetup);
	}
	
	public DatabaseConnection(DatabaseAccessSetup dbas) throws ClassNotFoundException, SQLException {
		this.url = dbas.databaseURL;
		this.user = dbas.databaseUser;
		this.pass = dbas.databasePassword;
		this.databaseName = dbas.databaseName;
		
		establishDatabaseConnection();
	}
	
	public String getDatabaseName() {
		return this.databaseName;
	}
	
	public Connection getConnection() {
		return this.con;
	}
	
	private Connection establishDatabaseConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		
		Connection con = DriverManager.getConnection(this.url+this.databaseName, this.user, this.pass);
		if (!silent) {
        	Statement st = con.createStatement();
        	ResultSet rs = st.executeQuery("SELECT VERSION()");
        	
            if (rs.next()) {
                System.out.println("Connection established: "+rs.getString(1));
            }
		}
		this.con = con;
        return con;
	}
	
	public void closeDatabaseConnection() throws SQLException {
		this.con.close();
		if (!silent) {
			System.out.println("Closed database connection.");
		}
	}
	
	public Connection reopen() throws ClassNotFoundException, SQLException {
		closeDatabaseConnection();
		return establishDatabaseConnection();
	}
	
	public static boolean databaseIsAccessible(DatabaseAccessSetup dbas) {
		try {
			Connection con = DriverManager.getConnection(dbas.databaseURL+dbas.databaseName, dbas.databaseUser, dbas.databasePassword);
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}

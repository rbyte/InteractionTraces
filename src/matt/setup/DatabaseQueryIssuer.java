package matt.setup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import matt.parameters.Params;
import matt.parameters.Params.DatabaseAccessSetup;
import matt.util.StringHandling;

public class DatabaseQueryIssuer {

	private DatabaseConnection db;

	public DatabaseQueryIssuer() throws ClassNotFoundException, SQLException {
		this(Params.databaseAccessSetup);
	}

	public DatabaseQueryIssuer(DatabaseAccessSetup dbs) throws ClassNotFoundException, SQLException {
		this.db = new DatabaseConnection(dbs);
	}

	public DatabaseQueryIssuer(DatabaseConnection con) {
		this.db = con;
	}

	public DatabaseConnection getDatabaseConnection() {
		return this.db;
	}

	public void closeDatabaseConnection() throws SQLException {
		db.closeDatabaseConnection();
	}

	public Long countColumns(String tableName) {
		return getLong("SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '"
				+ tableName + "';");
	}

	/**
	 * @param query
	 *            May be the name of a table or a query that returns a table.
	 *            May not end on ";".
	 * @return The query results length / number of rows.
	 */
	public Long count(String query) {
		if (query.contains(" ")) {
			return getLong("SELECT COUNT(*) FROM (" + query + ") AS T1");
		} else {
			assert (!query.endsWith(";"));
			return getLong("SELECT COUNT(*) FROM " + query);
		}
	}

	public Long getLong(String query) {
		try {
			Statement st = this.db.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			Long count = 0L;
			if (rs.next()) {
				count = rs.getLong(1);
			}
			return count;
		} catch (SQLException e) {
			handleSQLException(e, query);
		}
		return 0L;
	}

	public String[] getColumnNames(String tableName) {
		return getStringArray(countColumns(tableName).intValue(),
				"SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '"
						+ tableName + "';");
	}

	public String[] getStringColumn(String tableName, String column) {
		return getStringArray(count(tableName).intValue(), "SELECT " + column
				+ " FROM " + tableName + ";");
	}

	public String[] getStringArray(String query) {
		return getStringArray(count(query).intValue(), query);
	}

	private String[] getStringArray(int size, String query) {
		try {
			String[] result = new String[size];

			Statement st = this.db.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			int i = 0;
			while (rs.next() && i < size) {
				result[i++] = rs.getString(1);
			}
			return result;
		} catch (SQLException e) {
			handleSQLException(e, query);
		}
		return new String[0];
	}

	public int[] getIntColumn(String tableName, String column) {
		return getIntArray(count(tableName).intValue(), "SELECT " + column
				+ " FROM " + tableName + ";");
	}

	private int[] getIntArray(int size, String query) {
		try {
			int[] result = new int[size];

			Statement st = this.db.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			int i = 0;
			while (rs.next() && i < size) {
				result[i++] = rs.getInt(1);
			}
			return result;
		} catch (SQLException e) {
			handleSQLException(e, query);
		}
		return new int[0];
	}

	public long[] getLongColumnAll(String tableName, String column) {
		return getLongArray(count(tableName).intValue(), "SELECT " + column
				+ " FROM " + tableName + ";");
	}

	public long[] getLongArray(String query) {
		return getLongArray(
				getLong("SELECT COUNT(*) FROM (" + query + ") AS T1")
						.intValue(), query);
	}

	private long[] getLongArray(int size, String query) {
		try {
			long[] result = new long[size];

			Statement st = this.db.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			int i = 0;
			while (rs.next() && i < size) {
				result[i++] = rs.getLong(1);
			}
			return result;
		} catch (SQLException e) {
			handleSQLException(e, query);
		}
		return new long[0];
	}

	public HashMap<String, String[]> getTable(String tableName) {
		return getTable(tableName, getColumnNames(tableName));
	}

	public HashMap<String, String[]> getTable(String tableName,
			String[] columnNames) {
		return getMap(count(tableName).intValue(),
				"SELECT " + StringHandling.concat(columnNames, ",") + " FROM "
						+ tableName + ";", columnNames);
	}

	public HashMap<String, String> getRow(String tableName,
			String[] columnNames, String id) {
		HashMap<String, String[]> ret = getMap(1,
				"SELECT " + StringHandling.concat(columnNames, ",") + " FROM "
						+ tableName + " WHERE id='" + id + "';", columnNames);
		assert (ret.size() == 1);
		HashMap<String, String> result = new HashMap<String, String>();
		for (String e : ret.keySet()) {
			result.put(e, ret.get(e)[0]);
		}
		return result;
	}

	public HashMap<String, String> getRow(String tableName, String id) {
		return getRow(tableName, getColumnNames(tableName), id);
	}

	private HashMap<String, String[]> getMap(int rows, String query,
			String[] columnNames) {
		try {
			HashMap<String, String[]> result = new HashMap<String, String[]>();
			for (String col : columnNames) {
				result.put(col, new String[rows]);
			}

			Statement st = this.db.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);

			int currentRow = 0;
			while (rs.next() && currentRow < rows) {
				for (int i = 0; i < columnNames.length; i++) {
					result.get(columnNames[i])[currentRow] = rs
							.getString(i + 1);
				}
				currentRow++;
			}
			return result;
		} catch (SQLException e) {
			handleSQLException(e, query);
		}
		return new HashMap<String, String[]>();
	}

	public void createTableIfNonexisting(String tableName, String sqlTableVarDef) {
		try {
			if (!tableExists(tableName)) {
				PreparedStatement prestmt = this.db.getConnection()
						.prepareStatement(
								"create table " + tableName + " ("
										+ sqlTableVarDef + ");");
				prestmt.executeUpdate();
				prestmt.close();
			}
		} catch (SQLException e) {
			handleSQLException(e);
		}
	}

	public boolean tableExists(String tableName) {
		try {
			Statement st = this.db.getConnection().createStatement();
			ResultSet rs = st.executeQuery("show tables like '" + tableName
					+ "';");
			return rs.next();
		} catch (SQLException e) {
			handleSQLException(e);
		}
		return false;
	}

	public void insertStringValuesIntoTable(String tableName, String[] values) {
		PreparedStatement prestmt;
		try {
			assert (tableExists(tableName));

			prestmt = this.db.getConnection().prepareStatement(
					"insert into " + this.db.getDatabaseName() + "."
							+ tableName + " values ("
							+ StringHandling.concat("?", values.length, ",")
							+ ");");
			for (int i = 0; i < values.length; i++) {
				prestmt.setString(i + 1, values[i]);
			}
			prestmt.executeUpdate();
			prestmt.close();
		} catch (SQLException e) {
			handleSQLException(e);
		}
	}

	public void insertLongValuesIntoTable(String tableName, Long[] values) {
		PreparedStatement prestmt;
		try {
			assert (tableExists(tableName));

			prestmt = this.db.getConnection().prepareStatement(
					"insert into " + this.db.getDatabaseName() + "."
							+ tableName + " values ("
							+ StringHandling.concat("?", values.length, ",")
							+ ");");
			for (int i = 0; i < values.length; i++) {
				prestmt.setLong(i + 1, values[i]);
			}
			prestmt.executeUpdate();
			prestmt.close();
		} catch (SQLException e) {
			handleSQLException(e);
		}
	}

	public void dropTable(String tableName) {
		if (tableExists(tableName)) {
			runQuery("DROP TABLE " + tableName);
		}
	}

	public void runQuery(String query) {
		PreparedStatement prestmt;
		try {
			prestmt = this.db.getConnection().prepareStatement(query);
			prestmt.executeUpdate();
			prestmt.close();
		} catch (SQLException e) {
			handleSQLException(e, query);
		}
	}

	public void createTableFromQuery(String tableName, String query) {
		dropTable(tableName);
		runQuery("CREATE TABLE " + tableName + " (" + query + ")");
	}

	private void handleSQLException(SQLException e) {
		e.printStackTrace();
	}

	private void handleSQLException(SQLException e, String query) {
		System.err.println(query);
		e.printStackTrace();
	}

}

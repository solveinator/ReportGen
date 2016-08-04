package application;

import java.sql.SQLException;

/* This class is designed to generate SQl Queries based on given criteria. The output is 
 * of the typical SQL form
 * SELECT colName1, colName2, ...
 * FROM tableName
 * WHERE value = someOtherValue
 * GROUP BY theme
 * HAVING value = someOtherValue
 * ORDER BY columnName
 */

public class Query {
	/*
	 * These include
	 * columnNames, 
	 */
	private String query;
	
	public Query(String col, String tbl, String where) throws SQLException {
		query = "SELECT " + col + " FROM " + tbl + " WHERE " + where;
	}
	public Query(String[] columns, String tableName, String[] where, String groupBy, 
			String[] having, String orderBy) throws SQLException {
		if(columns == null || tableName == null || tableName == "") {
			throw new SQLException();
		}
		else {
			query = "SELECT ";
			if(columns.length == 0) {
				query += "* ";
			}
			else {
				for(int i = 0; i < columns.length; i++) {
					query += columns[i] + " ";
				}
			}
			query += "FROM " + tableName + " ";				
			if(where.length > 0) {
				query += "WHERE ";
				for(int i = 0; i < where.length; i++){
					query += where[i] + " ";
				}
			}
			if(groupBy != "" & groupBy != null) {
				query += "GROUP BY " + groupBy + " ";
				if(having.length > 0) {
					query += "HAVING ";
					for(int i = 0; i < having.length; i++){
						query += having[i] + " ";
					}
				}
				if(orderBy != "" & orderBy != null) {
					query += "ORDER BY " + orderBy;
				}
			}
			query = query.trim();
			System.out.println(query);
		}
	
	
	
	}
	
	public String getQuery() {
		return query;
	}
	
	public static void main(String[] args) {
		String[] str = new String[2];
		try {
			Query testQ = new Query(str, "TableName", str, "", str, "");
		}
		catch(SQLException e) {
			System.out.print("SQLExpression misformed.");
		}
		str[0] = "Cows";
		str[1] = "Sheep";
		try {
			Query testQ = new Query(str, "TableName", str, "Cows", str, "");
		}
		catch(SQLException e) {
			System.out.print("Booooo.");
		}
		try {
			Query testQ = new Query("Cow", "Farm", "Cows=8");
			System.out.println(testQ.getQuery());
		}
		catch(SQLException e) {
			System.out.print("Booooo.");
		}
	}
}

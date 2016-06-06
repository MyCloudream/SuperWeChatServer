package cn.ucai.superwechat.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcUtils {
	static{
		/*
		 * 加载jdbc驱动程序
		 */
		try{
			Class.forName(PropertiesUtil.getValue("sqlDriver","jdbc.properties"));
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	public static Connection getConnection(){
		try{
			Connection connection = DriverManager.getConnection(
				PropertiesUtil.getValue("sqlUrl","jdbc.properties"));
			System.out.println("JdbcUtils*****connection="+connection);
			return connection;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	public static void closeAll(ResultSet set, PreparedStatement statement, Connection connection){
		try{
			if(set!=null){
				set.close();
			}
			if(statement!=null){
				statement.close();
			}
			if(connection!=null){
				connection.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
}

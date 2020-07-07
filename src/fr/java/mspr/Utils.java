package fr.java.mspr;

import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JComponent;


public class Utils {
	public static Classe_Utilisateur connectedVisitor;

	public static void setID(JComponent comp, String ID) {
		comp.putClientProperty("id", ID);
	}
	public static String getID(JComponent comp) {
		return String.valueOf(comp.getClientProperty("id"));
	}
	public static JComponent getCompByID(JComponent comp, String ID) {
		for(Component subcomp : comp.getComponents()) 
			if(getID((JComponent)subcomp).equals(ID))
				return (JComponent)subcomp;		
		return null;
	}
	public static ArrayList<ArrayList<String>> MysqlRequest(String request) {
		try {  
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection(  
					"jdbc:mysql://mysql-msprjava.alwaysdata.net:3306/msprjava_projet","msprjava","EpSi2020");  
			Statement stmt=con.createStatement();  
			if(request.startsWith("SELECT")) {
				ResultSet rs=stmt.executeQuery(request);  
				ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();			
				while(rs.next())  {
					int i = 1;
					ArrayList<String> row = new ArrayList<String>();

					while(true) {
						try {
							row.add(String.valueOf(rs.getObject(i)));
							i++;
						} catch(Exception e){ break; } 
					}

					result.add(row);
				}
				con.close();  
				return result;
			}
			else {
				try {
					int e = stmt.executeUpdate(request); 
					if(e == 0)
						return null;
					else
						return new ArrayList<ArrayList<String>>();
				}catch(Exception e){ System.out.println(e);}
			}


		}catch(Exception e){ System.out.println(e);}
		return null; 
	}
}

package com;

import static io.restassured.RestAssured.get;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.BeforeSuite;
import org.testng.Assert;
import org.testng.annotations.*;

import io.restassured.response.Response;

public class Testing {
	
	
	@BeforeSuite
		public Connection connecttodb() throws SQLException {
			
		String mysqlurl="jdbc:mysql://localhost:3306/gemini";
		Connection con = DriverManager.getConnection(mysqlurl, "root", "Mirchi@0711");
		
		System.out.println("Connection established......");
		return con;
		
		}
		
	@Test
		public void List_Countries_Presentin_API_but_Missingin_DB() throws SQLException, ParseException {
		// TODO Auto-generated method stub
		Connection con=connecttodb();
		
		Response resp=get("https://restcountries.eu/rest/v2/all");
		String data=resp.asString();
		
		List<String> dblist=new ArrayList<String>();
		List<String> jsonlist=new ArrayList<String>();
		
		
		Statement s=con.createStatement();
		ResultSet rs=s.executeQuery("select * from countries;");
		
		
		while(rs.next())
		{
			dblist.add(rs.getString("Country"));
		}	
		
		
		JSONParser parse= new JSONParser();
		JSONArray jarr=(JSONArray)parse.parse(data);
		for(Object obj:jarr) 
		{
			JSONObject record= (JSONObject)obj;
		
			jsonlist.add((String) record.get("name"));
		
		}
			
		int dbsize=dblist.size();
		int jsonsize=jsonlist.size();
		
		Assert.assertTrue(dbsize==jsonsize);
		
		jsonlist.removeAll(dblist);
		System.out.println(" List of all the countries which are missing in table: countries but present in API response: " +jsonlist);
		
		int newjsonsize=jsonlist.size();
		
		Assert.assertFalse(jsonsize==newjsonsize);
}
		



}

package com;

import static io.restassured.RestAssured.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.restassured.response.Response;

public class Json_to_database {

	public Connection connecttodb() throws SQLException, IOException {

		Properties prop=new Properties();
		FileInputStream fls=new FileInputStream(System.getProperty("user.dir")+"\\src\\test\\java\\com\\global.properties");
		prop.load(fls);
		String password=prop.getProperty("password");
		
		String mysqlurl = "jdbc:mysql://localhost:3306/gemini";
		Connection con = DriverManager.getConnection(mysqlurl, "root", password);
		System.out.println("Connection established......");
		return con;

	}

	public String apiResponse() {

		String URL = "https://restcountries.eu/rest/v2/all";
		Response resp = get(URL);
		String data = resp.asString();
		return data;

	}

	
	public void insertValuesInCountries() throws SQLException, ParseException, IOException {

		Connection con = connecttodb();
		String data = apiResponse();
		JSONParser parse = new JSONParser();
		JSONArray jarr = (JSONArray) parse.parse(data);

		String sql = "insert into countries (C_ID,Country,Capital, Currency_Code) values(?,?,?,?)";
		PreparedStatement pstmt = con.prepareStatement(sql);

		for (Object obj : jarr) // for each loop syntax
		{
			JSONObject record = (JSONObject) obj;

			pstmt.setString(1, (String) record.get("alpha3Code"));
			pstmt.setString(2, (String) record.get("name"));
			pstmt.setString(3, (String) record.get("capital"));
			JSONArray curraary = (JSONArray) record.get("currencies");

			JSONObject code = (JSONObject) curraary.get(0);
			pstmt.setString(4, (String) code.get("code"));
			
			pstmt.executeUpdate();
		}
		
		System.out.println("Record inserted into countries table");
		
	}

	public void insertValuesInBorders() throws SQLException, ParseException, IOException {
		
		Connection con = connecttodb();
		String data = apiResponse();
		JSONParser parse = new JSONParser();
		JSONArray jarr = (JSONArray) parse.parse(data);
		
		String sql2 = "insert into borders (C_ID,B_ID) values(?,?)";
		PreparedStatement pstmt = con.prepareStatement(sql2);

		for (int i = 0; i < jarr.size(); i++) {
			
			JSONObject record = (JSONObject) jarr.get(i);
			pstmt.setString(1, (String) record.get("alpha3Code"));
			JSONArray bordersarry = (JSONArray) record.get("borders");
				
			for (int j = 0; j < bordersarry.size(); j++) {
				
					Object list = bordersarry.get(j);
					pstmt.setString(2, (String) list);
					pstmt.executeUpdate();
					
			}

		}

		System.out.println("Records inserted into Borders table");

	}

}

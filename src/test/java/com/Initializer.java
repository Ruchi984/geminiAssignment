package com;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.json.simple.parser.ParseException;
import org.testng.annotations.BeforeSuite;

public class Initializer extends Json_to_database{
	
	public static String apiResponse;
	public static Connection sqldb;
	
	@BeforeSuite
	public void initialise() throws SQLException, ParseException, IOException
	{
		sqldb=connecttodb();
		apiResponse=apiResponse();
		//insertValuesInCountries();
		//insertValuesInBorders();
		
	}
	
	
	
	
	
}

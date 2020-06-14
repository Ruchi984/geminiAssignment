package com;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.testng.annotations.Test;

public class TestCases extends Initializer {

	@Test
	public void listCountriesPresentinAPIbutMissinginDB() throws SQLException, ParseException {
		// TODO Auto-generated method stub

		List<String> dblist = new ArrayList<String>();
		List<String> jsonlist = new ArrayList<String>();

		Statement s = sqldb.createStatement();
		ResultSet rs = s.executeQuery("select * from countries;");

		while (rs.next()) {
			dblist.add(rs.getString("Country"));
		}

		JSONParser parse = new JSONParser();
		JSONArray jarr = (JSONArray) parse.parse(apiResponse);
		for (Object obj : jarr) {
			JSONObject record = (JSONObject) obj;

			jsonlist.add((String) record.get("name"));

		}

		jsonlist.removeAll(dblist);
		System.out.println(" List of all the countries which are missing in table: countries but present in API response: "+ jsonlist);

	}

	@Test
	public void comparingCapitalData() throws ParseException, SQLException {

		JSONParser parse = new JSONParser();
		JSONArray jarr = (JSONArray) parse.parse(apiResponse);

		for (int i = 0; i < jarr.size(); i++) {
			String apiCountry;
			String apiCapital;
			String dbCapital;

			JSONObject record = (JSONObject) jarr.get(i);

			apiCountry = (String) record.get("name");
			apiCapital = (String) record.get("capital");

			String sql = "select Capital,Currency_Code from countries where Country=?";
			PreparedStatement pstmt = sqldb.prepareStatement(sql);
			pstmt.setString(1, apiCountry);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				dbCapital = rs.getString(1);

				if (!dbCapital.equals(apiCapital)) {
					System.out.println("Country Name: " + apiCountry);
					System.out.println("Data: Capital");
					System.out.println("API Value: " + apiCapital);
					System.out.println("DB Value: " + dbCapital);
					System.out.println();

					Assert.assertFalse(dbCapital.equals(apiCapital));
				}

			}

		}

	}

	@Test
	public void maxBordersComparison() throws ParseException, SQLException {

		JSONParser parse = new JSONParser();
		JSONArray jarr = (JSONArray) parse.parse(apiResponse);

		int totalsize = jarr.size();
		int apiarr[] = new int[totalsize];
		int dbarr[] = new int[totalsize];

		for (int i = 0; i < totalsize; i++) {

			JSONObject record = (JSONObject) jarr.get(i);
			String apiCountryCode = (String) record.get("alpha3Code");
			JSONArray bordersarray = (JSONArray) record.get("borders");

			int n = bordersarray.size();

			apiarr[i] = n;

			String sql = "select count(B_ID) from borders where C_ID=? ";

			PreparedStatement stamt = sqldb.prepareStatement(sql);
			stamt.setString(1, apiCountryCode);
			ResultSet rs = stamt.executeQuery();

			int k = 0;
			if (rs.next()) {

				k = rs.getInt(1);

			}

			dbarr[i] = k;

		}

		int dbmax = 0, apimax = 0;
		for (int i = 0; i < totalsize; i++) {
			if (dbarr[i] > dbmax) {
				dbmax = dbarr[i];
			}
			if (apiarr[i] > apimax) {
				apimax = apiarr[i];
			}
		}

		Assert.assertTrue(apimax == dbmax);

		if (apimax == dbmax) {
			System.out.println("Maximum bordering from API: " + apimax + " and from Database: " + dbmax + " mactches");
		}
	}

}

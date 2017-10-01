package uta.edu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/Alerts")
public class Alerts {
	Connection conn = null;
	ResultSet rs = null;
	Statement stmt = null;
	PreparedStatement prepStmt = null;
	String data = "";
	String lineSeperator = "#&#";
	String columentSeperator = "@&@";

	/* DB Connection */
	public Connection dbConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://hackutaedu.csdxgsoo7wtb.us-east-2.rds.amazonaws.com",
					"root", "root1234");
			return conn;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	@SuppressWarnings("unchecked")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/getUserInfo")
	public String getUserInfo(@QueryParam("uid") int uid) {
		try {
			conn = dbConnection();
			stmt = conn.createStatement();
			System.out.println("select * from hackutaedu.user_detail where id_User=" + uid + "");
			rs = stmt.executeQuery("select * from hackutaedu.user_detail where id_User=" + uid + "");

			while (rs.next()) {
				String userName = rs.getString("userName");
				String userContact = rs.getString("userPhone");
				String guardianName = rs.getString("userGaurdianName");
				String guardianContact = rs.getString("userGaurdianPhone");
				String userBloodGroup = rs.getString("userBloodGroup");
				int userAge = rs.getInt("userAge");
				String userMedicalDetails = rs.getString("userMedicalDetails");

				JSONObject user = new JSONObject();
				user.put("userName", userName);
				user.put("userPhone", userContact);
				user.put("userGuardianName", guardianName);
				user.put("userGuardianPhone", guardianContact);
				user.put("userBloodGroup", userBloodGroup);
				user.put("userAge", userAge);
				user.put("userMedicalDetails", userMedicalDetails);
				return user.toString();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "User Id not Present";
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/setAlert")
	public String setAlert(@QueryParam("lat") float lat, @QueryParam("lon") float log,
			@QueryParam("message") String message, @QueryParam("uid") int uid, @QueryParam("sos") String sos ,  @QueryParam("datetime") String datetime) {
		
		
	
		try {
			conn = dbConnection();
			stmt = conn.createStatement();
			
			
			
			if (sos.equalsIgnoreCase("yes") || sos.equalsIgnoreCase("y")) {
				String query = String.format(
						"insert into "
								+ "hackutaedu.emergencyRequestAlert(emergencyUserId, emergencyLocationLat, emergencyLocationLong, "
								+ "emergencyDateTime, emergencyMessage) values(%d, %f, %f, '%s', '%s')",
						uid, lat, log, datetime,message);
				System.out.println(query);
				stmt.executeUpdate(query);
			} else {
				String query = String.format(
						"insert into "
								+ "hackutaedu.generalRequestAlert(generalUserId, generalLocationLat, generalLocationLong, "
								+ "generalDateTime, generalMessage) values(%d, %f, %f, '%s', '%s')",
						uid, lat, log,datetime, message);
				System.out.println(query);
				stmt.executeUpdate(query);
			}
			return "Successful";

		} catch (SQLException e) {
			e.printStackTrace();
			return "Failed";
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/getAlertInfo")
	public String getAlertInfo(@QueryParam("alertid") int alertid, @QueryParam("sos") String sos) {
		try {
			conn = dbConnection();
			stmt = conn.createStatement();
			if (sos.equalsIgnoreCase("yes") || sos.equalsIgnoreCase("y")) {
				rs = stmt.executeQuery("select * from hackutaedu.emergencyRequestAlert where idEmergencyRequestAlert='"
						+ alertid + "'");
				while (rs.next()) {
					int emergencyUserId = rs.getInt("emergencyUserId");
					int emergencyLocationLat = rs.getInt("emergencyLocationLat");
					int emergencyLocationLong = rs.getInt("emergencyLocationLong");
					String emergencyDateTime = rs.getString("emergencyDateTime");
					String emergencyMessage = rs.getString("emergencyMessage");

					JSONObject user = new JSONObject();
					user.put("UserId", emergencyUserId);
					user.put("LocationLat", emergencyLocationLat);
					user.put("LocationLong", emergencyLocationLong);
					user.put("DateTime", emergencyDateTime);
					user.put("Message", emergencyMessage);
					return user.toString();

				}
			} else {
				rs = stmt.executeQuery(
						"select * from hackutaedu.generalRequestAlert where idGeneralRequestAlert='" + alertid + "'");
				while (rs.next()) {
					int generalUserId = rs.getInt("generalUserId");
					int generalLocationLat = rs.getInt("generalLocationLat");
					int generalLocationLong = rs.getInt("generalLocationLong");
					String generalDateTime = rs.getString("generalDateTime");
					String generalMessage = rs.getString("generalMessage");

					JSONObject user = new JSONObject();
					user.put("UserId", generalUserId);
					user.put("LocationLat", generalLocationLat);
					user.put("LocationLong", generalLocationLong);
					user.put("DateTime", generalDateTime);
					user.put("Message", generalMessage);
					return user.toString();

				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return "Alert Id not Present";
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/getAlerts")
	public String getAlerts(@QueryParam("lat") float lat, @QueryParam("lon") float lon) {
		try {
			conn = dbConnection();
			stmt = conn.createStatement();
			JSONArray results = new JSONArray();
			String query = String.format(
					"select idemergencyRequestAlert, emergencyLocationLat, emergencyLocationLong, emergencyDateTime,  emergencyMessage, emergencyUserId, userName"
					+ " from hackutaedu.emergencyRequestAlert JOIN hackutaedu.user_detail ON emergencyUserId = id_User  where emergencyLocationLat <= %f AND emergencyLocationLat >= %f AND emergencyLocationLong <= %f AND emergencyLocationLong >= %f",
					lat + 0.25, lat - 0.25, lon + 0.25, lon - 0.25);
			System.out.println(query);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				int alertId = rs.getInt("idemergencyRequestAlert");
				float locLat = rs.getFloat("emergencyLocationLat");
				float locLong = rs.getFloat("emergencyLocationLong");
				String dateTime = rs.getString("emergencyDateTime");
				String msg = rs.getString("emergencyMessage");
				String uname = rs.getString("userName");
				int uid = rs.getInt("emergencyUserId");


				JSONObject alert = new JSONObject();
				alert.put("alertID", alertId);
				alert.put("locLat", locLat);
				alert.put("locLong", locLong);
				alert.put("dateTime", dateTime);
				alert.put("msg", msg);
				alert.put("sos", "yes");
				alert.put("name", uname);
				alert.put("id_User", uid);

				results.add(alert);
			}
			String query1 = String.format(
					"select  idGeneralRequestAlert, generalLocationLat, generalLocationLong, generalDateTime, generalMessage, userName, generalUserId"
					+ " from hackutaedu.generalRequestAlert JOIN hackutaedu.user_detail ON generalUserId = id_User where generalLocationLat <= %f AND generalLocationLat >= %f AND generalLocationLong <= %f AND generalLocationLong >= %f",
					lat + 0.25, lat - 0.25, lon + 0.25, lon - 0.25);
			rs = stmt.executeQuery(query1);
			System.out.println(query1);
			while (rs.next()) {
				int alertId = rs.getInt("idGeneralRequestAlert");
				float locLat = rs.getFloat("generalLocationLat");
				float locLong = rs.getFloat("generalLocationLong");
				String dateTime = rs.getString("generalDateTime");
				String msg = rs.getString("generalMessage");
				String uname = rs.getString("userName");
				int uid = rs.getInt("generalUserId");

				JSONObject alert = new JSONObject();
				alert.put("alertID", alertId);
				alert.put("locLat", locLat);
				alert.put("locLong", locLong);
				alert.put("dateTime", dateTime);
				alert.put("msg", msg);
				alert.put("sos", "no");
				alert.put("name", uname);
				alert.put("id_User", uid);

				results.add(alert);
			}
			if(results.size() <= 0){
				return "Empty";
			} else {
				return results.toString();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "Empty";
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
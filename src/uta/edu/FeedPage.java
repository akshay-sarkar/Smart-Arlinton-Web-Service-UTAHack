package uta.edu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Path("/FeedWS")
public class FeedPage {
	
	static Connection conn = null;
	static ResultSet rs = null;
    static Statement stmt = null;
    java.sql.PreparedStatement prepStmt = null;
    String data="";

	/* DB Connection */
	public Connection dbConnection(){
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://hackutaedu.csdxgsoo7wtb.us-east-2.rds.amazonaws.com", "root", "root1234");
			return conn;
		}catch (ClassNotFoundException e) {
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
	@Path("/getFeeds")
	public String getFeed(){
		try {
			conn = dbConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT feedData.idfeedData, feedData.feedHeading,"
					+ " feedData.feedContent, feedData.feedSubmittedBy FROM hackutaedu.feedData");
			JSONArray  array = new JSONArray();
			while(rs.next()){
					JSONObject feedData = new JSONObject();
					feedData.put("feedHeading", rs.getString("feedHeading"));
					feedData.put("feedContent", rs.getString("feedContent"));
					array.add(feedData);
			}
			if(array.size() <= 0){
				return null;
			}
			return array.toString();
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/setFeed")
    public String setFeed(@QueryParam("title") String title,
                          @QueryParam("message") String message,
                          @QueryParam("sub") String submittedBy){
        try {
            conn = dbConnection();
            stmt = conn.createStatement();
            String ins_query = String.format("INSERT INTO hackutaedu.feedData(feedHeading, feedContent, feedSubmittedBy) " +
                    "VALUES('%s', '%s', '%s')", title, message, submittedBy);
            stmt.executeUpdate(ins_query);
            return "Success";
        }catch (SQLException e) {
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
}

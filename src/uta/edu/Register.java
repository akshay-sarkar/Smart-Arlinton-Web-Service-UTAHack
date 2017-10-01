package uta.edu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.simple.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/Register")
public class Register {
    Connection conn = null;
    ResultSet rs = null;
    Statement stmt = null;
    java.sql.PreparedStatement prepStmt = null;
    String data="";
    String lineSeperator="#&#";
    String columentSeperator = "@&@";

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

    /* Called during Student and Admin login */
    @SuppressWarnings("unchecked")
	@GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/CreateUser")
    public String CreateUser(@QueryParam("uname") String userName,
                        @QueryParam("uph") String phoneNo,
                        @QueryParam("gph") String guardianNo,
                        @QueryParam("age") int age,
                        @QueryParam("med") String medicalDetails,
                        @QueryParam("blood") String blood,
                        @QueryParam("ugname") String userGaurdianName
                        ){
        try {
            conn = dbConnection();
            stmt = conn.createStatement();
            
            String ins_query = String.format("INSERT INTO hackutaedu.user_detail(userName, userPhone, userGaurdianPhone, userBloodGroup, userAge, userMedicalDetails, userGaurdianName) " +
                    "VALUES('%s', '%s', '%s', '%s', %d, '%s', '%s')", userName, phoneNo, guardianNo, blood, age, medicalDetails, userGaurdianName) ;
            
            stmt.executeUpdate(ins_query);

            String sel_query = String.format("SELECT id_User FROM hackutaedu.user_detail WHERE userPhone = '%s'", phoneNo) ;
            
            rs = stmt.executeQuery(sel_query);
            
            if(rs.next()) {
                
            	String userId = rs.getString("id_User");
                JSONObject user = new JSONObject();
                user.put("id_User", userId);
                
                return  user.toString();
            }else{
            	return null;
            }
        }catch(java.sql.SQLIntegrityConstraintViolationException ex){
        	ex.printStackTrace();
            return "User Already Exist";
        }catch (SQLException ex) {
            ex.printStackTrace();
            return null;
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

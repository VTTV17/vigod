package utilities.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import pages.dashboard.SignupPage;

import java.sql.*;

import static utilities.account.AccountTest.*;

public class InitConnection {
	
	final static Logger logger = LogManager.getLogger(InitConnection.class);
	
    public java.sql.Connection createConnection() throws SQLException {
        String connectionUrl = "jdbc:postgresql://%s:%s/%s?user=%s&password=%s&loginTimeout=30".formatted(DB_HOST, DB_PORT, DB_DATABASE, DB_USER, DB_PASS);
        return DriverManager.getConnection(connectionUrl);
    }

    public String getActivationKey(String phoneNumber) throws SQLException {
        String query = "select * from \"gateway-services\".jhi_user ju where login = '%s'".formatted(phoneNumber);
        ResultSet resultSet = createConnection().prepareStatement(query).executeQuery();
        String key = null;
        while (resultSet.next()) {
        	key = resultSet.getString("activation_key");
        }
        logger.debug("Phone number to get activation key from: " + phoneNumber); 
        logger.info("Activation key retrieved: " + key); 
        return key;
    }     
    
    public String getResetKey(String phoneNumber) throws SQLException {
    	String query = "select * from \"gateway-services\".jhi_user ju where login = '%s'".formatted(phoneNumber);
    	ResultSet resultSet = createConnection().prepareStatement(query).executeQuery();
    	String key = null;
    	while (resultSet.next()) {
    		key = resultSet.getString("reset_key");
    	}
        logger.debug("Phone number to get reset key from: " + phoneNumber); 
    	logger.info("Reset key retrieved: " + key); 
    	return key;
    }     
    
    @Test
    public void test() throws SQLException {
        java.sql.Connection connection = createConnection();
        String query = "select activation_key from \"gateway-services\".jhi_user ju where login = '+500:8942531099'";
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getInt("activation_key"));
        }
    }
}

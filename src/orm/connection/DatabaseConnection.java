package orm.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Diego Karabin
 */
public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private Connection connection;
    private String host = "localhost";
    private String dbname = "proyectojava";
    private String user = "root";
    private String password = "12345678";
    
    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = (com.mysql.jdbc.Connection) DriverManager
                .getConnection("jdbc:mysql://" + host + '/' + dbname, user, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new  DatabaseConnection();
        } else if (instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        
        return instance;
    }
    
    public ResultSet execute(String query, Object[] data)
        throws SQLException
    {
        return execute(false, query, data);
    }
    
    public ResultSet execute(boolean receive, String query, Object[] data)
        throws SQLException
    {
        ResultSet rs = null;
        
        if(connection != null) {
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            
            if(data.length > 0)
                for(int i = 0; i < data.length; i++)
                    preparedStmt.setObject(i + 1, data[i]);
            
            if(receive)
                rs = preparedStmt.executeQuery();
            else
                preparedStmt.executeUpdate();
        }
        
        return rs;
    }

}

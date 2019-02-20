package orm.builders;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import orm.connection.DatabaseConnection;

public class Query {

    private DatabaseConnection databaseConnection;
    private String queryString;
    private ArrayList parameters = new ArrayList();

    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }

    public void setDatabaseConnection(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public ArrayList getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList parameters) {
        this.parameters = parameters;
    }

    public PreparedStatement getStatement() {
        PreparedStatement statement = null;
        
        try {
            statement = databaseConnection.getConnection().prepareStatement(this.queryString);
            
            if (!parameters.isEmpty()) {
                for (int i = 0; i < parameters.size(); i++) {
                    statement.setObject(i + 1, parameters.get(i));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return statement;
    }
    
}

package orm.builders;

import java.sql.SQLException;
import orm.connection.DatabaseConnection;
import orm.Model;

public abstract class QueryBuilder {
    
    private Query query;
    
    public <T extends Model> QueryBuilder() {
        try {
            this.query = new Query();
            this.query.setDatabaseConnection(DatabaseConnection.getInstance());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }
    
}

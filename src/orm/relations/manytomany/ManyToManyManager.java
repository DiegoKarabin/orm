package orm.relations.manytomany;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import orm.Model;
import orm.builders.InsertBuilder;
import orm.connection.DatabaseConnection;
import orm.relations.RelationManager;

public class ManyToManyManager<T extends Model> extends RelationManager {
    
    private String relationTable;
    private String refererIdColumn;
    private String referedIdColumn;
    private T objectRefered;
    
    public ManyToManyManager(Class<T> refered, Class<T> referer, String relationTable,
        String refererIdColumn, String referedIdColumn, T objectRefered)
    {
        this.setRefered(refered);
        this.setReferer(referer);
        this.setRelationTable(relationTable);
        this.setReferedIdColumn(referedIdColumn);
        this.setRefererIdColumn(refererIdColumn);
        this.objectRefered = objectRefered;
    }

    public String getRelationTable() {
        return relationTable;
    }

    public void setRelationTable(String relationTable) {
        this.relationTable = relationTable;
    }

    public String getRefererIdColumn() {
        return refererIdColumn;
    }

    public void setRefererIdColumn(String refererIdColumn) {
        this.refererIdColumn = refererIdColumn;
    }

    public String getReferedIdColumn() {
        return referedIdColumn;
    }

    public void setReferedIdColumn(String referedIdColumn) {
        this.referedIdColumn = referedIdColumn;
    }

    @Override
    public T[] all() {
        ArrayList<T> outputArray = new ArrayList();
        
        try {
            String sql = String.format(
                "SELECT %s FROM %s WHERE %s = ?",
                this.refererIdColumn,
                this.relationTable,
                this.referedIdColumn
            );
            
            ResultSet results = DatabaseConnection.getInstance()
                .execute(true, sql, new Object[] {this.objectRefered.id});
            
            while (results.next()) {
                int refererId = results.getInt(this.refererIdColumn);
                
                T refererObject = Model.find(refererId, (Class<T>) this.getReferer());
                outputArray.add(refererObject);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        T[] layoutArray = (T[]) Array.newInstance(this.getReferer(), outputArray.size());
        return outputArray.toArray(layoutArray);
    }
    
    public <T extends Model> void attach(T object) {
        try {
            attachWithColumns(object).getQuery().getStatement().executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public <T extends Model> InsertBuilder attachWithColumns(T object) {
        InsertBuilder insertBuilder = new InsertBuilder(new Model() {});
        insertBuilder.setTableName(relationTable);
        insertBuilder.addField(referedIdColumn, this.objectRefered.id);
        insertBuilder.addField(refererIdColumn, object.id);
        
        return insertBuilder;
    }
    
}

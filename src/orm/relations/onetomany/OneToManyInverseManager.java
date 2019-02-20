package orm.relations.onetomany;

import java.sql.ResultSet;
import java.sql.SQLException;
import orm.Model;
import orm.connection.DatabaseConnection;

public class OneToManyInverseManager<T extends Model> {
    
    private T refererObject;
    private String foreignKeyColumn;
    private Class<T> refered;
    private Class<T> referer;

    public OneToManyInverseManager(Class<T> refered,
        Class<T> referer, String foreignKeyColumn, T refererObject)
    {
        this.refered = refered;
        this.referer = referer;
        this.setForeignKeyColumn(foreignKeyColumn);
        this.refererObject = refererObject;
    }

    public String getForeignKeyColumn() {
        return foreignKeyColumn;
    }

    public void setForeignKeyColumn(String foreignKeyColumn) {
        this.foreignKeyColumn = foreignKeyColumn;
    }
    
    public <T extends Model> T get() {
        try {
            String sql = String.format(
                "SELECT %s FROM %s WHERE id = ?",
                foreignKeyColumn,
                referer.newInstance().getTableName()
            );
            
            ResultSet result = DatabaseConnection.getInstance()
                .execute(true, sql, new Object[] {refererObject.id});
            int foreignKeyValue = 0;
            
            if (result.next()) {
                foreignKeyValue = result.getInt(foreignKeyColumn);
            }
            
            return Model.find(foreignKeyValue, (Class<T>) refered);
        } catch (InstantiationException | IllegalAccessException | SQLException ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
}

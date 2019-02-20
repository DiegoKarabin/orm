package orm.relations.onetomany;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import orm.Model;
import orm.builders.InsertBuilder;
import orm.builders.SelectBuilder;
import orm.builders.UpdateBuilder;
import orm.relations.RelationManager;

public class OneToManyManager extends RelationManager {
    
    private String foreignKeyColumn;
    private Object foreignKeyValue;

    
    public OneToManyManager(Class<? extends Model> refered, Class<? extends Model> referer,
        String foreignKeyColumn, Object foreignKeyValue)
    {
        this.setRefered(refered);
        this.setReferer(referer);
        this.setForeignKeyColumn(foreignKeyColumn);
        this.setForeignKeyValue(foreignKeyValue);
    }

    public String getForeignKeyColumn() {
        return foreignKeyColumn;
    }

    public void setForeignKeyColumn(String foreignKeyColumn) {
        this.foreignKeyColumn = foreignKeyColumn;
    }

    public Object getForeignKeyValue() {
        return foreignKeyValue;
    }

    public void setForeignKeyValue(Object foreignKeyValue) {
        this.foreignKeyValue = foreignKeyValue;
    }
    
    public <T extends Model> T[] all() {
        ArrayList<T> outputArray = new ArrayList();
        
        try {
            SelectBuilder selectBuilder = new SelectBuilder(this.getReferer());
            selectBuilder.where(this.foreignKeyColumn, this.foreignKeyValue);
            ResultSet results = selectBuilder.getQuery().getStatement().executeQuery();

            while (results.next()) {
                T model = (T) this.getReferer().newInstance();
                String[] columns = model.getColumnsName();
                
                model.id = results.getInt("id");
                
                for (String column : columns) {
                    model.getFieldByName(column).set(model, results.getObject(column));
                }

                outputArray.add(model);
            }
        } catch (InstantiationException | IllegalAccessException | SQLException ex) {
            ex.printStackTrace();
        }
        
        T[] layoutArray = (T[]) Array.newInstance(this.getReferer(), outputArray.size());

        return outputArray.toArray(layoutArray);
    }

    
    public <T extends Model> void attach(T object) {        
        try {
            T model = T.find(object.id, (Class<T>) object.getClass());

            if (model == null) {
                (new InsertBuilder(object)).addField(foreignKeyColumn, foreignKeyValue)
                                           .getQuery()
                                           .getStatement()
                                           .executeUpdate();
            } else {
                (new UpdateBuilder(object)).addField(foreignKeyColumn, foreignKeyValue)
                                           .where("id", object.id)
                                           .getQuery()
                                           .getStatement()
                                           .executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
}

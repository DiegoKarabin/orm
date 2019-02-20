package orm.builders;

import java.util.ArrayList;
import orm.Model;

public class InsertBuilder extends QueryBuilder {
    
    private ArrayList<String> fields = new ArrayList();
    private ArrayList values = new ArrayList();
    private String tableName;
    
    public <T extends Model> InsertBuilder(T model) {
        super();
        
        this.tableName = model.getTableName();
        
        try{
            String[] columns = model.getColumnsName();
            
            for (String column : columns) {
                fields.add(column);
                values.add(model.getFieldByName(column).get(model));
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        
        this.constructQuery();
    }

    public ArrayList<String> getFields() {
        return fields;
    }

    public void setFields(ArrayList<String> fields) {
        this.fields = fields;
    }

    public ArrayList getValues() {
        return values;
    }

    public void setValues(ArrayList values) {
        this.values = values;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public InsertBuilder addField(String fieldName, Object value) {
        this.fields.add(fieldName);
        this.values.add(value);
        this.constructQuery();
        
        return this;
    }
    
    private void constructQuery() {
        String sql = String.format(
            "INSERT INTO %s (",
            this.tableName
        );
        
        int fieldsQty = this.fields.size();
        
        for (int i = 0; i < fieldsQty; i++) {
            sql += this.fields.get(i);
            sql += i == fieldsQty - 1 ? ')' : ", ";
        }
        
        sql += " VALUES (";
        ArrayList parameters = new ArrayList();
        
        for (int i = 0; i < fieldsQty; i++) {
            sql += '?';
            parameters.add(this.values.get(i));
            sql += i == fieldsQty - 1 ? ')' : ", ";
        }
        
        this.getQuery().setQueryString(sql);
        this.getQuery().setParameters(parameters);
    }
    
}

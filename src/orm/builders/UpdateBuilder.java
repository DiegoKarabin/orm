package orm.builders;

import java.util.ArrayList;
import orm.Model;

public class UpdateBuilder extends WhereableQueryBuilder {
    
    private ArrayList<String> fields = new ArrayList();
    private ArrayList values = new ArrayList();
    private String tableName;

    public <T extends Model>UpdateBuilder(T model) {
        super();
        
        this.tableName = model.getTableName();

        try {
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
    
    private void constructQuery() {
        String sql = String.format(
                "UPDATE %s SET ",
                this.tableName
        );

        int fieldsQty = this.fields.size();
        this.getQuery().setParameters(new ArrayList());

        for (int i = 0; i < fieldsQty; i++) {
            sql += this.fields.get(i);
            sql += i == fieldsQty - 1 ? "" : "= ?, ";
            this.getQuery().getParameters().add(this.values.get(i));
        }

        this.getQuery().setQueryString(sql);
    }
    
    public UpdateBuilder addField(String fieldName, Object value) {
        this.fields.add(fieldName);
        this.values.add(value);
        this.constructQuery();

        return this;
    }
    
}

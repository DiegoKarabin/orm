package orm;

import orm.connection.DatabaseConnection;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import orm.annotations.Column;
import orm.annotations.Id;
import orm.annotations.Table;
import orm.builders.InsertBuilder;
import orm.builders.SelectBuilder;
import orm.builders.UpdateBuilder;
import orm.relations.manytomany.ManyToManyManager;
import orm.relations.onetomany.OneToManyInverseManager;
import orm.relations.onetomany.OneToManyManager;

public abstract class Model {
    public int id;
    
    public static <T extends Model> T find (int id, Class<T> modelSubClass) {
        try {
            ResultSet result;
            T instance = modelSubClass.newInstance();
            String[] columnsName = instance.getColumnsName();
            SelectBuilder queryBuilder = new SelectBuilder(modelSubClass);
            
            queryBuilder.where("id", id);
            result = queryBuilder.getQuery().getStatement().executeQuery();
            
            if (result.first()) {
            instance.id = result.getInt("id");
            
            for (String column : columnsName) {
            Field field = instance.getFieldByName(column);
            field.set(instance, result.getObject(column));
            }
            
            return instance;
            }
        } catch (InstantiationException | IllegalAccessException |
                 SQLException ex)
        {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    public static <T extends Model> T[] all(Class<T> modelSubClass) {
        ArrayList<T> outputArray = new ArrayList();
        
        try {
            SelectBuilder queryBuilder = new SelectBuilder(modelSubClass);
            ResultSet results = queryBuilder.getQuery().getStatement().executeQuery();
            
            while (results.next()) {
                T model = modelSubClass.newInstance();
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
            
        T[] layoutArray = (T[]) Array.newInstance(modelSubClass, outputArray.size());
        
        return outputArray.toArray(layoutArray);
    }
    
    public void save() {
        try {
            String idFieldName = this.getIdField();
            
            int idValue;
            
            if (!idFieldName.equals("id")) {
                idValue = this.getClass().getField(idFieldName).getInt(this);
            } else {
                idValue = this.id;
            }            
            
            Model model = Model.find(idValue, this.getClass());
            
            if (model == null) {
                (new InsertBuilder(this)).getQuery()
                                         .getStatement()
                                         .executeUpdate();
                
                String sql = String.format(
                    "SELECT MAX(id) as new_id FROM %s",
                    this.getTableName()
                );
                ResultSet result = DatabaseConnection.getInstance()
                        .execute(true, sql, new Object[] {});
                
                if (result.next()) {
                    this.id = result.getInt("new_id");
                }
            } else {
            (new UpdateBuilder(this)).where("id", idValue)
                                     .getQuery()
                                     .getStatement()
                                     .executeUpdate();
            }
        } catch (NoSuchFieldException | IllegalAccessException | SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void delete() {
        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Model model = Model.find(this.id, this.getClass());
            
            if (model != null) {
                String sql = String.format(
                    "DELETE FROM %s ",
                    model.getTableName()
                );
                
                sql += "WHERE id = ?";
                
                databaseConnection.execute(sql, new Object[] {this.id});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public <T extends Model> OneToManyManager hasMany(Class<T> modelSubClass) {
        String foreignKey = String.format(
            "%s_id",
            this.getClass().getSimpleName().toLowerCase()
        );
    
        return hasMany(modelSubClass, foreignKey);
    }
    
    public <T extends Model> OneToManyManager hasMany(Class<T> modelSubClass,
        String foreignKey)
    {
        return new OneToManyManager(this.getClass(), modelSubClass, foreignKey, this.id);
    }
    
    public <T extends Model> OneToManyInverseManager belongsToOne(Class<T> modelSubClass) {
        String foreignKey = String.format(
            "%s_id",
            modelSubClass.getSimpleName().toLowerCase()
        );
    
        return belongsToOne(modelSubClass, foreignKey);
    }
    
    public <T extends Model> OneToManyInverseManager belongsToOne(Class<T> modelSubClass,
        String foreignKey)
    {
        return new OneToManyInverseManager(modelSubClass, this.getClass(), foreignKey, this);
    }
    
    public <T extends Model> ManyToManyManager belongsToMany(Class<T> modelSubClass)
    {
        String tableName = this.getClass().getSimpleName().toLowerCase();
        String refererTableName = modelSubClass.getSimpleName().toLowerCase();
        String relationTableName;
        
        if (tableName.compareTo(refererTableName) < 0) {
            relationTableName = String.format(
                    "%s_%s",
                    tableName, refererTableName
            );
        } else {
            relationTableName = String.format(
                    "%s_%s",
                    refererTableName, tableName
            );
        }
        return belongsToMany(modelSubClass, relationTableName);
    }
    
    public <T extends Model> ManyToManyManager belongsToMany(Class<T> modelSubClass,
        String relationTable)
    {
        String modelName = this.getClass().getSimpleName().toLowerCase() + "_id";
        String refererName = modelSubClass.getSimpleName().toLowerCase() + "_id";
        
        return belongsToMany(modelSubClass, relationTable, refererName, modelName);
    }
    
    public <T extends Model> ManyToManyManager belongsToMany(Class<T> modelSubClass,
        String relationTable, String refererIdColumn, String referedIdColumn)
    {
        return new ManyToManyManager(this.getClass(), modelSubClass, relationTable, refererIdColumn, referedIdColumn, this);
    }
    
    public String getTableName() {
        Annotation[] annotations = this.getClass().getAnnotations();
        
        if (annotations.length > 0) {
            Table tableAnnotation = (Table) annotations[0];
            return tableAnnotation.name();
        }
        
        return this.getClass().getSimpleName().toLowerCase() + 's';
    }
    
    public String[] getColumnsName() {
        Field[] fields = this.getClass().getDeclaredFields();
        ArrayList<String> columnsName = new ArrayList();
        
        for (Field field : fields) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            
            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name();
                
                columnsName.add(
                    columnName.equals("")
                    ? field.getName()
                    : columnName
                );
            }
        }
        
        return columnsName.toArray(new String[] {});
    }
    
    public String getIdField() {
        Field[] fields = this.getClass().getDeclaredFields();
        
        for (Field field : fields) {
            Id idAnnotation = field.getAnnotation(Id.class);
            
            if (idAnnotation != null) {
                String idName = idAnnotation.name();
                
                return idName.equals("") ? field.getName() : idName;
            }
        }
        
        return "id";
    }
    
    public Field getFieldByName(String name) {
        Field[] fields = this.getClass().getDeclaredFields();
        
        for (Field field : fields) {
            Column annotation;

            if ((annotation = field.getAnnotation(Column.class)) != null) {
                if (annotation.name().equals(name) || field.getName().equals(name)) {
                    return field;
                }
            }
        }
        
        return null;
    }

}

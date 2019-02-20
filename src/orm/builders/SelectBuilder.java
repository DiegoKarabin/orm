package orm.builders;

import orm.Model;

public class SelectBuilder extends WhereableQueryBuilder {

    public <T extends Model> SelectBuilder(Class<T> modelSubClass) {
        super();
        
        try {
            Model model = modelSubClass.newInstance();
            
            String queryString = "SELECT id, ";
            String[] columns = model.getColumnsName();

            for (int i = 0; i < columns.length; i++) {
                queryString += columns[i];
                queryString += i == columns.length - 1 ? ' ' : ", ";
            }

            queryString += String.format(
                "FROM %s",
                model.getTableName()
            );

            this.getQuery().setQueryString(queryString);
        } catch (InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
    
    public SelectBuilder orderBy(String ... columns) {
        if (columns.length > 0) {
            String queryString = super.getQuery().getQueryString();
            queryString += " ORDER BY ";
            
            for (int i = 0; i < columns.length; i++) {
                queryString += columns[i];
                queryString += i == columns.length - 1 ? "" : ", ";
            }
            
            super.getQuery().setQueryString(queryString);
        }
        
        return this;
    }
    
    public SelectBuilder groupBy(String ... columns) {
        if (columns.length > 0) {
            String queryString = super.getQuery().getQueryString();
            queryString += " GROUP BY ";

            for (int i = 0; i < columns.length; i++) {
                queryString += columns[i];
                queryString += i == columns.length - 1 ? "" : ", ";
            }

            super.getQuery().setQueryString(queryString);
        }
        
        return this;
    }
    
}

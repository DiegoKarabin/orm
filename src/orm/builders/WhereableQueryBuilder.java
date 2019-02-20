package orm.builders;

import orm.Operator;

public abstract class WhereableQueryBuilder extends QueryBuilder {
    
    public WhereableQueryBuilder() {
        super();
    }
    
    public WhereableQueryBuilder where(String column, Operator operator, Object second) {
        String queryString = super.getQuery().getQueryString();
        
        queryString += 
            !super.getQuery().getQueryString().contains("WHERE")
            ? " WHERE"
            : " AND";
        queryString += String.format(" (%s %s ?)", column, operator.getValue());
        this.getQuery().setQueryString(queryString);
        this.getQuery().getParameters().add(second);
        
        return this;
    }
    
    public WhereableQueryBuilder where(String column, Object second) {
        return where(column, Operator.EQUALS, second);
    }
    
}

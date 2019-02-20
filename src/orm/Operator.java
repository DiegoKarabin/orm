package orm;

public enum Operator {
    
    EQUALS("="), LOWER_THAN("<"), GREATER_THAN(">"), LOWER_EQUAL("<="),
    GREATER_EQUAL(">="), UNLIKE("!="), DISTINC("<>"), LIKE("LIKE"), AND("AND"),
    OR("OR");
    
    private String value;
    
    Operator(String value)
    {
        this.value = value;
    }
    
    public String getValue()
    {
        return this.value;
    }
}

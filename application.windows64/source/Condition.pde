class Condition {
    String col = null;
    String operator = null;
    float value = -1; 
    String string_value = null; 
    ArrayList<String> string_values = null;

    // create a new Condition object that specifies some data column
    // should have some relationship to some value
    //   col: column name of data the relationship applies to
    //   op: operator (e.g. "<=")
    //   value: value to compare to
    Condition(String col, String op, float value) {
        this.col = col;
        this.operator = op;
        this.value = value;
    }

    Condition(String col, String op, String string_value) {
        this.col = col;
        this.operator = op;
        this.string_value = string_value;
    }

    Condition(String col, String op, ArrayList<String> string_values) {
        this.col = col;
        this.operator = op;
        this.string_values = string_values;
    }
    
    public String toString() {
        return col + " " + operator + " " + value + " " + string_value;
    }
    
    boolean equals(Condition cond){
        return operator.equals(cond.operator) && 
        value == cond.value && 
        col.equals(cond.col);
    }
}


boolean checkConditions(Condition[] conds, TableRow row) {
    if(conds == null || row == null){
        return false;
    }
    boolean and = true;
    for (int i = 0; i < conds.length; i++) {
        and = and && checkCondition(conds[i], row);
    }
    return and;
}

boolean checkCondition(Condition cond, TableRow row) {
    if (cond.operator.equals("=")) { // no need to know col
        float cur = row.getFloat(cond.col);
        return abs(cur - cond.value) < 0.0001;
    }
    if (cond.operator.equals("<=")) {
        float cur = row.getFloat(cond.col);
        return cur - cond.value <= 0.0001;
    }

    if (cond.operator.equals(">=")) {
        float cur = row.getFloat(cond.col);
        return cur - cond.value >= -0.0001;
    }

    if (cond.operator.equals("equals")) {
        String cur = row.getString(cond.col);
        return cur.equals(cond.string_value);
    }

    if (cond.operator.equals("oneOf")) {
        String cur = row.getString(cond.col);
        for (int i = 0; i < cond.string_values.size(); i++) {
            if (cur.equals(cond.string_values.get(i))) return true;
        }
    }

    return false;
}

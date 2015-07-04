Table _loadTable(String file, String options)
{ Table t = new Table(file,options); return t; }

class Table {
    ArrayList<TableRow> data;
    ArrayList<String> header;
    String delimiter = ",";

    int numrows;
  
    Table(String filename) {
        initialize(filename, "");
    }
  
    Table(String filename, String option) {
        initialize(filename, option);
    }

    Table() {
        header = new ArrayList<String>();
        data = new ArrayList<TableRow>();
    }

    void initialize(String filename, String options) {
        String[] lines = loadStrings(filename);
        int numcols = 0;
        numcols = lineSplit(lines[0], options).length; 

        int start = 0;
        if(options.contains("header")) {
            header = new ArrayList<String>(); 
            String[] labels = lineSplit(lines[0], options);
            for (int i = 0; i < labels.length; i++) {
                header.add(i, labels[i]);
            }
            start++;
        } else {
            header = null;
        }

        numrows = lines.length-start;
       // println("num rows: " + numrows);

        data = new ArrayList<TableRow>(numrows);
            for( int i=start; i<lines.length; i++) {
                String[] cells = lineSplit(lines[i], options);
                String[] _header = (String []) header.toArray(new String[0]);
                data.add(i-start, new TableRow(cells, _header));
        }
    }

    public String[] getColumnTitles() {
        return (String []) header.toArray(new String[0]);
    }

    public int getRowCount() {
        return numrows;
    }

    public int getColumnCount() {
        return header.size();
    }

    public String[] getStringColumn(String label) {
        String[] stringCol = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            stringCol[i] = data.get(i).getString(label);
        }
        return stringCol;
    }

    public float[] getFloatColumn(String label) {
        float[] floatCol = new float[data.size()];
        for (int i = 0; i < data.size(); i++) {
            float test = data.get(i).getFloat(label);
         //   println(test + " " + label);
            floatCol[i] = data.get(i).getFloat(label);
        }
        return floatCol;
    }

    public String getString(int index, String col) {
        return data.get(index).getString(col);
    }

    public float getFloat(int index, String col) {
        return data.get(index).getFloat(col);
    } 
    public int getInt(int index, String col) {
    	return data.get(index).getInt(col);
    }

    public int[] getIntColumn(String col) {
    	int[] column = new int[numrows];
    	for (int i = 0; i < numrows; i++) {
    		column[i] = (int)data.get(i).getInt(col);
    	}
    	return column;
    }

    public TableRow[] rows() {
        return (TableRow []) data.toArray(new TableRow[0]);
    }

    private String[] lineSplit(String line, String options) {
        if(options.contains("tsv")) { 
            return splitTokens(line);
        } else {
            return split(line, ',');
        }
    }

    /*
    public void addColumn(String label) {
        header.add(label);
        println(data);

        for (TableRow row : data) {
            row.addColumn(label);
        }
    }
    */

    public TableRow addRow() {
        String[] labels = (String []) header.toArray(new String[0]);

        TableRow row = new TableRow(new String[labels.length], labels);

        data.add(row);
        numrows++;
        return row;
    }

    public TableRow getRow(int index) {
        return data.get(index);
    }
}

class TableRow {
    ArrayList<String> labels;
    ArrayList<String> data;
  
    TableRow(String[] D, String[] L) {
        if( L != null ) {
            labels = new ArrayList<String>(L.length);
            for (int i = 0; i < L.length; i++) {
                labels.add(i, L[i]);
            }
        } else {
            labels = null; 
        }
    //    println("length: " + D.length);
        data = new ArrayList<String>(D.length);
      //  println (data);
        for (int i = 0; i < D.length; i++) {
            data.add(i, D[i]);
        }
        // for (String s : labels) {
        //     println("table row" + s);
        // }
    }

    void addColumn(String label) {
        labels.add(label);
    //    println("adding" + label);
        data.add("");
    }

    String getString(String label) {
        for (String s : labels) {
            // println("looking for " + label);
            // print(s + " ");
        }
        if (labels != null) {
          for (int i = 0; i < labels.size(); i++) {
        //     println("looking for " + label);
          //   println("the label is " + labels.get(i));
                if(labels.get(i).equals(label)) { 
                    return data.get(i);
                }
            }
        } else {
         //   println("Table does not have headers.");
        }
      //  println("label "+label+" not found");
        return null;
    }

    int getInt(String label) {
        return parseInt(getString(label));
    } 

    float getFloat(String label) {
        String f = getString(label);
        if (f != null) {
            return parseFloat(f);
        } else {
      //      println("Float is null");
            return -1;
        }
    }

    void setFloat(String label, float value) {
        if (labels != null) {
          for (int i = 0; i < labels.size(); i++) {
                if(labels.get(i).equals(label)) { 
                    data.set(i, nf(value, 1, 1));
                }
            }
        } else {
       //     println("Table does not have headers.");
        }
      //  println("label "+label+" not found");
    }

    public void printRow() {
        for (String s : data) {
            print(s + ", ");
        }
   //     println("");
    }
}

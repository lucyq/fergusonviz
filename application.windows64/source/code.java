import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class code extends PApplet {

//import java.lang.Float;

public void setup() {
    size(pw, ph);
    //size(displayWidth, displayHeight);
    background(backgroundColor);
    initSettings();
}

public void draw() {
    background(backgroundColor);

    pushStyle();
    textSize(24);
    fill(0);
    textAlign(CENTER);

    textFont(titleFont);
    fill(color(240));
    text("#FERGUSON REACTIONS ON TWITTER", width / 2, margin);
    popStyle();


    textLeading(lineHeight);

    contrl.hover();
    contrl.drawViews();
    contrl.drawInfoView();
}

public void initSettings() {
    parse("dummy.csv");
//    parse("../data/completedata.csv");

    marks = new boolean[data.getRowCount()];
    contrl = new SPLOMController();
    contrl.initViews();
    contrl.cleanSelectedArea();
}


public void keyPressed() {
    if (keyCode == UP) {
        opacity += 20;
    } else if (keyCode == DOWN) {
        opacity -= 20;
    } else if (keyCode == RIGHT) {
        toggle_state = 1; // log
        current_max = log_max;
    } else if (keyCode == LEFT) {
        toggle_state = 0; // linear
        current_max = linear_max;
    }
    if (opacity <= 0) opacity = 0;
    if (opacity >= 255) opacity = 255;
    lineColor = color(195, 97, 97, opacity);
}

public void mouseClicked() {
    contrl.mouseClicked();
}

public void parse(String filename) {
   data = _loadTable(filename, "header");
   titles = data.getColumnTitles();
}
abstract class AbstractView {
    protected float leftX = -1;
    protected float leftY = -1;
    protected float w = -1;
    protected float h = -1;

    protected Controller contrl = null;
    protected String name = null;
    protected Table data = null;
    
    protected boolean[] marks = null;
    protected String[] header = null;

    public abstract void hoverDots();
    public abstract void mouseClicked();
    public abstract void hover();
    public abstract void display();

    AbstractView() {
    }

    public AbstractView setController(Controller contrl) {
        this.contrl = contrl;
        return this;
    }

    public AbstractView setMarks(boolean[] ms) {
        this.marks = ms;
        return this;
    }

    public AbstractView setName(String name) {
        this.name = name;
        return this;
    }

    public AbstractView setDataSrc(Table t, String[] str, boolean[] marks) {
        this.data = t;
        this.header = str;
        this.marks = marks;
        return this;
    }

    public AbstractView setPosition(float x, float y) { 
        this.leftX = x;
        this.leftY = y;
        return this;
    }

    public AbstractView setSize(float w, float h) {
        this.w = w;
        this.h = h;
        return this;
    }  

    public float getXPosition() {
        return this.leftX;
    }

    public AbstractView setXPosition(float x) {
        this.leftX = x;
        return this;
    }

    public void sendMsg(Message msg) {
        if(contrl != null){
            contrl.receiveMsg(msg);
        }
    }
    
    public boolean isOnMe() {
        return mouseX >= leftX && mouseX <= (leftX + w) && mouseY >= leftY && mouseY <= (leftY + h);
    }

    public boolean isIntersected(Selection rect1, Selection rect2) {
       boolean flag1 = abs(rect2.p2.x + rect2.p1.x - rect1.p2.x - rect1.p1.x) - (rect1.p2.x - rect1.p1.x + rect2.p2.x - rect2.p1.x) <= 0;
       boolean flag2 = abs(rect2.p2.y + rect2.p1.y - rect1.p2.y - rect1.p1.y) - (rect1.p2.y - rect1.p1.y + rect2.p2.y - rect2.p1.y) <= 0;
       return flag1 && flag2;
    }

    public Selection getIntersectRegion(Selection rect) {
        Selection rect2 = new Selection(leftX, leftY, leftX + w, leftY + h);
        return getIntersectRegion(rect, rect2);
    }

    private Selection getIntersectRegion(Selection rect1, Selection rect2){
          if(isIntersected(rect1, rect2)){
              float x1 = max(rect1.p1.x, rect2.p1.x);
              float y1 = max(rect1.p1.y, rect2.p1.y);
              float x2 = min(rect1.p2.x, rect2.p2.x);
              float y2 = min(rect1.p2.y, rect2.p2.y);
              return new Selection(x1, y1, x2, y2);
          }
          return null;
     }
}
class Circle {
	float x, y;
	float radius;
	int _color;
	int index;
	int day_id;

	Circle(){};

	Circle(float x, float y, float radius, int _color, int index, int day_id) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.index = index;
		this._color = _color;
		this.day_id = day_id;
	}

	public Circle set (float x, float y, float radius, int _color, int index, int day_id) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this._color = _color;
		this.index = index;
		this.day_id = day_id;
		return this;
	}

	public boolean isWithin() {
		return pow(mouseX - x, 2) + pow(mouseY - y, 2) < pow(radius, 2);
	}

	public void draw() {
		fill(_color);
		ellipse(x, y, 2 * radius, 2 * radius);
	}

	public void change_color(int new_color) {
		_color = new_color;
	}


}
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
    
    public boolean equals(Condition cond){
        return operator.equals(cond.operator) && 
        value == cond.value && 
        col.equals(cond.col);
    }
}


public boolean checkConditions(Condition[] conds, TableRow row) {
    if(conds == null || row == null){
        return false;
    }
    boolean and = true;
    for (int i = 0; i < conds.length; i++) {
        and = and && checkCondition(conds[i], row);
    }
    return and;
}

public boolean checkCondition(Condition cond, TableRow row) {
    if (cond.operator.equals("=")) { // no need to know col
        float cur = row.getFloat(cond.col);
        return abs(cur - cond.value) < 0.0001f;
    }
    if (cond.operator.equals("<=")) {
        float cur = row.getFloat(cond.col);
        return cur - cond.value <= 0.0001f;
    }

    if (cond.operator.equals(">=")) {
        float cur = row.getFloat(cond.col);
        return cur - cond.value >= -0.0001f;
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
//import java.util.Iterator;

abstract class Controller {
    protected ArrayList < AbstractView > vizs = null;
    protected Selection selectArea = null;
    protected Message preMsg = null;

    public abstract void receiveMsg(Message msg);
    public abstract void initViews();
    public abstract void drawInfoView();
    public abstract void mouseClicked();

    public void hover() {
        Message msg = new Message();
            msg.setSource("controller");
            msg.setAction("clean");
        receiveMsg(msg);
       
        for (int i = 0; i < vizs.size(); i++) {
            AbstractView curr_viz = vizs.get(i);
            if (curr_viz.isOnMe()) {
                curr_viz.hover();
                curr_viz.hoverDots();
                break;
            }
        }
    }


    public void drawViews() {
        for (int i = 0; i < vizs.size(); i++) {
            vizs.get(i).display();
        }
    }

    public void cleanSelectedArea() {
        if (selectArea != null) {
            Message msg = new Message();
            msg.setSource("controller")
                .setAction("clean");
            receiveMsg(msg);
            selectArea = null;
        }
    }

    public void setSelectedArea(float x, float y, float x1, float y1) {
        selectArea = new Selection(x, y, x1, y1);
    }


    public void resetMarks() {
        // marks are global
        marks = new boolean[data.getRowCount()];
    }

    public void setMarksOfViews(){
        for (int i = 0; i < vizs.size(); i++) {
            vizs.get(i).setMarks(marks);
        }
    }
}

class SPLOMController extends Controller {

    InfoView infoView = new InfoView();
    SPLOMController() {
        vizs = new ArrayList < AbstractView > ();
        selectArea = null;
    }

    public void mouseClicked() {
        infoView.mouseClicked();
    }


    private int dayToCol(int day) {
        return (day * 6) + 1;
    }

    private void addDay(int day) {
        int col = dayToCol(day);
        int num_cols = data.getColumnCount();
        float w = ((width * .67f) - 2 * margin) / (num_days - 1);

        ParallelCoordsView pcView = new ParallelCoordsView();
            pcView
                .setController(this)
                .setName("Parallel")
            //    .setName("Parallel " + titles[col] + " v. " + titles[dayToCol(current_day + 1)])
                .setSize(w, (height * .85f) - (2 * margin))
                .setPosition((width * .33f) + w * current_day + margin, (height * .1f) + margin)
                .setMarks(marks);
            pcView.setData(data.getIntColumn(titles[col]),
                           data.getIntColumn(titles[dayToCol(current_day + 1)]), current_day + 1,
                           data.getStringColumn(titles[0]))
                .setTitles(titles[col], titles[dayToCol(current_day + 1)])
                .initRange()
                .displayRight(true);
            vizs.add(pcView);
    }

    private void subtractDay() {
        vizs.remove(vizs.size() - 1);
    }

    public void initViews() {
        float w = 5;
        ParallelCoordsView pcView = new ParallelCoordsView();
            pcView
                .setController(this)
                .setName("Parallel")
                .setSize(w, (height * .85f) - (2 * margin))
                .setPosition((width * .33f) + margin, (height * .1f) + margin)
                .setMarks(marks);
            pcView.setData(data.getIntColumn(titles[dayToCol(current_day)]),
                           data.getIntColumn(titles[dayToCol(current_day + 1)]), current_day,
                           data.getStringColumn(titles[0]))
                .setTitles(titles[dayToCol(current_day)], titles[dayToCol(current_day + 1)])
                .initRange()
                .displayLeft(true)
                .displayRight(false)
                .drawLines(false);
            vizs.add(pcView);


        StackedBarView sbView = new StackedBarView();
        sbView
            .setController(this)
            .setName("StackedBar")
            .setSize((width * .33f) - margin, (height * .44f) - (2 * margin))
            .setPosition(margin, (height * .37f) + (margin / 2))
            .setDataSrc(data, null, marks);

        vizs.add(sbView);


        // Tweet View
        TweetView twView = new TweetView();
        twView
            .setController(this)
            .setName("Tweet")
            .setSize((width * .33f) - margin, (height * .30f) - (2 * margin))
            .setPosition(margin, (height * .70f) + (margin / 2))
            .setDataSrc(data, null, marks);

        vizs.add(twView);

        // Info View
        infoView
            .setController(this)
            .setSize((width * .33f) - margin, (height * .38f) - (.1f * height) - margin)
            .setPosition(margin, (height * .1f) + (margin));
    }

    public void drawInfoView() {
        infoView.display();
    }

    public void receiveMsg(Message msg) {
        if (msg.equals(preMsg)) {
            return;
        }

        preMsg = msg;

        if (msg.action.equals("clean")) {
            resetMarks();
            setMarksOfViews();
            return;
        }

        if (msg.action.equals("previous")) {
            if (current_day >= first_day) {
                subtractDay();
            }
            return;
        }

        if (msg.action.equals("next")) {
            if (current_day <= last_day) {
                addDay(current_day);
            }
            return;
        }

        /* iterates through all rows and check conditions. sets marks accordingly */
        int index = 0;
        for (int i = 0; i < data.getRowCount(); i++) {
            // get row
            TableRow curr_row = data.getRow(i);
            if (checkConditions(msg.conds, curr_row)) {
                marks[index] = true;
            }
            index++;
        }
        setMarksOfViews();
    }
}
class InfoView extends AbstractView {

	Rectangle nextButton = null; 
	Rectangle prevButton = null; 

    public void display() {
    	fill(baseColor);
        noStroke();
    	rect(this.leftX, this.leftY, this.w, this.h); // border
        renderTitle(headlines[current_day]);
        renderText(content[current_day]);
    	drawButtons();
     };

    public void hoverDots() {};
    public void hover() {};


    public void mouseClicked() {
        if(nextButton.isWithin()) {
            if (current_day < last_day) {
                Message message = new Message();
                message.setSource("InfoView");
                message.setAction("next");
                sendMsg(message); 
                current_day++;
            }
        }
        if(prevButton.isWithin()) {
            if (current_day > first_day) {
                Message message = new Message();
                message.setSource("InfoView");
                message.setAction("previous");
                sendMsg(message); 
                current_day--;
            }
        }
    }

    private void renderTitle(String title) {
        pushStyle();
        textSize(18);
        textFont(headingFont);
        textAlign(CENTER);
        fill(255);
        text(title, this.leftX, this.leftY + 20, this.w, 80);
        popStyle();
    }

    private void renderText(String s) {
        pushStyle();
        fill(255);
        textSize(12);
        textFont(generalFont);
        textAlign(LEFT);
        text(s, this.leftX + 15, this.leftY + 60, this.w - 40, 100);
        popStyle();
    }

	private void drawButtons() {
       
        if (nextButton == null) {
            float width = w * .15f;
            float height = h * .20f;
          
         
            nextButton = new Rectangle(leftX + width + 10, leftY - height - 10, 
                                       width, height, lineColor, "Next", true);
            prevButton = new Rectangle(leftX, leftY - height - 10, 
                                       width, height, lineColor, "Previous", true);
           
        }

        nextButton._color = buttonColor;
        prevButton._color = buttonColor;
        if (nextButton.isWithin()) {
            nextButton._color = highlight_red;
        }
        if (prevButton.isWithin()) {
            prevButton._color = highlight_red;
        } 
        pushStyle();
        textSize(20);
        textFont(buttonFont);
		nextButton.drawButton();
        prevButton.drawButton();
        popStyle();
	}
  
}
class Message {
    String src = null;
    Condition[] conds = null;
    String action = "normal";

    Message() {
    }

    public Message setSource(String str) {
        this.src = str;
        return this;
    }

    public Message setAction(String str) {
        this.action = str;
        return this;
    }

    public Message setConditions(Condition[] conds) {
        this.conds = conds;
        return this;
    }

    public boolean equals(Message msg) {
        if (msg == null) {
            return false;
        }
        if (src == null && msg.src == null) {
            return true;
        }
        if (src == null || msg.src == null) {
            return false;
        }
        if (!src.equals(msg.src)) {
            return false;
        }
        if (conds != null && msg.conds != null) {
            if (conds.length != msg.conds.length) {
                return false;
            }
            for (int i = 0; i < conds.length; i++) {
                if (!conds[i].equals(msg.conds[i])) {
                    return false;
                }
            }
            return true;
        } else {
            if (conds == null && msg.conds == null) {
                return true;
            } else {
                return false;
            }
        }
    }

    public String toString() {
        String str = "";
        for (int i = 0; i < conds.length; i++) {
            str += conds[i].toString();
        }

        return str + "\n\n";
    }
}
class ParallelCoordsView extends AbstractView {
    int[] left = null;
	int[] right = null;
    String[] words = null;
    float frame = 0;
    float rate = 20;
    Circle[] axisDots;
    int id = -1;
 
	int rightMin = -1;
	int rightMax = -1;
	int leftMin = -1;
	int leftMax = -1;

    int num_ticks = 8;

	String leftTitle = null;
	String rightTitle = null;

	boolean drawLeft = false;
	boolean drawRight = false;
    boolean drawLines = true;

    public void display() {
        pushStyle();
        initRange();
        displayLeftAxis();  
        displayRightAxis(); 
        displayAxisLabels();   
        displayAxisDots(); 

        if (drawLines) {
            if (frame < rate) {
                frame++;
            }
            makeLines(frame/rate);
        }
        
        /* Point Highlighting */
        for (int i = 0; i < right.length; i++) {
             if (marks[i]) {
                noStroke();
                axisDots[i].change_color(lineHighlight); 
                axisDots[i].draw();
                break;
             }
        }
        popStyle();
        if (isOnMe()) {
            pushStyle();
            for (int i = 0; i < right.length; i++) {
                fill(255);
                if (marks[i]) {
                   
                    textFont(headingFont);
                    
                    text(words[i], mouseX - 10, mouseY - 15);
                    text(words[i], mouseX - 10, mouseY - 15);
                    break;
                }
            }
            popStyle();
        }
    };

    private void displayRightAxis() {
            pushStyle();
            fill(0);
            textAlign(CENTER, CENTER);
            if (drawRight) {
                stroke(axisColor);
                line(leftX + w, leftY, leftX + w, leftY + h);
                int date = 23 + id;
                String label = "11/" + date;
                fill(255);
                textFont(generalFont);
                text(label, leftX + w, leftY + h + 10);
            }

            for (int i = 0; i <= num_ticks; i++) {
                float prop = PApplet.parseFloat(i) / PApplet.parseFloat(num_ticks);
                line(leftX + w - 4, leftY + h * prop, leftX + w + 4, leftY + h * prop);
            }
            
            popStyle();
    }
    
    private void displayLeftAxis() {
            pushStyle();
           
            textAlign(CENTER, CENTER);
                if (drawLeft) {
                    stroke(axisColor);
                    line(leftX, leftY, leftX, leftY + h);
                    int date = 23 + id;
                    String label = "11/" + date;
                    fill(255);
                    textFont(generalFont);
                    text(label, leftX, leftY + h + 10);
                }

            for (int i = 0; i <= num_ticks; i++) {
                float prop = PApplet.parseFloat(i) / PApplet.parseFloat(num_ticks);
                line(leftX - 4, leftY + h * prop, leftX + 4, leftY + h * prop);
            }
            popStyle();
    }

    private void makeLines(float delta) {
        for (int i = 0; i < left.length; i++) {
            if (marks[i]) {
                stroke(lineHighlight);
                strokeWeight(3);
            } else {
                strokeWeight(1);
            //    stroke(colorPalette[i]);
                stroke(lineColor);
            }
            smooth();
            
            float startX = leftX;
            float endX = leftX + (this.w * delta);
            
            float left_value = left[i];
            float right_value = right[i];
            
            if (toggle_state == 1) {
                left_value = (float)Math.log(left_value);
                right_value = (float)Math.log(right_value);
            }
            
            float startY = leftScale(left_value);           
            float endY = (rightScale(right_value) * delta) + ((1 - delta) * startY);

            line(startX, startY, endX, endY);
        }
    }

    public void mouseClicked(){};

    public void hoverDots() {
        String word = "";

        for (int i = 0; i < axisDots.length; i++) {
            Circle curr_circ = axisDots[i];
            if (curr_circ.isWithin()) {
                word = words[curr_circ.index];
                hoverDot = true;
                hover_day = curr_circ.day_id;
                Condition cond0 = new Condition("Word", "equals", word);
                Condition[] conds = new Condition[1];
                conds[0] = cond0;

                Message message = new Message();
                message.setSource("hoverDots");
                message.setConditions(conds);
                sendMsg(message);
                break;
            }
            hoverDot = false;
        }
    }
 
    public void hover() {
        String word = "";
        for (int i = 0; i < left.length; i++) {
            float left_value = left[i];
            float right_value  = right[i];
            if (toggle_state == 1) {
                left_value = (float)Math.log(left_value);
                right_value = (float)Math.log(right_value);
            }
            if (isOnLine(leftX, leftScale(left_value),
                         leftX + w, rightScale(right_value))) {
                word = words[i];
                break;
            }
        }
        Condition cond0 = new Condition("Word", "equals", word);
        Condition[] conds = new Condition[1];
        conds[0] = cond0;

        Message message = new Message();
        message.setSource("hover");
        message.setConditions(conds);
        sendMsg(message);
    };

    private boolean isOnLine(float x1, float y1, 
                             float x2, float y2) {
        float m = (y2 - y1) / (x2 - x1);
        float b = y1 - (m * x1);

        return isWithinEpsilon(mouseY, (m * mouseX) + b, 4.0f);
    }

    private boolean isWithinEpsilon(float a, float b, float epsilon) {
        return Math.abs(a - b) <= epsilon;
    }

    public ParallelCoordsView setData(int[] left, int [] right, int id, String[] words) {
        this.left = new int[left.length - 2];
        this.right = new int[right.length - 2];
        this.id = id;
        this.words = words;

        this.left = left;
        this.right = right;

        this.axisDots = new Circle[left.length];
        for (int i = 0; i < axisDots.length; i++) {
            axisDots[i] = new Circle();
        }
        return this;
    };

    public ParallelCoordsView setTitles(String left, String right) {
    	this.leftTitle = left;
    	this.rightTitle = right;

    	return this;
    }

    public ParallelCoordsView initRange() {
    	this.leftMin = 0;
    	this.leftMax = current_max;
    	this.rightMin = 0;
    	this.rightMax = current_max;

        return this;
    }


    public ParallelCoordsView displayRight(boolean drawRight) {
    	this.drawRight = drawRight;
    	return this;
    }

    public ParallelCoordsView displayLeft(boolean drawLeft) {
    	this.drawLeft = drawLeft;

    	return this;
    }

    public ParallelCoordsView drawLines(boolean drawLines) {
        this.drawLines = drawLines;

        return this;
    }


    private float rightScale(float y) {
    	return leftY + h - ((y - rightMin) / (rightMax - rightMin) * h);
    }

    private float leftScale(float y) {
    	return leftY + h - ((y - leftMin) / (leftMax - leftMin) * h);
    }

    //fade out/fade in
    private void displayAxisLabels() {
        pushStyle();
        for (int i = 0; i <= num_ticks; i++) {
            float prop = PApplet.parseFloat(i) / PApplet.parseFloat(num_ticks);
            fill(255);
            textFont(generalFont);
            textSize(9);
            int display_val = 0;
            if (drawLeft) {
                line(leftX - 4, leftY + h * prop, leftX + 4, leftY + h * prop);
                float range = leftMax - leftMin;
                display_val = PApplet.parseInt(leftMax - (prop * range));
                if (toggle_state == 1) {
                    display_val = (int)Math.pow(Math.E, display_val);
                }
                text(display_val, leftX - 35, leftY + h * prop);
            }
            if (drawRight) {
                line(leftX + w - 4, leftY + h * prop, leftX + w + 4, leftY + h * prop);
                float range = rightMax - rightMin;
                display_val = PApplet.parseInt(rightMax - (prop * range));
                  if (toggle_state == 1) {
                    display_val = (int)Math.pow(Math.E, display_val);
                }
                // only draw labels on left axis
                // text(display_val, leftX + w - 35,
                //     leftY + h * prop);
            }
        }
        popStyle();
    }

    private void displayAxisDots() {
        pushStyle();
        if (drawLeft) {
            for (int i = 0; i < left.length; i++) {

                noStroke();
                float left_value = left[i];
                if (toggle_state == 1) {
                    left_value = (float)Math.log(left_value);
                }
               // color dot_color = setGradient(i);
               // axisDots[i].set(leftX, leftScale(left_value), 5, colorPalette[i], i)
                 //          .draw();
                axisDots[i].set(leftX, leftScale(left_value), 5, lineColor, i, id)
                           .draw();
            }
        }
        
        if (frame == rate) {
            if (drawRight) {
                for (int i = 0; i < right.length; i++) {
                    noStroke();
                    float right_value = right[i];
                    if (toggle_state == 1) {
                        right_value = (float)Math.log(right_value);
                    }
                 //   color dot_color = setGradient(i);
                 //   axisDots[i].set(leftX + w, rightScale(right_value), 5, colorPalette[i], i)
                   //            .draw();
                    axisDots[i].set(leftX + w, rightScale(right_value), 5, lineColor, i, id)
                               .draw();
                
                }
            }
        }
        popStyle();
    }
}
class Rectangle {
    float x, y; //position of top left corner
    float width;
    float height;
    int _color;
    String text;
    boolean showText;
    String id;

    Rectangle(float x, float y, float width, float height, 
        int _color, String text, boolean showText) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this._color = _color;
        this.showText = showText;
        this.id = "";
    }

    public boolean isWithin() {
        return (mouseX >= x && mouseX <= (x + width)
          && mouseY >= y && mouseY <= (y + height));
    }

    public boolean intersects(Selection rect) {
        return (x < rect.p2.x && x + width > rect.p1.x &&
                y < rect.p2.y && y + height > rect.p1.y);
    }

    public void draw() {
        fill(_color);
        rect(x, y, width, height);
        fill(0);
        if (showText == true) {
            textAlign(CENTER, CENTER);
            text(text, x, y, width, height);
        }
    }

    public void drawButton() {
        pushStyle();
        fill(_color);
        noStroke();
        rect(x, y, width, height, 7); // round corners
        fill(255);
        if (showText == true) {
            textAlign(CENTER, CENTER);
            textSize(14);
            text(text, x, y, width, height);
        }
        popStyle();
    }
}

class Selection { // model dragging area
    PVector p1 = null;
    PVector p2 = null;

    Selection(float x1_, float y1_, float x2_, float y2_) {
        float x1 = x1_ < x2_ ? x1_ : x2_;
        float x2 = x1_ >= x2_ ? x1_ : x2_;
        float y1 = y1_ < y2_ ? y1_ : y2_;
        float y2 = y1_ >= y2_ ? y1_ : y2_;
        p1 = new PVector(x1, y1);
        p2 = new PVector(x2, y2);
    }
}
class StackedBarView extends AbstractView {

    float startX = -1; // where the X axis begins
    float endX = -1;
    float startY = -1; // Bottom of barchart
    float endY = -1; // Top of barchart
    float bar_width = -1;
    float max_value = -1;
    float bottom_values[] = null; // #Ferguson
    float top_values[] = null; // Ferguson
    Rectangle bar_list[] = null;
    float bar_spacing[] = null;
    float y_Max = 10000;
    float all_max_y = 0;

    public void display(){
        pushStyle();
        fill(backgroundColor);
        noStroke();
        rect(leftX, leftY, w, h);
       
        displayTitle();
        displayAxes();
        setSpacing();
        setData();
        makeBars();

        textSize(9);


        // highlight on mouseover
        for (int i = 0 ; i < 2; i++) {
            if (marks[i] && hoverDot) {
                 if (i == 0) {
                    bar_list[2 * hover_day]._color = lineHighlight;
                } else if (i == 1) {
                    bar_list[(2 * hover_day) + 1]._color = lineHighlight;
                }
            }
            // all bars in row hightlight
            if (marks[i] && !hoverDot) {
                if (i < 2) {
                    int j = 0;
                    if (i == 1) {
                        j++;
                    }
                    while (j < bar_list.length) {
    
                        bar_list[j]._color = lineHighlight;
                        j = j + 2;
                    }
                }
            }
        }

        for (int i = 0; i < bar_list.length; i++) {

            if (bar_list[i].isWithin()) {
                bar_list[i]._color = lineHighlight;
            }
            String label = bar_list[i].text;
            noStroke();
            bar_list[i].draw();
            if (i % 2 == 0) {
                // TEXT: dates at bottom of x-axis
                fill(255);
                textFont(generalFont);
                textSize(11);
                text(label, bar_spacing[i/2], 
                     startY + 5, bar_width + 5, 15); //fix
            }
        }

        float increment = yScale(all_max_y / 4.0f);

        for (float i = 1; i <= 4; i++) {
            float val = all_max_y * (i / 4);
            float maxy = yScale(all_max_y);
            float ypos = startY - maxy + ((4 - i) * increment);
            // TEXT: y-axis
            fill(255);
            textFont(generalFont);
            textSize(9);
            text(PApplet.parseInt(val), leftX + 5, ypos);
            line(leftX + 21, ypos, leftX + 29, ypos);
        }

        popStyle();

        if (isOnMe()) {
            pushStyle();
            for (int i = 0; i < marks.length; i++) {
                fill(255);
                if (marks[i]) {
                    String word = data.getString(i, "Word");
                    textFont(headingFont);
                    text(word, mouseX - 10, mouseY - 15);
                    break;
                }
            }
            popStyle();
        }
    }

    public void hover() { 
        for (int i = 0; i < bar_list.length; i++) {
            if (bar_list[i].isWithin()) {
                String col_label = "Count" + (i / 2);
                int hover_day = (i / 2);
                int x = data.getInt(i % 2, col_label);
                Condition cond0 = new Condition(col_label, "=", x);
                Condition[] conds = new Condition[1];
                conds[0] = cond0;

                Message message = new Message();
                message.setSource("hover");
                message.setConditions(conds);
                sendMsg(message);
            }
        }
    }

    public void hoverDots() {}
    public void mouseClicked() {}
 

    private void displayTitle() {
        pushStyle();
        textAlign(CENTER, CENTER);
        textSize(12);
        fill(255);
        textFont(headingFont);
        text("Number of Tweets about Ferguson", leftX + (w / 2), leftY + 10);
        popStyle(); 
    }

    private void setSpacing() {
        int space = 12;
        bar_width = (endX - startX - (space * num_days)) / num_days;

        bar_spacing  = new float[num_days];
        bar_spacing[0] = startX + space;
        for (int i = 1; i < num_days; i++) {
            bar_spacing[i] = bar_spacing[i - 1] + space + bar_width;
            fill(0);
        }

    }

    private void displayAxes() {
        fill(0);
        startX = leftX + 25;
        endX = leftX + w - 20;
        startY = leftY + h - 30;
        endY = leftY + 35;
        line(startX, endY, startX, startY); // y axis 
        line(startX, startY, endX + 10, startY); // x axis

        // top - number of ferguson + number of #ferguson
    }

    private void setData() {
        bottom_values = new float[num_days];
        top_values = new float[num_days];

        for (int i = 0; i < num_days; i++) {
            String day_label = "Count" + i;
            // #Ferguson on bottom
            bottom_values[i] = (float)data.getInt(0, day_label);
            top_values[i] = (float)data.getInt(1, day_label);
        }

        bar_list = new Rectangle[num_days * 2];

        max_value = top_values[0] + bottom_values[0];
        for (int i = 0; i < num_days; i++) {
            if ((top_values[i] + bottom_values[i]) > max_value) {
                max_value = top_values[i] + bottom_values[i];
            }
        }    
    }

    // finds y value (in pixels) within the range
    public float yScale (float y) {
        float val = y * (startY - endY); // startY - end Y is the total pixels in range
        val = val / y_Max;
 
        return val;
    }

    private void makeBars() {
        int bar_count = 0;
        for (int i = 0; i < num_days; i++) {
            float top = yScale(bottom_values[i]); // value of bottom bar
            int date = 23 + i;
            String curr_day = "11/" + date;

            float total = bottom_values[i] + top_values[i];

            if (total > all_max_y) {
                all_max_y = total;
            }

            bar_list[bar_count++] = new Rectangle(bar_spacing[i], startY - top, 
                                                  bar_width, top, color(235), curr_day, false);
            bar_list[bar_count++] = new Rectangle(bar_spacing[i], startY - top - yScale(top_values[i]), 
                                                  bar_width, yScale(top_values[i]), barColor, curr_day, false);
           }
    }
}
class TweetView extends AbstractView {
    String user = "HOVER ON A DOT TO SEE A TWEET!";
    String tweet_text = " " ;
    String retweets = " ";

    public void display() {
    	
       
        pushStyle();
        noStroke();

        fill(tVColor);

        for (int i = 0; i < data.getRowCount(); i++) {
            if (this.marks[i] && hoverDot) {
                changeTweet(i);
                fill(lineHighlight);
            }
        }
        rect(this.leftX, this.leftY, this.w, this.h);

        displayCurrentTweet();
        popStyle();
    }

    private void changeTweet(int i) {
        String user_text = "User" + (hover_day);
        String tweet_text = "Text" + (hover_day);
        String retweet_text = "RTCount" + (hover_day);
        String fav_text = "FCount" + (hover_day);
            
        this.user = "@" + data.getString(i, user_text);
        this.tweet_text = data.getString(i, tweet_text);
        this.retweets = "Retweets: " + data.getString(i, retweet_text);
      }

    private void displayCurrentTweet() {
        pushStyle();
        fill(255);
        textAlign(LEFT);
        textFont(headingFont);
        textSize(18);
        text(this.user, this.leftX + 10, this.leftY + 10, this.w - 20, this.h - 10);
        textSize(14);
        textFont(generalFont);
        textLeading(lineHeight);
        text(this.tweet_text, this.leftX + 10, this.leftY + 35, this.w - 20, this.h - 10);
        textFont(generalFont);
        text(this.retweets, this.leftX + 10, this.leftY + 100, this.w - 20, this.h - 10);
        popStyle();
    }

    public void mouseClicked(){};
 
    public void hoverDots() {};

    public void hover() {};

}

String[] content = {
"On August 9th, 2014, in Ferguson, MO, Michael Brown, an unarmed black teenager, was fatally shot by "
+"Darren Wilson, a white police officer. The shooting prompted a grand jury investigation, the result of "
+"which was announced on November 24th, 2014.",
"The St. Louis county prosecutor decided not to indict Darren Wilson, a ruling that incited "
+ "protests in major cities across the nation.",
"The National Bar Association publicly called for charges against Officer Wilson. "
+ "Protests broke out in Boston. Officer Darren Wilson was interviewed by ABC and spoke "
+ "publicly about the events for the first time since he shot Michael Brown.",
"Highways were blocked in over 170 cities across the U.S. as protests continued.",
"In a campaign to boycott Black Friday, demonstrators marched out on the streets "
+ "and into malls.",
"Darren Wilson resigned from the Ferguson Police Department. News was released that " 
+ "ABC allegedly paid Darren Wilson six figures for the interview. Protesters "
+ "began a 120-mile march from Ferguson to the state capital." ,
"Civil rights leaders of the protests began to arrange meetings with President Obama."
};

String[] headlines = {
    "Day 0: November 23, 2014",
    "Day 1: November 24, 2014",
    "Day 2: November 25, 2014",
    "Day 3: November 26, 2014",
    "Day 4: November 27, 2014",
    "Day 5: November 28, 2014",
    "Day 6: November 29, 2014"
};

//top words other than ferguson
String[] wordCounts = {"Atlanta: 36, Grand: 26, Police: 26, Michael: 19, Brown's: 18",
						"Grand: 1508, Police: 1478, Decision: 1368, About: 1108, People: 849",
						"Brown: 737, Protestors: 685, Wilson: 643, People: 592, Police: 589",
						"Protestors: 1374, @wordstarfunny: 845, About: 321, Police: 290, Protest: 207",
						"&amp: 154, Walmart: 138, About: 114, People: 109, Police: 101",
						"Black: 323, Police: 247, Friday: 212, Protestors: 212, Protests: 209",
						"Wilson: 2814, Darren: 2536, Officer: 2344, Police: 1991, Resigns: 1524"};

//The " + "march began with over 150 people at the site where Michael Brown was shot."
public Table _loadTable(String file, String options)
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

    public void initialize(String filename, String options) {
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

    public void addColumn(String label) {
        labels.add(label);
    //    println("adding" + label);
        data.add("");
    }

    public String getString(String label) {
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

    public int getInt(String label) {
        return parseInt(getString(label));
    } 

    public float getFloat(String label) {
        String f = getString(label);
        if (f != null) {
            return parseFloat(f);
        } else {
      //      println("Float is null");
            return -1;
        }
    }

    public void setFloat(String label, float value) {
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
int pw = 1300, ph = 800;
int margin = 60;

int toggle_state = 0;

int current_day = 0; // range from 0 to 6
int first_day = 0;
int last_day = 6;
int num_days = 7;
int hover_day = 0;

int log_max = 9;
int linear_max = 7000;

int current_max = linear_max;

int opacity = 97;


/* FONTS */
String[] fontList = PFont.list();
PFont titleFont = createFont("BebasNeue", 48);
PFont headingFont = createFont("BebasNeue", 28);
PFont generalFont = createFont("Tahoma", 12);
PFont buttonFont = headingFont;
   
int backgroundColor = color(150);

/* colors */
int orig_blue = color(69,117,180, opacity);
int faded_teal = color(152, 184, 184);
int dusty_pink = 0xffccada5;
int light_teal = 0xffacbdbd;
int highlight_red = 0xffbb0505;
int faded_red = color(195, 97, 97, 90);
int faded_red_solid = color(195, 97, 97);
int parchment = color(255, 246, 206);

int lineColor = faded_red;
int lineHighlight = highlight_red;
int axisColor = color(200, 200, 200); // light gray
int baseColor = faded_teal;
int buttonColor = faded_red_solid;
int accentColor = faded_red;
int barColor = parchment;
int tVColor = faded_red_solid;
int buttonHighlight = faded_red;


Table data = null;
String[] titles;
boolean[] marks = null;

boolean hoverDot = false;

float lineHeight = 16;

Controller contrl = null;
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "code" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

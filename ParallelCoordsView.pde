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
                float prop = float(i) / float(num_ticks);
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
                float prop = float(i) / float(num_ticks);
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
        for (Circle c : axisDots) {
            if (c.isWithin()) {
                word = words[c.index];  
                hoverDot = true;      
                hover_day = c.day_id;
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

        return isWithinEpsilon(mouseY, (m * mouseX) + b, 4.0);
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
            float prop = float(i) / float(num_ticks);
            fill(255);
            textFont(generalFont);
            textSize(9);
            int display_val = 0;
            if (drawLeft) {
                line(leftX - 4, leftY + h * prop, leftX + 4, leftY + h * prop);
                float range = leftMax - leftMin;
                display_val = int(leftMax - (prop * range));
                if (toggle_state == 1) {
                    display_val = (int)Math.pow(Math.E, display_val);
                }
                text(display_val, leftX - 35, leftY + h * prop);
            }
            if (drawRight) {
                line(leftX + w - 4, leftY + h * prop, leftX + w + 4, leftY + h * prop);
                float range = rightMax - rightMin;
                display_val = int(rightMax - (prop * range));
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
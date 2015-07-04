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
        for (int i = 0; i < bar_list.length; i++) {
            // highlight when hovered on par coords
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

        float increment = yScale(all_max_y / 4.0);

        for (float i = 1; i <= 4; i++) {
            float val = all_max_y * (i / 4);
            float maxy = yScale(all_max_y);
            float ypos = startY - maxy + ((4 - i) * increment);
            // TEXT: y-axis
            fill(255);
            textFont(generalFont);
            textSize(9);
            text(int(val), leftX + 5, ypos);
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
    float yScale (float y) {
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
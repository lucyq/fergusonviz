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
            float width = w * .15;
            float height = h * .20;
          
         
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
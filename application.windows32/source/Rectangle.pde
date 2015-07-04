class Rectangle {
    float x, y; //position of top left corner
    float width;
    float height;
    color _color;
    String text;
    boolean showText;
    String id;

    Rectangle(float x, float y, float width, float height, 
        color _color, String text, boolean showText) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this._color = _color;
        this.showText = showText;
        this.id = "";
    }

    boolean isWithin() {
        return (mouseX >= x && mouseX <= (x + width)
          && mouseY >= y && mouseY <= (y + height));
    }

    boolean intersects(Selection rect) {
        return (x < rect.p2.x && x + width > rect.p1.x &&
                y < rect.p2.y && y + height > rect.p1.y);
    }

    void draw() {
        fill(_color);
        rect(x, y, width, height);
        fill(0);
        if (showText == true) {
            textAlign(CENTER, CENTER);
            text(text, x, y, width, height);
        }
    }

    void drawButton() {
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


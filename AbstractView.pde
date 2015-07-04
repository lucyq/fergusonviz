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

    AbstractView setController(Controller contrl) {
        this.contrl = contrl;
        return this;
    }

    AbstractView setMarks(boolean[] ms) {
        this.marks = ms;
        return this;
    }

    AbstractView setName(String name) {
        this.name = name;
        return this;
    }

    AbstractView setDataSrc(Table t, String[] str, boolean[] marks) {
        this.data = t;
        this.header = str;
        this.marks = marks;
        return this;
    }

    AbstractView setPosition(float x, float y) { 
        this.leftX = x;
        this.leftY = y;
        return this;
    }

    AbstractView setSize(float w, float h) {
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

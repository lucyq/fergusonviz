//import java.lang.Float;

void setup() {
    size(pw, ph);
    //size(displayWidth, displayHeight);
    background(backgroundColor);
    initSettings();
}

void draw() {
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

void initSettings() {
    parse("dummy.csv");
//    parse("../data/completedata.csv");

    marks = new boolean[data.getRowCount()];
    contrl = new SPLOMController();
    contrl.initViews();
    contrl.cleanSelectedArea();
}


void keyPressed() {
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

void mouseClicked() {
    contrl.mouseClicked();
}

void parse(String filename) {
   data = _loadTable(filename, "header");
   titles = data.getColumnTitles();
}

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
        float w = ((width * .67) - 2 * margin) / (num_days - 1);

        ParallelCoordsView pcView = new ParallelCoordsView();
            pcView
                .setController(this)
                .setName("Parallel")
            //    .setName("Parallel " + titles[col] + " v. " + titles[dayToCol(current_day + 1)])
                .setSize(w, (height * .85) - (2 * margin))
                .setPosition((width * .33) + w * current_day + margin, (height * .1) + margin)
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
                .setSize(w, (height * .85) - (2 * margin))
                .setPosition((width * .33) + margin, (height * .1) + margin)
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
            .setSize((width * .33) - margin, (height * .44) - (2 * margin))
            .setPosition(margin, (height * .37) + (margin / 2))
            .setDataSrc(data, null, marks);

        vizs.add(sbView);


        // Tweet View
        TweetView twView = new TweetView();
        twView
            .setController(this)
            .setName("Tweet")
            .setSize((width * .33) - margin, (height * .30) - (2 * margin))
            .setPosition(margin, (height * .70) + (margin / 2))
            .setDataSrc(data, null, marks);

        vizs.add(twView);

        // Info View
        infoView
            .setController(this)
            .setSize((width * .33) - margin, (height * .38) - (.1 * height) - margin)
            .setPosition(margin, (height * .1) + (margin));
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

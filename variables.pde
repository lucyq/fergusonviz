//int pw = 1300, ph = 800;
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
   
color backgroundColor = color(150);

/* colors */
color orig_blue = color(69,117,180, opacity);
color faded_teal = color(152, 184, 184);
color dusty_pink = #ccada5;
color light_teal = #acbdbd;
color highlight_red = #bb0505;
color faded_red = color(195, 97, 97, 90);
color faded_red_solid = color(195, 97, 97);
color parchment = color(255, 246, 206);

color lineColor = faded_red;
color lineHighlight = highlight_red;
color axisColor = color(200, 200, 200); // light gray
color baseColor = faded_teal;
color buttonColor = faded_red_solid;
color accentColor = faded_red;
color barColor = parchment;
color tVColor = faded_red_solid;
color buttonHighlight = faded_red;


Table data = null;
String[] titles;
boolean[] marks = null;

boolean hoverDot = false;

float lineHeight = 16;

Controller contrl = null;

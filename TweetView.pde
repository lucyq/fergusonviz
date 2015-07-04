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

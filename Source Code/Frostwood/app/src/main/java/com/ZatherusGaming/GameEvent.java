package com.ZatherusGaming;
/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * GameEvent
 * Description:
 * Event holder. Stores all the information about an event.
 *
 * Usage:
 * The EventHandler class parses the XML events file into these objects. When the EventHandler is
 * created it automatically creates the necessary Events. Simply a getter and setter class for info.
 *
 * Future Updates/Refactor:
 * As new tags get added I can just update this class to store the necessary information.
 */

public class GameEvent {
    private String eventText;
    private String button1Text;
    private String button2Text;
    private String button1Outcome;
    private String outcome1Text;
    private String button2Outcome;
    private String outcome2Text;

    GameEvent(){}

    public String getButton1Text(){
        return button1Text;
    }
    public String getButton2Text(){
        return button2Text;
    }
    public String getEventText(){
        return eventText;
    }
    public String getButton1Outcome(){
        return button1Outcome;
    }
    public String getButton2Outcome(){
        return button2Outcome;
    }
    public String getOutcome1Text() {
        return outcome1Text;
    }
    public String getOutcome2Text() {
        return outcome2Text;
    }

    public void setEventText(String eventText){
        this.eventText = eventText;
    }
    public void setButton1Text(String buttonText1) {
        this.button1Text = buttonText1;
    }
    public void setButton2Text(String buttonText2) {
        this.button2Text = buttonText2;
    }
    public void setButton1Outcome(String button1Outcome) {
        this.button1Outcome = button1Outcome;
    }
    public void setButton2Outcome(String button2Outcome) {
        this.button2Outcome = button2Outcome;
    }
    public void setOutcome1Text(String outcome1Text) {
        this.outcome1Text = outcome1Text;
    }
    public void setOutcome2Text(String outcome2Text) {
        this.outcome2Text = outcome2Text;
    }
}

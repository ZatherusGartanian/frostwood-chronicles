package com.ZatherusGaming;
import android.content.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * EventHandler
 * Description:
 * Handles the event system for the game. Uses an XML sheet to load in the events. Parses the data
 * necessary to handle the events and apply the updates to the game and player.
 *
 * Usage:
 * After instantiating the class all you have to do is call getNewEvent. You then use
 * processEventOutcome by sending the necessary string to parse and the handler does the rest. To
 * add a new event simply update the XML sheet using the same tag structure as prior events. The
 * ease of adding events to the game as a huge draw of building it this way.
 *
 * Future Updates/Refactor:
 * One refactor could be to just pass the event through instead of the specific string. The
 * EventHandler should now how to get the string for the correct outcome. One reason it currently
 * is not set up that way is the outcome choice is made outside the handler and I either had to
 * send an int of which outcome to use from the event or just send it the string to parse. What I
 * love about this parsing method though is that I can update events in a variety of ways because
 * I chose to use the XML parsing. One example is to add a <requirements> tag that could hold
 * information in order for the event to be chosen. For example, Health NotMax, Min Food 1, etc. I
 * still need to come up with a clever parsing structure for it but it would allow further control
 * over events.
 */

public class EventHandler {
    private ArrayList<GameEvent> uniqueEvents;
    private ArrayList<GameEvent> repeatableEvents;
    private int eventCounter = 0;

    EventHandler(Context context){
        uniqueEvents = new ArrayList<GameEvent>();
        repeatableEvents = new ArrayList<GameEvent>();

        loadEventsFromResource(context);
    }

    //Returns a new event from the event arrays
    public GameEvent getNewEvent(){
        GameEvent eventToComplete;
        Random random = new Random();

        //Every 3 events or if there are no more unique events return a repeatable event.
        if(eventCounter == 3 || uniqueEvents.isEmpty()){
            int randomIndex = random.nextInt(repeatableEvents.size());
            eventToComplete = repeatableEvents.get(randomIndex);
            eventCounter = 0;
        }
        //Otherwise return a unique event and then remove it from the array
        else {
            int randomIndex = random.nextInt(uniqueEvents.size());
            eventToComplete = uniqueEvents.get(randomIndex);
            uniqueEvents.remove(randomIndex);
        }

        eventCounter++;
        return eventToComplete;
    }

    //Parse the outcome string to obtain the results
    //Every outcome is a an attribute followed by a value
    public void processEventOutcome(GameState gameState, Player player, String eventOutcome){
        String[] words = eventOutcome.split("\\s+");

        for (int i = 0; i < words.length; i += 2) {
            String attribute = words[i];
            int value = Integer.parseInt(words[i + 1]);
            processAttribute(gameState, player, attribute, value);
        }
    }

    //UPDATE THIS METHOD WHENEVER THERE IS A UNIQUE OUTCOME STATE FROM EVENTS
    //Cycles through each attribute and calls the associated methods with the paired value
    public void processAttribute(GameState gameState, Player player, String attribute, int value){
        switch(attribute){
            case "Health":
                if(value < 0){
                    player.decreaseHealth(value*-1);
                }
                else
                    player.heal(value);
                break;

            case "MaxHealth":
                if(value < 0){
                    player.decreaseMaxHealth(value*-1);
                }
                else
                    player.increaseMaxHealth(value);
                break;

            case "Stamina":
                if(value < 0){
                    player.decreaseStamina(value*-1);
                }
                else
                    player.rest(value);
                break;
            case "MaxStamina":
                if(value < 0){
                    player.decreaseMaxStamina(value*-1);
                }
                else
                    player.increaseMaxStamina(value);
                break;

            case "Mana":
                if(value < 0){
                    player.decreaseMana(value*-1);
                }
                else
                    player.increaseMana(value);
                break;
            case "MaxMana":
                if(value < 0){
                    player.decreaseMaxMana(value*-1);
                }
                else
                    player.increaseMaxMana(value);
                break;

            case "Minutes":
                gameState.addMinutes(value);
                break;

            case "Food":
                for(int i = 0; i < value; i++) {
                    player.findFood();
                }
                break;
            case "Water":
                for(int i = 0; i < value; i++) {
                    player.findWater();
                }
                break;
        }
    }

    //EVENT LOADER FROM THE XML FILE
    //Parsing code heavily adapted from chatGPT suggestion. I knew the route I wanted to go but
    //was unsure how to do it with XML and android studio. Through research I found I liked the
    //document builder format rather then the XML parsers class. The use of NodeList is what
    //I really liked with this solution.
    public void loadEventsFromResource(Context context) {
        try {
            //Load the XML file in as a document
            int resID = R.raw.events;
            InputStream inputStream = context.getResources().openRawResource(resID);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            //Create a list of objects parsing using the event tag.
            NodeList eventList = doc.getElementsByTagName("event");

            //Cycle through each event and parse that element
            for (int i = 0; i < eventList.getLength(); i++) {
                Element eventElement = (Element) eventList.item(i);
                parseEventElement(eventElement);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Takes the element from the node list for loop and parses further by each tag
    //Add future event tags here and build a setter if necessary in the event class.
    private void parseEventElement(Element eventElement) {
        GameEvent event = new GameEvent();
        event.setEventText(getTextContent(eventElement, "eventText"));
        event.setButton1Text(getTextContent(eventElement, "button1Text"));
        event.setButton1Outcome(getTextContent(eventElement, "button1Outcome"));
        event.setButton2Text(getTextContent(eventElement, "button2Text"));
        event.setButton2Outcome(getTextContent(eventElement, "button2Outcome"));
        event.setOutcome1Text(getTextContent(eventElement, "outcome1Text"));
        event.setOutcome2Text(getTextContent(eventElement, "outcome2Text"));

        //If the event is repeatable store as a repeatable event otherwise store as unique
        String repeatable = getTextContent(eventElement, "repeatable");
        if(repeatable.equals("true")){
            repeatableEvents.add(event);
        }
        else
            uniqueEvents.add(event);
    }

    //Returns the string within the provided tag
    private String getTextContent(Element element, String tagName) {
        String textToStore = element.getElementsByTagName(tagName).item(0).getTextContent();
        textToStore = textToStore.trim();
        return textToStore;
    }
}

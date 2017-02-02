package com.cuttingedge.PokeApp;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This is a class that simulates the behaviour of a database.
 * Implementation of this class is not important for library!
 *
 * Created by Robbe on 19/08/2016.
 */
public class Pokedex {

    private static ArrayList<Pokemon> pokedex;
    private static ArrayList<Pokemon> billsPC;

    private static Context context;

    private static boolean initiated;

    public static void setup(Context mContext) {
        if (!initiated)
            initiate(mContext);
    }

    public static void initiate(Context mContext) {
        context = mContext;
        setupParser();
        initiated = true;
    }

    private static void setupParser() {
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            InputStream in_s = context.getResources().openRawResource(R.raw.pokedex);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            parseXML(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException {
        int eventType = parser.getEventType();
        Pokemon currentPokemon = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = null;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    pokedex = new ArrayList<>();
                    billsPC = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("pokemon")) {
                        currentPokemon = new Pokemon();
                    } else if (currentPokemon != null) {
                        switch (name) {
                            case "id":
                                currentPokemon.id = Integer.parseInt(parser.nextText());
                                currentPokemon.icon = context.getResources().getDrawable(context.getResources().getIdentifier("p" + String.valueOf(currentPokemon.id), "drawable", context.getPackageName()));
                                break;
                            case "name":
                                String next = parser.nextText();
                                currentPokemon.name = next.substring(0, 1).toUpperCase() + next.substring(1).toLowerCase();
                                break;
                            case "type":
                                currentPokemon.type += parser.nextText() + " ";
                                break;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("pokemon") && currentPokemon != null) {
                        pokedex.add(currentPokemon);
                    }
                    break;
            }
            eventType = parser.next();
        }
    }

    public static List<Pokemon> getAllPokemonAlphabetic() {
        ArrayList<Pokemon> sortList = new ArrayList<>(pokedex);
        Collections.sort(sortList, new Comparator<Pokemon>(){
            public int compare(Pokemon p1, Pokemon p2) {
                return p1.name.compareToIgnoreCase(p2.name);
            }
        });
        return sortList;
    }

    public static ArrayList<Pokemon> getAllPokemonByType() {
        ArrayList<Pokemon> sortList = new ArrayList<>(pokedex);
        Collections.sort(sortList, new Comparator<Pokemon>(){
            public int compare(Pokemon p1, Pokemon p2) {
                return p1.type.compareToIgnoreCase(p2.type);
            }
        });
        return sortList;
    }

    public static ArrayList<Pokemon> getBillsPCID() {
        ArrayList<Pokemon> sortList = new ArrayList<>(billsPC);
        Collections.sort(sortList, new Comparator<Pokemon>(){
            public int compare(Pokemon p1, Pokemon p2) {
                return p1.id - p2.id;
            }
        });
        return sortList;
    }

    public static void addToPokedex(Pokemon pokemon) {
        pokedex.add(pokemon);
    }

    public static void addToBillsPC(Pokemon pokemon) {
        billsPC.add(pokemon);
    }

    public static void removePokemon(Pokemon pokemon) {
        pokedex.remove(pokemon);
        billsPC.remove(pokemon);
    }

    public static void sendToBill(Pokemon pokemon) {
        pokedex.remove(pokemon);
        billsPC.add(pokemon);
    }

    public static void getFromBill(Pokemon pokemon) {
        billsPC.remove(pokemon);
        pokedex.add(pokemon);
    }
}

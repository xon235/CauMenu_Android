package com.yisuho.caumenu;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by xon23 on 2016-08-23.
 */
public class MenuXmlParser {
    private static final String sNamespace = null;
    private String mBuildingCode;
    private ArrayList<Menu> mMenuArrayList;

    public ArrayList<Menu> parse(String xml, String buildingCode) throws XmlPullParserException, IOException, NullPointerException{
        this.mBuildingCode = buildingCode;

        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
        xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        xmlPullParser.setInput(new StringReader(xml));
        xmlPullParser.nextTag();
        Log.d("MenuXmlParser",xml );
        //find first vector tag
        while (!xmlPullParser.getName().equals("vector")){
            xmlPullParser.next();
        }
        return readMenus(xmlPullParser);
    }


    private ArrayList<Menu> readMenus(XmlPullParser parser) throws XmlPullParserException, IOException {
        mMenuArrayList = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, sNamespace, "vector");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("map")) {
                Menu menu = readMenu(parser);
                if(menu != null){
                    mMenuArrayList.add(menu);
                }
            }
        }
        return mMenuArrayList;
    }

    private Menu readMenu(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, sNamespace, "map");
        String menuName = "";
        String menuDescription = "";
        String menuPrice = "";
        String menuBuilding = this.mBuildingCode;
        int menuSH = 0;
        int menuSM = 0;
        int menuEH = 0;
        int menuEM = 0;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("menunm")) {
                menuName = parser.getAttributeValue(sNamespace, "value");
                parser.next();
            } else if (name.equals("tm")) {
                //parse time
                String[] startEnd = parser.getAttributeValue(sNamespace, "value").split("~");
                String[] start = startEnd[0].split(":");
                String[] end = startEnd[1].split(":");
                try{
                    menuSH = Integer.parseInt(start[0]);
                    menuSM = Integer.parseInt(start[1]);
                    int eH = Integer.parseInt(end[0]);
                    int eM = Integer.parseInt(end[1]);
                    menuEH = ((eH * 60) + eM - 1) / 60;
                    menuEM = ((eH * 60) + eM - 1) % 60;
                } catch (Exception e){
                    return null;
                }
                parser.next();
            } else if (name.equals("amt")) {
                menuPrice = parser.getAttributeValue(sNamespace, "value");
                parser.next();
            } else if (name.equals("menunm1")) {
                menuDescription = parser.getAttributeValue(sNamespace, "value")
                        .replace("<br>", "\n");
                if(menuDescription.contains("운영없음")){
                    return null;
                }
                parser.next();
            }else {
                parser.next();
            }
        }
        int weight = (MyDataManager.BUILDING_CODE_MAP.get(mBuildingCode).getWeight() * 1000) + mMenuArrayList.size();
        return new Menu(menuName, menuDescription, menuPrice, menuBuilding, menuSH, menuSM, menuEH, menuEM, weight);
    }
}


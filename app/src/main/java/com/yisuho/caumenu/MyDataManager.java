package com.yisuho.caumenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by xon23 on 2016-08-23.
 */
public class MyDataManager {

    private static final Map<String, Building> sBuildingCodeMap;
    private static final String URL = "http://cautis.cau.ac.kr/TIS/portlet/comm/cPortlet001/selectList.do";

    public static final Map<String, Building> BUILDING_CODE_MAP;

    public static final String CONTENT_TYPE_BUILDING_CODE = "BUILDING CODE";
    public static final String CONTENT_TYPE_FILTER_START_TIME = "FILTER_START_TIME";
    public static final String CONTENT_TYPE_FILTER_END_TIME = "FILTER_END_TIME";
    //205관 없어짐
//    public static final String[] BUILDING_CODE_ARRAY_SORTED = {"11", "02", "03", "06", "07", "08", "12", "13", "09", "10"};
    public static final String[] BUILDING_CODE_ARRAY_SORTED = {"11", "06", "07", "08", "12", "13", "09", "10"};

    public static final String KEY_PREF_FAV_CODE_ = "FAVE_CODE_";
    public static final String KEY_PREF_LAST_MENU_UPDATE = "LAST_MENU_UPDATE";
    public static final String KEY_PREF_BUILDING_XML_ = "BUILDING_XML_";
    private static boolean sIsMenuInitFinshed = false;
    private static boolean sIsCardInitFinshed = false;
    private static int sNumOfAsyncDone;
    private static OnMyDataLoadedListener sOnMyDataLoadedListener;

    private static HashMap<String, ArrayList<Menu>> sMenuHashMap;
    private static ArrayList<Card> sCardArrayList;

    static {
        sBuildingCodeMap = new HashMap<>();
        sBuildingCodeMap.put("11", new Building("11", "University Club(102)", "University Club", "102관 11층", R.drawable.s102, 0));
//        sBuildingCodeMap.put("02", new Building("02", "슬기마루(205)", "슬기마루", "205관 1층",R.drawable.image_blue_mir, 1));
//        sBuildingCodeMap.put("03", new Building("03", "참마루(205)", "참마루", "205관 B1층", R.drawable.image_blue_mir, 2));
        sBuildingCodeMap.put("06", new Building("06", "학생식당(303)", "학생식당", "303관 B1층", R.drawable.s303, 3));
        sBuildingCodeMap.put("07", new Building("07", "교직원식당(303)", "교직원식당", "303관 B1층", R.drawable.s303, 4));
        sBuildingCodeMap.put("08", new Building("08", "생활관식당(308)", "생활관식당(308)", "블루미르 308관", R.drawable.s308, 5));
        sBuildingCodeMap.put("12", new Building("12", "생활관식당(309)", "생활관식당(309)", "블루미르 309관", R.drawable.s309, 6));
        sBuildingCodeMap.put("13", new Building("13", "참슬기식당(310)", "참슬기 식당", "310관 B4층", R.drawable.s310, 7));
        sBuildingCodeMap.put("09", new Building("09", "안성학생식당", "안성학생식당", "", R.drawable.as, 8));
        sBuildingCodeMap.put("10", new Building("10", "안성교직원식당", "안성교직원식당", "", R.drawable.ap, 9));
        BUILDING_CODE_MAP = Collections.unmodifiableMap(sBuildingCodeMap);

        if (sBuildingCodeMap.size() != BUILDING_CODE_ARRAY_SORTED.length) {
            Log.d("MyDataManager", "Please reconfigure BUILDING_CODE_ARRAY_SORTED or vice versa");
        }
    }

    private MyDataManager() {
    }

    public static void setOnDataLoadedListener(OnMyDataLoadedListener onMyDataLoadedListener) {
        sOnMyDataLoadedListener = onMyDataLoadedListener;
    }

    public static void update(Context context) {
        sIsMenuInitFinshed = false;
        sIsCardInitFinshed = false;
        initMenus(context);
    }

    private static void initMenus(final Context context) {
        sMenuHashMap = new HashMap<>();
        sNumOfAsyncDone = 0;

        String lastMenuUpdate = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_PREF_LAST_MENU_UPDATE, "0");
        final String today = new SimpleDateFormat("yyyyMMdd").format(new Date());


        if (lastMenuUpdate.equals(today)) {
            for (String buildingCode : BUILDING_CODE_MAP.keySet()) {
                MenuXmlParser menuXmlParser = new MenuXmlParser();
                try {
                    String savedXml = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_PREF_BUILDING_XML_ + buildingCode, "");
                    sMenuHashMap.put(buildingCode, menuXmlParser.parse(savedXml, buildingCode));
                    sNumOfAsyncDone += 1;
                } catch (Exception e){
                    e.printStackTrace();
                    sOnMyDataLoadedListener.onMyDataLoaded(null);
                }
            }

            if(sNumOfAsyncDone == sBuildingCodeMap.size()){
                sIsMenuInitFinshed = true;
                initCards();
                return;
            }
        }

        sNumOfAsyncDone = 0;

        final HashMap<String, String> xmlResponse = new HashMap<>();
        for (final String buildingCode : sBuildingCodeMap.keySet()) {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new CustomBodyStringRequest(Request.Method.POST, URL, buildingCode, new Response.Listener<String>() {

                @Override
                public void onResponse(String s) {
                    xmlResponse.put(buildingCode, s);
                    MenuXmlParser menuXmlParser = new MenuXmlParser();

                    try {
                        sMenuHashMap.put(buildingCode, menuXmlParser.parse(s, buildingCode));
                        sNumOfAsyncDone += 1;
                        if (sNumOfAsyncDone == sBuildingCodeMap.keySet().size()) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                            for (String key : xmlResponse.keySet()) {
                                editor.putString(KEY_PREF_BUILDING_XML_ + key, xmlResponse.get(key));
                            }

                            editor.putString(KEY_PREF_LAST_MENU_UPDATE, today);
                            editor.apply();
                            sIsMenuInitFinshed = true;
                            initCards();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        sOnMyDataLoadedListener.onMyDataLoaded(null);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d("MyDataManager", volleyError.toString());
                    sOnMyDataLoadedListener.onMyDataLoaded(null);
                }
            });
            requestQueue.add(stringRequest);
        }

    }

    private static void initCards() {
        sCardArrayList = new ArrayList<>();
        int countTotalMin;
        int lastCountTotalMin = 0;
        HashSet<String> lastAvailableStoreHashSet = new HashSet<>();

        Menu[] allMenus = getAllMenus();

        for (countTotalMin = 0; countTotalMin <= SimpleTime.TOTAL_MIN_OF_DAY; countTotalMin++) {
            HashSet<String> availableStoreHashSet = new HashSet<>();

            for (Menu menu : allMenus) {
                if (menu.isAvailableAt(countTotalMin)) {
                    availableStoreHashSet.add(menu.getBuildingCode());
                }
            }

            if (!lastAvailableStoreHashSet.equals(availableStoreHashSet) || countTotalMin == SimpleTime.TOTAL_MIN_OF_DAY) {
                String[] buildingCodes = new String[lastAvailableStoreHashSet.size()];

                Calendar calendar = Calendar.getInstance();
                int h = calendar.get(Calendar.HOUR_OF_DAY);
                int m = calendar.get(Calendar.MINUTE);
                int currentTotalMin = SimpleTime.convertToTotalMin(h, m);
                int timeMark = 0;

                if (countTotalMin <= currentTotalMin) {
                    timeMark = -1;
                } else if (currentTotalMin < lastCountTotalMin) {
                    timeMark = +1;
                } else {
                    timeMark = 0;
                }
                lastAvailableStoreHashSet.toArray(buildingCodes);
                sortBuildingString(buildingCodes);
                Card c = new Card(lastCountTotalMin / 60, lastCountTotalMin % 60,
                        (countTotalMin - 1) / 60, (countTotalMin - 1) % 60, buildingCodes, timeMark);
                sCardArrayList.add(c);


                lastAvailableStoreHashSet = availableStoreHashSet;
                lastCountTotalMin = countTotalMin;
            }
        }

        Card[] cArr = new Card[sCardArrayList.size()];
        if (sOnMyDataLoadedListener == null) {
            Log.d("MyDataManager", "sOnMyDataLoadedListener not set!");
        } else {
            sOnMyDataLoadedListener.onMyDataLoaded(sCardArrayList.toArray(cArr));
        }
        sIsCardInitFinshed = true;
    }

    private static void sortBuildingString(String[] bcArr) {
        //bubble sort
        for (int i = 0; i < bcArr.length - 1; i++) {
            for (int j = 0; j < bcArr.length - 1 - i; j++) {
                if (BUILDING_CODE_MAP.get(bcArr[j]).getWeight() > BUILDING_CODE_MAP.get(bcArr[j + 1]).getWeight()) {
                    String tmp = bcArr[j];
                    bcArr[j] = bcArr[j + 1];
                    bcArr[j + 1] = tmp;
                }
            }
        }
    }

    public static Menu[] getAllMenus() {
        if (sIsMenuInitFinshed) {
            ArrayList<Menu> allMenuArrayList = new ArrayList<>();
            for (String key : sMenuHashMap.keySet()) {
                allMenuArrayList.addAll(sMenuHashMap.get(key));
            }
            Menu[] menus = new Menu[allMenuArrayList.size()];
            return allMenuArrayList.toArray(menus);
        } else {
            return null;
        }
    }

    public static Menu[] getMenusIn(String bc) {
        if (sIsMenuInitFinshed) {
            ArrayList<Menu> menuInArrayList = new ArrayList<>();
            menuInArrayList.addAll(sMenuHashMap.get(bc));
            Menu[] menus = new Menu[menuInArrayList.size()];
            return menuInArrayList.toArray(menus);
        } else {
            return null;
        }
    }

    public static Card[] getCards() {
        if (sIsCardInitFinshed) {
            Card[] cards = new Card[sCardArrayList.size()];
            return sCardArrayList.toArray(cards);
        } else {
            return null;
        }
    }
}
package com.yisuho.caumenu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class BuildingPageActivity extends AppCompatActivity {

    private com.yisuho.caumenu.Menu[] mMenus;
    private int mFilterStartTime;
    private int mFilterEndTime;
    private boolean mLastFav;
    private Button mFilterBt;
    private Button mShowAllBt;
    private String mBuildingCode;
    private SharedPreferences mSharedPref;
    public static final int RESULT_UPDATE_CARDS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBuildingCode = getIntent().getStringExtra(MyDataManager.CONTENT_TYPE_BUILDING_CODE);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(MyDataManager.BUILDING_CODE_MAP.get(mBuildingCode).getName());

        ImageView backgroundIv = (ImageView) findViewById(R.id.backgroundIv);
        backgroundIv.setImageResource(MyDataManager.BUILDING_CODE_MAP.get(mBuildingCode).getImageResourceId());

        TextView descriptionTv = (TextView) findViewById(R.id.descriptionTv);
        descriptionTv.setText(MyDataManager.BUILDING_CODE_MAP.get(mBuildingCode).getDescription());

        mMenus = MyDataManager.getMenusIn(mBuildingCode);
        mFilterStartTime = getIntent().getIntExtra(MyDataManager.CONTENT_TYPE_FILTER_START_TIME, 0);
        mFilterEndTime = getIntent().getIntExtra(MyDataManager.CONTENT_TYPE_FILTER_END_TIME, (24 * 60) - 1);

        mFilterBt = (Button) findViewById(R.id.filterBt);
        mFilterBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timepickerdialog = TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
                                mFilterStartTime = (hourOfDay * 60) + minute;
                                mFilterEndTime = (hourOfDayEnd * 60) + minuteEnd;
                                setNewAdapter();
                            }
                        },
                        mFilterStartTime / 60,
                        mFilterStartTime % 60,
                        true
                );
                timepickerdialog.setAccentColor(ContextCompat.getColor(BuildingPageActivity.this, R.color.teal));
                timepickerdialog.show(getFragmentManager(), "Timepickerdialog");
            }
        });

        mShowAllBt = (Button) findViewById(R.id.showAllBt);
        mShowAllBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterStartTime = 0;
                mFilterEndTime = (24 * 60) - 1;
                setNewAdapter();
            }
        });

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setNewAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_building_page, menu);

        mLastFav = mSharedPref.getBoolean(MyDataManager.KEY_PREF_FAV_CODE_ + mBuildingCode, false);

        if(mLastFav){
            MenuItem favoriteMenuItem = menu.findItem(R.id.action_favorite);
            favoriteMenuItem.setIcon(R.drawable.ic_star);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_favorite) {
            SharedPreferences.Editor editor = mSharedPref.edit();
            if(mSharedPref.getBoolean(MyDataManager.KEY_PREF_FAV_CODE_ + mBuildingCode, false)){
                editor.putBoolean(MyDataManager.KEY_PREF_FAV_CODE_ + mBuildingCode, false);
                item.setIcon(R.drawable.ic_star_outline);
            } else {
                editor.putBoolean(MyDataManager.KEY_PREF_FAV_CODE_ + mBuildingCode, true);
                item.setIcon(R.drawable.ic_star);
            }
            editor.apply();
        } else if(id == android.R.id.home){
            onBackPressed();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if(mLastFav != mSharedPref.getBoolean(MyDataManager.KEY_PREF_FAV_CODE_ + mBuildingCode, false)){
            setResult(RESULT_UPDATE_CARDS);
        }
        super.onBackPressed();
    }

    private void setNewAdapter() {
        setFilterButtonText(mFilterStartTime, mFilterEndTime);
        if (mMenus == null) {
            return;
        }

        ArrayList<com.yisuho.caumenu.Menu> filteredMenu = new ArrayList<>();
        for (com.yisuho.caumenu.Menu m : mMenus) {
            if (!(m.getEndTime().getTotalMin() < mFilterStartTime) && !(mFilterEndTime < m.getStartTime().getTotalMin())) {
                filteredMenu.add(m);
            }
        }

        ArrayList<Object> objectArrayList = new ArrayList<>();
        int countTotalMin;
        int lastCountTotalMin = mFilterStartTime;
        HashSet<com.yisuho.caumenu.Menu> lastAvailableMenu = new HashSet<>();

        for (countTotalMin = mFilterStartTime; countTotalMin <= mFilterEndTime; countTotalMin++) {
            HashSet<com.yisuho.caumenu.Menu> availableMenu = new HashSet<>();

            for (com.yisuho.caumenu.Menu m : filteredMenu) {
                if (m.isAvailableAt(countTotalMin)) {
                    availableMenu.add(m);
                }
            }

            if (!lastAvailableMenu.equals(availableMenu) || countTotalMin == mFilterEndTime) {
                if (lastAvailableMenu.size() != 0) {
                    int sT = lastCountTotalMin;
                    int eT = countTotalMin;
                    if(countTotalMin != mFilterEndTime){
                        eT -= 1;
                    }
                    objectArrayList.add(String.format("%02d:%02d ~ %02d:%02d", sT/60, sT%60, eT/60, eT%60));

                    com.yisuho.caumenu.Menu[] menus = new com.yisuho.caumenu.Menu[lastAvailableMenu.size()];

                    ArrayList<com.yisuho.caumenu.Menu> menuArrayList = new ArrayList<>();
                    for (com.yisuho.caumenu.Menu m : lastAvailableMenu.toArray(menus)) {
                        menuArrayList.add(m);
                    }
                    Collections.sort(menuArrayList);

                    objectArrayList.addAll(menuArrayList);
                }

                lastCountTotalMin = countTotalMin;
                lastAvailableMenu = availableMenu;
            }
        }

        NonScrollExpandableListView expandableListView = (NonScrollExpandableListView) findViewById(R.id.menusElv);
        expandableListView.setAdapter(new MyExpandAdapter(objectArrayList, this));
    }

    private void setFilterButtonText(int st, int et) {
        String sH = String.format("%02d", st / 60);
        String sM = String.format("%02d", st % 60);
        String eH = String.format("%02d", et / 60);
        String eM = String.format("%02d", et % 60);
        mFilterBt.setText(sH + ":" + sM + " ~ " + eH + ":" + eM);
    }
}

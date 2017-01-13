package com.yisuho.caumenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_UPDATE_CARDS = 100;
    public final static int ABLED_ALPHA = 255;
    public final static int DISABLED_ALPHA = 88;

    public static final String KEY_PREF_CURRENT = "pref_current";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayOut;
    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setIcon(R.drawable.ic_cau);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);

        mTabLayOut = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayOut.setupWithViewPager(mViewPager);

        mTabLayOut.getTabAt(0).setIcon(R.drawable.ic_home);
        mTabLayOut.getTabAt(1).setIcon(R.drawable.ic_search);
        mTabLayOut.getTabAt(1).getIcon().setAlpha(DISABLED_ALPHA);
        mTabLayOut.getTabAt(2).setIcon(R.drawable.ic_information);
        mTabLayOut.getTabAt(2).getIcon().setAlpha(DISABLED_ALPHA);

        mTabLayOut.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setAlpha(ABLED_ALPHA);
                invalidateOptionsMenu();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setAlpha(DISABLED_ALPHA);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tab.getIcon().setAlpha(ABLED_ALPHA);
                Fragment fragment = mSectionsPagerAdapter.getRegisteredFragment(tab.getPosition());
                if (fragment instanceof TopScrollable) {
                    ((TopScrollable) fragment).scrollToTop();
                }
            }
        });

        //Check my website for news
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if(mSharedPref.getInt(KEY_PREF_CURRENT, -1) < 0){
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putInt(KEY_PREF_CURRENT, 0);
            editor.apply();
            mViewPager.setCurrentItem(2);
        } else {
            //Volley
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = getString(R.string.my_news_url) + "/checkCurrent";

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, (String)null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            checkCurrent(response);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub

                        }
                    });

            queue.add(jsObjRequest);
        }
    }

    private void checkCurrent(JSONObject responce) {
        try {
            int newCurrent = responce.getInt("current");
            int myCurrent = mSharedPref.getInt(KEY_PREF_CURRENT, -1);

            if(newCurrent > myCurrent){
                SharedPreferences.Editor editor = mSharedPref.edit();
                editor.putInt(KEY_PREF_CURRENT, newCurrent);
                editor.apply();
                startActivity(new Intent(this, NewsActivity.class));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (mViewPager.getCurrentItem()) {
            case 0:
                getMenuInflater().inflate(R.menu.menu_main_timeline, menu);
                break;
            case 2:
                getMenuInflater().inflate(R.menu.menu_main_about, menu);
                break;
            default:
                getMenuInflater().inflate(R.menu.menu_main_other, menu);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                SectionsPagerAdapter s = (SectionsPagerAdapter) mViewPager.getAdapter();
                PlaceholderFragment pf = (PlaceholderFragment) s.getRegisteredFragment(0);
                pf.setNewAdapterData(null, true);
                MyDataManager.update(this);
                return true;
            case R.id.action_news:
                startActivity(new Intent(this, NewsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == BuildingPageActivity.RESULT_UPDATE_CARDS) {
            SectionsPagerAdapter sectionsPagerAdapter = (SectionsPagerAdapter) mViewPager.getAdapter();

            PlaceholderFragment placeholderFragment = (PlaceholderFragment) sectionsPagerAdapter.getRegisteredFragment(0);
            placeholderFragment.setNewAdapterData(MyDataManager.getCards(), true);

            SearchFragment searchFragment = (SearchFragment) sectionsPagerAdapter.getRegisteredFragment(1);
            searchFragment.setNewAdapterData();

            Toast.makeText(this, "즐겨찾기 갱신", Toast.LENGTH_SHORT).show();
        }
    }
}

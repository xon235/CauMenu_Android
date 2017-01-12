package com.yisuho.caumenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by xon23 on 2016-08-19.
 */
public class SearchFragment extends Fragment {

    private ListView mListView;
    private SharedPreferences mSharedPref;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tabbed2, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listView);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        // specify an adapter (see also next example)
        setNewAdapterData();
        return rootView;
    }

    public void setNewAdapterData() {
        String[] buildingCodes = MyDataManager.BUILDING_CODE_ARRAY_SORTED.clone();
        ArrayList<String> favoriteArrayList = new ArrayList<>();
        ArrayList<String> normalArrayList = new ArrayList<>();

        for (String buildingCode: buildingCodes) {
            if(mSharedPref.getBoolean(MyDataManager.KEY_PREF_FAV_CODE_ + buildingCode, false)){
                favoriteArrayList.add(buildingCode);
            }else{
                normalArrayList.add(buildingCode);
            }
        }

        favoriteArrayList.addAll(normalArrayList);
        favoriteArrayList.toArray(buildingCodes);
        final MyListViewAdapter myListViewAdapter = new MyListViewAdapter(buildingCodes, mSharedPref);

        mListView.setAdapter(myListViewAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String buildingCode = (String) myListViewAdapter.getItem(i);
                Intent intent = new Intent(getContext(), BuildingPageActivity.class);
                intent.putExtra(MyDataManager.CONTENT_TYPE_BUILDING_CODE, buildingCode);
                getActivity().startActivityForResult(intent, MainActivity.REQUEST_UPDATE_CARDS);
            }
        });
    }
}

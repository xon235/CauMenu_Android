package com.yisuho.caumenu;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xon23 on 2016-08-19.
 */

interface TopScrollable {
    public void scrollToTop();
}

public class PlaceholderFragment extends Fragment implements TopScrollable {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mTopPosition;
    private View mRootView;

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(24);

        mLayoutManager = new LinearLayoutManagerWithSmoothScroller(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        setNewAdapterData(MyDataManager.getCards(), true);

        MyDataManager.setOnDataLoadedListener(new OnMyDataLoadedListener() {

            @Override
            public void onMyDataLoaded(Card[] cards) {
                setNewAdapterData(cards, cards != null);
            }
        });

        if (MyDataManager.getCards() == null) {
            MyDataManager.update(getContext());
        }

        return mRootView;
    }

    public void setNewAdapterData(Card[] cards, boolean successCode) {
        MyCardAdapter myCardAdapter = new MyCardAdapter(cards, getActivity());
        mRecyclerView.setAdapter(myCardAdapter);

        View progressBar = mRootView.findViewById(R.id.Pb);
        View failTv = mRootView.findViewById(R.id.failTv);

        if(successCode){
            progressBar.setVisibility(View.VISIBLE);
            failTv.setVisibility(View.INVISIBLE);
            if (myCardAdapter.getItemCount() != 0) {
                progressBar.setVisibility(View.INVISIBLE);
                failTv.setVisibility(View.INVISIBLE);
                for (int i = 0; i < cards.length; i++) {
                    if (cards[i].getTimeMark() == 0) {
                        mTopPosition = i;
                        mLayoutManager.scrollToPosition(mTopPosition);
                        return;
                    }
                }
            }
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            failTv.setVisibility(View.VISIBLE);
        }


        mTopPosition = 0;
        mLayoutManager.scrollToPosition(mTopPosition);
        return;
    }

    @Override
    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(mTopPosition);
    }

    public class LinearLayoutManagerWithSmoothScroller extends LinearLayoutManager {

        public LinearLayoutManagerWithSmoothScroller(Context context) {
            super(context, VERTICAL, false);
        }

        public LinearLayoutManagerWithSmoothScroller(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                           int position) {
            RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }

        private class TopSnappedSmoothScroller extends LinearSmoothScroller {
            public TopSnappedSmoothScroller(Context context) {
                super(context);

            }

            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return LinearLayoutManagerWithSmoothScroller.this
                        .computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected int getVerticalSnapPreference() {
                return SNAP_TO_START;
            }
        }
    }
}

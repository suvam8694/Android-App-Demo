package com.shubham.gupta.prokure;


import android.os.Bundle;
import android.support.library21.custom.SwipeRefreshLayoutBottom;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Random;

/**
 * Created by shubham on 9/27/15.
 */
public class HomeFragment extends Fragment
{
    boolean allowScrollUpdate = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.home_layout, container, false);

        // Set up RecyclerView
        RecyclerViewAdapter adapter = addContentsToRecycler(rootView);

        // Set up SwipeRefreshLayout
        setSwipeRefreshLayout(rootView, adapter);

        return rootView;
    }

    private RecyclerViewAdapter addContentsToRecycler(View rootView)
    {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.homeRecyclerView);

        recyclerView.setHasFixedSize(false);  // New items will be loaded dynamically

        // Use a grid layout
        final GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
        {
            @Override
            public int getSpanSize(int position)
            {
                switch (position)
                {
                    case 0: // Banner
                        return 2;
                    case 1: // Search Bar
                        return 2;
                    case 2: // Our products label
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);

        // Set the adapter
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext());
        recyclerView.setAdapter(adapter);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (adapter.getItemCount() - layoutManager.findLastVisibleItemPosition() < 3)   // Only few views remaining
                {
                    if ((new Random()).nextInt(10) <= 7 && allowScrollUpdates()) // Scrolling adds items 70% of the time
                        adapter.addNewItems();
                    else
                        setAllowScrollUpdates(false);  // Disable scroll updates, swipe refresh layout will re-enable it
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        return adapter;
    }

    private boolean allowScrollUpdates()
    {
        return allowScrollUpdate;
    }

    private void setAllowScrollUpdates(boolean value)
    {
        allowScrollUpdate = value;
    }

    private void setSwipeRefreshLayout(View rootView, final RecyclerViewAdapter adapter)
    {
        final SwipeRefreshLayoutBottom swipeRefreshLayout = (SwipeRefreshLayoutBottom) rootView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.action_bar_text);
        swipeRefreshLayout.setProgressBackgroundColor(R.color.action_bar_background);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayoutBottom.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                adapter.addNewItems(swipeRefreshLayout);
                setAllowScrollUpdates(true);
            }
        });
    }
}
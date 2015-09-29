package com.shubham.gupta.prokure;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.support.library21.custom.SwipeRefreshLayoutBottom;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by shubham on 9/28/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
{
    private final int NUM_IMAGES = 3;
    private final int NUM_PRODUCTS = 5; // Number of distinct products has been hardcoded here
    private final int NUM_ITEM_INCREMENT = 10;  // Number of new items added per refresh

    private Context context;
    private int num_items;
    private ArrayList<String> itemNames; // Name of different items
    private ArrayList<Integer> itemImages; // Images of different items
    private ArrayList<Integer> mapping;  // Mapping from item position to index in itemNames and itemImages
    private boolean loadingInProgress;  // Whether AsyncTask is loading new items or not

    public RecyclerViewAdapter(Context context)
    {
        this.context = context;
        loadingInProgress = false;
        initData();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        android.util.Log.e("Prokure", "Update Called");
        switch (viewType)
        {
            case 0:
                View bannerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_banner_layout, parent, false);
                return new TopBannerViewHolder(bannerView);
            case 1:
                View searchBarView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_bar_layout, parent, false);
                return new SearchBarViewHolder(searchBarView);
            case 2:
                View labelView = LayoutInflater.from(parent.getContext()).inflate(R.layout.our_products_layout, parent, false);
                return new LabelViewHolder(labelView);
            default:
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
                return new ItemViewHolder(itemView, viewType); // viewType contains position of item
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        if (position > 2)   // To avoid 'banner', 'search bar', and 'our products' layout
            ((ItemViewHolder) holder).setData(position - 3);
    }

    @Override
    public int getItemCount()
    {
        return num_items + 3;       // +3 for 'banner', 'searchbar' and 'our products' layout
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    // Generic class for a ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View v)
        {
            super(v);
        }
    }

    // Class for TopBanner
    private class TopBannerViewHolder extends RecyclerViewAdapter.ViewHolder
    {
        private ImageView bannerImageView1;
        private ImageView bannerImageView2;

        public TopBannerViewHolder(View v)
        {
            super(v);
            bannerImageView1 = (ImageView) v.findViewById(R.id.top_banner_imageview_1);
            bannerImageView2 = (ImageView) v.findViewById(R.id.top_banner_imageview_2);

            // Start the slideshow
            startSlideshow(v);
        }

        private void startSlideshow(final View rootView)
        {
            final int images[] = new int[NUM_IMAGES];
            TypedArray array = context.getResources().obtainTypedArray(R.array.banner_image_array);
            for (int j = 0; j < array.length(); j++)
                images[j] = array.getResourceId(j, 0);

            array.recycle();

            // Start slideshow
            Runnable r = new Runnable()
            {
                // Get the indicator imageviews
                ImageView left = (ImageView) rootView.findViewById(R.id.top_banner_indicator_0);
                ImageView middle = (ImageView) rootView.findViewById(R.id.top_banner_indicator_1);
                ImageView right = (ImageView) rootView.findViewById(R.id.top_banner_indicator_2);

                int i = 1;
                int last = 1;   // Last imageView displayed

                public void run()
                {
                    left.setBackgroundResource(R.color.indicator_light);
                    middle.setBackgroundResource(R.color.indicator_light);
                    right.setBackgroundResource(R.color.indicator_light);
                    switch (i)
                    {
                        case 0:
                            left.setBackgroundResource(R.color.indicator_dark);
                            break;
                        case 1:
                            middle.setBackgroundResource(R.color.indicator_dark);
                            break;
                        case 2:
                            right.setBackgroundResource(R.color.indicator_dark);
                            break;
                    }

                    if (last == 1)
                    {
                        bannerImageView2.setImageResource(images[i++]);

                        // Send bannerImageView1 out
                        Animation animationOut = AnimationUtils.loadAnimation(context, R.anim.slide_out);
                        bannerImageView1.startAnimation(animationOut);
                        bannerImageView1.setVisibility(View.GONE);

                        // Slide bannerImageView2 in
                        bannerImageView2.setVisibility(View.VISIBLE);
                        Animation animationIn = AnimationUtils.loadAnimation(context, R.anim.slide_in);
                        bannerImageView2.startAnimation(animationIn);

                        bannerImageView2.postDelayed(this, 2000);
                        last = 2;
                    } else
                    {
                        bannerImageView1.setImageResource(images[i++]);

                        // Slide bannerImageView2 out
                        Animation animationOut = AnimationUtils.loadAnimation(context, R.anim.slide_out);
                        bannerImageView2.startAnimation(animationOut);
                        bannerImageView2.setVisibility(View.GONE);

                        // Slide bannerImageView1 in
                        bannerImageView1.setVisibility(View.VISIBLE);
                        Animation animationIn = AnimationUtils.loadAnimation(context, R.anim.slide_in);
                        bannerImageView1.startAnimation(animationIn);

                        bannerImageView1.postDelayed(this, 2000);
                        last = 1;
                    }
                    if (i >= images.length)
                        i = 0;
                }
            };

            bannerImageView1.postDelayed(r, 2000); // set first time for 2 second
        }

    }

    // Class for SearchBar
    private class SearchBarViewHolder extends RecyclerViewAdapter.ViewHolder
    {
        public SearchBarViewHolder(View v)
        {
            super(v);
        }
    }

    // Class for Item
    private class ItemViewHolder extends RecyclerViewAdapter.ViewHolder
    {
        private ImageView imageView;
        private TextView textView;

        public ItemViewHolder(View v, int position)
        {
            super(v);

            imageView = (ImageView) v.findViewById(R.id.item_image_view);
            textView = (TextView) v.findViewById(R.id.item_name_text_view);

            if (position % 2 == 0)  // Card on right
                ((GridLayoutManager.LayoutParams) v.getLayoutParams()).setMargins(12, 12, 24, 12);
            else // Card on left
                ((GridLayoutManager.LayoutParams) v.getLayoutParams()).setMargins(24, 12, 12, 12);
        }

        public void setData(int position)
        {
            // Position must be the index NOT COUNTING first three views in RecyclerView
            // Load the appropriate image and name into this view
            imageView.setImageResource(itemImages.get(mapping.get(position)));
            textView.setText(itemNames.get(mapping.get(position)));
        }
    }

    private class LabelViewHolder extends RecyclerViewAdapter.ViewHolder
    {
        public LabelViewHolder(View v)
        {
            super(v);
        }
    }

    private void initData()
    {
        num_items = 10; // Initial number of items has been hardcoded here

        // Initialize the arrays
        mapping = new ArrayList<>();
        itemImages = new ArrayList<>();
        itemNames = new ArrayList<>();

        // Randomly assign items
        Random random = new Random();
        for (int i = 0; i < num_items; i++)
            mapping.add(i, random.nextInt(NUM_PRODUCTS));

        // Load the images
        TypedArray imagesArray = context.getResources().obtainTypedArray(R.array.product_image_array);
        for (int i = 0; i < imagesArray.length(); i++)
            itemImages.add(i, imagesArray.getResourceId(i, 0));
        imagesArray.recycle();

        // Load the item names
        TypedArray namesArray = context.getResources().obtainTypedArray(R.array.product_name_array);
        for (int i = 0; i < namesArray.length(); i++)
            itemNames.add(i, namesArray.getString(i));
        namesArray.recycle();
    }

    public void addNewItems(SwipeRefreshLayoutBottom swipeRefreshLayout)
    {
        addNewItems();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void addNewItems()
    {
        // Randomly assign items
        AsyncTask task = new AsyncTask()
        {
            @Override
            protected Object doInBackground(Object[] params)
            {
                loadingInProgress = true;
                Random random = new Random();
                for (int i = 0; i < NUM_ITEM_INCREMENT; i++)
                    mapping.add(num_items + i, random.nextInt(NUM_PRODUCTS));

                num_items += NUM_ITEM_INCREMENT;
                loadingInProgress = false;
                return null;
            }

            @Override
            protected void onPostExecute(Object o)
            {
                notifyDataSetChanged();
                super.onPostExecute(o);
            }
        };

        if (!loadingInProgress)
            task.execute();
    }
}

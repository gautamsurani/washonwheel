package com.washonwheel.android.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.Utils;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_ITEM = 1;
    private String mNavTitles[];    // String Array to store the passed titles Value from LoginActivity.java
    private int mIcons[];           // Int Array to store the passed icons resource value from LoginActivity.java
    private String name;            //String Resource for header View Name
    private int profile;            //int Resource for header view profile picture
    private String email;
    Context context;//String Resource for header view email

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;
        RelativeLayout rr;
        TextView textView;
        ImageView imageView;
        ImageView profile;
        TextView Name;
        TextView email;

        public ViewHolder(View itemView, int ViewType) {
            super(itemView);
            if (ViewType == TYPE_ITEM) {
                rr = itemView.findViewById(R.id.rr_nav);
                textView = itemView.findViewById(R.id.title); // Creating TextView object with the id of textView from item_row.xml
                imageView = itemView.findViewById(R.id.icon);// Creating ImageView object with the id of ImageView from item_row.xml
                Holderid = 1;                                            // setting holder id as 1 as the object being populated are of type item row
            } else {
                Name = itemView.findViewById(R.id.txtname);         // Creating Text View object from header.xml for name
                email = itemView.findViewById(R.id.txtemail);       // Creating Text View object from header.xml for email
                profile = itemView.findViewById(R.id.circleView);// Creating Image view object from header.xml for profile pic
                Holderid = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
            }
        }
    }

    public MyAdapter(Context context, String Titles[], int Icons[], String Name, String Email, int Profile) {
        // MyAdapter Constructor with titles and icons parameter
        // titles, icons, name, email, profile pic are passed from the main activity as we
        mNavTitles = Titles;
        this.context = context;
        mIcons = Icons;
        name = Name;
        email = Email;
        profile = Profile;                     //here we assign those passed values to the values we declared here
        //in adapter
    }

    // Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    // Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_list, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
            return vhItem;
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false); //Inflating the layout
            ViewHolder vhHeader = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
            return vhHeader; //returning the object created
        }
        return null;
    }

    // Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        if (holder.Holderid == 1) {
            // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image

            String i = mNavTitles[position - 1];
            holder.textView.setText(i); // Setting the Text with the array of our Titles
            holder.imageView.setImageResource(mIcons[position - 1]);// Settimg the image with array of our icons

            if (i.equals("Notification") || i.equals("Rate Us")) {
                holder.rr.setBackgroundResource(R.drawable.line_half);
            } else {
                holder.rr.setBackgroundResource(R.drawable.line_remove);
            }

        } else {
            holder.profile.setImageResource(profile);

            Glide.with(context)
                    .load(Utils.getUserImage(context))
                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.default_icon)
                    .into(holder.profile);

            holder.Name.setText(name);
            holder.email.setText(email);
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mNavTitles.length + 1; // the number of items in the list will be +1 the titles including the header view.
    }

    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}

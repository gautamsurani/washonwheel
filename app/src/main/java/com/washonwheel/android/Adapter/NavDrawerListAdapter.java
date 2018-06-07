package com.washonwheel.android.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.washonwheel.android.Pojo.NavDrawerItem;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.PrefManager;

import java.util.ArrayList;

public class NavDrawerListAdapter extends BaseAdapter
{
	private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;
    public static boolean flag=false;
    PrefManager pref;
    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems)
	{
        this.context = context;
        this.navDrawerItems = navDrawerItems;
		pref=new PrefManager(context);
    }
 
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		if (convertView == null)
		{
			LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.drawer_list, null);
		}

		ImageView imgIcon = convertView.findViewById(R.id.icon);
		TextView txtTitle = convertView.findViewById(R.id.title);
		imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
		txtTitle.setText(navDrawerItems.get(position).getTitle());

		// check whether it set visible or not
		/*if(navDrawerItems.get(position).getCounterVisibility()){
			txtCount.setText(navDrawerItems.get(position).getCount());
		}else{
			// hide the counter view
			txtCount.setVisibility(View.GONE);
		}*/

		return convertView;
	}


}

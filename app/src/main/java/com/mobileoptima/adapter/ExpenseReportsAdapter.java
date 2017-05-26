package com.mobileoptima.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.cache.TypefaceCache;
import com.mobileoptima.model.ExpenseReportsObj;
import com.mobileoptima.tarkieattendance.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class ExpenseReportsAdapter extends ArrayAdapter<ExpenseReportsObj> {
	private DisplayImageOptions options;
	private ArrayList<ExpenseReportsObj> items;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private Typeface light, bold;

	public ExpenseReportsAdapter(Context context, ArrayList<ExpenseReportsObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		Resources res = context.getResources();
		this.imageLoader = ImageLoader.getInstance();
		if(!imageLoader.isInited()) {
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_user_placeholder)
				.showImageForEmptyUri(R.drawable.ic_user_placeholder)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
		light = TypefaceCache.get(context.getAssets(), res.getString(R.string.proxima_nova_light));
		bold = TypefaceCache.get(context.getAssets(), res.getString(R.string.proxima_nova_bold));
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final ExpenseReportsObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.announcements_list_row, parent, false);
				holder = new ViewHolder();
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
		}
		return view;
	}

	private class ViewHolder {
	}
}

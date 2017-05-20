package com.mobileoptima.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.EntryObj;
import com.mobileoptima.tarkieattendance.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class EntriesAdapter extends ArrayAdapter<EntryObj> {

	private DisplayImageOptions options;
	private ArrayList<EntryObj> items;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private int orange, green, red;

	public EntriesAdapter(Context context, ArrayList<EntryObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		Resources res = context.getResources();
		this.orange = res.getColor(R.color.orange_pri);
		this.green = res.getColor(R.color.green_pri);
		this.red = res.getColor(R.color.red_pri);
		this.imageLoader = ImageLoader.getInstance();
		if(!imageLoader.isInited()) {
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.color.gray_qua)
				.showImageForEmptyUri(R.color.gray_qua)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final EntryObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.entries_list_row, parent, false);
				holder = new ViewHolder();
				holder.tvTitleEntries = (CodePanLabel) view.findViewById(R.id.tvTitleEntries);
				holder.tvDateEntries = (CodePanLabel) view.findViewById(R.id.tvDateEntries);
				holder.tvStatusEntries = (CodePanLabel) view.findViewById(R.id.tvStatusEntries);
				holder.tvStoreEntries = (CodePanLabel) view.findViewById(R.id.tvStoreEntries);
				holder.tvReferenceNoEntries = (CodePanLabel) view.findViewById(R.id.tvReferenceNoEntries);
				holder.cbEntries = (CheckBox) view.findViewById(R.id.cbEntries);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(obj.form != null) {
				holder.tvTitleEntries.setText(obj.form.name);
			}
			if(obj.store != null) {
				holder.tvStoreEntries.setText(obj.store.name);
				holder.tvStoreEntries.setVisibility(View.VISIBLE);
			}
			else {
				holder.tvStoreEntries.setVisibility(View.GONE);
			}
			if(obj.isSubmit) {
				holder.tvStatusEntries.setTextColor(green);
				holder.tvStatusEntries.setText(R.string.submitted_caps);
				holder.cbEntries.setVisibility(View.GONE);
			}
			else {
				if(!obj.isDelete) {
					holder.tvStatusEntries.setTextColor(orange);
					holder.tvStatusEntries.setText(R.string.draft_caps);
					if(obj.isHighlight) {
						holder.cbEntries.setVisibility(View.VISIBLE);
						holder.cbEntries.setChecked(obj.isCheck);
					}
					else {
						holder.cbEntries.setVisibility(View.GONE);
					}
				}
				else {
					holder.tvStatusEntries.setTextColor(red);
					holder.tvStatusEntries.setText(R.string.deleted_caps);
					holder.cbEntries.setVisibility(View.GONE);
				}
			}
			if(obj.dDate != null) {
				String date = CodePanUtils.getCalendarDate(obj.dDate, true, true);
				holder.tvDateEntries.setText(date);
			}
			if(obj.referenceNo != null) {
				holder.tvReferenceNoEntries.setText(obj.referenceNo);
			}
			else {
				holder.tvReferenceNoEntries.setText(R.string.pending_caps);
			}
		}
		return view;
	}

	private class ViewHolder {
		private CodePanLabel tvTitleEntries;
		private CodePanLabel tvStatusEntries;
		private CodePanLabel tvDateEntries;
		private CodePanLabel tvStoreEntries;
		private CodePanLabel tvReferenceNoEntries;
		private CheckBox cbEntries;
	}
}

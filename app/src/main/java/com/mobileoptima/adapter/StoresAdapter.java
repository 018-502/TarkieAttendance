package com.mobileoptima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.tarkieattendance.R;

import java.util.ArrayList;

public class StoresAdapter extends ArrayAdapter<StoreObj> {

	private ArrayList<StoreObj> items;
	private LayoutInflater inflater;

	public StoresAdapter(Context context, ArrayList<StoreObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final StoreObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.stores_list_row, parent, false);
				holder = new ViewHolder();
				holder.llHeader = (LinearLayout) view.findViewById(R.id.llHeader);
				holder.llFooter = (LinearLayout) view.findViewById(R.id.llFooter);
				holder.tvHeader = (CodePanLabel) view.findViewById(R.id.tvHeader);
				holder.tvFooter = (CodePanLabel) view.findViewById(R.id.tvFooter);
				holder.tvHeaderMain = (CodePanLabel) view.findViewById(R.id.tvHeaderMain);
				holder.tvStoreName = (CodePanLabel) view.findViewById(R.id.tvStoreName);
				holder.vDivider = view.findViewById(R.id.vDivider);
				holder.vHeader = view.findViewById(R.id.vHeader);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(position != 0) {
				holder.tvHeaderMain.setVisibility(View.GONE);
				holder.vHeader.setVisibility(View.GONE);
			}
			else {
				holder.vHeader.setVisibility(View.VISIBLE);
				holder.tvHeaderMain.setVisibility(View.VISIBLE);
				holder.tvHeaderMain.setText(obj.header);
			}
			if(obj.isHeader) {
				holder.llHeader.setVisibility(View.VISIBLE);
				if(obj.header != null) {
					holder.tvHeader.setText(obj.header);
				}
			}
			else {
				holder.llHeader.setVisibility(View.GONE);
			}
			if(obj.isFooter) {
				holder.llFooter.setVisibility(View.VISIBLE);
				holder.vDivider.setVisibility(View.GONE);
				if(obj.footer != null) {
					holder.tvFooter.setText(obj.footer);
				}
			}
			else {
				holder.llFooter.setVisibility(View.GONE);
				holder.vDivider.setVisibility(View.VISIBLE);
			}
			if(obj.name != null) {
				holder.tvStoreName.setText(obj.name);
			}
		}
		return view;
	}

	private class ViewHolder {
		private LinearLayout llHeader;
		private LinearLayout llFooter;
		private CodePanLabel tvHeader;
		private CodePanLabel tvFooter;
		private CodePanLabel tvStoreName;
		private CodePanLabel tvHeaderMain;
		private View vDivider;
		private View vHeader;
	}
}

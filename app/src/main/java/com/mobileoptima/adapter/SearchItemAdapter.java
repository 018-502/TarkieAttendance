package com.mobileoptima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.SearchObj;
import com.mobileoptima.tarkieattendance.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

public class SearchItemAdapter extends ArrayAdapter<SearchObj> {

	private DisplayImageOptions options;
	private ArrayList<SearchObj> items;
	private LayoutInflater inflater;

	public SearchItemAdapter(Context context, ArrayList<SearchObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final SearchObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.search_item_list_row, parent, false);
				holder = new ViewHolder();
				holder.tvNameSearchItem = (CodePanLabel) view.findViewById(R.id.tvNameSearchItem);
				holder.tvCountSearchItem = (CodePanLabel) view.findViewById(R.id.tvCountSearchItem);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			holder.tvNameSearchItem.setText(obj.name);
			holder.tvCountSearchItem.setText(obj.count);
		}
		return view;
	}

	private class ViewHolder {
		private CodePanLabel tvNameSearchItem;
		private CodePanLabel tvCountSearchItem;
	}
}

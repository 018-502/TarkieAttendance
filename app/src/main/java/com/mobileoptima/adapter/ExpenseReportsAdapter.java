package com.mobileoptima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mobileoptima.model.ExpenseReportsObj;
import com.mobileoptima.tarkieattendance.R;

import java.util.ArrayList;

public class ExpenseReportsAdapter extends ArrayAdapter<ExpenseReportsObj> {
	private ArrayList<ExpenseReportsObj> items;
	private LayoutInflater inflater;

	public ExpenseReportsAdapter(Context context, ArrayList<ExpenseReportsObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
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

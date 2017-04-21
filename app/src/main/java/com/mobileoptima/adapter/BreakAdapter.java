package com.mobileoptima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.BreakObj;
import com.mobileoptima.tarkieattendance.R;

import java.util.ArrayList;

public class BreakAdapter extends ArrayAdapter<BreakObj> {

	private ArrayList<BreakObj> items;
	private LayoutInflater inflater;

	public BreakAdapter(Context context, ArrayList<BreakObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final BreakObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.break_list_row, parent, false);
				holder = new ViewHolder();
				holder.tvNameForm = (CodePanLabel) view.findViewById(R.id.tvNameForm);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(obj.name != null) {
				holder.tvNameForm.setText(obj.name);
			}
			if(obj.isDone) {
				holder.tvNameForm.setEnabled(false);
			}
			else {
				holder.tvNameForm.setEnabled(true);
			}
			if(items.indexOf(obj) < items.size() - 1) {
				holder.tvNameForm.setBackgroundResource(R.drawable.state_rect_trans_dark);
			}
			else {
				holder.tvNameForm.setBackgroundResource(R.drawable.state_rect_trans_rad_bot_five);
			}
		}
		return view;
	}

	private class ViewHolder {
		private CodePanLabel tvNameForm;
	}
}

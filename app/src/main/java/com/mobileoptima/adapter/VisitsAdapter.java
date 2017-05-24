package com.mobileoptima.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.CheckInObj;
import com.mobileoptima.model.CheckOutObj;
import com.mobileoptima.model.VisitObj;
import com.mobileoptima.tarkieattendance.R;

import java.util.ArrayList;

public class VisitsAdapter extends ArrayAdapter<VisitObj> {

	private ArrayList<VisitObj> items;
	private LayoutInflater inflater;
	private int orange, green;

	public VisitsAdapter(Context context, ArrayList<VisitObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		Resources res = context.getResources();
		orange = res.getColor(R.color.orange_pri);
		green = res.getColor(R.color.green_pri);
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final VisitObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.visits_list_row, parent, false);
				holder = new ViewHolder();
				holder.tvNameVisits = (CodePanLabel) view.findViewById(R.id.tvNameVisits);
				holder.tvStatusVisits = (CodePanLabel) view.findViewById(R.id.tvStatusVisits);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(obj.name != null) {
				holder.tvNameVisits.setText(obj.name);
			}
			if(obj.in == null && obj.out == null) {
				holder.tvStatusVisits.setText(R.string.active_caps);
				holder.tvStatusVisits.setTextColor(orange);
			}
			else {
				String status = null;
				if(obj.in != null) {
					CheckInObj in = obj.in;
					status = in.dTime + " - ";
				}
				if(obj.out != null) {
					CheckOutObj out = obj.out;
					status += out.dTime;
				}
				else {
					status += "NO OUT";
				}
				holder.tvStatusVisits.setTextColor(green);
				holder.tvStatusVisits.setText(status);
			}
		}
		return view;
	}

	private class ViewHolder {
		private CodePanLabel tvNameVisits;
		private CodePanLabel tvStatusVisits;
	}
}

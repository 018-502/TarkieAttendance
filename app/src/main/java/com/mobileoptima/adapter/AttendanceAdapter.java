package com.mobileoptima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.AttendanceObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;
import com.mobileoptima.tarkieattendance.R;

import java.util.ArrayList;

public class AttendanceAdapter extends ArrayAdapter<AttendanceObj> {

	private ArrayList<AttendanceObj> items;
	private LayoutInflater inflater;
	private String timeOut;

	public AttendanceAdapter(Context context, ArrayList<AttendanceObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.timeOut = context.getResources().getString(R.string.no_out_caps);
		this.items = items;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final AttendanceObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.attendance_list_row, parent, false);
				holder = new ViewHolder();
				holder.tvDateAttendance = (CodePanLabel) view.findViewById(R.id.tvDateAttendance);
				holder.tvTimeAttendance = (CodePanLabel) view.findViewById(R.id.tvTimeAttendance);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(obj.out != null) {
				TimeOutObj out = obj.out;
				TimeInObj in = out.timeIn;
				String dateIn = CodePanUtils.getCalendarDate(in.dDate, false, true);
				holder.tvDateAttendance.setText(dateIn);
				String timeIn = CodePanUtils.getNormalTime(in.dTime, false);
				String timeOut = !in.isTimeOut ? this.timeOut :
						CodePanUtils.getNormalTime(out.dTime, false);
				String time = timeIn + " - " + timeOut;
				holder.tvTimeAttendance.setText(time);
			}
		}
		return view;
	}

	private class ViewHolder {
		private CodePanLabel tvDateAttendance;
		private CodePanLabel tvTimeAttendance;
	}
}

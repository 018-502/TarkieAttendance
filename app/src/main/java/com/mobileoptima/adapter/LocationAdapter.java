package com.mobileoptima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.LocationObj;
import com.mobileoptima.tarkieattendance.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class LocationAdapter extends ArrayAdapter<LocationObj> {

	private ArrayList<LocationObj> items;
	private LayoutInflater inflater;
	private DecimalFormat df;
	private NumberFormat nf;

	public LocationAdapter(Context context, ArrayList<LocationObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		df = new DecimalFormat("#");
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(6);
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final LocationObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.location_list_row, parent, false);
				holder = new ViewHolder();
				holder.tvDateLocation = (CodePanLabel) view.findViewById(R.id.tvDateLocation);
				holder.tvTimeLocation = (CodePanLabel) view.findViewById(R.id.tvTimeLocation);
				holder.tvCoordinatesLocation = (CodePanLabel) view.findViewById(R.id.tvCoordinatesLocation);
				holder.tvAccuracyLocation = (CodePanLabel) view.findViewById(R.id.tvAccuracyLocation);
				holder.tvProviderLocation = (CodePanLabel) view.findViewById(R.id.tvProviderLocation);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			holder.tvDateLocation.setText(obj.dDate);
			holder.tvTimeLocation.setText(obj.dTime);
			String coordinates = nf.format(obj.longitude) + "\n" + nf.format(obj.latitude);
			holder.tvCoordinatesLocation.setText(coordinates);
			holder.tvAccuracyLocation.setText(df.format(obj.accuracy));
			holder.tvProviderLocation.setText(obj.provider);
		}
		return view;
	}

	private class ViewHolder {
		private CodePanLabel tvDateLocation;
		private CodePanLabel tvTimeLocation;
		private CodePanLabel tvCoordinatesLocation;
		private CodePanLabel tvAccuracyLocation;
		private CodePanLabel tvProviderLocation;
	}
}

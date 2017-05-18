package com.mobileoptima.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileoptima.model.VisitsDateObj;
import com.mobileoptima.tarkieattendance.R;

import java.util.ArrayList;

public class VisitsDateAdapter extends PagerAdapter {

	private ArrayList<VisitsDateObj> dateList;
	private LayoutInflater inflater;
	private Context context;

	public VisitsDateAdapter(Context context, ArrayList<VisitsDateObj> dateList) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.dateList = dateList;
		this.context = context;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return dateList.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = inflater.inflate(R.layout.visits_date_item, container, false);


		return view;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}
}

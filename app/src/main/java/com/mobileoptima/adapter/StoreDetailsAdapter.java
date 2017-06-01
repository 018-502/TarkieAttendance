package com.mobileoptima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.ContactObj;
import com.mobileoptima.tarkieattendance.R;

import java.util.ArrayList;

import static android.view.View.GONE;

public class StoreDetailsAdapter extends ArrayAdapter<ContactObj> {

	private ArrayList<ContactObj> contactList;
	private LayoutInflater inflater;

	public StoreDetailsAdapter(Context context, ArrayList<ContactObj> contactList) {
		super(context, 0, contactList);
		this.contactList = contactList;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if(view == null) {
			view = inflater.inflate(R.layout.store_details_list_row, parent, false);
			holder = new ViewHolder();
			holder.tvEmployee = (CodePanLabel) view.findViewById(R.id.tvEmployee);
			holder.tvDesignation = (CodePanLabel) view.findViewById(R.id.tvPosition);
			holder.tvMobile = (CodePanLabel) view.findViewById(R.id.tvCell);
			holder.tvLandline = (CodePanLabel) view.findViewById(R.id.tvPhone);
			holder.tvEmail = (CodePanLabel) view.findViewById(R.id.tvEmail);
			holder.tvBirthday = (CodePanLabel) view.findViewById(R.id.tvBirthday);
			holder.tvRemarks = (CodePanLabel) view.findViewById(R.id.tvRemarks);
			holder.llDesignation = (LinearLayout) view.findViewById(R.id.llDesignation);
			holder.llMobile = (LinearLayout) view.findViewById(R.id.llMobile);
			holder.llLandline = (LinearLayout) view.findViewById(R.id.llLandline);
			holder.llEmail = (LinearLayout) view.findViewById(R.id.llEmail);
			holder.llBirthday = (LinearLayout) view.findViewById(R.id.llBirthday);
			holder.llRemarks = (LinearLayout) view.findViewById(R.id.llRemarks);
			view.setTag(holder);
		}
		else {
			holder = (ViewHolder) view.getTag();
		}
		ContactObj obj = contactList.get(position);
		if(obj.name != null) {
			holder.tvEmployee.setText(obj.name);
		}
		if(obj.designation != null && obj.designation.isEmpty()) {
			holder.tvDesignation.setText(obj.designation);
		}
		else {
			holder.llDesignation.setVisibility(GONE);
		}
		if(obj.mobile != null && obj.mobile.isEmpty()) {
			holder.tvMobile.setText(obj.mobile);
		}
		else {
			holder.llMobile.setVisibility(GONE);
		}
		if(obj.landline != null && obj.landline.isEmpty()) {
			holder.tvLandline.setText(obj.landline);
		}
		else {
			holder.llLandline.setVisibility(GONE);
		}
		if(obj.email != null && !obj.email.isEmpty()) {
			holder.tvEmail.setText(obj.email);
		}
		else {
			holder.llEmail.setVisibility(GONE);
		}
		if(obj.birthday != null) {
			holder.tvBirthday.setText(obj.birthday);
		}
		else {
			holder.llBirthday.setVisibility(GONE);
		}
		if(obj.remarks != null && !obj.remarks.isEmpty()) {
			holder.tvRemarks.setText(obj.remarks);
		}
		else {
			holder.llRemarks.setVisibility(GONE);
		}
		return view;
	}

	private class ViewHolder {
		private CodePanLabel tvEmployee;
		private CodePanLabel tvDesignation;
		private CodePanLabel tvMobile;
		private CodePanLabel tvLandline;
		private CodePanLabel tvEmail;
		private CodePanLabel tvBirthday;
		private CodePanLabel tvRemarks;
		private LinearLayout llDesignation;
		private LinearLayout llMobile;
		private LinearLayout llLandline;
		private LinearLayout llEmail;
		private LinearLayout llBirthday;
		private LinearLayout llRemarks;
	}
}

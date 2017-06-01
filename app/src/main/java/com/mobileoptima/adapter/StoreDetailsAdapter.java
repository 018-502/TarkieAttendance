package com.mobileoptima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.ContactObj;
import com.mobileoptima.tarkieattendance.R;

import java.util.ArrayList;

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
			view = inflater.inflate(R.layout.contact_list_row, parent, false);
			holder = new ViewHolder();
			holder.tvEmployee = (CodePanLabel) view.findViewById(R.id.tvEmployee);
			holder.tvPosition = (CodePanLabel) view.findViewById(R.id.tvPosition);
			holder.tvCell = (CodePanLabel) view.findViewById(R.id.tvCell);
			holder.tvPhone = (CodePanLabel) view.findViewById(R.id.tvPhone);
			holder.tvEmail = (CodePanLabel) view.findViewById(R.id.tvEmail);
			holder.tvBirthday = (CodePanLabel) view.findViewById(R.id.tvBirthday);
			holder.tvRemarks = (CodePanLabel) view.findViewById(R.id.tvRemarks);
			view.setTag(holder);
		}
		else {
			holder = (ViewHolder) view.getTag();
		}
		ContactObj obj = contactList.get(position);
		holder.tvEmployee.setText(obj.name);
		holder.tvPosition.setText(obj.position);
		holder.tvCell.setText(obj.mobile);
		holder.tvPhone.setText(obj.landline);
		holder.tvEmail.setText(obj.email);
		holder.tvBirthday.setText(obj.birthday);
		holder.tvRemarks.setText(obj.remarks);
		return view;
	}

	private class ViewHolder {
		private CodePanLabel tvEmployee;
		private CodePanLabel tvPosition;
		private CodePanLabel tvCell;
		private CodePanLabel tvPhone;
		private CodePanLabel tvEmail;
		private CodePanLabel tvBirthday;
		private CodePanLabel tvRemarks;
	}
}

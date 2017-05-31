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

/**
 * Created by IOS on 5/19/2017.
 */

public class ContactsAdapter extends ArrayAdapter<ContactObj> {

	private ArrayList<ContactObj> contactList;
	private LayoutInflater inflater;

	public ContactsAdapter(Context context, ArrayList<ContactObj> contactList) {
		super(context, 0, contactList);
		this.contactList = contactList;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if(view == null) {
			view = inflater.inflate(R.layout.contact_list_row, parent, false);
			holder = new ViewHolder();
			holder.lblEmployee = (CodePanLabel) view.findViewById(R.id.lblEmployee);
			holder.lblPosition = (CodePanLabel) view.findViewById(R.id.lblPosition);
			holder.lblCell = (CodePanLabel) view.findViewById(R.id.lblCell);
			holder.lblPhone = (CodePanLabel) view.findViewById(R.id.lblPhone);
			holder.lblEmail = (CodePanLabel) view.findViewById(R.id.lblEmail);
			holder.lblBirthday = (CodePanLabel) view.findViewById(R.id.lblBirthday);
			holder.lblRemarks = (CodePanLabel) view.findViewById(R.id.lblRemarks);
			view.setTag(holder);
		}
		else {
			holder = (ViewHolder) view.getTag();
		}
		ContactObj obj = contactList.get(position);
		holder.lblEmployee.setText(obj.name);
		holder.lblPosition.setText(obj.position);
		holder.lblCell.setText(obj.cellNo);
		holder.lblPhone.setText(obj.phoneNo);
		holder.lblEmail.setText(obj.email);
		holder.lblBirthday.setText(obj.birthday);
		holder.lblRemarks.setText(obj.remarks);
		return view;
	}

	private class ViewHolder {
		private CodePanLabel lblEmployee;
		private CodePanLabel lblPosition;
		private CodePanLabel lblCell;
		private CodePanLabel lblPhone;
		private CodePanLabel lblEmail;
		private CodePanLabel lblBirthday;
		private CodePanLabel lblRemarks;
	}
}

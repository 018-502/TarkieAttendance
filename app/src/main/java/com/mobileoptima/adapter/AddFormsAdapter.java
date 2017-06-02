package com.mobileoptima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.tarkieattendance.R;

import java.util.ArrayList;

public class AddFormsAdapter extends ArrayAdapter<FormObj> {

	private ArrayList<FormObj> items;
	private LayoutInflater inflater;

	public AddFormsAdapter(Context context, ArrayList<FormObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final FormObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.add_forms_list_row, parent, false);
				holder = new ViewHolder();
				holder.tvNameAddForms = (CodePanLabel) view.findViewById(R.id.tvNameAddForms);
				holder.cbAddForms = (CheckBox) view.findViewById(R.id.cbAddForms);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(obj.name != null) {
				holder.tvNameAddForms.setText(obj.name);
			}
			holder.cbAddForms.setChecked(obj.isCheck);
		}
		return view;
	}

	private class ViewHolder {
		private CodePanLabel tvNameAddForms;
		private CheckBox cbAddForms;
	}
}

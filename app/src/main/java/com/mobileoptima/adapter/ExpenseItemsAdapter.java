package com.mobileoptima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.ExpenseItemsObj;
import com.mobileoptima.tarkieattendance.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ExpenseItemsAdapter extends ArrayAdapter<ExpenseItemsObj> {
	private ArrayList<ExpenseItemsObj> items;
	private LayoutInflater inflater;
	private NumberFormat nf;

	public ExpenseItemsAdapter(Context context, ArrayList<ExpenseItemsObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		nf = NumberFormat.getInstance();
		nf.setGroupingUsed(true);
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final ExpenseItemsObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.expense_items_list_row, parent, false);
				holder = new ViewHolder();
				holder.tvDateExpenseItems = (CodePanLabel) view.findViewById(R.id.tvDateExpenseItems);
				holder.tvTotalExpenseItems = (CodePanLabel) view.findViewById(R.id.tvTotalExpenseItems);
				holder.ivCollapsibleExpenseItems = (ImageView) view.findViewById(R.id.ivCollapsibleExpenseItems);
				holder.llItemsExpenseItems = (LinearLayout) view.findViewById(R.id.llItemsExpenseItems);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(obj.dDate != null) {
				String date = CodePanUtils.getCalendarDate(obj.dDate, true, true);
				holder.tvDateExpenseItems.setText(date);
			}
			String totalAmount = nf.format(obj.totalAmount);
			holder.tvTotalExpenseItems.setText(totalAmount);
			holder.llItemsExpenseItems.removeAllViews();
			if(obj.isAdded && obj.childList != null) {
				for(View child : obj.childList) {
					if(child != null && child.getParent() != null) {
						((ViewGroup) child.getParent()).removeView(child);
					}
					holder.llItemsExpenseItems.addView(child);
				}
			}
			if(obj.isOpen) {
				holder.ivCollapsibleExpenseItems.setImageResource(R.drawable.ic_up_dark);
				holder.llItemsExpenseItems.setVisibility(View.VISIBLE);
			}
			else {
				holder.ivCollapsibleExpenseItems.setImageResource(R.drawable.ic_down_dark);
				holder.llItemsExpenseItems.setVisibility(View.GONE);
			}
		}
		return view;
	}

	private class ViewHolder {
		private CodePanLabel tvDateExpenseItems;
		private CodePanLabel tvTotalExpenseItems;
		private ImageView ivCollapsibleExpenseItems;
		private LinearLayout llItemsExpenseItems;
	}
}
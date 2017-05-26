package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.adapter.ExpenseItemsAdapter;
import com.mobileoptima.core.Data;
import com.mobileoptima.model.ExpenseItemsObj;
import com.mobileoptima.model.ExpenseObj;

import java.text.NumberFormat;
import java.util.ArrayList;

import static android.view.View.*;
import static android.view.View.GONE;
import static android.view.View.OnLongClickListener;

public class ExpenseItemsFragment extends Fragment implements OnClickListener {
	private ArrayList<ExpenseItemsObj> expenseItemsList;
	private ExpenseItemsAdapter adapter;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private ListView lvExpenseItems;
	private NumberFormat nf;
	private SQLiteAdapter db;
	private String dDate;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		nf = NumberFormat.getInstance();
		nf.setGroupingUsed(true);
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		db = main.getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.expense_items_layout, container, false);
		lvExpenseItems = (ListView) view.findViewById(R.id.lvExpenseItems);
		lvExpenseItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
				final ExpenseItemsObj obj = expenseItemsList.get(i);
				ImageView ivCollapsibleExpenseItems = (ImageView) view.findViewById(R.id.ivCollapsibleExpenseItems);
				final LinearLayout llItemsExpenseItems = (LinearLayout) view.findViewById(R.id.llItemsExpenseItems);
				if(llItemsExpenseItems.getVisibility() == GONE) {
					if(!obj.isAdded) {
						obj.childList = new ArrayList<>();
						final ArrayList<ExpenseObj> expenseList = Data.loadExpense(db, obj.dDate);
						for(final ExpenseObj expense : expenseList) {
							final String expenseType = expense.expenseType;
							String time = CodePanUtils.getNormalTime(expense.dTime, false);
							final String amount = nf.format(expense.amount);
							final View child = inflater.inflate(R.layout.expense_items_list_row_collapsible, adapterView, false);
							RelativeLayout rlExpenseItems = (RelativeLayout) child.findViewById(R.id.rlExpenseItems);
							CodePanLabel tvExpenseType = (CodePanLabel) child.findViewById(R.id.tvExpenseTypeExpenseItems);
							CodePanLabel tvTime = (CodePanLabel) child.findViewById(R.id.tvTimeExpenseItems);
							CodePanLabel tvAmount = (CodePanLabel) child.findViewById(R.id.tvAmountExpenseItems);
							rlExpenseItems.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									ExpenseItemsDetailsFragment expenseItemsDetails = new ExpenseItemsDetailsFragment();
									expenseItemsDetails.setExpense(expense);
									transaction = getActivity().getSupportFragmentManager().beginTransaction();
									transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
											R.anim.slide_in_ltr, R.anim.slide_out_ltr);
									transaction.add(R.id.rlMain, expenseItemsDetails);
									transaction.addToBackStack(null);
									transaction.commit();
								}
							});
							rlExpenseItems.setOnLongClickListener(new OnLongClickListener() {
								@Override
								public boolean onLongClick(View v) {
									String bold = getString(R.string.proxima_nova_bold);
									String text = getString(R.string.delete_expense_message);
									String message = text + expenseType + " PHP " + amount;
									int start1 = text.length();
									int end1 = start1 + expenseType.length();
									int start2 = end1 + 5;
									int end2 = start2 + amount.length();
									ArrayList<SpannableMap> list = new ArrayList<>();
									list.add(new SpannableMap(getActivity(), bold, start1, end1));
									list.add(new SpannableMap(getActivity(), bold, start2, end2));
									final AlertDialogFragment alert = new AlertDialogFragment();
									alert.setDialogTitle(R.string.delete_expense_title);
									alert.setDialogMessage(message);
									alert.setSpannableList(list);
									alert.setNegativeButton("No", new OnClickListener() {
										@Override
										public void onClick(View view) {
											manager.popBackStack();
										}
									});
									alert.setPositiveButton("Yes", new OnClickListener() {
										@Override
										public void onClick(View view) {
											expenseList.remove(expense);
											llItemsExpenseItems.removeView(child);
											obj.totalAmount -= expense.amount;
											obj.childList.remove(child);
											if(obj.childList.size() == 0) {
												expenseItemsList.remove(obj);
											}
											adapter.notifyDataSetChanged();
											lvExpenseItems.invalidate();
											manager.popBackStack();
										}
									});
									transaction = manager.beginTransaction();
									transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
											R.anim.fade_in, R.anim.fade_out);
									transaction.add(R.id.rlMain, alert);
									transaction.addToBackStack(null);
									transaction.commit();
									return true;
								}
							});
							tvExpenseType.setText(expenseType);
							tvTime.setText(time);
							tvAmount.setText(amount);
							llItemsExpenseItems.addView(child);
							obj.childList.add(child);
							obj.isAdded = true;
						}
					}
					CodePanUtils.expandView(llItemsExpenseItems, true);
					ivCollapsibleExpenseItems.setImageResource(R.drawable.ic_up_dark);
					obj.isOpen = true;
				}
				else {
					CodePanUtils.collapseView(llItemsExpenseItems, true);
					ivCollapsibleExpenseItems.setImageResource(R.drawable.ic_down_dark);
					obj.isOpen = false;
				}
			}
		});
		dDate = "";
		loadExpenseItems(db);
		return view;
	}

	@Override
	public void onClick(View v) {

	}

	public void loadExpenseItems(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					expenseItemsList = Data.loadExpenseItems(db, dDate);
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message message) {
			adapter = new ExpenseItemsAdapter(getActivity(), expenseItemsList);
			lvExpenseItems.setAdapter(adapter);
			return true;
		}
	});
}
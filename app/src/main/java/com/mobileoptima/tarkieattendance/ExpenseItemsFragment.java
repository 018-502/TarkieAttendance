package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.codepan.calendar.callback.Interface.OnPickDateCallback;
import com.codepan.calendar.view.CalendarView;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.adapter.ExpenseItemsAdapter;
import com.mobileoptima.callback.Interface.OnUpdateExpenseCallback;
import com.mobileoptima.constant.DateType;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.ExpenseItemsObj;
import com.mobileoptima.model.ExpenseObj;

import java.text.NumberFormat;
import java.util.ArrayList;

import static android.view.View.OnLongClickListener;

public class ExpenseItemsFragment extends Fragment implements OnClickListener, OnPickDateCallback {
	private ArrayList<ExpenseItemsObj> expenseItemsList;
	private CodePanLabel tvStartDateExpenseItems, tvEndDateExpenseItems;
	private ExpenseItemsAdapter adapter;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private LayoutInflater inflater;
	private ListView lvExpenseItems;
	private MainActivity main;
	private NumberFormat nf;
	private RelativeLayout rlPlaceholderExpenseItems;
	private SQLiteAdapter db;
	private String startDate, endDate;
	private ViewGroup container;
	private int dateType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		nf = NumberFormat.getInstance();
		nf.setGroupingUsed(true);
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		db = main.getDatabase();
		db.openConnection();
		startDate = CodePanUtils.getDateAfter(CodePanUtils.getDate(), 30);
		endDate = CodePanUtils.getDateAfter(CodePanUtils.getDate(), -30);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		this.container = container;
		View view = inflater.inflate(R.layout.expense_items_layout, container, false);
		tvStartDateExpenseItems = (CodePanLabel) view.findViewById(R.id.tvStartDateExpenseItems);
		tvEndDateExpenseItems = (CodePanLabel) view.findViewById(R.id.tvEndDateExpenseItems);
		lvExpenseItems = (ListView) view.findViewById(R.id.lvExpenseItems);
		rlPlaceholderExpenseItems = (RelativeLayout) view.findViewById(R.id.rlPlaceholderExpenseItems);
		tvStartDateExpenseItems.setText(CodePanUtils.getCalendarDate(startDate, true, true));
		tvStartDateExpenseItems.setOnClickListener(this);
		tvEndDateExpenseItems.setText(CodePanUtils.getCalendarDate(endDate, true, true));
		tvEndDateExpenseItems.setOnClickListener(this);
		lvExpenseItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
				ExpenseItemsObj obj = expenseItemsList.get(i);
				ImageView ivCollapsibleExpenseItems = (ImageView) view.findViewById(R.id.ivCollapsibleExpenseItems);
				LinearLayout llItemsExpenseItems = (LinearLayout) view.findViewById(R.id.llItemsExpenseItems);
				if(llItemsExpenseItems.getVisibility() == View.GONE) {
					if(!obj.isAdded) {
						obj.childList = new ArrayList<>();
						ArrayList<ExpenseObj> expenseList = Data.loadExpense(db, obj.dDate);
						Log.e("paul", expenseList.size() + "");
						for(ExpenseObj expense : expenseList) {
							View v = inflater.inflate(R.layout.expense_items_list_row_collapsible, llItemsExpenseItems, false);
							View child = getChild(v, obj, expenseList, expense);
							llItemsExpenseItems.addView(child);
							obj.childList.add(child);
						}
						obj.isAdded = true;
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
		loadExpenseItems(db);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.tvStartDateExpenseItems:
				showCalendar(startDate);
				dateType = DateType.START;
				break;
			case R.id.tvEndDateExpenseItems:
				showCalendar(endDate);
				dateType = DateType.END;
				break;
		}
	}

	@Override
	public void onPickDate(String date) {
		switch(dateType) {
			case DateType.START:
				if(date != null) {
					long startMillis = CodePanUtils.dateToMillis(date);
					long endMillis = CodePanUtils.dateToMillis(endDate);
					if(startMillis <= endMillis) {
						String start = CodePanUtils.getCalendarDate(date, true, true);
						tvStartDateExpenseItems.setText(start);
						startDate = date;
					}
					else {
						CodePanUtils.alertToast(main, "Start date must be less than end date.");
					}
				}
				break;
			case DateType.END:
				if(date != null) {
					long startMillis = CodePanUtils.dateToMillis(startDate);
					long endMIllis = CodePanUtils.dateToMillis(date);
					if(endMIllis >= startMillis) {
						String end = CodePanUtils.getCalendarDate(date, true, true);
						tvEndDateExpenseItems.setText(end);
						endDate = date;
					}
					else {
						CodePanUtils.alertToast(main, "End date must be greater than start date.");
					}
				}
				break;
		}
		loadExpenseItems(db);
	}

	public void addExpenseItem(String dDate, String dTime, String expenseID) {
		for(int i = 0; i < expenseItemsList.size(); i++) {
			ExpenseItemsObj obj = expenseItemsList.get(i);
			if(obj.dDate.equals(dDate)) {
				if(obj.isAdded) {
					LinearLayout llItemsExpenseItems = (LinearLayout) lvExpenseItems.getChildAt(i).findViewById(R.id.llItemsExpenseItems);
					obj.childList = new ArrayList<>();
					ArrayList<ExpenseObj> expenseList = Data.loadExpense(db, obj.dDate);
					Log.e("paul", expenseList.size() + "");
					for(ExpenseObj expense : expenseList) {
						View v = inflater.inflate(R.layout.expense_items_list_row_collapsible, llItemsExpenseItems, false);
						View child = getChild(v, obj, expenseList, expense);
						obj.childList.add(child);
					}
					adapter.notifyDataSetChanged();
					lvExpenseItems.invalidate();
				}
				return;
			}
		}
		ExpenseItemsObj obj = new ExpenseItemsObj();
		obj.dDate = dDate;
		obj.totalAmount = 0;
		obj.childList = new ArrayList<>();
		ArrayList<ExpenseObj> expenseList = new ArrayList<>();
		ExpenseObj expense = new ExpenseObj();
		expense.ID = expenseID;
		expense.dDate = dDate;
		expense.dTime = dTime;
		expenseList.add(expense);
		View v = inflater.inflate(R.layout.expense_items_list_row_collapsible, container, false);
		View child = getChild(v, obj, expenseList, expense);
		obj.childList.add(child);
		expenseItemsList.add(0, obj);
		if(expenseItemsList.size() == 0) {
			rlPlaceholderExpenseItems.setVisibility(View.VISIBLE);
		}
		else {
			rlPlaceholderExpenseItems.setVisibility(View.GONE);
		}
		adapter.notifyDataSetChanged();
		lvExpenseItems.invalidate();
	}

	public void loadExpenseItems(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					expenseItemsList = Data.loadExpenseItems(db, startDate, endDate);
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
			if(expenseItemsList.size() == 0) {
				rlPlaceholderExpenseItems.setVisibility(View.VISIBLE);
			}
			else {
				rlPlaceholderExpenseItems.setVisibility(View.GONE);
			}
			adapter = new ExpenseItemsAdapter(main, expenseItemsList);
			lvExpenseItems.setAdapter(adapter);
			return true;
		}
	});

	public View getChild(final View view, final ExpenseItemsObj obj, final ArrayList<ExpenseObj> expenseList, final ExpenseObj expense) {
		final String expenseID = expense.ID;
		final int expenseType = expense.typeID;
		String time = CodePanUtils.getNormalTime(expense.dTime, false);
		final String amount = nf.format(expense.amount);
		RelativeLayout rlExpenseItems = (RelativeLayout) view.findViewById(R.id.rlExpenseItems);
		CodePanLabel tvExpenseType = (CodePanLabel) view.findViewById(R.id.tvExpenseTypeExpenseItems);
		CodePanLabel tvTime = (CodePanLabel) view.findViewById(R.id.tvTimeExpenseItems);
		CodePanLabel tvAmount = (CodePanLabel) view.findViewById(R.id.tvAmountExpenseItems);
		rlExpenseItems.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ExpenseItemsDetailsFragment expenseItemsDetails = new ExpenseItemsDetailsFragment();
				expenseItemsDetails.setExpense(expense);
				expenseItemsDetails.setOnUpdateExpenseCallback(new OnUpdateExpenseCallback() {
					@Override
					public void onUpdateExpense(ExpenseObj updatedExpense) {
						int pos = expenseList.indexOf(expense);
						View child = getChild(view, obj, expenseList, expense);
						obj.childList.set(pos, child);
						obj.totalAmount = updateTotalAmount(obj.childList);
						adapter.notifyDataSetChanged();
						lvExpenseItems.invalidate();
					}
				});
				transaction = main.getSupportFragmentManager().beginTransaction();
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
				String text = getString(R.string.delete_expense_items_message);
				String message = text + (expenseType == 0 ? "Expense " + expenseID : expenseType) + " PHP " + amount;
				int start1 = text.length();
				int end1 = start1 + (expenseType == 0 ? ("Expense " + expenseID).length() : String.valueOf(expenseType).length());
				int start2 = end1 + 5;
				int end2 = start2 + amount.length();
				ArrayList<SpannableMap> list = new ArrayList<>();
				list.add(new SpannableMap(main, bold, start1, end1));
				list.add(new SpannableMap(main, bold, start2, end2));
				final AlertDialogFragment alert = new AlertDialogFragment();
				alert.setDialogTitle(R.string.delete_expense_items_title);
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
						if(TarkieLib.deleteExpense(db, expense.ID)) {
							int pos = expenseList.indexOf(expense);
							expenseList.remove(pos);
							obj.childList.remove(pos);
							obj.totalAmount = updateTotalAmount(obj.childList);
							if(obj.childList.size() == 0) {
								expenseItemsList.remove(obj);
								if(expenseItemsList.size() == 0) {
									rlPlaceholderExpenseItems.setVisibility(View.VISIBLE);
								}
								else {
									rlPlaceholderExpenseItems.setVisibility(View.GONE);
								}
							}
							adapter.notifyDataSetChanged();
							lvExpenseItems.invalidate();
							manager.popBackStack();
							CodePanUtils.alertToast(main, (expenseType == 0 ? "Expense " + expenseID : expenseType) + " has been successfully deleted.", Toast.LENGTH_SHORT);
						}
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
		tvExpenseType.setText(expenseType == 0 ? "Expense " + expenseID : "" + expenseType);
		tvTime.setText(time);
		tvAmount.setText(amount);
		return view;
	}

	public float updateTotalAmount(ArrayList<View> child) {
		float total = 0;
		for(int c = 0; c < child.size(); c++) {
			CodePanLabel tvAmountExpenseItems = (CodePanLabel) child.get(c).findViewById(R.id.tvAmountExpenseItems);
			total += Float.parseFloat(tvAmountExpenseItems.getText().toString().replace(",", ""));
		}
		return total;
	}

	public void showCalendar(String currentDate) {
		CalendarView calendar = new CalendarView();
		calendar.setOnPickDateCallback(ExpenseItemsFragment.this);
		calendar.setCurrentDate(currentDate);
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, calendar);
		transaction.addToBackStack(null);
		transaction.commit();
	}
}
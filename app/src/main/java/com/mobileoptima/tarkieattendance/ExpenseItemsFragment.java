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
import android.widget.Toast;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.adapter.ExpenseItemsAdapter;
import com.mobileoptima.callback.Interface.OnUpdateExpenseCallback;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.ExpenseItemsObj;
import com.mobileoptima.model.ExpenseObj;

import java.text.NumberFormat;
import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.OnLongClickListener;

public class ExpenseItemsFragment extends Fragment {
	private ArrayList<ExpenseItemsObj> expenseItemsList;
	private ExpenseItemsAdapter adapter;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private ListView lvExpenseItems;
	private NumberFormat nf;
	private SQLiteAdapter db;
	private String startDate, endDate;

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
			public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
				ExpenseItemsObj obj = expenseItemsList.get(i);
				ImageView ivCollapsibleExpenseItems = (ImageView) view.findViewById(R.id.ivCollapsibleExpenseItems);
				LinearLayout llItemsExpenseItems = (LinearLayout) view.findViewById(R.id.llItemsExpenseItems);
				if(llItemsExpenseItems.getVisibility() == GONE) {
					if(!obj.isAdded) {
						obj.childList = new ArrayList<>();
						ArrayList<ExpenseObj> expenseList = Data.loadExpense(db, obj.dDate);
						for(ExpenseObj expense : expenseList) {
							View child = getChild(inflater, adapterView, obj, expenseList, expense);
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
		startDate = CodePanUtils.getDate();
		endDate = CodePanUtils.getDate();
		loadExpenseItems(db);
		return view;
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
			adapter = new ExpenseItemsAdapter(getActivity(), expenseItemsList);
			lvExpenseItems.setAdapter(adapter);
			return true;
		}
	});

	public View getChild(final LayoutInflater inflater, final AdapterView adapterView, final ExpenseItemsObj obj, final ArrayList<ExpenseObj> expenseList, final ExpenseObj expense) {
		final String expenseID = expense.ID;
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
				expenseItemsDetails.setOnUpdateExpenseCallback(new OnUpdateExpenseCallback() {
					@Override
					public void onUpdateExpense(ExpenseObj updatedExpense) {
						View view = getChild(inflater, adapterView, obj, expenseList, expense);
						int pos = expenseList.indexOf(expense);
						obj.childList.set(pos, view);
						obj.totalAmount = updateTotalAmount(obj.childList);
						adapter.notifyDataSetChanged();
						lvExpenseItems.invalidate();
					}
				});
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
				String message = text + (expenseType == null ? "Expense " + expenseID : expenseType) + " PHP " + amount;
				int start1 = text.length();
				int end1 = start1 + (expenseType == null ? ("Expense " + expenseID).length() : expenseType.length());
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
						if(TarkieLib.deleteExpense(db, expense.ID)) {
							int pos = expenseList.indexOf(expense);
							expenseList.remove(pos);
							obj.childList.remove(pos);
							obj.totalAmount = updateTotalAmount(obj.childList);
							if(obj.childList.size() == 0) {
								expenseItemsList.remove(obj);
							}
							adapter.notifyDataSetChanged();
							lvExpenseItems.invalidate();
							manager.popBackStack();
							CodePanUtils.alertToast(getActivity(), (expenseType == null ? "Expense " + expenseID : expenseType) + " has been successfully deleted.", Toast.LENGTH_SHORT);
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
		tvExpenseType.setText(expenseType == null ? "Expense " + expenseID : expenseType);
		tvTime.setText(time);
		tvAmount.setText(amount);
		return child;
	}

	public float updateTotalAmount(ArrayList<View> child) {
		float total = 0;
		for(int c = 0; c < child.size(); c++) {
			CodePanLabel tvAmountExpenseItems = (CodePanLabel) child.get(c).findViewById(R.id.tvAmountExpenseItems);
			total += Float.parseFloat(tvAmountExpenseItems.getText().toString().replace(",", ""));
		}
		return total;
	}
}
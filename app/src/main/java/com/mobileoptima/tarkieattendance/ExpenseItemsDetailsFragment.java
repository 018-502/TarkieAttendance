package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface;
import com.mobileoptima.callback.Interface.OnOptionSelectedCallback;
import com.mobileoptima.callback.Interface.OnUpdateExpenseCallback;
import com.mobileoptima.constant.ExpenseType;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.ChoiceObj;
import com.mobileoptima.model.ExpenseDefaultObj;
import com.mobileoptima.model.ExpenseFuelConsumptionObj;
import com.mobileoptima.model.ExpenseFuelPurchaseObj;
import com.mobileoptima.model.ExpenseObj;
import com.mobileoptima.model.StoreObj;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.NumberFormat;
import java.util.ArrayList;

import static android.view.View.*;

public class ExpenseItemsDetailsFragment extends Fragment implements OnClickListener, OnFocusChangeListener, OnTouchListener {
	private CodePanButton btnBackExpenseItemsDetails, btnSaveExpenseItemsDetails, btnStoreExpenseItemsDetails, btnItemExpenseItemsDetails, btnPhotoExpenseItemsDetails;
	private CodePanLabel tvDateExpenseItemsDetails, tvTimeExpenseItemsDetails;
	private CodePanTextField etAmountExpenseItemsDetails, etOriginExpenseItemsDetails, etDestinationExpenseItemsDetails, etNotesExpenseItemsDetails;
	private CheckBox cbWithORExpenseItemsDetails;
	private DisplayImageOptions options;
	private ExpenseObj expense;
	private ExpenseFuelConsumptionObj fc;
	private ExpenseFuelPurchaseObj fp;
	private ExpenseDefaultObj d;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private FrameLayout flPhotoExpenseItemsDetails;
	private ImageLoader imageLoader;
	private ImageView ivPhotoExpenseItemsDetails;
	private NumberFormat nf;
	private OnUpdateExpenseCallback updateExpenseCallback;
	private RelativeLayout rlWithORExpenseItemsDetails;
	private SQLiteAdapter db;
	private String expenseID;

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
		this.imageLoader = ImageLoader.getInstance();
		if(!imageLoader.isInited()) {
			imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		}
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_user_placeholder)
				.showImageForEmptyUri(R.drawable.ic_user_placeholder)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
		expense = TarkieLib.getExpense(db, expenseID);
		expense.store.name = TarkieLib.getStoreName(db, expense.store.ID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.expense_items_details_layout, container, false);
		btnBackExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnBackExpenseItemsDetails);
		btnSaveExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnSaveExpenseItemsDetails);
		tvDateExpenseItemsDetails = (CodePanLabel) view.findViewById(R.id.tvDateExpenseItemsDetails);
		tvTimeExpenseItemsDetails = (CodePanLabel) view.findViewById(R.id.tvTimeExpenseItemsDetails);
		btnStoreExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnStoreExpenseItemsDetails);
		btnItemExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnItemExpenseItemsDetails);
		etAmountExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etAmountExpenseItemsDetails);
		etOriginExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etOriginExpenseItemsDetails);
		etDestinationExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etDestinationExpenseItemsDetails);
		etNotesExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etNotesExpenseItemsDetails);
		flPhotoExpenseItemsDetails = (FrameLayout) view.findViewById(R.id.flPhotoExpenseItemsDetails);
		ivPhotoExpenseItemsDetails = (ImageView) view.findViewById(R.id.ivPhotoExpenseItemsDetails);
		btnPhotoExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnPhotoExpenseItemsDetails);
		rlWithORExpenseItemsDetails = (RelativeLayout) view.findViewById(R.id.rlWithORExpenseItemsDetails);
		cbWithORExpenseItemsDetails = (CheckBox) view.findViewById(R.id.cbWithORExpenseItemsDetails);
		btnBackExpenseItemsDetails.setOnClickListener(this);
		btnSaveExpenseItemsDetails.setOnClickListener(this);;
		btnStoreExpenseItemsDetails.setOnClickListener(this);
		btnItemExpenseItemsDetails.setOnClickListener(this);
		btnPhotoExpenseItemsDetails.setOnClickListener(this);

		tvDateExpenseItemsDetails.setText(expense.dDate);
		tvTimeExpenseItemsDetails.setText(expense.dTime);
		btnStoreExpenseItemsDetails.setText(expense.store.name);
		etAmountExpenseItemsDetails.setText(nf.format(expense.amount));
		editText(etAmountExpenseItemsDetails);
		etOriginExpenseItemsDetails.setText(expense.origin);
		etDestinationExpenseItemsDetails.setText(expense.destination);
		etNotesExpenseItemsDetails.setText(expense.notes);

		updateDetails(expense);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackExpenseItemsDetails:
				manager.popBackStack();
				break;
			case R.id.btnSaveExpenseItemsDetails:
				boolean result;
				Log.e("paul", cbWithORExpenseItemsDetails.isChecked() + "");
				CodePanUtils.hideKeyboard(view, getActivity());
				expense.amount = Float.parseFloat(etAmountExpenseItemsDetails.getText().toString().replace(",", ""));
				expense.origin = etOriginExpenseItemsDetails.getText().toString();
				expense.destination = etDestinationExpenseItemsDetails.getText().toString();
				expense.notes = etNotesExpenseItemsDetails.getText().toString();
				if(expense instanceof ExpenseFuelConsumptionObj) {
					fc = (ExpenseFuelConsumptionObj) expense;
					fc.start = "";
					fc.end = "";
					fc.rate = "";
					fc.startPhoto = "";
					fc.endPhoto = "";
					result = TarkieLib.updateExpense(db, fc);
				}
				else if(expense instanceof ExpenseFuelPurchaseObj) {
					fp = (ExpenseFuelPurchaseObj) expense;
					fp.start = "";
					fp.liters = "";
					fp.price = "";
					fp.photo = "";
					fp.startPhoto = "";
					fp.withOR = cbWithORExpenseItemsDetails.isChecked();
					result = TarkieLib.updateExpense(db, fp);
				}
				else {
					d = (ExpenseDefaultObj) expense;
					d.photo = "";
					d.withOR = cbWithORExpenseItemsDetails.isChecked();
					result = TarkieLib.updateExpense(db, d);
				}
				if(result) {
					if(updateExpenseCallback != null) {
						updateExpenseCallback.onUpdateExpense(expense);
					}
					AlertDialogFragment alert = new AlertDialogFragment();
					alert.setDialogTitle(R.string.save_expense_items_title);
					alert.setDialogMessage(R.string.save_expense_items_message);
					alert.setPositiveButton("Ok", new OnClickListener() {
						@Override
						public void onClick(View view) {
							manager.popBackStack();
							manager.popBackStack();
						}
					});
					transaction = manager.beginTransaction();
					transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
							R.anim.fade_in, R.anim.fade_out);
					transaction.add(R.id.rlMain, alert);
					transaction.addToBackStack(null);
					transaction.commit();
				}
				break;
			case R.id.btnStoreExpenseItemsDetails:
				StoresFragment stores = new StoresFragment();
				stores.setOnSelectStoreCallback(new Interface.OnSelectStoreCallback() {
					@Override
					public void onSelectStore(StoreObj store) {
						expense.store = store;
						btnStoreExpenseItemsDetails.setText(store.name);
						manager.popBackStack();
					}
				});
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.add(R.id.rlMain, stores);
				transaction.hide(this);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case R.id.btnItemExpenseItemsDetails:
				OptionsFragment options = new OptionsFragment();
				ArrayList<ChoiceObj> optionList = Data.loadExpenseTypes(db);
				options.setItems(optionList, "Expense Type");
				options.setOnOptionSelectedCallback(new OnOptionSelectedCallback() {
					@Override
					public void onOptionSelected(ChoiceObj choice) {
						expense.type.ID = choice.ID;
						expense.type.name = choice.name;
						btnItemExpenseItemsDetails.setText(expense.type.name);
						switch(Integer.parseInt(expense.type.ID)) {
							case ExpenseType.FUEL_CONSUMPTION:
								fc = new ExpenseFuelConsumptionObj();
								fc.ID = expense.ID;
								fc.dDate = expense.dDate;
								fc.dTime = expense.dTime;
								fc.amount = expense.amount;
								fc.type = expense.type;
								fc.store = expense.store;
								fc.origin = expense.origin;
								fc.destination = expense.destination;
								fc.isTag = expense.isTag;
								fc.isSubmit = expense.isSubmit;
								updateDetails(fc);
								break;
							case ExpenseType.FUEL_PURCHASE:
								fp = new ExpenseFuelPurchaseObj();
								fp.ID = expense.ID;
								fp.dDate = expense.dDate;
								fp.dTime = expense.dTime;
								fp.amount = expense.amount;
								fp.type = expense.type;
								fp.store = expense.store;
								fp.origin = expense.origin;
								fp.destination = expense.destination;
								fp.isTag = expense.isTag;
								fp.isSubmit = expense.isSubmit;
								updateDetails(fp);
								break;
							default:
								d = new ExpenseDefaultObj();
								d.ID = expense.ID;
								d.dDate = expense.dDate;
								d.dTime = expense.dTime;
								d.amount = expense.amount;
								d.type = expense.type;
								d.store = expense.store;
								d.origin = expense.origin;
								d.destination = expense.destination;
								d.isTag = expense.isTag;
								d.isSubmit = expense.isSubmit;
								updateDetails(d);
								break;
						}
					}
				});
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
						R.anim.fade_in, R.anim.fade_out);
				transaction.add(R.id.rlMain, options);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus) {
			final EditText editText = (EditText) v;
			editText.post(new Runnable() {
				@Override
				public void run() {
					editText.setSelection(editText.getText().length());
				}
			});
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		final EditText editText = (EditText) v;
		editText.post(new Runnable() {
			@Override
			public void run() {
				editText.setSelection(editText.getText().length());
			}
		});
		return false;
	}

	public void setExpense(String ID) {
		this.expenseID = ID;
	}

	public void setOnUpdateExpenseCallback(OnUpdateExpenseCallback saveExpenseCallback) {
		this.updateExpenseCallback = saveExpenseCallback;
	}

	public void updateDetails(ExpenseObj expense) {
		this.expense = expense;
		btnItemExpenseItemsDetails.setText(expense.type.name);
		if(expense instanceof ExpenseFuelConsumptionObj) {
			fc = (ExpenseFuelConsumptionObj) expense;
			rlWithORExpenseItemsDetails.setVisibility(GONE);
		}
		else if(expense instanceof ExpenseFuelPurchaseObj) {
			fp = (ExpenseFuelPurchaseObj) expense;
			cbWithORExpenseItemsDetails.setChecked(fp.withOR);
			rlWithORExpenseItemsDetails.setVisibility(VISIBLE);
		}
		else {
			d = (ExpenseDefaultObj) expense;
			cbWithORExpenseItemsDetails.setChecked(d.withOR);
			rlWithORExpenseItemsDetails.setVisibility(VISIBLE);
		}
		Log.e("paul", cbWithORExpenseItemsDetails.isChecked() + "");
//		if(expense.isSubmit) {
//			btnSaveExpenseItemsDetails.setEnabled(false);
//			etStoreExpenseItemsDetails.setEnabled(false);
//			btnItemExpenseItemsDetails.setEnabled(false);
//			btnPhotoExpenseItemsDetails.setEnabled(false);
//			etAmountExpenseItemsDetails.setEnabled(false);
//			etOriginExpenseItemsDetails.setEnabled(false);
//			etDestinationExpenseItemsDetails.setEnabled(false);
//			cbWithORExpenseItemsDetails.setEnabled(false);
//			etNotesExpenseItemsDetails.setEnabled(false);
//		}
//		if(expense.defPhoto != null && !expense.defPhoto.isEmpty()) {
//			Bitmap photo = BitmapFactory.decodeFile(expense.defPhoto);
//			ivPhotoExpenseItemsDetails.setImageBitmap(photo);
//		}
	}

	public void editText(View view) {
		final EditText editText = (EditText) view;
		editText.setOnFocusChangeListener(this);
		editText.setOnTouchListener(this);
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString();
				if (text.equals("")) {
					text = "0";
					editText.setText(text);
					editText.setSelection(text.length());
				} else if (text.substring(0, 1).equals(".")) {
					text = "0" + text;
					editText.setText(text);
					editText.setSelection(text.length());
				} else if (text.length() == 2 && text.substring(0, 1).equals("0") && !text.substring(1, 2).equals(".")) {
					text = text.substring(1);
					editText.setText(text);
					editText.setSelection(text.length());
				}
			}
		});
	}
}
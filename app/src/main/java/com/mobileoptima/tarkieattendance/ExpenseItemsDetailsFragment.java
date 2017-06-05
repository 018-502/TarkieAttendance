package com.mobileoptima.tarkieattendance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface;
import com.mobileoptima.callback.Interface.OnUsePhotoCallback;
import com.mobileoptima.callback.Interface.OnOptionSelectedCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnUpdateExpenseCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.constant.ExpenseType;
import com.mobileoptima.constant.ImageType;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.ChoiceObj;
import com.mobileoptima.model.ExpenseDefaultObj;
import com.mobileoptima.model.ExpenseFuelConsumptionObj;
import com.mobileoptima.model.ExpenseFuelPurchaseObj;
import com.mobileoptima.model.ExpenseObj;
import com.mobileoptima.model.StoreObj;

import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.OnFocusChangeListener;
import static android.view.View.OnTouchListener;
import static android.view.View.VISIBLE;

public class ExpenseItemsDetailsFragment extends Fragment implements OnClickListener, OnFocusChangeListener, OnTouchListener {

	private Bitmap defaultPhoto;
	private CheckBox cbWithORExpenseItemsDetails;
	private CodePanButton btnBackExpenseItemsDetails, btnSaveExpenseItemsDetails,
			btnStoreExpenseItemsDetails, btnItemExpenseItemsDetails,
			btnPhotoExpenseItemsDetails, btnStartExpenseItemsDetails, btnEndExpenseItemsDetails;
	private CodePanLabel tvDateExpenseItemsDetails, tvTimeExpenseItemsDetails, tvStoreExpenseItemsDetails;
	private CodePanTextField etAmountExpenseItemsDetails, etOriginExpenseItemsDetails,
			etDestinationExpenseItemsDetails, etNotesExpenseItemsDetails, etRateExpenseItemsDetails,
			etStartExpenseItemsDetails, etEndExpenseItemsDetails;
	private ExpenseObj expense;
	private ExpenseFuelConsumptionObj fc;
	private ExpenseFuelPurchaseObj fp;
	private ExpenseDefaultObj d;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private ImageView ivPhotoExpenseItemsDetails, ivStartExpenseItemsDetails, ivEndExpenseItemsDetails;
	private LinearLayout llRateExpenseItemsDetails, llStartEndExpenseItemsDetails;
	private NumberFormat nf;
	private OnUpdateExpenseCallback updateExpenseCallback;
	private OnOverrideCallback overrideCallback;
	private RelativeLayout rlStartExpenseItemsDetails, rlEndExpenseItemsDetails, rlWithORExpenseItemsDetails;
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
		expense = TarkieLib.getExpense(db, expenseID);
		defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_camera);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.expense_items_details_layout, container, false);
		btnBackExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnBackExpenseItemsDetails);
		btnSaveExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnSaveExpenseItemsDetails);
		tvDateExpenseItemsDetails = (CodePanLabel) view.findViewById(R.id.tvDateExpenseItemsDetails);
		tvTimeExpenseItemsDetails = (CodePanLabel) view.findViewById(R.id.tvTimeExpenseItemsDetails);
		tvStoreExpenseItemsDetails = (CodePanLabel) view.findViewById(R.id.tvStoreExpenseItemsDetails);
		btnStoreExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnStoreExpenseItemsDetails);
		btnItemExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnItemExpenseItemsDetails);
		etAmountExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etAmountExpenseItemsDetails);
		etOriginExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etOriginExpenseItemsDetails);
		etDestinationExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etDestinationExpenseItemsDetails);
		etNotesExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etNotesExpenseItemsDetails);
		ivPhotoExpenseItemsDetails = (ImageView) view.findViewById(R.id.ivPhotoExpenseItemsDetails);
		btnPhotoExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnPhotoExpenseItemsDetails);
		llRateExpenseItemsDetails = (LinearLayout) view.findViewById(R.id.llRateExpenseItemsDetails);
		etRateExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etRateExpenseItemsDetails);
		llStartEndExpenseItemsDetails = (LinearLayout) view.findViewById(R.id.llStartEndExpenseItemsDetails);
		rlStartExpenseItemsDetails = (RelativeLayout) view.findViewById(R.id.rlStartExpenseItemsDetails);
		etStartExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etStartExpenseItemsDetails);
		ivStartExpenseItemsDetails = (ImageView) view.findViewById(R.id.ivStartExpenseItemsDetails);
		btnStartExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnStartExpenseItemsDetails);
		rlEndExpenseItemsDetails = (RelativeLayout) view.findViewById(R.id.rlEndExpenseItemsDetails);
		etEndExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etEndExpenseItemsDetails);
		ivEndExpenseItemsDetails = (ImageView) view.findViewById(R.id.ivEndExpenseItemsDetails);
		btnEndExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnEndExpenseItemsDetails);
		rlWithORExpenseItemsDetails = (RelativeLayout) view.findViewById(R.id.rlWithORExpenseItemsDetails);
		cbWithORExpenseItemsDetails = (CheckBox) view.findViewById(R.id.cbWithORExpenseItemsDetails);
		btnBackExpenseItemsDetails.setOnClickListener(this);
		btnSaveExpenseItemsDetails.setOnClickListener(this);
		btnStoreExpenseItemsDetails.setOnClickListener(this);
		btnItemExpenseItemsDetails.setOnClickListener(this);
		btnPhotoExpenseItemsDetails.setOnClickListener(this);
		btnStartExpenseItemsDetails.setOnClickListener(this);
		btnEndExpenseItemsDetails.setOnClickListener(this);
		rlWithORExpenseItemsDetails.setOnClickListener(this);
		tvDateExpenseItemsDetails.setText(expense.dDate);
		tvDateExpenseItemsDetails.setText(expense.dDate);
		tvTimeExpenseItemsDetails.setText(expense.dTime);
		tvStoreExpenseItemsDetails.setText(StringUtils.capitalize(TarkieLib.getConvention(db, Convention.STORES)));
		if(expense.store.ID != null) {
			expense.store.name = TarkieLib.getStoreName(db, expense.store.ID);
			btnStoreExpenseItemsDetails.setText(expense.store.name);
		}
		if(expense.amount != 0) {
			etAmountExpenseItemsDetails.setText(nf.format(expense.amount));
		}
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
				CodePanUtils.hideKeyboard(view, getActivity());
				manager.popBackStack();
				break;
			case R.id.btnSaveExpenseItemsDetails:
				boolean result;
				CodePanUtils.hideKeyboard(view, getActivity());
				String amount = etAmountExpenseItemsDetails.getText().toString().replace(",", "");
				if(!amount.isEmpty()) {
					expense.amount = Float.parseFloat(amount);
				}
				expense.origin = etOriginExpenseItemsDetails.getText().toString();
				expense.destination = etDestinationExpenseItemsDetails.getText().toString();
				expense.notes = etNotesExpenseItemsDetails.getText().toString();
				int typeID = 0;
				if(expense.type.ID != null) {
					typeID = Integer.parseInt(expense.type.ID);
				}
				switch(typeID) {
					case ExpenseType.FUEL_CONSUMPTION:
						fc = (ExpenseFuelConsumptionObj) expense;
						fc.start = etStartExpenseItemsDetails.getText().toString();
						fc.end = etEndExpenseItemsDetails.getText().toString();
						String rate = etRateExpenseItemsDetails.getText().toString().replace(",", "");
						if(!rate.isEmpty()) {
							fc.rate = Float.parseFloat(rate);
						}
						result = TarkieLib.updateExpense(db, fc);
						break;
					case ExpenseType.FUEL_PURCHASE:
						fp = (ExpenseFuelPurchaseObj) expense;
						fp.start = etStartExpenseItemsDetails.getText().toString();
						fp.liters = "";
						fp.price = "";
						fp.withOR = cbWithORExpenseItemsDetails.isChecked();
						result = TarkieLib.updateExpense(db, fp);
						break;
					default:
						d = (ExpenseDefaultObj) expense;
						d.withOR = cbWithORExpenseItemsDetails.isChecked();
						result = TarkieLib.updateExpense(db, d);
						break;
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
				CodePanUtils.hideKeyboard(view, getActivity());
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
				CodePanUtils.hideKeyboard(view, getActivity());
				OptionsFragment options = new OptionsFragment();
				ArrayList<ChoiceObj> optionList = Data.loadExpenseTypes(db);
				options.setItems(optionList, "Expense Type");
				options.setOnOptionSelectedCallback(new OnOptionSelectedCallback() {
					@Override
					public void onOptionSelected(ChoiceObj choice) {
						if(expense.type.ID == null || !expense.type.ID.equals(choice.ID)) {
							expense.type.ID = choice.ID;
							expense.type.name = choice.name;
							expense = updateDetails(expense);
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
			case R.id.btnPhotoExpenseItemsDetails:
				CodePanUtils.hideKeyboard(view, getActivity());
				cameraFragment(new OnUsePhotoCallback() {
					@Override
					public void onUsePhoto(String fileName) {
						int typeID = 0;
						if(expense.type.ID != null) {
							typeID = Integer.parseInt(expense.type.ID);
						}
						switch(typeID) {
							case ExpenseType.FUEL_CONSUMPTION:
								break;
							case ExpenseType.FUEL_PURCHASE:
								fp = (ExpenseFuelPurchaseObj) expense;
								fp.start = etStartExpenseItemsDetails.getText().toString();
								fp.liters = "";
								fp.price = "";
								fp.withOR = cbWithORExpenseItemsDetails.isChecked();
								fp.photo = fileName;
								updateDetails(fp);
								break;
							default:
								d = (ExpenseDefaultObj) expense;
								d.withOR = cbWithORExpenseItemsDetails.isChecked();
								d.photo = fileName;
								updateDetails(d);
								break;
						}
					}
				});
				break;
			case R.id.btnStartExpenseItemsDetails:
				CodePanUtils.hideKeyboard(view, getActivity());
				cameraFragment(new OnUsePhotoCallback() {
					@Override
					public void onUsePhoto(String fileName) {
						int typeID = 0;
						if(expense.type.ID != null) {
							typeID = Integer.parseInt(expense.type.ID);
						}
						switch(typeID) {
							case ExpenseType.FUEL_CONSUMPTION:
								fc = (ExpenseFuelConsumptionObj) expense;
								fc.start = etStartExpenseItemsDetails.getText().toString();
								fc.end = etEndExpenseItemsDetails.getText().toString();
								String rate = etRateExpenseItemsDetails.getText().toString().replace(",", "");
								if(!rate.isEmpty()) {
									fc.rate = Float.parseFloat(rate);
								}
								fc.startPhoto = fileName;
								updateDetails(fc);
								break;
							case ExpenseType.FUEL_PURCHASE:
								fp = (ExpenseFuelPurchaseObj) expense;
								fp.start = etStartExpenseItemsDetails.getText().toString();
								fp.liters = "";
								fp.price = "";
								fp.withOR = cbWithORExpenseItemsDetails.isChecked();
								fp.startPhoto = fileName;
								updateDetails(fp);
								break;
						}
					}
				});
				break;
			case R.id.btnEndExpenseItemsDetails:
				CodePanUtils.hideKeyboard(view, getActivity());
				cameraFragment(new OnUsePhotoCallback() {
					@Override
					public void onUsePhoto(String fileName) {
						int typeID = 0;
						if(expense.type.ID != null) {
							typeID = Integer.parseInt(expense.type.ID);
						}
						switch(typeID) {
							case ExpenseType.FUEL_CONSUMPTION:
								fc = (ExpenseFuelConsumptionObj) expense;
								fc.start = etStartExpenseItemsDetails.getText().toString();
								fc.end = etEndExpenseItemsDetails.getText().toString();
								String rate = etRateExpenseItemsDetails.getText().toString().replace(",", "");
								if(!rate.isEmpty()) {
									fc.rate = Float.parseFloat(rate);
								}
								fc.endPhoto = fileName;
								updateDetails(fc);
								break;
						}
					}
				});
				break;
			case R.id.rlWithORExpenseItemsDetails:
				CodePanUtils.hideKeyboard(view, getActivity());
				if(cbWithORExpenseItemsDetails.isChecked()) {
					cbWithORExpenseItemsDetails.setChecked(false);
				}
				else {
					cbWithORExpenseItemsDetails.setChecked(true);
				}
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

	public void setExpense(String expenseID) {
		this.expenseID = expenseID;
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setOnUpdateExpenseCallback(OnUpdateExpenseCallback saveExpenseCallback) {
		this.updateExpenseCallback = saveExpenseCallback;
	}

	public ExpenseObj updateDetails(ExpenseObj expense) {
		btnItemExpenseItemsDetails.setText(expense.type.name);
		int type = 0;
		if(expense.type.ID != null) {
			type = Integer.parseInt(expense.type.ID);
		}
		String start;
		String end;
		String rate;
		switch(type) {
			case ExpenseType.FUEL_CONSUMPTION:
				if(!(expense instanceof ExpenseFuelConsumptionObj)) {
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
					expense = fc;
				}
				fc = (ExpenseFuelConsumptionObj) expense;
				ivPhotoExpenseItemsDetails.setVisibility(GONE);
				btnPhotoExpenseItemsDetails.setVisibility(GONE);
				rate = etRateExpenseItemsDetails.getText().toString();
				if(!rate.isEmpty()) {
					fc.rate = Integer.parseInt(rate);
				}
				if(fc.rate != 0) {
					etRateExpenseItemsDetails.setText(nf.format(fc.rate));
				}
				llRateExpenseItemsDetails.setVisibility(VISIBLE);
				start = etStartExpenseItemsDetails.getText().toString();
				if(!start.isEmpty()) {
					fc.start = start;
				}
				etStartExpenseItemsDetails.setText(fc.start);
				if(fc.startPhoto == null && fp != null && fp.startPhoto != null) {
					fc.startPhoto = fp.startPhoto;
				}
				ivStartExpenseItemsDetails.setImageBitmap(getPhoto(fc.startPhoto));
				end = etEndExpenseItemsDetails.getText().toString();
				if(!end.isEmpty()) {
					fc.end = end;
				}
				etEndExpenseItemsDetails.setText(fc.end);
				ivEndExpenseItemsDetails.setImageBitmap(getPhoto(fc.endPhoto));
				llStartEndExpenseItemsDetails.setVisibility(VISIBLE);
				rlStartExpenseItemsDetails.setVisibility(VISIBLE);
				rlEndExpenseItemsDetails.setVisibility(VISIBLE);
				rlWithORExpenseItemsDetails.setVisibility(GONE);
				return fc;
			case ExpenseType.FUEL_PURCHASE:
				if(!(expense instanceof ExpenseFuelPurchaseObj)) {
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
					expense = fp;
				}
				fp = (ExpenseFuelPurchaseObj) expense;
				if(fp.photo == null && d != null && d.photo != null) {
					fp.photo = d.photo;
				}
				ivPhotoExpenseItemsDetails.setImageBitmap(getPhoto(fp.photo));
				ivPhotoExpenseItemsDetails.setVisibility(VISIBLE);
				btnPhotoExpenseItemsDetails.setVisibility(VISIBLE);
				llRateExpenseItemsDetails.setVisibility(GONE);
				start = etStartExpenseItemsDetails.getText().toString();
				if(!start.isEmpty()) {
					fp.start = start;
				}
				etStartExpenseItemsDetails.setText(fp.start);
				if(fp.startPhoto == null && fc != null && fc.startPhoto != null) {
					fp.startPhoto = fc.startPhoto;
				}
				ivStartExpenseItemsDetails.setImageBitmap(getPhoto(fp.startPhoto));
				llStartEndExpenseItemsDetails.setVisibility(VISIBLE);
				rlStartExpenseItemsDetails.setVisibility(VISIBLE);
				rlEndExpenseItemsDetails.setVisibility(GONE);
				fp.withOR = cbWithORExpenseItemsDetails.isChecked();
				cbWithORExpenseItemsDetails.setChecked(fp.withOR);
				rlWithORExpenseItemsDetails.setVisibility(VISIBLE);
				return fp;
			default:
				if(!(expense instanceof ExpenseDefaultObj)) {
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
					expense = d;
				}
				d = (ExpenseDefaultObj) expense;
				if(d.photo == null && fp != null && fp.photo != null) {
					d.photo = fp.photo;
				}
				ivPhotoExpenseItemsDetails.setImageBitmap(getPhoto(d.photo));
				ivPhotoExpenseItemsDetails.setVisibility(VISIBLE);
				btnPhotoExpenseItemsDetails.setVisibility(VISIBLE);
				llRateExpenseItemsDetails.setVisibility(GONE);
				llStartEndExpenseItemsDetails.setVisibility(GONE);
				rlStartExpenseItemsDetails.setVisibility(GONE);
				rlEndExpenseItemsDetails.setVisibility(GONE);
				d.withOR = cbWithORExpenseItemsDetails.isChecked();
				cbWithORExpenseItemsDetails.setChecked(d.withOR);
				rlWithORExpenseItemsDetails.setVisibility(VISIBLE);
				return d;
		}
	}

	public void cameraFragment(OnUsePhotoCallback usePhotoCallback) {
		CameraFragment camera = new CameraFragment();
		camera.setImageType(ImageType.EXPENSE);
		camera.setOnExpenseCallback(usePhotoCallback);
		camera.setOnOverrideCallback(overrideCallback);
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		transaction.add(R.id.rlMain, camera);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public Bitmap getPhoto(String fileName) {
		if(fileName != null && !fileName.isEmpty()) {
			return CodePanUtils.getBitmapImage(getActivity(), App.FOLDER, fileName);
		}
		return defaultPhoto;
	}
}
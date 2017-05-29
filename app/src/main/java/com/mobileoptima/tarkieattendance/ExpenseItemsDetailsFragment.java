package com.mobileoptima.tarkieattendance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ImageView;

import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface.OnUpdateExpenseCallback;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.ExpenseObj;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.NumberFormat;

import static android.view.View.*;

public class ExpenseItemsDetailsFragment extends Fragment implements OnClickListener, OnFocusChangeListener, OnTouchListener {
	private CodePanButton btnBackExpenseItemsDetails, btnSaveExpenseItemsDetails, btnItemExpenseItemsDetails, btnPhotoExpenseItemsDetails;
	private CodePanLabel tvDatetExpenseItemsDetails, tvTimeExpenseItemsDetails;
	private CodePanTextField etClientExpenseItemsDetails, etAmountExpenseItemsDetails, etOriginExpenseItemsDetails, etDestinationExpenseItemsDetails, etNotesExpenseItemsDetails;
	private CheckBox cbWithORExpenseItemsDetails;
	private DisplayImageOptions options;
	private ExpenseObj obj;
	private FragmentManager manager;
	private ImageLoader imageLoader;
	private ImageView ivPhotoExpenseItemsDetails;
	private NumberFormat nf;
	private OnUpdateExpenseCallback updateExpenseCallback;
	private SQLiteAdapter db;

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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.expense_items_details_layout, container, false);
		btnBackExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnBackExpenseItemsDetails);
		btnSaveExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnSaveExpenseItemsDetails);
		etClientExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etClientExpenseItemsDetails);
		btnItemExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnItemExpenseItemsDetails);
		tvDatetExpenseItemsDetails = (CodePanLabel) view.findViewById(R.id.tvDatetExpenseItemsDetails);
		tvTimeExpenseItemsDetails = (CodePanLabel) view.findViewById(R.id.tvTimeExpenseItemsDetails);
		ivPhotoExpenseItemsDetails = (ImageView) view.findViewById(R.id.ivPhotoExpenseItemsDetails);
		btnPhotoExpenseItemsDetails = (CodePanButton) view.findViewById(R.id.btnPhotoExpenseItemsDetails);
		etAmountExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etAmountExpenseItemsDetails);
		etOriginExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etOriginExpenseItemsDetails);
		etDestinationExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etDestinationExpenseItemsDetails);
		cbWithORExpenseItemsDetails = (CheckBox) view.findViewById(R.id.cbWithORExpenseItemsDetails);
		etNotesExpenseItemsDetails = (CodePanTextField) view.findViewById(R.id.etNotesExpenseItemsDetails);
		btnBackExpenseItemsDetails.setOnClickListener(this);
		btnSaveExpenseItemsDetails.setOnClickListener(this);
		btnItemExpenseItemsDetails.setOnClickListener(this);
		btnPhotoExpenseItemsDetails.setOnClickListener(this);
		if(obj.isSubmit) {
			btnSaveExpenseItemsDetails.setEnabled(false);
			etClientExpenseItemsDetails.setEnabled(false);
			btnItemExpenseItemsDetails.setEnabled(false);
			btnPhotoExpenseItemsDetails.setEnabled(false);
			etAmountExpenseItemsDetails.setEnabled(false);
			etOriginExpenseItemsDetails.setEnabled(false);
			etDestinationExpenseItemsDetails.setEnabled(false);
			cbWithORExpenseItemsDetails.setEnabled(false);
			etNotesExpenseItemsDetails.setEnabled(false);
		}
		etClientExpenseItemsDetails.setText(obj.client);
		btnItemExpenseItemsDetails.setText(obj.expenseType == null ? "Expense " + obj.ID : obj.expenseType);
		tvDatetExpenseItemsDetails.setText(obj.dDate);
		tvTimeExpenseItemsDetails.setText(obj.dTime);
		if(obj.photo != null && !obj.photo.isEmpty()) {
			Bitmap photo = BitmapFactory.decodeFile(obj.photo);
			ivPhotoExpenseItemsDetails.setImageBitmap(photo);
		}
		etAmountExpenseItemsDetails.setText(nf.format(obj.amount));
		editText(etAmountExpenseItemsDetails);
		etOriginExpenseItemsDetails.setText(obj.origin);
		etDestinationExpenseItemsDetails.setText(obj.destination);
		cbWithORExpenseItemsDetails.setChecked(obj.withOR);
		etNotesExpenseItemsDetails.setText(obj.notes);
		return view;
	}

	public void setExpense(ExpenseObj obj) {
		this.obj = obj;
	}

	public void setOnUpdateExpenseCallback(OnUpdateExpenseCallback saveExpenseCallback) {
		this.updateExpenseCallback = saveExpenseCallback;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackExpenseItemsDetails:
				manager.popBackStack();
				break;
			case R.id.btnSaveExpenseItemsDetails:
				obj.expenseType = btnItemExpenseItemsDetails.getText().toString();
				obj.client = etClientExpenseItemsDetails.getText().toString();
				obj.photo = "";
				obj.origin = etOriginExpenseItemsDetails.getText().toString();
				obj.destination = etDestinationExpenseItemsDetails.getText().toString();
				obj.notes = etNotesExpenseItemsDetails.getText().toString();
				obj.withOR = cbWithORExpenseItemsDetails.isChecked();
				obj.amount = Float.parseFloat(etAmountExpenseItemsDetails.getText().toString().replace(",", ""));
				if(TarkieLib.updateExpense(db, obj)) {
					if(updateExpenseCallback != null) {
						updateExpenseCallback.onUpdateExpense(obj);
					}
					manager.popBackStack();
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
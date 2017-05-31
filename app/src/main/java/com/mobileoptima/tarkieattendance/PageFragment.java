package com.mobileoptima.tarkieattendance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.codepan.calendar.callback.Interface;
import com.codepan.calendar.view.CalendarView;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface.OnClearCallback;
import com.mobileoptima.callback.Interface.OnDeletePhotoCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnSignCallback;
import com.mobileoptima.constant.AnswerType;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.FieldType;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.AnswerObj;
import com.mobileoptima.model.ChoiceObj;
import com.mobileoptima.model.EntryObj;
import com.mobileoptima.model.FieldObj;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.ImageObj;
import com.mobileoptima.model.PageObj;

import java.util.ArrayList;

public class PageFragment extends Fragment implements OnFragmentCallback {

	private final int LIMIT = 15;

	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private boolean withChanges, isLoadable;
	private ArrayList<FieldObj> fieldList;
	private FragmentManager manager;
	private LinearLayout llPage;
	private ScrollView svPage;
	private SQLiteAdapter db;
	private View lastChild;
	private EntryObj entry;
	private FormObj form;
	private PageObj page;
	private int start;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page_layout, container, false);
		llPage = (LinearLayout) view.findViewById(R.id.llPage);
		svPage = (ScrollView) view.findViewById(R.id.svPage);
		ViewTreeObserver vto = svPage.getViewTreeObserver();
		vto.addOnScrollChangedListener(new OnScrollChangedListener() {
			@Override
			public void onScrollChanged() {
				if(lastChild != null && isLoadable &&
						CodePanUtils.isViewVisible(svPage, lastChild)) {
					loadFields(db);
				}
			}
		});
		loadFields(db);
		return view;
	}

	public void loadFields(final SQLiteAdapter db) {
		isLoadable = false;
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(fieldList == null) {
						fieldList = Data.loadFields(db, form, entry, page);
					}
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	public void setForm(FormObj form) {
		this.form = form;
	}

	public void setEntry(EntryObj entry) {
		this.entry = entry;
	}

	public void setFieldList(ArrayList<FieldObj> fieldList) {
		this.fieldList = fieldList;
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			View view = getView();
			if(view != null) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				ViewGroup container = (ViewGroup) view.getParent();
				int size = fieldList.size();
				int end = start + LIMIT;
				end = end > size ? size : end;
				for(int i = start; i < end; i++) {
					final FieldObj field = fieldList.get(i);
					boolean isEditable = entry == null || !entry.isSubmit;
					View child = getField(inflater, container, field, isEditable);
					llPage.addView(child);
					if(i == end - 1) {
						start = end;
						lastChild = child;
						if(end < size) {
							isLoadable = true;
						}
					}
				}
			}
			return true;
		}
	});

	public View getField(LayoutInflater inflater, ViewGroup container, final FieldObj field, final boolean isEditable) {
		final AnswerObj answer = field.answer;
		View view = null;
		switch(field.type) {
			case FieldType.SEC:
				view = inflater.inflate(R.layout.field_section_layout, container, false);
				CodePanLabel tvTitleSec = (CodePanLabel) view.findViewById(R.id.tvTitleSec);
				final CodePanLabel tvDescSec = (CodePanLabel) view.findViewById(R.id.tvDescSec);
				if(field.name != null && !field.name.isEmpty()) {
					tvTitleSec.setText(field.name);
					tvTitleSec.setVisibility(View.VISIBLE);
				}
				else {
					tvTitleSec.setVisibility(View.GONE);
				}
				if(field.description != null && !field.description.isEmpty()) {
					tvDescSec.setText(field.description);
					tvDescSec.setVisibility(View.VISIBLE);
				}
				else {
					tvDescSec.setVisibility(View.GONE);
				}
				break;
			case FieldType.TEXT:
				view = inflater.inflate(R.layout.field_text_layout, container, false);
				CodePanLabel tvQuestionText = (CodePanLabel) view.findViewById(R.id.tvQuestionText);
				CodePanTextField etAnswerText = (CodePanTextField) view.findViewById(R.id.etAnswerText);
				etAnswerText.setText(answer.value);
				etAnswerText.setEnabled(isEditable);
				etAnswerText.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence cs, int start, int count, int after) {
					}

					@Override
					public void onTextChanged(CharSequence cs, int start, int before, int count) {
						answer.value = cs.toString();
						withChanges = true;
					}

					@Override
					public void afterTextChanged(Editable e) {
					}
				});
				if(field.isRequired) {
					requiredField(tvQuestionText, field.name);
				}
				else {
					tvQuestionText.setText(field.name);
				}
				break;
			case FieldType.NUM:
				view = inflater.inflate(R.layout.field_numeric_layout, container, false);
				CodePanLabel tvQuestionNum = (CodePanLabel) view.findViewById(R.id.tvQuestionNum);
				CodePanTextField etAnswerNum = (CodePanTextField) view.findViewById(R.id.etAnswerNum);
				etAnswerNum.setText(answer.value);
				etAnswerNum.setEnabled(isEditable);
				etAnswerNum.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence cs, int start, int count, int after) {
					}

					@Override
					public void onTextChanged(CharSequence cs, int start, int before, int count) {
						answer.value = cs.toString();
						withChanges = true;
					}

					@Override
					public void afterTextChanged(Editable e) {
					}
				});
				if(field.isRequired) {
					requiredField(tvQuestionNum, field.name);
				}
				else {
					tvQuestionNum.setText(field.name);
				}
				break;
			case FieldType.LTEXT:
				view = inflater.inflate(R.layout.field_long_text_layout, container, false);
				CodePanLabel tvQuestionLText = (CodePanLabel) view.findViewById(R.id.tvQuestionLText);
				CodePanTextField etAnswerLText = (CodePanTextField) view.findViewById(R.id.etAnswerLText);
				etAnswerLText.setText(answer.value);
				etAnswerLText.setEnabled(isEditable);
				etAnswerLText.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence cs, int start, int count, int after) {
					}

					@Override
					public void onTextChanged(CharSequence cs, int start, int before, int count) {
						answer.value = cs.toString();
						withChanges = true;
					}

					@Override
					public void afterTextChanged(Editable e) {
					}
				});
				if(field.isRequired) {
					requiredField(tvQuestionLText, field.name);
				}
				else {
					tvQuestionLText.setText(field.name);
				}
				break;
			case FieldType.DATE:
				view = inflater.inflate(R.layout.field_date_layout, container, false);
				CodePanLabel tvQuestionDate = (CodePanLabel) view.findViewById(R.id.tvQuestionDate);
				final CodePanButton btnCalendarDate = (CodePanButton) view.findViewById(R.id.btnCalendarDate);
				if(field.isRequired) {
					requiredField(tvQuestionDate, field.name);
				}
				else {
					tvQuestionDate.setText(field.name);
				}
				if(answer.value != null) {
					String date = CodePanUtils.getCalendarDate(answer.value, false, true);
					btnCalendarDate.setText(date);
				}
				btnCalendarDate.setEnabled(isEditable);
				btnCalendarDate.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						CalendarView calendar = new CalendarView();
						calendar.setOnFragmentCallback(PageFragment.this);
						calendar.setOnPickDateCallback(new Interface.OnPickDateCallback() {
							@Override
							public void onPickDate(String date) {
								String selected = CodePanUtils.getCalendarDate(date, false, true);
								btnCalendarDate.setText(selected);
								answer.value = date;
								withChanges = true;
							}
						});
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
								R.anim.fade_in, R.anim.fade_out);
						transaction.add(R.id.rlMain, calendar);
						transaction.addToBackStack(null);
						transaction.commit();
					}
				});
				break;
			case FieldType.DD:
				view = inflater.inflate(R.layout.field_dropdown_layout, container, false);
				CodePanLabel tvQuestionDd = (CodePanLabel) view.findViewById(R.id.tvQuestionDd);
				final CodePanButton btnOptionsDd = (CodePanButton) view.findViewById(R.id.btnOptionsDd);
				if(field.isRequired) {
					requiredField(tvQuestionDd, field.name);
				}
				else {
					tvQuestionDd.setText(field.name);
				}
				final ArrayList<ChoiceObj> optionList = Data.loadChoices(db, field.ID);
				if(answer.value != null) {
					for(ChoiceObj choice : optionList) {
						if(choice.code.equals(answer.value)) {
							btnOptionsDd.setText(choice.name);
						}
					}
				}
				btnOptionsDd.setEnabled(isEditable);
				btnOptionsDd.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						OptionsFragment options = new OptionsFragment();
						options.setItems(optionList, field.name);
						options.setOnFragmentCallback(PageFragment.this);
						options.setOnOptionSelectedCallback(new com.mobileoptima.callback.Interface.OnOptionSelectedCallback() {
							@Override
							public void onOptionSelected(ChoiceObj choice) {
								btnOptionsDd.setText(choice.name);
								answer.value = choice.code;
								withChanges = true;
							}
						});
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
								R.anim.fade_in, R.anim.fade_out);
						transaction.add(R.id.rlMain, options);
						transaction.addToBackStack(null);
						transaction.commit();
					}
				});
				break;
			case FieldType.CB:
				view = inflater.inflate(R.layout.field_checkbox_layout, container, false);
				CodePanLabel tvQuestionCb = (CodePanLabel) view.findViewById(R.id.tvQuestionCb);
				LinearLayout llQuestionCb = (LinearLayout) view.findViewById(R.id.llQuestionCb);
				final CheckBox cbBoxCb = (CheckBox) view.findViewById(R.id.cbBoxCb);
				tvQuestionCb.setText(field.name);
				if(answer.value != null) {
					switch(answer.value) {
						case AnswerType.CHECK:
							answer.isCheck = true;
							break;
						case AnswerType.UNCHECK:
							answer.isCheck = false;
							break;
					}
					cbBoxCb.setChecked(answer.isCheck);
				}
				llQuestionCb.setEnabled(isEditable);
				llQuestionCb.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if(!cbBoxCb.isChecked()) {
							cbBoxCb.setChecked(true);
							answer.isCheck = true;
							answer.value = AnswerType.CHECK;
						}
						else {
							cbBoxCb.setChecked(false);
							answer.isCheck = false;
							answer.value = AnswerType.UNCHECK;
						}
						withChanges = true;
					}
				});
				break;
			case FieldType.YON:
				view = inflater.inflate(R.layout.field_yes_no_layout, container, false);
				CodePanLabel tvQuestionYon = (CodePanLabel) view.findViewById(R.id.tvQuestionYon);
				final CodePanButton btnNoYon = (CodePanButton) view.findViewById(R.id.btnNoYon);
				final CodePanButton btnYesYon = (CodePanButton) view.findViewById(R.id.btnYesYon);
				if(field.isRequired) {
					requiredField(tvQuestionYon, field.name);
				}
				else {
					tvQuestionYon.setText(field.name);
				}
				if(answer.value != null) {
					switch(answer.value) {
						case AnswerType.YES:
							btnNoYon.setEnabled(true);
							btnYesYon.setEnabled(false);
							answer.isCheck = true;
							answer.isActive = true;
							break;
						case AnswerType.NO:
							btnNoYon.setEnabled(false);
							btnYesYon.setEnabled(true);
							answer.isCheck = false;
							answer.isActive = true;
							break;
					}
				}
				if(isEditable) {
					btnNoYon.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							btnNoYon.setEnabled(false);
							btnYesYon.setEnabled(true);
							answer.isCheck = false;
							answer.isActive = true;
							answer.value = AnswerType.NO;
							withChanges = true;
						}
					});
					btnYesYon.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							btnNoYon.setEnabled(true);
							btnYesYon.setEnabled(false);
							answer.isCheck = true;
							answer.isActive = true;
							answer.value = AnswerType.YES;
							withChanges = true;
						}
					});
				}
				break;
			case FieldType.MS:
				view = inflater.inflate(R.layout.field_multiple_selection_layout, container, false);
				CodePanLabel tvQuestionMs = (CodePanLabel) view.findViewById(R.id.tvQuestionMs);
				LinearLayout llChoicesMs = (LinearLayout) view.findViewById(R.id.llChoicesMs);
				if(field.isRequired) {
					requiredField(tvQuestionMs, field.name);
				}
				else {
					tvQuestionMs.setText(field.name);
				}
				final ArrayList<ChoiceObj> choiceList = Data.loadChoices(db, field.ID);
				answer.choiceList = choiceList;
				for(final ChoiceObj choice : choiceList) {
					View v = inflater.inflate(R.layout.multiple_selection_item, container, false);
					LinearLayout llChoiceMs = (LinearLayout) v.findViewById(R.id.llChoiceMs);
					CodePanLabel tvChoiceMs = (CodePanLabel) v.findViewById(R.id.tvChoiceMs);
					final CheckBox cbChoiceMs = (CheckBox) v.findViewById(R.id.cbChoiceMs);
					final int index = choiceList.indexOf(choice);
					if(answer.value != null && answer.value.contains(choice.code)) {
						cbChoiceMs.setChecked(true);
						answer.choiceList.get(index).isCheck = true;
					}
					else {
						cbChoiceMs.setChecked(false);
						answer.choiceList.get(index).isCheck = false;
					}
					tvChoiceMs.setText(choice.name);
					llChoiceMs.setEnabled(isEditable);
					llChoiceMs.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							if(!cbChoiceMs.isChecked()) {
								cbChoiceMs.setChecked(true);
								answer.choiceList.get(index).isCheck = true;
							}
							else {
								cbChoiceMs.setChecked(false);
								answer.choiceList.get(index).isCheck = false;
							}
							withChanges = true;
						}
					});
					llChoicesMs.addView(v);
				}
				break;
			case FieldType.LAB:
				view = inflater.inflate(R.layout.field_label_layout, container, false);
				CodePanLabel tvDescLabel = (CodePanLabel) view.findViewById(R.id.tvDescLabel);
				tvDescLabel.setText(field.name);
				break;
			case FieldType.LINK:
				view = inflater.inflate(R.layout.field_link_layout, container, false);
				CodePanLabel tvQuestionLink = (CodePanLabel) view.findViewById(R.id.tvQuestionLink);
				CodePanButton btnUrlLink = (CodePanButton) view.findViewById(R.id.btnUrlLink);
				btnUrlLink.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						String url = CodePanUtils.validateURL(field.description);
						if(url != null && !url.isEmpty()) {
							if(CodePanUtils.isValidURL(url)) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri.parse(url));
								startActivity(intent);
							}
							else {
								String title = "Invalid URL";
								String message = "\"" + field.description + "\"";
								TarkieLib.alertDialog(getActivity(), title, message);
							}
						}
						else {
							CodePanUtils.alertToast(getActivity(), "No inputted URL.");
						}
					}
				});
				tvQuestionLink.setText(field.name);
				break;
			case FieldType.GPS:
				view = inflater.inflate(R.layout.field_gps_layout, container, false);
				final CodePanLabel tvStatusGps = (CodePanLabel) view.findViewById(R.id.tvStatusGps);
				final CodePanLabel tvResultGps = (CodePanLabel) view.findViewById(R.id.tvResultGps);
				CodePanLabel tvQuestionGps = (CodePanLabel) view.findViewById(R.id.tvQuestionGps);
				CodePanButton btnGetGps = (CodePanButton) view.findViewById(R.id.btnGetGps);
				if(field.isRequired) {
					requiredField(tvQuestionGps, field.name);
				}
				else {
					tvQuestionGps.setText(field.name);
				}
				if(answer.value != null) {
					String array[] = answer.value.split(",");
					if(array.length == 2) {
						String result = array[0] + ", " + array[1];
						tvResultGps.setText(result);
						tvStatusGps.setText(R.string.location_acquired);
						tvResultGps.setVisibility(View.VISIBLE);
					}
				}
				btnGetGps.setEnabled(isEditable);
				btnGetGps.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if(((MainActivity) getActivity()).isGpsSecured()) {
							SearchGpsFragment search = new SearchGpsFragment();
							search.setOnFragmentCallback(PageFragment.this);
							search.setOnGpsFixedCallback(new com.mobileoptima.callback.Interface.OnGpsFixedCallback() {
								@Override
								public void onGpsFixed(GpsObj gps) {
									String result = gps.latitude + ", " + gps.longitude;
									tvStatusGps.setText(R.string.location_acquired);
									tvResultGps.setText(result);
									tvResultGps.setVisibility(View.VISIBLE);
									answer.value = gps.latitude + "," + gps.longitude;
									withChanges = true;
								}
							});
							transaction = manager.beginTransaction();
							transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
									R.anim.fade_in, R.anim.fade_out);
							transaction.add(R.id.rlMain, search);
							transaction.addToBackStack(null);
							transaction.commit();
						}
					}
				});
				break;
			case FieldType.SIG:
				view = inflater.inflate(R.layout.field_signature_layout, container, false);
				CodePanLabel tvQuestionSignature = (CodePanLabel) view.findViewById(R.id.tvQuestionSignature);
				CodePanButton btnAddSignature = (CodePanButton) view.findViewById(R.id.btnAddSignature);
				final CodePanLabel tvAddSignature = (CodePanLabel) view.findViewById(R.id.tvAddSignature);
				if(field.isRequired) {
					requiredField(tvQuestionSignature, field.name);
				}
				else {
					tvQuestionSignature.setText(field.name);
				}
				if(answer.value != null) {
					tvAddSignature.setText(R.string.signature_captured);
				}
				else {
					btnAddSignature.setEnabled(isEditable);
				}
				btnAddSignature.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						SignatureDialogFragment signature = new SignatureDialogFragment();
						signature.setTitle(field.name);
						signature.setPhotoID(answer.value);
						signature.setEditable(isEditable);
						signature.setOnFragmentCallback(PageFragment.this);
						signature.setOnSignCallback(new OnSignCallback() {
							@Override
							public void onSign(ImageObj image) {
								tvAddSignature.setText(R.string.signature_captured);
								answer.value = image.ID;
								withChanges = true;
							}
						});
						signature.setOnClearCallback(new OnClearCallback() {
							@Override
							public void onClear() {
								answer.value = null;
							}
						});
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
								R.anim.fade_in, R.anim.fade_out);
						transaction.add(R.id.rlMain, signature);
						transaction.addToBackStack(null);
						transaction.commit();
					}
				});
				break;
			case FieldType.TIME:
				view = inflater.inflate(R.layout.field_time_layout, container, false);
				CodePanLabel tvQuestionTime = (CodePanLabel) view.findViewById(R.id.tvQuestionTime);
				final CodePanButton btnGetTime = (CodePanButton) view.findViewById(R.id.btnGetTime);
				final CodePanLabel tvResultTime = (CodePanLabel) view.findViewById(R.id.tvResultTime);
				final CodePanLabel tvStatusTime = (CodePanLabel) view.findViewById(R.id.tvStatusTime);
				if(field.isRequired) {
					requiredField(tvQuestionTime, field.name);
				}
				else {
					tvQuestionTime.setText(field.name);
				}
				if(answer.value != null) {
					String array[] = answer.value.split(",");
					if(array.length == 2) {
						String date = CodePanUtils.getCalendarDate(array[0], false, true);
						String time = CodePanUtils.getNormalTime(array[1], false);
						String result = date + " " + time;
						tvResultTime.setText(result);
						tvResultTime.setVisibility(View.VISIBLE);
						tvStatusTime.setText(R.string.time_captured);
					}
				}
				btnGetTime.setEnabled(isEditable);
				btnGetTime.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						String dTime = CodePanUtils.getTime();
						String dDate = CodePanUtils.getDate();
						String date = CodePanUtils.getCalendarDate(dDate, false, true);
						String time = CodePanUtils.getNormalTime(dTime, false);
						String result = date + " " + time;
						tvResultTime.setText(result);
						tvResultTime.setVisibility(View.VISIBLE);
						tvStatusTime.setText(R.string.time_captured);
						answer.value = dDate + "," + dTime;
						withChanges = true;
					}
				});
				break;
			case FieldType.PHOTO:
				view = inflater.inflate(R.layout.field_photo_layout, container, false);
				CodePanLabel tvQuestionPhoto = (CodePanLabel) view.findViewById(R.id.tvQuestionPhoto);
				CodePanButton btnAddPhoto = (CodePanButton) view.findViewById(R.id.btnAddPhoto);
				FrameLayout flAddPhoto = (FrameLayout) view.findViewById(R.id.flAddPhoto);
				final LinearLayout llGridPhoto = (LinearLayout) view.findViewById(R.id.llGridPhoto);
				if(field.isRequired) {
					requiredField(tvQuestionPhoto, field.name);
				}
				else {
					tvQuestionPhoto.setText(field.name);
				}
				if(answer.value != null) {
					String array[] = answer.value.split(",");
					ArrayList<ImageObj> imageList = new ArrayList<>();
					for(String photoID : array) {
						ImageObj image = new ImageObj();
						String fileName = TarkieLib.getFileName(db, photoID);
						if(fileName != null && !fileName.isEmpty()) {
							image.ID = photoID;
							image.fileName = fileName;
							imageList.add(image);
						}
					}
					updatePhotoGrid(llGridPhoto, imageList);
					answer.imageList = imageList;
				}
				if(isEditable) {
					flAddPhoto.setVisibility(View.VISIBLE);
				}
				else {
					flAddPhoto.setVisibility(View.GONE);
				}
				btnAddPhoto.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						FormFragment form = (FormFragment) manager.findFragmentByTag(Tag.FORM);
						CameraMultiShotFragment camera = new CameraMultiShotFragment();
						camera.setOnBackPressedCallback(form.getOnBackPressedCallback());
						camera.setOnOverrideCallback(overrideCallback);
						camera.setOnFragmentCallback(PageFragment.this);
						camera.setOnCameraDoneCallback(new com.mobileoptima.callback.Interface.OnCameraDoneCallback() {
							@Override
							public void onCameraDone(ArrayList<ImageObj> imageList) {
								if(answer.imageList != null) {
									imageList.addAll(0, answer.imageList);
								}
								updatePhotoGrid(llGridPhoto, imageList);
								answer.imageList = imageList;
								withChanges = true;
							}
						});
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
								R.anim.slide_in_ltr, R.anim.slide_out_ltr);
						transaction.add(R.id.rlMain, camera);
						transaction.hide(form);
						transaction.addToBackStack(null);
						transaction.commit();
					}
				});
				break;
		}
		return view;
	}

	public void setPage(PageObj page) {
		this.page = page;
	}

	public void updatePhotoGrid(final LinearLayout llGridPhoto, final ArrayList<ImageObj> imageList) {
		View view = getView();
		if(view != null) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			ViewGroup container = (ViewGroup) view.getParent();
			llGridPhoto.removeAllViews();
			for(final ImageObj obj : imageList) {
				View child = inflater.inflate(R.layout.photo_item, container, false);
				CodePanButton btnPhoto = (CodePanButton) child.findViewById(R.id.btnPhoto);
				ImageView ivPhoto = (ImageView) child.findViewById(R.id.ivPhoto);
				int size = CodePanUtils.getWidth(child);
				Bitmap bitmap = CodePanUtils.getBitmapThumbnails(getActivity(), App.FOLDER, obj.fileName, size);
				ivPhoto.setImageBitmap(bitmap);
				obj.bitmap = bitmap;
				btnPhoto.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int position = imageList.indexOf(obj);
						onPhotoGridItemClick(llGridPhoto, imageList, position);
					}
				});
				llGridPhoto.addView(child);
			}
			ViewParent parent = llGridPhoto.getParent();
			final HorizontalScrollView hsvPhoto = (HorizontalScrollView) parent.getParent();
			hsvPhoto.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
				@Override
				public void onLayoutChange(View view, int l, int t, int r, int b, int ol,
										   int ot, int or, int ob) {
					hsvPhoto.removeOnLayoutChangeListener(this);
					hsvPhoto.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
				}
			});
		}
	}

	public void onPhotoGridItemClick(final LinearLayout llGridPhoto, final ArrayList<ImageObj> imageList, int position) {
		FormFragment form = (FormFragment) manager.findFragmentByTag(Tag.FORM);
		boolean isEditable = entry == null || !entry.isSubmit;
		ImagePreviewFragment preview = new ImagePreviewFragment();
		preview.setDeletable(isEditable);
		preview.setImageList(imageList, position);
		preview.setOnDeletePhotoCallback(new OnDeletePhotoCallback() {
			@Override
			public void onDeletePhoto(int position) {
				imageList.remove(position);
				llGridPhoto.removeViewAt(position);
				if(imageList.size() > 0) {
					invalidateViews(llGridPhoto, imageList);
				}
			}
		});
		preview.setOnFragmentCallback(PageFragment.this);
		transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		transaction.add(R.id.rlMain, preview);
		transaction.hide(form);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void invalidateViews(final LinearLayout llGridPhoto, final ArrayList<ImageObj> imageList) {
		int count = llGridPhoto.getChildCount();
		for(int i = 0; i < count; i++) {
			final int position = i;
			View view = llGridPhoto.getChildAt(i);
			CodePanButton btnPhoto = (CodePanButton) view.findViewById(R.id.btnPhoto);
			btnPhoto.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onPhotoGridItemClick(llGridPhoto, imageList, position);
				}
			});
		}
	}

	public void requiredField(CodePanLabel label, String text) {
		if(text != null) {
			int length = text.length();
			String name = text + "*";
			ArrayList<SpannableMap> list = new ArrayList<>();
			list.add(new SpannableMap(length, length + 1, Color.RED));
			SpannableStringBuilder ssb = CodePanUtils.customizeText(list, name);
			label.setText(ssb);
		}
	}

	public boolean hasSelected(ArrayList<ChoiceObj> choiceList) {
		for(ChoiceObj obj : choiceList) {
			if(obj.isCheck) {
				return true;
			}
		}
		return false;
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public boolean withChanges() {
		return this.withChanges;
	}

	public ArrayList<FieldObj> getFieldList() {
		if(fieldList != null) {
			for(FieldObj field : fieldList) {
				AnswerObj answer = field.answer;
				switch(field.type) {
					case FieldType.MS:
						if(answer.choiceList != null && !answer.choiceList.isEmpty()) {
							answer.value = "";
							for(ChoiceObj choiceObj : answer.choiceList) {
								if(choiceObj.isCheck) {
									answer.value += choiceObj.code + ",";
								}
							}
							int length = answer.value.length();
							if(length != 0) {
								answer.value = answer.value.substring(0, length - 1);
							}
						}
						break;
					case FieldType.PHOTO:
						if(answer.imageList != null && !answer.imageList.isEmpty()) {
							answer.value = "";
							for(ImageObj image : answer.imageList) {
								answer.value += image.ID + ",";
							}
							int length = answer.value.length();
							if(length != 0) {
								answer.value = answer.value.substring(0, length - 1);
							}
						}
						break;
				}
			}
		}
		return this.fieldList;
	}

	@Override
	public void onFragment(boolean status) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(!status);
		}
	}
}

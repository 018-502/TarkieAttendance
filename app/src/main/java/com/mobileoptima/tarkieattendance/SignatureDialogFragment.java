package com.mobileoptima.tarkieattendance;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.SignatureView;
import com.mobileoptima.callback.Interface.OnClearCallback;
import com.mobileoptima.callback.Interface.OnSignCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.ImageObj;

public class SignatureDialogFragment extends Fragment implements View.OnClickListener {

	private CodePanButton btnCancelSignatureDialog, btnSaveSignatureDialog;
	private OnFragmentCallback fragmentCallback;
	private CodePanLabel tvTitleSignatureDialog;
	private OnClearCallback clearCallback;
	private OnSignCallback signCallback;
	private SignatureView svSignatureDialog;
	private ImageView ivSignatureDialog;
	private SQLiteAdapter db;
	private String photoID;
	private String title;

	@Override
	public void onStart() {
		super.onStart();
		setOnBackStack(true);
	}

	@Override
	public void onStop() {
		super.onStop();
		setOnBackStack(false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = ((MainActivity) getActivity()).getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.signature_dialog_layout, container, false);
		btnCancelSignatureDialog = (CodePanButton) view.findViewById(R.id.btnCancelSignatureDialog);
		btnSaveSignatureDialog = (CodePanButton) view.findViewById(R.id.btnSaveSignatureDialog);
		tvTitleSignatureDialog = (CodePanLabel) view.findViewById(R.id.tvTitleSignatureDialog);
		svSignatureDialog = (SignatureView) view.findViewById(R.id.svSignatureDialog);
		ivSignatureDialog = (ImageView) view.findViewById(R.id.ivSignatureDialog);
		btnCancelSignatureDialog.setOnClickListener(this);
		btnSaveSignatureDialog.setOnClickListener(this);
		tvTitleSignatureDialog.setText(title);
		if(photoID != null && !photoID.isEmpty()) {
			String fileName = TarkieLib.getFileName(db, photoID);
			Bitmap bitmap = CodePanUtils.getBitmapImage(getActivity(), App.FOLDER, fileName);
			ivSignatureDialog.setImageBitmap(bitmap);
			ivSignatureDialog.setVisibility(View.VISIBLE);
			svSignatureDialog.setVisibility(View.GONE);
			btnSaveSignatureDialog.setText(R.string.clear);
		}
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnCancelSignatureDialog:
				getActivity().getSupportFragmentManager().popBackStack();
				break;
			case R.id.btnSaveSignatureDialog:
				if(photoID != null) {
					btnSaveSignatureDialog.setText(R.string.save);
					boolean result = TarkieLib.deletePhoto(getActivity(), db, photoID);
					if(result) {
						ivSignatureDialog.setVisibility(View.GONE);
						svSignatureDialog.setVisibility(View.VISIBLE);
						if(clearCallback != null) {
							clearCallback.onClear();
						}
						photoID = null;
					}
				}
				else {
					if(!svSignatureDialog.isEmpty()) {
						String fileName = System.currentTimeMillis() + ".jpg";
						String path = getActivity().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath();
						int width = svSignatureDialog.getWidth();
						int height = svSignatureDialog.getHeight();
						boolean result = svSignatureDialog.exportFile(path, fileName, width, height);
						if(result && signCallback != null) {
							ImageObj image = new ImageObj();
							image.fileName = fileName;
							image.dDate = CodePanUtils.getDate();
							image.dTime = CodePanUtils.getTime();
							image.isSignature = true;
							image.ID = TarkieLib.savePhoto(db, image);
							getActivity().getSupportFragmentManager().popBackStack();
							signCallback.onSign(image);
						}
					}
					else {
						CodePanUtils.alertToast(getActivity(), "Please affix your signature.");
					}
				}
				break;
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPhotoID(String photoID) {
		this.photoID = photoID;
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	public void setOnSignCallback(OnSignCallback signCallback) {
		this.signCallback = signCallback;
	}

	public void setOnClearCallback(OnClearCallback clearCallback) {
		this.clearCallback = clearCallback;
	}

	public void setOnBackStack(boolean isOnBackStack) {
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}
}

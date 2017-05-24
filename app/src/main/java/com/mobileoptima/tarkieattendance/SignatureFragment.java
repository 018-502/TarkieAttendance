package com.mobileoptima.tarkieattendance;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.callback.Interface.OnSignCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.SignatureView;
import com.mobileoptima.constant.App;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.AttendanceObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;

public class SignatureFragment extends Fragment implements OnClickListener {

	private CodePanButton btnBackSignature, btnCancelSignature,
			btnSaveSignature, btnClearSignature;
	private OnSignCallback signCallback;
	private SignatureView svSignature;
	private AttendanceObj attendance;
	private FragmentManager manager;
	private boolean isTimeOut;
	private SQLiteAdapter db;
	private String signature;

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
		View view = inflater.inflate(R.layout.signature_layout, container, false);
		btnBackSignature = (CodePanButton) view.findViewById(R.id.btnBackSignature);
		btnCancelSignature = (CodePanButton) view.findViewById(R.id.btnCancelSignature);
		btnClearSignature = (CodePanButton) view.findViewById(R.id.btnClearSignature);
		btnSaveSignature = (CodePanButton) view.findViewById(R.id.btnSaveSignature);
		svSignature = (SignatureView) view.findViewById(R.id.svSignature);
		btnBackSignature.setOnClickListener(this);
		btnCancelSignature.setOnClickListener(this);
		btnClearSignature.setOnClickListener(this);
		btnSaveSignature.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackSignature:
				manager.popBackStack();
				break;
			case R.id.btnCancelSignature:
				manager.popBackStack();
				break;
			case R.id.btnClearSignature:
				svSignature.clear();
				break;
			case R.id.btnSaveSignature:
				if(!svSignature.isEmpty()) {
					String fileName = System.currentTimeMillis() + ".jpg";
					String path = getActivity().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath();
					int width = svSignature.getWidth();
					int height = svSignature.getHeight();
					boolean result = svSignature.exportFile(path, fileName, width, height);
					if(result) {
						manager.popBackStack();
						if(signCallback != null) {
							signCallback.onSign(fileName);
						}
						if(signature != null) {
							CodePanUtils.deleteFile(getActivity(), App.FOLDER, signature);
						}
						if(attendance != null) {
							TimeOutObj out = attendance.out;
							TimeInObj in = out.timeIn;
							if(in != null && !isTimeOut) {
								out.signature = fileName;
								result = TarkieLib.updateSignature(db, out);
								if(result) {
									((MainActivity) getActivity()).updateSyncCount();
									CodePanUtils.alertToast(getActivity(), "Signature updated");
								}
							}
						}
					}
				}
				else {
					CodePanUtils.alertToast(getActivity(), "Please affix your signature.");
				}
				break;
		}
	}

	public void setOnSignCallback(OnSignCallback signCallback) {
		this.signCallback = signCallback;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public void setIsTimeOut(boolean isTimeOut) {
		this.isTimeOut = isTimeOut;
	}

	public void setAttendance(AttendanceObj attendance) {
		this.attendance = attendance;
	}
}
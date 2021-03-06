package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CircularImageView;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnDeleteAnnouncementCallback;
import com.mobileoptima.model.AnnouncementObj;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ExpenseReportsDetailsFragment extends Fragment implements OnClickListener {

	private CodePanLabel tvTitleAnnouncementDetails, tvSubjectAnnouncementDetails,
			tvAnnouncedByAnnouncementDetails, tvAnnouncedTimeAnnouncementDetails,
			tvMessageAnnouncementDetails;
	private CodePanButton btnBackAnnouncementDetails, btnDeleteAnnouncementDetails;
	private OnDeleteAnnouncementCallback deleteAnnouncementCallback;
	private CircularImageView ivPhotoAnnouncementDetails;
	private FragmentTransaction transaction;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private FragmentManager manager;
	private AnnouncementObj obj;
	private SQLiteAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
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
		View view = inflater.inflate(R.layout.announcement_details_layout, container, false);
		btnBackAnnouncementDetails = (CodePanButton) view.findViewById(R.id.btnBackAnnouncementDetails);
		tvTitleAnnouncementDetails = (CodePanLabel) view.findViewById(R.id.tvTitleAnnouncementDetails);
		btnDeleteAnnouncementDetails = (CodePanButton) view.findViewById(R.id.btnDeleteAnnouncementDetails);
		ivPhotoAnnouncementDetails = (CircularImageView) view.findViewById(R.id.ivPhotoAnnouncementDetails);
		tvSubjectAnnouncementDetails = (CodePanLabel) view.findViewById(R.id.tvSubjectAnnouncementDetails);
		tvAnnouncedByAnnouncementDetails = (CodePanLabel) view.findViewById(R.id.tvAnnouncedByAnnouncementDetails);
		tvAnnouncedTimeAnnouncementDetails = (CodePanLabel) view.findViewById(R.id.tvAnnouncedTimeAnnouncementDetails);
		tvMessageAnnouncementDetails = (CodePanLabel) view.findViewById(R.id.tvMessageAnnouncementDetails);
		btnBackAnnouncementDetails.setOnClickListener(this);
		btnDeleteAnnouncementDetails.setOnClickListener(this);
		String date = CodePanUtils.getCalendarDate(obj.announcedDate, true, true);
		tvTitleAnnouncementDetails.setText(date);
		imageLoader.displayImage(obj.announcedByImageURL, ivPhotoAnnouncementDetails, options);
		tvSubjectAnnouncementDetails.setText(obj.subject);
		tvAnnouncedByAnnouncementDetails.setText(obj.announcedBy);
		String time = CodePanUtils.getNormalTime(obj.announcedTime, false);
		tvAnnouncedTimeAnnouncementDetails.setText(" | " + time);
		tvMessageAnnouncementDetails.setText(obj.message);
		return view;
	}

	public void setAnnouncement(AnnouncementObj obj) {
		this.obj = obj;
	}

	public void setOnDeleteAnnouncementCallback(OnDeleteAnnouncementCallback deleteAnnouncementCallback) {
		this.deleteAnnouncementCallback = deleteAnnouncementCallback;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackAnnouncementDetails:
				manager.popBackStack();
				break;
			case R.id.btnDeleteAnnouncementDetails:
				final AlertDialogFragment alert = new AlertDialogFragment();
				alert.setDialogTitle(R.string.delete_announcement_title);
				alert.setDialogMessage(R.string.delete_announcement_message);
				alert.setNegativeButton("Cancel", new OnClickListener() {
					@Override
					public void onClick(View view) {
						manager.popBackStack();
					}
				});
				alert.setPositiveButton("Yes", new OnClickListener() {
					@Override
					public void onClick(View view) {
						if(deleteAnnouncementCallback != null) {
							deleteAnnouncementCallback.onDeleteAnnouncement(obj);
						}
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
				break;
		}
	}
}
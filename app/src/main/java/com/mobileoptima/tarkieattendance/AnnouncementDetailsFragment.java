package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface;
import com.mobileoptima.callback.Interface.OnDeleteAnnouncementCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.model.AnnouncementObj;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class AnnouncementDetailsFragment extends Fragment implements OnClickListener, OnBackPressedCallback {
	private AnnouncementObj obj;
	private DisplayImageOptions options;
	private ImageView ivPhotoAnnouncementDetails;
	private CodePanButton btnBackAnnouncementDetails, btnDeleteAnnouncementDetails;
	private CodePanLabel tvTitleAnnouncementDetails, tvSubjectAnnouncementDetails, tvAnnouncedByAnnouncementDetails, tvAnnouncedTimeAnnouncementDetails, tvMessageAnnouncementDetails;
	private ImageLoader imageLoader;
	private FragmentManager manager;
	private OnDeleteAnnouncementCallback deleteAnnouncementCallback;
	private FragmentTransaction transaction;
	private SQLiteAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity main = (MainActivity) getActivity();
		main.setOnBackPressedCallback(this);
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
		Bundle bundle = getArguments();
		obj = (AnnouncementObj) bundle.getSerializable("Announcement");
		this.imageLoader = ImageLoader.getInstance();
		if(!imageLoader.isInited()) {
			imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		}
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.color.gray_qua)
				.showImageForEmptyUri(R.color.gray_qua)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(50))
				.build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.announcement_details_layout, container, false);
		btnBackAnnouncementDetails = (CodePanButton) view.findViewById(R.id.btnBackAnnouncementDetails);
		tvTitleAnnouncementDetails = (CodePanLabel) view.findViewById(R.id.tvTitleAnnouncementDetails);
		btnDeleteAnnouncementDetails = (CodePanButton) view.findViewById(R.id.btnDeleteAnnouncementDetails);
		ivPhotoAnnouncementDetails = (ImageView) view.findViewById(R.id.ivPhotoAnnouncementDetails);
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
		tvAnnouncedTimeAnnouncementDetails.setText(" | ".concat(obj.announcedTime));
		tvMessageAnnouncementDetails.setText(obj.message);
		return view;
	}

	public void setOnDeleteAnnouncementCallback(OnDeleteAnnouncementCallback deleteAnnouncementCallback) {
		this.deleteAnnouncementCallback = deleteAnnouncementCallback;
	}

	@Override
	public void onBackPressed() {
		int count = manager.getBackStackEntryCount();
		if(count > 0) {
			manager.popBackStack();
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackAnnouncementDetails:
				onBackPressed();
				break;
			case R.id.btnDeleteAnnouncementDetails:
				final AlertDialogFragment alert = new AlertDialogFragment();
				alert.setDialogTitle(R.string.delete_announcement_title);
				alert.setDialogMessage(R.string.delete_announcement_message);
				alert.setNegativeButton("Cancel", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						manager.popBackStack();
					}
				});
				alert.setPositiveButton("Yes", new View.OnClickListener() {
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
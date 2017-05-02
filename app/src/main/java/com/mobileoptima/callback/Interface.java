package com.mobileoptima.callback;

import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.mobileoptima.model.ChoiceObj;
import com.mobileoptima.model.ImageObj;
import com.mobileoptima.model.StoreObj;

import java.util.ArrayList;

public class Interface {

	public interface OnInitializeCallback {
		void onInitialize(SQLiteAdapter db);
	}

	public interface OnOverrideCallback {
		void onOverride(boolean isOverridden);
	}

	public interface OnLoginCallback {
		void onLogin();
	}

	public interface OnRetakeCameraCallback {
		void onRetakeCamera();
	}

	public interface OnTimeValidatedCallback {
		void onTimeValidated();
	}

	public interface OnGpsFixedCallback {
		void onGpsFixed(GpsObj gps);
	}

	public interface OnCountdownFinishCallback {
		void onCountdownFinish();
	}

	public interface OnSelectStoreCallback {
		void onSelectStore(StoreObj store);
	}

	public interface OnOptionSelectedCallback {
		void onOptionSelected(ChoiceObj obj);
	}

	public interface OnSignCallback {
		void onSign(ImageObj image);
	}

	public interface OnClearCallback {
		void onClear();
	}

	public interface OnDeletePhotoCallback {
		void onDeletePhoto(int position);
	}

	public interface OnCameraDoneCallback {
		void onCameraDone(ArrayList<ImageObj> imageList);
	}

	public interface OnHighlightEntriesCallback {
		void onHighlightEntries(boolean isHighlight);
	}
}

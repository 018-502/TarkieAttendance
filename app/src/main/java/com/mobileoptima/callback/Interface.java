package com.mobileoptima.callback;

import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.mobileoptima.model.AnnouncementObj;
import com.mobileoptima.model.ChoiceObj;
import com.mobileoptima.model.EntryObj;
import com.mobileoptima.model.ImageObj;
import com.mobileoptima.model.SearchObj;
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

	public interface OnDeleteAnnouncementCallback {
		void onDeleteAnnouncement(AnnouncementObj obj);
	}

	public interface OnCameraDoneCallback {
		void onCameraDone(ArrayList<ImageObj> imageList);
	}

	public interface OnHighlightEntriesCallback {
		void onHighlightEntries(boolean isHighlight);
	}

	public interface OnMultiUpdateCallback {
		void onMultiUpdate();
	}

	public interface OnSearchItemCallback {
		void onSearchItem(SearchObj search, int type);
	}

	public interface OnSaveEntryCallback {
		void onSaveEntry(EntryObj entry);
	}

	public interface OnDeleteEntryCallback {
		void onDeleteEntry(EntryObj entry);
	}

	public interface OnResultCallback{
		void onResult(boolean result);
	}
}

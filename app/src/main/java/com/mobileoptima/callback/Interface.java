package com.mobileoptima.callback;

import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.mobileoptima.model.AnnouncementObj;
import com.mobileoptima.model.CheckInObj;
import com.mobileoptima.model.CheckOutObj;
import com.mobileoptima.model.ChoiceObj;
import com.mobileoptima.model.EntryObj;
import com.mobileoptima.model.ExpenseObj;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.ImageObj;
import com.mobileoptima.model.PhotoObj;
import com.mobileoptima.model.SearchObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskStatusObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;
import com.mobileoptima.model.VisitObj;

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

	public interface OnAddStoreCallback {
		void onAddStore(StoreObj store);
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
		void onCameraDone(ArrayList<PhotoObj> photoList);
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

	public interface OnUpdateExpenseCallback {
		void onUpdateExpense(ExpenseObj expense);
	}

	public interface OnResultCallback {
		void onResult(boolean result);
	}

	public interface OnTimeInCallback {
		void onTimeIn(TimeInObj in);
	}

	public interface OnTimeOutCallback {
		void onTimeOut(TimeOutObj out);
	}

	public interface OnCheckInCallback {
		void onCheckIn(CheckInObj in);
	}

	public interface OnCheckOutCallback {
		void onCheckOut(CheckOutObj out);
	}

	public interface OnUsePhotoCallback {
		void onUsePhoto(String fileName);
	}

	public interface OnSelectStatusCallback {
		void onSelectStatus(TaskStatusObj status);
	}

	public interface OnSaveVisitCallback {
		void onSaveVisit(VisitObj visit);
	}

	public interface OnTagFormsCallback {
		void onTagForms(ArrayList<FormObj> formList);
	}
}

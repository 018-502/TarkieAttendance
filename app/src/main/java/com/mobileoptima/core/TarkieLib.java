package com.mobileoptima.core;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableStringBuilder;
import android.view.View;

import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.Condition;
import com.codepan.database.Condition.Operator;
import com.codepan.database.Field;
import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.codepan.database.SQLiteQuery;
import com.codepan.model.GpsObj;
import com.codepan.model.TimeObj;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.constant.FieldType;
import com.mobileoptima.constant.Incident;
import com.mobileoptima.model.AnswerObj;
import com.mobileoptima.model.AttendanceObj;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreakObj;
import com.mobileoptima.model.CheckInObj;
import com.mobileoptima.model.ChoiceObj;
import com.mobileoptima.model.ContactObj;
import com.mobileoptima.model.EmployeeObj;
import com.mobileoptima.model.EntryObj;
import com.mobileoptima.model.ExpenseObj;
import com.mobileoptima.model.FieldObj;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.ImageObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;
import com.mobileoptima.model.VisitsDateObj;
import com.mobileoptima.schema.Tables;
import com.mobileoptima.schema.Tables.TB;
import com.mobileoptima.tarkieattendance.AlertDialogFragment;
import com.mobileoptima.tarkieattendance.R;

import net.sqlcipher.Cursor;

import java.util.ArrayList;

import static com.codepan.database.SQLiteQuery.DataType;
import static com.mobileoptima.schema.Tables.TB.ANSWERS;
import static com.mobileoptima.schema.Tables.TB.COMPANY;
import static com.mobileoptima.schema.Tables.TB.CONVENTION;
import static com.mobileoptima.schema.Tables.TB.CREDENTIALS;
import static com.mobileoptima.schema.Tables.TB.EMPLOYEE;
import static com.mobileoptima.schema.Tables.TB.ENTRIES;
import static com.mobileoptima.schema.Tables.TB.EXPENSE;
import static com.mobileoptima.schema.Tables.TB.EXPENSE_DEFAULT;
import static com.mobileoptima.schema.Tables.TB.EXPENSE_FUEL_CONSUMPTION;
import static com.mobileoptima.schema.Tables.TB.EXPENSE_FUEL_PURCHASE;
import static com.mobileoptima.schema.Tables.TB.FIELDS;
import static com.mobileoptima.schema.Tables.TB.PHOTO;
import static com.mobileoptima.schema.Tables.TB.SETTINGS;
import static com.mobileoptima.schema.Tables.TB.SYNC_BATCH;

public class TarkieLib {
	public static void alertDialog(final FragmentActivity activity, String title, String message,
								   OnFragmentCallback callback) {
		final FragmentManager manager = activity.getSupportFragmentManager();
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(title);
		alert.setDialogMessage(message);
		alert.setOnFragmentCallback(callback);
		alert.setPositiveButton("OK", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				manager.popBackStack();
			}
		});
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, alert);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public static void alertDialog(final FragmentActivity activity, String title, String message) {
		final FragmentManager manager = activity.getSupportFragmentManager();
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(title);
		alert.setDialogMessage(message);
		alert.setPositiveButton("OK", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				manager.popBackStack();
			}
		});
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, alert);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public static void alertDialog(final FragmentActivity activity, int title, int message,
								   OnFragmentCallback callback) {
		final FragmentManager manager = activity.getSupportFragmentManager();
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(title);
		alert.setDialogMessage(message);
		alert.setOnFragmentCallback(callback);
		alert.setPositiveButton("OK", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				manager.popBackStack();
			}
		});
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, alert);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public static void alertDialog(final FragmentActivity activity, int title, int message) {
		final FragmentManager manager = activity.getSupportFragmentManager();
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(title);
		alert.setDialogMessage(message);
		alert.setPositiveButton("OK", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				manager.popBackStack();
			}
		});
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, alert);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public static void createTables(SQLiteAdapter db) {
		db.execQuery(Tables.create(TB.API_KEY));
		db.execQuery(Tables.create(TB.SYNC_BATCH));
		db.execQuery(Tables.create(TB.CREDENTIALS));
		db.execQuery(Tables.create(TB.COMPANY));
		db.execQuery(Tables.create(TB.EMPLOYEE));
		db.execQuery(Tables.create(TB.BREAK));
		db.execQuery(Tables.create(TB.STORES));
		db.execQuery(Tables.create(TB.CONVENTION));
		db.execQuery(Tables.create(TB.GPS));
		db.execQuery(Tables.create(TB.TIME_IN));
		db.execQuery(Tables.create(TB.TIME_OUT));
		db.execQuery(Tables.create(TB.BREAK_IN));
		db.execQuery(Tables.create(TB.BREAK_OUT));
		db.execQuery(Tables.create(TB.INCIDENT));
		db.execQuery(Tables.create(TB.INCIDENT_REPORT));
		db.execQuery(Tables.create(TB.TIME_SECURITY));
		db.execQuery(Tables.create(TB.LOCATION));
		db.execQuery(Tables.create(TB.PHOTO));
		db.execQuery(Tables.create(TB.TASK_PHOTO));
		db.execQuery(Tables.create(TB.EXPENSE));
		db.execQuery(Tables.create(TB.EXPENSE_DEFAULT));
		db.execQuery(Tables.create(TB.EXPENSE_FUEL_CONSUMPTION));
		db.execQuery(Tables.create(TB.EXPENSE_FUEL_PURCHASE));
		db.execQuery(Tables.create(TB.FORMS));
		db.execQuery(Tables.create(TB.FIELDS));
		db.execQuery(Tables.create(TB.CHOICES));
		db.execQuery(Tables.create(TB.ENTRIES));
		db.execQuery(Tables.create(TB.ANSWERS));
		db.execQuery(Tables.create(TB.TASK));
		db.execQuery(Tables.create(TB.TASK_ENTRY));
		db.execQuery(Tables.create(TB.TASK_FORM));
		db.execQuery(Tables.create(TB.CHECK_IN));
		db.execQuery(Tables.create(TB.CHECK_OUT));
		db.execQuery(Tables.create(SETTINGS));
		db.execQuery(Tables.create(TB.SETTINGS_GROUP));
		db.execQuery(Tables.create(TB.STORES));
	}

	public static void updateTables(SQLiteAdapter db, int o, int n) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String column = "photo";
		String table = Tables.getName(TB.TIME_IN);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.TEXT, column);
		}
		column = "storeID";
		table = Tables.getName(TB.TIME_IN);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.INTEGER, column);
		}
		column = "isPhotoUpload";
		table = Tables.getName(TB.TIME_IN);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, column, 0);
		}
		column = "photo";
		table = Tables.getName(TB.TIME_OUT);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.TEXT, column);
		}
		column = "signature";
		table = Tables.getName(TB.TIME_OUT);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.TEXT, column);
		}
		column = "isPhotoUpload";
		table = Tables.getName(TB.TIME_OUT);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, column, 0);
		}
		column = "isSignatureUpload";
		table = Tables.getName(TB.TIME_OUT);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, column, 0);
		}
		column = "dDate";
		table = Tables.getName(TB.SYNC_BATCH);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.TEXT, column);
		}
		column = "dTime";
		table = Tables.getName(TB.SYNC_BATCH);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.TEXT, column);
		}
		column = "breakID";
		table = Tables.getName(TB.BREAK_IN);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.INTEGER, column);
		}
		column = "timeInID";
		table = Tables.getName(TB.ENTRIES);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.INTEGER, column);
		}
		column = "category";
		table = Tables.getName(TB.FORMS);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.TEXT, column);
		}
		column = "gpsID";
		table = Tables.getName(TB.TIME_IN);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.INTEGER, column);
		}
		column = "gpsID";
		table = Tables.getName(TB.TIME_OUT);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.INTEGER, column);
		}
		column = "gpsID";
		table = Tables.getName(TB.BREAK_IN);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.INTEGER, column);
		}
		column = "gpsID";
		table = Tables.getName(TB.BREAK_OUT);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.INTEGER, column);
		}
		column = "gpsID";
		table = Tables.getName(TB.INCIDENT_REPORT);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.INTEGER, column);
		}
		column = "name";
		table = Tables.getName(TB.TASK);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.TEXT, column);
		}
		column = "isEnabled";
		table = Tables.getName(TB.GPS);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.INTEGER, column);
		}
		column = "withHistory";
		table = Tables.getName(TB.GPS);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.INTEGER, column);
		}
		column = "isValid";
		table = Tables.getName(TB.GPS);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.INTEGER, column);
		}
		column = "syncBatchID";
		table = Tables.getName(TB.STORES);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.TEXT, column);
		}
		column = "webStoreID";
		table = Tables.getName(TB.STORES);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.INTEGER, column);
		}
		column = "webStoreID";
		table = Tables.getName(TB.STORES);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.INTEGER, column);
		}
		column = "isDefault";
		table = Tables.getName(TB.STORES);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, column, 0);
		}
		column = "isSync";
		table = Tables.getName(TB.STORES);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, column, 0);
		}
		column = "isFromWeb";
		table = Tables.getName(TB.STORES);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, column, 0);
		}
		column = "dDate";
		table = Tables.getName(TB.STORES);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.TEXT, column);
		}
		column = "dTime";
		table = Tables.getName(TB.STORES);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.TEXT, column);
		}
		binder.finish();
	}

	public static boolean isAuthorized(SQLiteAdapter db) {
		String table = Tables.getName(TB.API_KEY);
		String query = "SELECT ID FROM " + table + " WHERE ID = 1";
		return db.isRecordExists(query);
	}

	public static boolean isLoggedIn(SQLiteAdapter db) {
		String table = Tables.getName(TB.CREDENTIALS);
		String query = "SELECT ID FROM " + table + " WHERE ID = 1 AND isLogOut = 0";
		return db.isRecordExists(query);
	}

	public static String getAPIKey(SQLiteAdapter db) {
		String table = Tables.getName(TB.API_KEY);
		String query = "SELECT apiKey FROM " + table + " WHERE ID = 1";
		return db.getString(query);
	}

	public static String getCompanyName(SQLiteAdapter db) {
		String table = Tables.getName(COMPANY);
		String query = "SELECT name FROM " + table + " LIMIT 1";
		return db.getString(query);
	}

	public static String getCompanyLogo(SQLiteAdapter db) {
		String table = Tables.getName(COMPANY);
		String query = "SELECT logoUrl FROM " + table + " LIMIT 1";
		return db.getString(query);
	}

	public static String getSyncBatchID(SQLiteAdapter db) {
		String table = Tables.getName(SYNC_BATCH);
		String query = "SELECT syncBatchID FROM " + table + " WHERE ID = 1";
		return db.getString(query);
	}

	public static String getEmployeeID(SQLiteAdapter db) {
		String table = Tables.getName(CREDENTIALS);
		String query = "SELECT empID FROM " + table + " WHERE ID = 1";
		return db.getString(query);
	}

	public static String getConvention(SQLiteAdapter db, String name) {
		String table = Tables.getName(CONVENTION);
		String query = "SELECT convention FROM " + table + " WHERE name = '" + name + "'";
		String convention = db.getString(query);
		if(convention != null && convention.equalsIgnoreCase(Convention.DEFAULT)) {
			convention = name;
		}
		return convention;
	}

	public static String getSettingsID(SQLiteAdapter db, String code) {
		String table = Tables.getName(SETTINGS);
		String query = "SELECT ID FROM " + table + " WHERE code = '" + code + "'";
		return db.getString(query);
	}

	public static EmployeeObj getEmployee(SQLiteAdapter db, String empID) {
		EmployeeObj employee = new EmployeeObj();
		String table = Tables.getName(TB.EMPLOYEE);
		String query = "SELECT employeeNo, firstName, lastName, email, mobile, imageUrl FROM " +
				table + " WHERE ID = '" + empID + "'";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			employee.ID = empID;
			employee.employeeNo = cursor.getString(0);
			employee.firstName = cursor.getString(1);
			employee.lastName = cursor.getString(2);
			employee.fullName = employee.firstName + " " + employee.lastName;
			employee.email = cursor.getString(3);
			employee.mobile = cursor.getString(4);
			employee.imageUrl = cursor.getString(5);
		}
		cursor.close();
		return employee;
	}

	public static String getGroupID(SQLiteAdapter db) {
		String e = Tables.getName(TB.EMPLOYEE);
		String c = Tables.getName(TB.CREDENTIALS);
		String query = "SELECT e.groupID FROM " + e + " e, " + c + " c WHERE c.ID = 1 " +
				"AND e.ID = c.empID";
		return db.getString(query);
	}

	public static String getEmployeeName(SQLiteAdapter db, String empID) {
		String name = null;
		String table = Tables.getName(EMPLOYEE);
		String query = "SELECT firstName, lastName FROM " + table + " WHERE ID = '" + empID + "'";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			String firstName = cursor.getString(0);
			String lastName = cursor.getString(1);
			name = firstName + " " + lastName;
		}
		cursor.close();
		return name;
	}

	public static int getLastOrderNo(SQLiteAdapter db, String formID) {
		String table = Tables.getName(FIELDS);
		String query = "SELECT orderNo FROM " + table + " WHERE formID = '" + formID + "' " +
				"ORDER BY orderNo DESC LIMIT 1";
		return db.getInt(query);
	}

	public static int getFirstOrderNo(SQLiteAdapter db, String formID) {
		String table = Tables.getName(FIELDS);
		String query = "SELECT orderNo FROM " + table + " WHERE formID = '" + formID + "' " +
				"ORDER BY orderNo LIMIT 1";
		return db.getInt(query);
	}

	public static AnswerObj getAnswer(SQLiteAdapter db, EntryObj entry, FieldObj field) {
		AnswerObj answer = new AnswerObj();
		if(entry != null) {
			String table = Tables.getName(TB.ANSWERS);
			String query = "SELECT ID, value FROM " + table + " WHERE entryID = '" +
					entry.ID + "' AND fieldID = '" + field.ID + "'";
			Cursor cursor = db.read(query);
			while(cursor.moveToNext()) {
				answer.ID = cursor.getString(0);
				answer.value = cursor.getString(1);
			}
			cursor.close();
		}
		return answer;
	}

	public static boolean logout(SQLiteAdapter db) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String table = Tables.getName(CREDENTIALS);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isLogOut", true));
		binder.update(table, query, 1);
		return binder.finish();
	}

	public static boolean isTimeIn(SQLiteAdapter db) {
		String empID = getEmployeeID(db);
		String table = Tables.getName(TB.TIME_IN);
		String query = "SELECT ID FROM " + table + " WHERE empID = '" + empID + "' " +
				"AND isTimeOut = 0";
		return db.isRecordExists(query);
	}

	public static boolean isCheckIn(SQLiteAdapter db) {
		String empID = getEmployeeID(db);
		String t = Tables.getName(TB.TASK);
		String i = Tables.getName(TB.CHECK_IN);
		String query = "SELECT i.ID FROM " + i + " i, " + t + " t WHERE t.isCheckIn = 1 " +
				"AND t.isCheckOut = 0 AND i.empID = '" + empID + "' AND i.taskID = t.ID " +
				"ORDER BY i.ID DESC LIMIT 1";
		return db.isRecordExists(query);
	}

	public static String getPendingVisit(SQLiteAdapter db) {
		String empID = getEmployeeID(db);
		String t = Tables.getName(TB.TASK);
		String i = Tables.getName(TB.CHECK_IN);
		String query = "SELECT t.name FROM " + i + " i, " + t + " t WHERE t.isCheckIn = 1 " +
				"AND t.isCheckOut = 0 AND i.empID = '" + empID + "' AND i.taskID = t.ID " +
				"ORDER BY i.ID DESC LIMIT 1";
		return db.getString(query);
	}

	public static boolean hasBreak(SQLiteAdapter db) {
		String table = Tables.getName(TB.BREAK);
		String query = "SELECT ID FROM " + table;
		return db.isRecordExists(query);
	}

	public static String getTimeInID(SQLiteAdapter db) {
		String empID = getEmployeeID(db);
		String table = Tables.getName(TB.TIME_IN);
		String query = "SELECT ID FROM " + table + " WHERE isTimeOut = 0 " +
				"AND empID = '" + empID + "' ORDER BY ID DESC LIMIT 1";
		return db.getString(query);
	}

	public static String getCheckInID(SQLiteAdapter db) {
		String empID = getEmployeeID(db);
		String t = Tables.getName(TB.TASK);
		String i = Tables.getName(TB.CHECK_IN);
		String query = "SELECT i.ID FROM " + i + " i, " + t + " t WHERE t.isCheckIn = 1 " +
				"AND t.isCheckOut = 0 AND i.empID = '" + empID + "' AND i.taskID = t.ID " +
				"ORDER BY i.ID DESC LIMIT 1";
		return db.getString(query);
	}

	public static boolean hasIncident(SQLiteAdapter db, int incidentID) {
		String timeInID = getTimeInID(db);
		String table = Tables.getName(TB.INCIDENT_REPORT);
		String query = "SELECT ID FROM " + table + " WHERE incidentID = '" +
				incidentID + "' AND timeInID = '" + timeInID + "'";
		return db.isRecordExists(query);
	}

	public static String saveGps(SQLiteAdapter db, GpsObj gps) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("gpsDate", gps.date));
		query.add(new FieldValue("gpsTime", gps.time));
		query.add(new FieldValue("gpsLongitude", gps.longitude));
		query.add(new FieldValue("gpsLatitude", gps.latitude));
		query.add(new FieldValue("isEnabled", gps.isEnabled));
		query.add(new FieldValue("withHistory", gps.withHistory));
		query.add(new FieldValue("isValid", gps.isValid));
		String gpsID = binder.insert(Tables.getName(TB.GPS), query);
		binder.finish();
		return gpsID;
	}

	public static boolean saveTimeIn(SQLiteAdapter db, GpsObj gps, String storeID, String photo) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String gpsID = saveGps(db, gps);
		String empID = getEmployeeID(db);
		String dDate = CodePanUtils.getDate();
		String dTime = CodePanUtils.getTime();
		String syncBatchID = getSyncBatchID(db);
		int battery = CodePanUtils.getBatteryLevel(db.getContext());
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("photo", photo));
		query.add(new FieldValue("gpsID", gpsID));
		query.add(new FieldValue("storeID", storeID));
		query.add(new FieldValue("batteryLevel", battery));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		binder.insert(Tables.getName(TB.TIME_IN), query);
		return binder.finish();
	}

	public static boolean saveTimeOut(SQLiteAdapter db, GpsObj gps, String dDate, String dTime,
									  String photo, String signature) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String gpsID = saveGps(db, gps);
		String timeInID = getTimeInID(db);
		String syncBatchID = getSyncBatchID(db);
		int battery = CodePanUtils.getBatteryLevel(db.getContext());
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("photo", photo));
		query.add(new FieldValue("gpsID", gpsID));
		query.add(new FieldValue("timeInID", timeInID));
		query.add(new FieldValue("signature", signature));
		query.add(new FieldValue("batteryLevel", battery));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		String timeOutID = binder.insert(Tables.getName(TB.TIME_OUT), query);
		if(timeOutID != null) {
			query.clearAll();
			query.add(new FieldValue("isTimeOut", true));
			binder.update(Tables.getName(TB.TIME_IN), query, timeInID);
		}
		return binder.finish();
	}

	public static boolean saveBreakIn(SQLiteAdapter db, GpsObj gps, BreakObj obj) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String gpsID = saveGps(db, gps);
		String empID = getEmployeeID(db);
		String timeInID = getTimeInID(db);
		String dDate = CodePanUtils.getDate();
		String dTime = CodePanUtils.getTime();
		String syncBatchID = getSyncBatchID(db);
		int battery = CodePanUtils.getBatteryLevel(db.getContext());
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("gpsID", gpsID));
		query.add(new FieldValue("batteryLevel", battery));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		query.add(new FieldValue("breakID", obj.ID));
		query.add(new FieldValue("timeInID", timeInID));
		binder.insert(Tables.getName(TB.BREAK_IN), query);
		return binder.finish();
	}

	public static boolean saveBreakOut(SQLiteAdapter db, GpsObj gps, BreakInObj in) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String gpsID = saveGps(db, gps);
		String dDate = CodePanUtils.getDate();
		String dTime = CodePanUtils.getTime();
		String syncBatchID = getSyncBatchID(db);
		int battery = CodePanUtils.getBatteryLevel(db.getContext());
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("gpsID", gpsID));
		query.add(new FieldValue("batteryLevel", battery));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		query.add(new FieldValue("breakInID", in.ID));
		String breakOutID = binder.insert(Tables.getName(TB.BREAK_OUT), query);
		if(breakOutID != null) {
			query.clearAll();
			query.add(new FieldValue("isBreakOut", true));
			binder.update(Tables.getName(TB.BREAK_IN), query, in.ID);
		}
		return binder.finish();
	}

	public static boolean saveIncident(SQLiteAdapter db, GpsObj gps, int incidentID,
									   int value) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String gpsID = saveGps(db, gps);
		String empID = getEmployeeID(db);
		String timeInID = getTimeInID(db);
		String syncBatchID = getSyncBatchID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("gpsID", gpsID));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		query.add(new FieldValue("incidentID", incidentID));
		query.add(new FieldValue("timeInID", timeInID));
		query.add(new FieldValue("value", value));
		if(incidentID != Incident.TAMPERED_TIME) {
			String dDate = CodePanUtils.getDate();
			String dTime = CodePanUtils.getTime();
			query.add(new FieldValue("dDate", dDate));
			query.add(new FieldValue("dTime", dTime));
		}
		else {
			TimeObj server = TimeSecurity.getServerTime(db);
			query.add(new FieldValue("dDate", server.date));
			query.add(new FieldValue("dTime", server.time));
		}
		binder.insert(Tables.getName(TB.INCIDENT_REPORT), query);
		return binder.finish();
	}

	public static boolean saveIncident(SQLiteAdapter db, GpsObj gps, int incidentID,
									   String value) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String gpsID = saveGps(db, gps);
		String empID = getEmployeeID(db);
		String timeInID = getTimeInID(db);
		String syncBatchID = getSyncBatchID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("gpsID", gpsID));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		query.add(new FieldValue("incidentID", incidentID));
		query.add(new FieldValue("timeInID", timeInID));
		query.add(new FieldValue("value", value));
		if(incidentID != Incident.TAMPERED_TIME) {
			String dDate = CodePanUtils.getDate();
			String dTime = CodePanUtils.getTime();
			query.add(new FieldValue("dDate", dDate));
			query.add(new FieldValue("dTime", dTime));
		}
		else {
			TimeObj server = TimeSecurity.getServerTime(db);
			query.add(new FieldValue("dDate", server.date));
			query.add(new FieldValue("dTime", server.time));
		}
		binder.insert(Tables.getName(TB.INCIDENT_REPORT), query);
		return binder.finish();
	}

	public static boolean saveIncident(SQLiteAdapter db, GpsObj gps, int incidentID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String gpsID = saveGps(db, gps);
		String empID = getEmployeeID(db);
		String timeInID = getTimeInID(db);
		String syncBatchID = getSyncBatchID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("gpsID", gpsID));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		query.add(new FieldValue("incidentID", incidentID));
		query.add(new FieldValue("timeInID", timeInID));
		if(incidentID != Incident.TAMPERED_TIME) {
			String dDate = CodePanUtils.getDate();
			String dTime = CodePanUtils.getTime();
			query.add(new FieldValue("dDate", dDate));
			query.add(new FieldValue("dTime", dTime));
		}
		else {
			TimeObj server = TimeSecurity.getServerTime(db);
			query.add(new FieldValue("dDate", server.date));
			query.add(new FieldValue("dTime", server.time));
		}
		binder.insert(Tables.getName(TB.INCIDENT_REPORT), query);
		return binder.finish();
	}

	public static boolean saveLocation(SQLiteAdapter db, GpsObj gps, String provider) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("dDate", CodePanUtils.getDate()));
		query.add(new FieldValue("dTime", CodePanUtils.getTime()));
		query.add(new FieldValue("longitude", gps.longitude));
		query.add(new FieldValue("latitude", gps.latitude));
		query.add(new FieldValue("accuracy", gps.accuracy));
		query.add(new FieldValue("provider", provider));
		binder.insert(Tables.getName(TB.LOCATION), query);
		return binder.finish();
	}

	public static boolean addTask(SQLiteAdapter db, String storeID, String startDate, String endDate) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String empID = getEmployeeID(db);
		String dDate = CodePanUtils.getDate();
		String dTime = CodePanUtils.getTime();
		String syncBatchID = getSyncBatchID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("storeID", storeID));
		query.add(new FieldValue("startDate", startDate));
		query.add(new FieldValue("endDate", endDate));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		binder.insert(Tables.getName(TB.TASK), query);
		return binder.finish();
	}

	public static boolean addTask(SQLiteAdapter db, String storeID, String startDate,
								  String endDate, String notes, ArrayList<FormObj> formList,
								  ArrayList<ImageObj> imageList) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String empID = getEmployeeID(db);
		String dDate = CodePanUtils.getDate();
		String dTime = CodePanUtils.getTime();
		String syncBatchID = getSyncBatchID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("notes", notes));
		query.add(new FieldValue("storeID", storeID));
		query.add(new FieldValue("startDate", startDate));
		query.add(new FieldValue("endDate", endDate));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		String taskID = binder.insert(Tables.getName(TB.TASK), query);
		if(taskID != null) {
			for(FormObj form : formList) {
				query.clearAll();
				query.add(new FieldValue("formID", form.ID));
				query.add(new FieldValue("taskID", taskID));
				binder.insert(Tables.getName(TB.TASK_FORM), query);
			}
			for(ImageObj image : imageList) {
				query.clearAll();
				query.add(new FieldValue("photoID", image.ID));
				query.add(new FieldValue("taskID", taskID));
				binder.insert(Tables.getName(TB.TASK_FORM), query);
			}
		}
		return binder.finish();
	}

	public static boolean editTask(SQLiteAdapter db, String storeID, String taskID, String notes,
								   ArrayList<EntryObj> entryList, ArrayList<ImageObj> imageList) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String t = Tables.getName(TB.TASK);
		String tf = Tables.getName(TB.TASK_FORM);
		String te = Tables.getName(TB.TASK_ENTRY);
		String tp = Tables.getName(TB.TASK_PHOTO);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("notes", notes));
		query.add(new FieldValue("storeID", storeID));
		binder.update(t, query, taskID);
		query.clearAll();
		query.add(new FieldValue("isTag", false));
		query.add(new Condition("taskID", taskID));
		binder.update(tf, query);
		binder.update(tp, query);
		for(EntryObj entry : entryList) {
			if(entry.ID != null) {
				query.clearAll();
				query.add(new Field("ID"));
				query.add(new Condition("entryID", entry.ID));
				query.add(new Condition("taskID", taskID));
				query.add(new FieldValue("entryID", entry.ID));
				query.add(new FieldValue("taskID", taskID));
				String sql = query.select(te);
				if(!db.isRecordExists(sql)) {
					binder.insert(te, query);
				}
			}
			FormObj form = entry.form;
			query.clearAll();
			query.add(new Field("ID"));
			query.add(new Condition("formID", form.ID));
			query.add(new Condition("taskID", taskID));
			query.add(new FieldValue("formID", form.ID));
			query.add(new FieldValue("taskID", taskID));
			query.add(new FieldValue("isTag", true));
			String sql = query.select(tf);
			if(db.isRecordExists(sql)) {
				binder.update(tf, query);
			}
			else {
				binder.insert(tf, query);
			}
		}
		for(ImageObj image : imageList) {
			query.clearAll();
			query.add(new Field("ID"));
			query.add(new Condition("photoID", image.ID));
			query.add(new Condition("taskID", taskID));
			query.add(new FieldValue("photoID", image.ID));
			query.add(new FieldValue("taskID", taskID));
			query.add(new FieldValue("isTag", true));
			String sql = query.select(tp);
			if(db.isRecordExists(sql)) {
				binder.update(tp, query);
			}
			else {
				binder.insert(tp, query);
			}
		}
		return binder.finish();
	}

	public static boolean saveCheckIn(SQLiteAdapter db, GpsObj gps, String taskID,
									  String dDate, String dTime, String photo) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String gpsID = saveGps(db, gps);
		String empID = getEmployeeID(db);
		String timeInID = getTimeInID(db);
		String syncBatchID = getSyncBatchID(db);
		int battery = CodePanUtils.getBatteryLevel(db.getContext());
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("gpsID", gpsID));
		query.add(new FieldValue("photo", photo));
		query.add(new FieldValue("taskID", taskID));
		query.add(new FieldValue("timeInID", timeInID));
		query.add(new FieldValue("batteryLevel", battery));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		String checkInID = binder.insert(Tables.getName(TB.CHECK_IN), query);
		if(checkInID != null) {
			query.clearAll();
			query.add(new FieldValue("isCheckIn", true));
			binder.update(Tables.getName(TB.TASK), query, taskID);
		}
		return binder.finish();
	}

	public static boolean saveCheckOut(SQLiteAdapter db, GpsObj gps, CheckInObj in, String dDate,
									   String dTime, String photo, String status, String notes) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String gpsID = saveGps(db, gps);
		String syncBatchID = getSyncBatchID(db);
		int battery = CodePanUtils.getBatteryLevel(db.getContext());
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("gpsID", gpsID));
		query.add(new FieldValue("photo", photo));
		query.add(new FieldValue("checkInID", in.ID));
		query.add(new FieldValue("batteryLevel", battery));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		String checkOutID = binder.insert(Tables.getName(TB.CHECK_OUT), query);
		if(checkOutID != null) {
			TaskObj task = in.task;
			query.clearAll();
			query.add(new FieldValue("isCheckOut", true));
			query.add(new FieldValue("status", status));
			if(task.notes != null) {
				if(notes != null && !notes.isEmpty()) {
					task.notes += " " + notes;
				}
			}
			else {
				task.notes = notes;
			}
			query.add(new FieldValue("notes", task.notes));
			binder.update(Tables.getName(TB.TASK), query, task.ID);
		}
		return binder.finish();
	}

	public static String saveExpense(SQLiteAdapter db, String dDate, String dTime, GpsObj gps) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String gpsID = saveGps(db, gps);
		String empID = getEmployeeID(db);
		String timeInID = getTimeInID(db);
		String syncBatchID = getSyncBatchID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("gpsID", gpsID));
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("timeInID", timeInID));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		String expenseID = binder.insert(Tables.getName(EXPENSE), query);
		if(binder.finish()) {
			return expenseID;
		}
		return "";
	}

	public static boolean updateExpense(SQLiteAdapter db, ExpenseObj obj) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("amount", obj.amount));
		query.add(new FieldValue("typeID", obj.typeID));
		query.add(new FieldValue("storeID", obj.storeID));
		query.add(new FieldValue("notes", obj.notes));
		query.add(new FieldValue("isUpdate", 1));
		query.add(new FieldValue("isSync", 0));
		query.add(new FieldValue("isWebUpdate", 0));
		binder.update(Tables.getName(EXPENSE), query, obj.ID);
		query = new SQLiteQuery();
		query.add(new Condition("expenseID", obj.ID));
		if(obj.typeID == 1) {
			query.add(new FieldValue("start", obj.fcStart));
			query.add(new FieldValue("end", obj.fcEnd));
			query.add(new FieldValue("rate", obj.fcRate));
			query.add(new FieldValue("startPhoto", obj.fcStartPhoto));
			query.add(new FieldValue("endPhoto", obj.fcEndPhoto));
			query.add(new FieldValue("isStartPhotoUpload", 0));
			query.add(new FieldValue("isEndPhotoUpload", 0));
			binder.update(Tables.getName(EXPENSE_FUEL_CONSUMPTION), query);
		}
		else if(obj.typeID == 2) {
			query.add(new FieldValue("start", obj.fpStart));
			query.add(new FieldValue("liters", obj.fpLiters));
			query.add(new FieldValue("price", obj.fpPrice));
			query.add(new FieldValue("photo", obj.fpPhoto));
			query.add(new FieldValue("startPhoto", obj.fpStartPhoto));
			query.add(new FieldValue("withOR", obj.fpWithOR));
			query.add(new FieldValue("isPhotoUpload", 0));
			query.add(new FieldValue("isStartPhotoUpload", 0));
			binder.update(Tables.getName(EXPENSE_FUEL_PURCHASE), query);
		}
		else {
			query.add(new FieldValue("start", obj.defStart));
			query.add(new FieldValue("end", obj.defEnd));
			query.add(new FieldValue("photo", obj.defPhoto));
			query.add(new FieldValue("withOR", obj.defWithOR));
			query.add(new FieldValue("isPhotoUpload", 0));
			binder.update(Tables.getName(EXPENSE_DEFAULT), query);
		}
		return binder.finish();
	}

	public static boolean deleteExpense(SQLiteAdapter db, String expenseID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isDelete", true));
		binder.update(Tables.getName(EXPENSE), query, expenseID);
		return binder.finish();
	}

	public static StoreObj getDefaultStore(SQLiteAdapter db) {
		StoreObj store = null;
		String table = Tables.getName(TB.STORES);
		String query = "SELECT ID, name, address, gpsLongitude, gpsLatitude, radius " +
				"FROM " + table + " WHERE isDefault = 1";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			store = new StoreObj();
			store.ID = cursor.getString(0);
			store.name = cursor.getString(1);
			store.address = cursor.getString(2);
			store.longitude = cursor.getDouble(3);
			store.latitude = cursor.getDouble(4);
			store.radius = cursor.getInt(5);
		}
		cursor.close();
		return store;
	}

	public static boolean setDefaultStore(SQLiteAdapter db, String storeID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String table = Tables.getName(TB.STORES);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isDefault", false));
		query.add(new Condition("isDefault", true));
		binder.update(table, query);
		query.clearAll();
		query.add(new FieldValue("isDefault", true));
		binder.update(table, query, storeID);
		return binder.finish();
	}

	public static AttendanceObj getAttendance(SQLiteAdapter db, String timeInID) {
		AttendanceObj attendance = new AttendanceObj();
		long totalBreak = getBreakDuration(db, timeInID);
		String i = Tables.getName(TB.TIME_IN);
		String o = Tables.getName(TB.TIME_OUT);
		String query = "SELECT i.dDate, i.dTime, i.isTimeOut, o.dDate, o.dTime, o.signature FROM " +
				i + " i LEFT JOIN " + o + " o ON o.timeInID = i.ID " +
				"WHERE i.ID = '" + timeInID + "'";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			TimeInObj in = new TimeInObj();
			in.ID = timeInID;
			in.dDate = cursor.getString(0);
			in.dTime = cursor.getString(1);
			in.isTimeOut = cursor.getInt(2) == 1;
			TimeOutObj out = new TimeOutObj();
			out.dDate = cursor.getString(3);
			out.dTime = cursor.getString(4);
			out.signature = cursor.getString(5);
			out.timeIn = in;
			attendance.out = out;
			attendance.totalBreak = totalBreak;
		}
		cursor.close();
		return attendance;
	}

	public static long getBreakDuration(SQLiteAdapter db, String timeInID) {
		long totalBreak = 0L;
		String i = Tables.getName(TB.BREAK_IN);
		String o = Tables.getName(TB.BREAK_OUT);
		String query = "SELECT i.dDate, i.dTime, o.dDate, o.dTime FROM " +
				i + " i LEFT JOIN " + o + " o ON o.breakInID = i.ID " +
				"WHERE i.timeInID = '" + timeInID + "'";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			String dateIn = cursor.getString(0);
			String timeIn = cursor.getString(1);
			String dateOut = cursor.getString(2);
			String timeOut = cursor.getString(3);
			long millisIn = CodePanUtils.dateTimeToMillis(dateIn, timeIn);
			long millisOut = CodePanUtils.dateTimeToMillis(dateOut, timeOut);
			long duration = millisOut - millisIn;
			totalBreak += duration;
		}
		cursor.close();
		return totalBreak;
	}

	public static String getSyncBatchID(SQLiteAdapter db, TB tb, String recID) {
		String table = Tables.getName(tb);
		String query = "SELECT syncBatchID FROM " + table + " WHERE ID = '" + recID + "'";
		return db.getString(query);
	}

	public static boolean updateStatusSync(SQLiteAdapter db, TB tb, String recID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String table = Tables.getName(tb);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isSync", true));
		binder.update(table, query, recID);
		return binder.finish();
	}

	public static boolean updateStatusUpload(SQLiteAdapter db, TB tb, String recID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String table = Tables.getName(tb);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isUpload", true));
		binder.update(table, query, recID);
		return binder.finish();
	}

	public static boolean updateStatusPhotoUpload(SQLiteAdapter db, TB tb, String recID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String table = Tables.getName(tb);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isPhotoUpload", true));
		binder.update(table, query, recID);
		return binder.finish();
	}

	public static boolean updateStatusSignatureUpload(SQLiteAdapter db, TB tb, String timeInID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String table = Tables.getName(tb);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isSignatureUpload", true));
		query.add(new Condition("timeInID", timeInID));
		binder.update(table, query);
		return binder.finish();
	}

	public static BreakObj getBreak(SQLiteAdapter db, String breakInID) {
		BreakObj obj = new BreakObj();
		String query = "SELECT b.ID, b.name, b.duration FROM " + Tables.getName(TB.BREAK) + " b, " +
				Tables.getName(TB.BREAK_IN) + " i WHERE i.ID = '" + breakInID + "' AND " +
				"b.ID = i.breakID";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			obj.ID = cursor.getString(0);
			obj.name = cursor.getString(1);
			obj.duration = cursor.getInt(2);
		}
		cursor.close();
		return obj;
	}

	public static String getLastSynced(SQLiteAdapter db) {
		String lastSynced = null;
		String table = Tables.getName(TB.SYNC_BATCH);
		String query = "SELECT dDate, dTime FROM " + table + " WHERE ID = 1";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			String dDate = cursor.getString(0);
			String dTime = cursor.getString(1);
			if(dDate != null && dTime != null) {
				String date = CodePanUtils.getCalendarDate(dDate, true, false).toUpperCase();
				String time = CodePanUtils.getNormalTime(dTime, false);
				lastSynced = date + ", " + time;
			}
		}
		cursor.close();
		return lastSynced;
	}

	public static boolean isBreakDone(SQLiteAdapter db, String breakID) {
		String timeInID = getTimeInID(db);
		String empID = getEmployeeID(db);
		String table = Tables.getName(TB.BREAK_IN);
		String query = "SELECT ID FROM " + table + " WHERE breakID = '" + breakID + "' " +
				"AND timeInID = '" + timeInID + "' AND empID = '" + empID + "'";
		return db.isRecordExists(query);
	}

	public static boolean isBreakIn(SQLiteAdapter db) {
		String timeInID = getTimeInID(db);
		String empID = getEmployeeID(db);
		String table = Tables.getName(TB.BREAK_IN);
		String query = "SELECT ID FROM " + table + " WHERE empID = '" + empID + "' " +
				"AND timeInID = '" + timeInID + "' AND isBreakOut = 0";
		return db.isRecordExists(query);
	}

	public static boolean alternateIncident(SQLiteAdapter db, GpsObj gps, boolean isEnabled,
											int on, int off) {
		String timeInID = getTimeInID(db);
		int lastOn = getLastIncidentReportID(db, timeInID, on);
		int lastOff = getLastIncidentReportID(db, timeInID, off);
		if(isEnabled) {
			return lastOff < lastOn || saveIncident(db, gps, on);
		}
		else {
			return lastOn <= lastOff || saveIncident(db, gps, off);
		}
	}

	public static int getLastIncidentReportID(SQLiteAdapter db, String timeInID, int incidentID) {
		String empID = getEmployeeID(db);
		String table = Tables.getName(TB.INCIDENT_REPORT);
		String query = "SELECT ID FROM " + table + " WHERE incidentID = " + incidentID + " " +
				"AND timeInID = '" + timeInID + "' AND empID = '" + empID + "' " +
				"ORDER BY ID DESC LIMIT 1";
		return db.getInt(query);
	}

	public static BreakInObj getBreakIn(SQLiteAdapter db) {
		BreakInObj in = new BreakInObj();
		String timeInID = getTimeInID(db);
		String empID = getEmployeeID(db);
		String table = Tables.getName(TB.BREAK_IN);
		String query = "SELECT ID, dDate, dTime FROM " + table + " WHERE timeInID = '" +
				timeInID + "' AND empID = '" + empID + "' AND isBreakOut = 0";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			in.ID = cursor.getString(0);
			in.dDate = cursor.getString(1);
			in.dTime = cursor.getString(2);
		}
		cursor.close();
		return in;
	}

	public static boolean updateSignature(SQLiteAdapter db, TimeOutObj out) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("signature", out.signature));
		query.add(new FieldValue("isSignatureUpload", false));
		binder.update(Tables.getName(TB.TIME_OUT), query, out.ID);
		return binder.finish();
	}

	public static boolean updateLastSynced(SQLiteAdapter db) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String dDate = CodePanUtils.getDate();
		String dTime = CodePanUtils.getTime();
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		binder.update(Tables.getName(TB.SYNC_BATCH), query, 1);
		return binder.finish();
	}

	public static int getCountSync(SQLiteAdapter db) {
		int count = 0;
		ArrayList<TB> tableList = new ArrayList<>();
		tableList.add(TB.TIME_IN);
		tableList.add(TB.TIME_OUT);
		tableList.add(TB.BREAK_IN);
		tableList.add(TB.BREAK_OUT);
		tableList.add(TB.INCIDENT_REPORT);
		tableList.add(TB.ENTRIES);
		SQLiteQuery query = new SQLiteQuery();
		for(TB tb : tableList) {
			query.clearAll();
			switch(tb) {
				case ENTRIES:
					query.add(new Condition("isDelete", false));
					query.add(new Condition("isSubmit", true));
					query.add(new Condition("isSync", false));
					break;
				default:
					query.add(new Condition("isSync", false));
					break;
			}
			String table = Tables.getName(tb);
			String sql = "SELECT COUNT(ID) FROM " + table + " WHERE " + query.getConditions();
			count += db.getInt(sql);
		}
		return count;
	}

	public static int getCountPhotoUpload(SQLiteAdapter db) {
		int count = 0;
		ArrayList<TB> tableList = new ArrayList<>();
		tableList.add(TB.TIME_IN);
		tableList.add(TB.TIME_OUT);
		tableList.add(TB.PHOTO);
		SQLiteQuery query = new SQLiteQuery();
		for(TB tb : tableList) {
			query.clearAll();
			switch(tb) {
				case PHOTO:
					query.add(new Condition("isUpload", false));
					query.add(new Condition("isSignature", false));
					query.add(new Condition("isDelete", false));
					query.add(new Condition("fileName", Operator.NOT_NULL));
					break;
				default:
					query.add(new Condition("isPhotoUpload", false));
					query.add(new Condition("photo", Operator.NOT_NULL));
					break;
			}
			String table = Tables.getName(tb);
			String sql = "SELECT COUNT(ID) FROM " + table + " WHERE " + query.getConditions();
			count += db.getInt(sql);
		}
		return count;
	}

	public static int getCountSignatureUpload(SQLiteAdapter db) {
		int count = 0;
		ArrayList<TB> tableList = new ArrayList<>();
		tableList.add(TB.PHOTO);
		tableList.add(TB.TIME_OUT);
		SQLiteQuery query = new SQLiteQuery();
		for(TB tb : tableList) {
			query.clearAll();
			switch(tb) {
				case PHOTO:
					query.add(new Condition("isUpload", false));
					query.add(new Condition("isSignature", true));
					query.add(new Condition("isDelete", false));
					query.add(new Condition("fileName", Operator.NOT_NULL));
					break;
				case TIME_OUT:
					query.add(new Condition("isSignatureUpload", false));
					query.add(new Condition("signature", Operator.NOT_NULL));
					break;
			}
			String table = Tables.getName(tb);
			String sql = "SELECT COUNT(ID) FROM " + table + " WHERE " + query.getConditions();
			count += db.getInt(sql);
		}
		return count;
	}

	public static int getCountSyncTotal(SQLiteAdapter db) {
		int syncCount = getCountSync(db);
		int photoCount = getCountPhotoUpload(db);
		int signatureCount = getCountSignatureUpload(db);
		return syncCount + photoCount + signatureCount;
	}

	public static String addEntry(SQLiteAdapter db, String formID, ArrayList<FieldObj> fieldList,
								   boolean isSubmit) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String dDate = CodePanUtils.getDate();
		String dTime = CodePanUtils.getTime();
		String empID = getEmployeeID(db);
		String syncBatchID = getSyncBatchID(db);
		String timeInID = getTimeInID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("formID", formID));
		query.add(new FieldValue("isSubmit", isSubmit));
		query.add(new FieldValue("timeInID", timeInID));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		if(isSubmit) {
			query.add(new FieldValue("dateSubmitted", dDate));
			query.add(new FieldValue("timeSubmitted", dTime));
		}
		String entryID = binder.insert(Tables.getName(ENTRIES), query);
		for(FieldObj field : fieldList) {
			if(field.isQuestion) {
				AnswerObj answer = field.answer;
				query.clearAll();
				query.add(new FieldValue("entryID", entryID));
				query.add(new FieldValue("fieldID", field.ID));
				query.add(new FieldValue("value", answer.value));
				query.add(new FieldValue("isUpdate", true));
				binder.insert(Tables.getName(ANSWERS), query);
			}
		}
		binder.finish();
		return entryID;
	}

	public static boolean editEntry(SQLiteAdapter db, String entryID, ArrayList<FieldObj> fieldList, boolean isSubmit) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isSubmit", isSubmit));
		if(isSubmit) {
			String date = CodePanUtils.getDate();
			String time = CodePanUtils.getTime();
			query.add(new FieldValue("dateSubmitted", date));
			query.add(new FieldValue("timeSubmitted", time));
		}
		binder.update(Tables.getName(ENTRIES), query, entryID);
		for(FieldObj field : fieldList) {
			if(field.isQuestion) {
				AnswerObj answer = field.answer;
				query.clearAll();
				query.add(new FieldValue("value", answer.value));
				String table = Tables.getName(TB.ANSWERS);
				String sql = "SELECT ID FROM " + table + " WHERE fieldID = '" + field.ID + "' " +
						"AND entryID = '" + entryID + "'";
				if(db.isRecordExists(sql)) {
					String answerID = db.getString(sql);
					binder.update(table, query, answerID);
				}
				else {
					query.add(new FieldValue("entryID", entryID));
					query.add(new FieldValue("fieldID", field.ID));
					binder.insert(table, query);
				}
			}
		}
		return binder.finish();
	}

	public static boolean hasUnfilledUpFields(SQLiteAdapter db, String entryID) {
		boolean result = false;
		String e = Tables.getName(TB.ENTRIES);
		String f = Tables.getName(TB.FORMS);
		String q = Tables.getName(TB.FIELDS);
		String a = Tables.getName(TB.ANSWERS);
		String query = "SELECT a.value FROM " + e + " e, " + f + " f, " + q + " q LEFT JOIN " + a + " a " +
				"ON a.entryID = e.ID AND a.fieldID = q.ID WHERE e.ID = '" + entryID + "' AND f.ID = e.formID " +
				"AND q.formID = f.ID AND q.isRequired = 1 and q.isActive = 1";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			String value = cursor.getString(0);
			result = value == null || value.isEmpty();
		}
		cursor.close();
		return result;
	}

	public static boolean hasUnsubmittedEntries(SQLiteAdapter db) {
		String timeInID = getTimeInID(db);
		String empID = getEmployeeID(db);
		String table = Tables.getName(TB.ENTRIES);
		String query = "SELECT ID FROM " + table + " WHERE timeInID = '" + timeInID + "' " +
				"AND empID = '" + empID + "' AND isSubmit = 0 AND isDelete = 0";
		return db.isRecordExists(query);
	}

	public static boolean submitEntry(SQLiteAdapter db, String entryID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		String date = CodePanUtils.getDate();
		String time = CodePanUtils.getTime();
		query.add(new FieldValue("isSubmit", true));
		query.add(new FieldValue("dateSubmitted", date));
		query.add(new FieldValue("timeSubmitted", time));
		binder.update(Tables.getName(ENTRIES), query, entryID);
		return binder.finish();
	}

	public static boolean deleteEntry(SQLiteAdapter db, String entryID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isDelete", true));
		binder.update(Tables.getName(ENTRIES), query, entryID);
		return binder.finish();
	}

	public static String getFileName(SQLiteAdapter db, String photoID) {
		String table = Tables.getName(TB.PHOTO);
		String query = "SELECT fileName FROM " + table + " WHERE ID = '" + photoID + "' AND isDelete = 0";
		return db.getString(query);
	}

	public static String savePhoto(SQLiteAdapter db, ImageObj obj) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String syncBatchID = getSyncBatchID(db);
		String empID = getEmployeeID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("dDate", obj.dDate));
		query.add(new FieldValue("dTime", obj.dTime));
		query.add(new FieldValue("fileName", obj.fileName));
		query.add(new FieldValue("isSignature", obj.isSignature));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		String photoID = binder.insert(Tables.getName(PHOTO), query);
		binder.finish();
		return photoID;
	}

	public static boolean deletePhoto(Context context, SQLiteAdapter db, String photoID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isDelete", true));
		binder.update(Tables.getName(PHOTO), query, photoID);
		String fileName = TarkieLib.getFileName(db, photoID);
		String path = context.getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + fileName;
		CodePanUtils.deleteFile(path);
		return binder.finish();
	}

	public static boolean deletePhoto(Context context, SQLiteAdapter db, ImageObj obj) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isDelete", true));
		binder.update(Tables.getName(PHOTO), query, obj.ID);
		String path = context.getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + obj.fileName;
		CodePanUtils.deleteFile(path);
		return binder.finish();
	}

	public static boolean deletePhotos(Context context, SQLiteAdapter db, ArrayList<ImageObj> deleteList) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		for(ImageObj obj : deleteList) {
			query.clearAll();
			query.add(new FieldValue("isDelete", true));
			binder.update(Tables.getName(PHOTO), query, obj.ID);
			String path = context.getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + obj.fileName;
			CodePanUtils.deleteFile(path);
		}
		return binder.finish();
	}

	public static FieldObj getUnfilledUpField(ArrayList<FieldObj> fieldList) {
		for(FieldObj field : fieldList) {
			if(field.isRequired && field.isQuestion) {
				AnswerObj answer = field.answer;
				switch(field.type) {
					case FieldType.MS:
						if(!hasSelected(answer.choiceList)) {
							return field;
						}
						break;
					case FieldType.YON:
						if(!answer.isActive) {
							return field;
						}
						break;
					case FieldType.PHOTO:
						if(answer.imageList != null) {
							if(answer.imageList.isEmpty()) {
								return field;
							}
						}
						else {
							return field;
						}
						break;
					default:
						if(answer.value != null) {
							if(answer.value.isEmpty()) {
								return field;
							}
						}
						else {
							return field;
						}
						break;
				}
			}
		}
		return null;
	}

	public static boolean hasSelected(ArrayList<ChoiceObj> choiceList) {
		for(ChoiceObj obj : choiceList) {
			if(obj.isCheck) {
				return true;
			}
		}
		return false;
	}

	public static String getBackupFileName(SQLiteAdapter db) {
		String empID = getEmployeeID(db);
		String companyName = TarkieLib.getCompanyName(db);
		String employeeName = TarkieLib.getEmployeeName(db, empID);
		String version = CodePanUtils.getVersionName(db.getContext());
		String date = CodePanUtils.getDate();
		String time = CodePanUtils.getTime();
		String fileName = companyName + "_" + employeeName + "_" + date + "_" + time + "_" + version + ".zip";
		fileName = fileName.replace(" ", "_");
		fileName = fileName.replace(":", "-");
		return fileName;
	}

	public static String getWebPhotoIDs(SQLiteAdapter db, String value) {
		String webPhotoIDs = "";
		String table = Tables.getName(TB.PHOTO);
		if(value != null && !value.isEmpty()) {
			String condition = "ID = " + value.replace(",", " OR ID = ");
			String query = "SELECT webPhotoID FROM " + table + " WHERE (" + condition + ") " +
					"AND isDelete = 0 AND isUpload = 1";
			Cursor cursor = db.read(query);
			while(cursor.moveToNext()) {
				if(cursor.getPosition() != cursor.getCount() - 1) {
					webPhotoIDs += cursor.getString(0) + ",";
				}
				else {
					webPhotoIDs += cursor.getString(0);
				}
			}
			cursor.close();
		}
		return webPhotoIDs;
	}

	public static ArrayList<VisitsDateObj> getVisitsDate(String date, int noOfDays) {
		ArrayList<VisitsDateObj> visitsDateList = new ArrayList<>();
		for(int i = 0; i < noOfDays; i++) {
			VisitsDateObj obj = new VisitsDateObj();
			obj.date = CodePanUtils.getDateAfter(date, i);
			obj.day = CodePanUtils.getDay(obj.date);
			visitsDateList.add(obj);
		}
		return visitsDateList;
	}

	public static boolean isSettingsEnabled(SQLiteAdapter db, String code) {
		String groupID = getGroupID(db);
		String s = Tables.getName(TB.SETTINGS);
		String sg = Tables.getName(TB.SETTINGS_GROUP);
		String query = "SELECT sg.value FROM " + s + " s, " + sg + " sg WHERE " +
				"sg.groupID = '" + groupID + "' AND sg.settingsID = s.ID " +
				"AND s.code = '" + code + "'";
		return db.getInt(query) == 1;
	}

	public static void requiredField(CodePanLabel label, String text) {
		if(text != null) {
			int length = text.length();
			String name = text + "*";
			ArrayList<SpannableMap> list = new ArrayList<>();
			list.add(new SpannableMap(length, length + 1, Color.RED));
			SpannableStringBuilder ssb = CodePanUtils.customizeText(list, name);
			label.setText(ssb);
		}
	}

	public static String getUserID(SQLiteAdapter db) {
		return db.getString("SELECT empID FROM " + Tables.getName(Tables.TB.CREDENTIALS));
	}

	public static boolean saveNewContact(SQLiteAdapter db, ContactObj contact) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("storeID", contact.storeID));
		query.add(new FieldValue("userID", getUserID(db)));
		query.add(new FieldValue("name", contact.name));
		query.add(new FieldValue("position", contact.position));
		query.add(new FieldValue("cellNo", contact.cellNo));
		query.add(new FieldValue("phoneNo", contact.phoneNo));
		query.add(new FieldValue("email", contact.email));
		query.add(new FieldValue("birthday", contact.birthday));
		query.add(new FieldValue("remarks", contact.remarks));
		binder.insert(Tables.getName(TB.CONTACTS), query);
		return binder.finish();
	}

	public static boolean addStore(SQLiteAdapter db, StoreObj obj) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		String dDate = CodePanUtils.getDate();
		String dTime = CodePanUtils.getTime();
		String syncBatchID = getSyncBatchID(db);
		query.add(new FieldValue("name", obj.name));
		query.add(new FieldValue("address", obj.address));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		binder.insert(Tables.getName(TB.STORES), query);
		return binder.finish();
	}
}

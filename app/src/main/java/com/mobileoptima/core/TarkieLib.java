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
import com.mobileoptima.constant.ExpenseType;
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
import com.mobileoptima.model.ExpenseDefaultObj;
import com.mobileoptima.model.ExpenseFuelConsumptionObj;
import com.mobileoptima.model.ExpenseFuelPurchaseObj;
import com.mobileoptima.model.ExpenseObj;
import com.mobileoptima.model.ExpenseTypeObj;
import com.mobileoptima.model.FieldObj;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.PhotoObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;
import com.mobileoptima.model.VisitObj;
import com.mobileoptima.model.VisitsDateObj;
import com.mobileoptima.schema.Tables;
import com.mobileoptima.schema.Tables.TB;
import com.mobileoptima.tarkieattendance.AlertDialogFragment;
import com.mobileoptima.tarkieattendance.R;

import net.sqlcipher.Cursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		SQLiteBinder binder = new SQLiteBinder(db);
		List<TB> tableList = Arrays.asList(TB.values());
		for(TB tb : tableList) {
			String table = Tables.getName(tb);
			binder.createTable(table, Tables.create(tb));
		}
		binder.finish();
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
		column = "designation";
		table = Tables.getName(TB.CONTACTS);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.TEXT, column);
		}
		column = "isFromWeb";
		table = Tables.getName(TB.TASK_FORM);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, column, 0);
		}
		column = "name";
		table = Tables.getName(TB.EXPENSE);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.TEXT, column);
		}
		column = "origin";
		table = Tables.getName(TB.EXPENSE);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.TEXT, column);
		}
		column = "destination";
		table = Tables.getName(TB.EXPENSE);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.TEXT, column);
		}
		column = "taskFormID";
		table = Tables.getName(TB.TASK_ENTRY);
		if(!db.isColumnExists(table, column)) {
			binder.addColumn(table, DataType.INTEGER, column);
		}
		switch(o) {
			case 1:
				TB tb = TB.STORES;
				table = Tables.getName(tb);
				binder.dropTable(table);
				binder.createTable(table, Tables.create(tb));
				break;
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
		if(convention == null || convention.equalsIgnoreCase(Convention.DEFAULT)) {
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

	public static boolean addTasks(SQLiteAdapter db, ArrayList<TaskObj> taskList) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String syncBatchID = getSyncBatchID(db);
		String table = Tables.getName(TB.TASK);
		String empID = getEmployeeID(db);
		String dDate = CodePanUtils.getDate();
		String dTime = CodePanUtils.getTime();
		SQLiteQuery query = new SQLiteQuery();
		for(TaskObj task : taskList) {
			StoreObj store = task.store;
			query.clearAll();
			query.add(new FieldValue("empID", empID));
			query.add(new FieldValue("name", store.name));
			query.add(new FieldValue("storeID", store.ID));
			query.add(new FieldValue("dateCreated", dDate));
			query.add(new FieldValue("timeCreated", dTime));
			query.add(new FieldValue("startDate", task.startDate));
			query.add(new FieldValue("endDate", task.endDate));
			query.add(new FieldValue("syncBatchID", syncBatchID));
			binder.insert(table, query);
		}
		return binder.finish();
	}

	public static TaskObj addTask(SQLiteAdapter db, StoreObj store) {
		TaskObj task = new VisitObj();
		SQLiteBinder binder = new SQLiteBinder(db);
		String empID = getEmployeeID(db);
		String dDate = CodePanUtils.getDate();
		String dTime = CodePanUtils.getTime();
		String syncBatchID = getSyncBatchID(db);
		String table = Tables.getName(TB.TASK);
		SQLiteQuery query = new SQLiteQuery();
		if(store != null) {
			query.add(new FieldValue("storeID", store.ID));
			query.add(new FieldValue("name", store.name));
		}
		else {
			String sql = "SELECT COUNT(ID) FROM " + table + " WHERE isFromWeb = 0 AND " +
					"'" + dDate + "' BETWEEN startDate AND endDate AND empID = '" + empID + "'";
			int count = db.getInt(sql) + 1;
			task.name = "New Visit " + count;
		}
		query.add(new FieldValue("name", task.name));
		query.add(new FieldValue("dateCreated", dDate));
		query.add(new FieldValue("timeCreated", dTime));
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("startDate", dDate));
		query.add(new FieldValue("endDate", dDate));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		task.ID = binder.insert(table, query);
		binder.finish();
		return task;
	}

	public static boolean editTask(SQLiteAdapter db, StoreObj store, String taskID, String notes,
								   ArrayList<EntryObj> entryList, ArrayList<PhotoObj> photoList) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String t = Tables.getName(TB.TASK);
		String tf = Tables.getName(TB.TASK_FORM);
		String te = Tables.getName(TB.TASK_ENTRY);
		String tp = Tables.getName(TB.TASK_PHOTO);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("notes", notes));
		query.add(new FieldValue("isUpdate", true));
		query.add(new FieldValue("isWebUpdate", false));
		query.add(new FieldValue("name", store.name));
		query.add(new FieldValue("storeID", store.ID));
		binder.update(t, query, taskID);
		query.clearAll();
		query.add(new FieldValue("isTag", false));
		query.add(new Condition("taskID", taskID));
		binder.update(tf, query);
		binder.update(tp, query);
		for(EntryObj entry : entryList) {
			String taskFormID = null;
			FormObj form = entry.form;
			if(form.isChecked) {
				query.clearAll();
				query.add(new Field("ID"));
				query.add(new Condition("formID", form.ID));
				query.add(new Condition("taskID", taskID));
				query.add(new FieldValue("formID", form.ID));
				query.add(new FieldValue("taskID", taskID));
				query.add(new FieldValue("isTag", true));
				String sql = query.select(tf);
				if(db.isRecordExists(sql)) {
					taskFormID = db.getString(sql);
					binder.update(tf, query);
				}
				else {
					taskFormID = binder.insert(tf, query);
				}
			}
			if(entry.ID != null) {
				query.clearAll();
				query.add(new Field("ID"));
				query.add(new Condition("entryID", entry.ID));
				query.add(new Condition("taskFormID", taskFormID));
				query.add(new FieldValue("entryID", entry.ID));
				query.add(new FieldValue("taskFormID", taskFormID));
				String sql = query.select(te);
				if(!db.isRecordExists(sql)) {
					binder.insert(te, query);
				}
			}
		}
		for(PhotoObj photo : photoList) {
			query.clearAll();
			query.add(new Field("ID"));
			query.add(new Condition("photoID", photo.ID));
			query.add(new Condition("taskID", taskID));
			query.add(new FieldValue("photoID", photo.ID));
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

	public static String getExpenseTitle(SQLiteAdapter db) {
		String table = Tables.getName(EXPENSE);
		String timeInID = getTimeInID(db);
		String sql = "SELECT COUNT(ID) FROM " + table + " WHERE timeInID = " + timeInID;
		int count = db.getInt(sql) + 1;
		return "Expense " + count;
	}

	public static String saveExpense(SQLiteAdapter db, GpsObj gps, String dDate, String dTime) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String gpsID = saveGps(db, gps);
		String empID = getEmployeeID(db);
		String timeInID = getTimeInID(db);
		String name = getExpenseTitle(db);
		String syncBatchID = getSyncBatchID(db);
		String table = Tables.getName(EXPENSE);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("name", name));
		query.add(new FieldValue("gpsID", gpsID));
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("timeInID", timeInID));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		String expenseID = binder.insert(table, query);
		binder.finish();
		return expenseID;
	}

	public static boolean updateExpense(SQLiteAdapter db, ExpenseObj expense) {
		String table = Tables.getName(EXPENSE);
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("amount", expense.amount));
		query.add(new FieldValue("origin", expense.origin));
		query.add(new FieldValue("destination", expense.destination));
		query.add(new FieldValue("notes", expense.notes));
		if(expense.type.ID != null) {
			query.add(new FieldValue("typeID", expense.type.ID));
			query.add(new FieldValue("name", expense.type.name));
		}
		query.add(new FieldValue("storeID", expense.store.ID));
		String sql = "SELECT isSync FROM " + table + " WHERE ID = " + expense.ID;
		if(db.getInt(sql) == 1) {
			query.add(new FieldValue("isUpdate", true));
			query.add(new FieldValue("isWebUpdate", false));
		}
		binder.update(table, query, expense.ID);
		if(expense instanceof ExpenseFuelConsumptionObj) {
			ExpenseFuelConsumptionObj fc = (ExpenseFuelConsumptionObj) expense;
			table = Tables.getName(EXPENSE_FUEL_CONSUMPTION);
			query = new SQLiteQuery();
			query.add(new FieldValue("expenseID", fc.ID));
			query.add(new FieldValue("start", fc.start));
			query.add(new FieldValue("end", fc.end));
			query.add(new FieldValue("rate", fc.rate));
			query.add(new FieldValue("startPhoto", fc.startPhoto));
			query.add(new FieldValue("endPhoto", fc.endPhoto));
			query.add(new FieldValue("isStartPhotoUpload", false));
			query.add(new FieldValue("isEndPhotoUpload", false));
			sql = "SELECT ID FROM " + table + " WHERE expenseID = " + fc.ID;
			if(db.isRecordExists(sql)) {
				query.add(new Condition("expenseID", fc.ID));
				binder.update(table, query);
			}
			else {
				binder.insert(table, query);
			}
		}
		else if(expense instanceof ExpenseFuelPurchaseObj) {
			ExpenseFuelPurchaseObj fp = (ExpenseFuelPurchaseObj) expense;
			table = Tables.getName(EXPENSE_FUEL_PURCHASE);
			query = new SQLiteQuery();
			query.add(new FieldValue("expenseID", fp.ID));
			query.add(new FieldValue("start", fp.start));
			query.add(new FieldValue("liters", fp.liters));
			query.add(new FieldValue("price", fp.price));
			query.add(new FieldValue("photo", fp.photo));
			query.add(new FieldValue("startPhoto", fp.startPhoto));
			query.add(new FieldValue("withOR", fp.withOR));
			query.add(new FieldValue("isPhotoUpload", true));
			query.add(new FieldValue("isStartPhotoUpload", true));
			sql = "SELECT ID FROM " + table + " WHERE expenseID = " + fp.ID;
			if(db.isRecordExists(sql)) {
				query.add(new Condition("expenseID", fp.ID));
				binder.update(table, query);
			}
			else {
				binder.insert(table, query);
			}
		}
		else {
			ExpenseDefaultObj d = (ExpenseDefaultObj) expense;
			table = Tables.getName(EXPENSE_DEFAULT);
			query = new SQLiteQuery();
			query.add(new FieldValue("expenseID", d.ID));
			query.add(new FieldValue("photo", d.photo));
			query.add(new FieldValue("withOR", d.withOR));
			query.add(new FieldValue("isPhotoUpload", false));
			sql = "SELECT ID FROM " + table + " WHERE expenseID = " + d.ID;
			if(db.isRecordExists(sql)) {
				query.add(new Condition("expenseID", d.ID));
				binder.update(table, query);
			}
			else {
				binder.insert(table, query);
			}
		}
		return binder.finish();
	}

	public static ExpenseObj getExpense(SQLiteAdapter db, String expenseID) {
		ExpenseObj expense = new ExpenseObj();
		String table = Tables.getName(TB.EXPENSE);
		String query = "SELECT dDate, dTime, amount, typeID, name, storeID, origin, destination, " +
				"notes, isTag, isSubmit FROM " + table + " WHERE ID = " + expenseID;
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			int typeID = cursor.getInt(3);
			Cursor c;
			switch(typeID) {
				case ExpenseType.FUEL_CONSUMPTION:
					ExpenseFuelConsumptionObj fc = new ExpenseFuelConsumptionObj();
					table = Tables.getName(TB.EXPENSE_FUEL_CONSUMPTION);
					query = "SELECT start, end, rate, startPhoto, endPhoto FROM " + table + " " +
							"WHERE expenseID = " + expenseID;
					c = db.read(query);
					while(c.moveToNext()) {
						fc.start = c.getString(0);
						fc.end = c.getString(1);
						fc.rate = c.getFloat(2);
						fc.startPhoto = c.getString(3);
						fc.endPhoto = c.getString(4);
					}
					c.close();
					expense = fc;
					break;
				case ExpenseType.FUEL_PURCHASE:
					ExpenseFuelPurchaseObj fp = new ExpenseFuelPurchaseObj();
					table = Tables.getName(TB.EXPENSE_FUEL_PURCHASE);
					query = "SELECT start, liters, price, photo, startPhoto, withOR FROM " + table + " " +
							"WHERE expenseID = " + expenseID;
					c = db.read(query);
					while(c.moveToNext()) {
						fp.start = c.getString(0);
						fp.liters = c.getString(1);
						fp.price = c.getString(2);
						fp.photo = c.getString(3);
						fp.startPhoto = c.getString(4);
						fp.withOR = c.getInt(5) == 1;
					}
					c.close();
					expense = fp;
					break;
				default:
					ExpenseDefaultObj d = new ExpenseDefaultObj();
					table = Tables.getName(TB.EXPENSE_DEFAULT);
					query = "SELECT photo, withOR FROM " + table + " WHERE expenseID = " + expenseID;
					c = db.read(query);
					while(c.moveToNext()) {
						d.photo = c.getString(0);
						d.withOR = c.getInt(1) == 1;
					}
					c.close();
					expense = d;
					break;
			}
			expense.ID = expenseID;
			expense.dDate = cursor.getString(0);
			expense.dTime = cursor.getString(1);
			expense.amount = cursor.getFloat(2);
			ExpenseTypeObj type = new ExpenseTypeObj();
			type.ID = cursor.getString(3);
			type.name = cursor.getString(4);
			expense.type = type;
			StoreObj store = new StoreObj();
			store.ID = cursor.getString(5);
			expense.store = store;
			expense.origin = cursor.getString(6);
			expense.destination = cursor.getString(7);
			expense.notes = cursor.getString(8);
			expense.isTag = cursor.getInt(9) == 1;
			expense.isSubmit = cursor.getInt(10) == 1;
		}
		cursor.close();
		return expense;
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

	public static boolean updateStatusWebUpdate(SQLiteAdapter db, TB tb, String recID) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String table = Tables.getName(tb);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isWebUpdate", true));
		binder.update(table, query, recID);
		return binder.finish();
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
		tableList.add(TB.TASK);
		tableList.add(TB.TIME_IN);
		tableList.add(TB.TIME_OUT);
		tableList.add(TB.BREAK_IN);
		tableList.add(TB.BREAK_OUT);
		tableList.add(TB.CHECK_IN);
		tableList.add(TB.CHECK_OUT);
		tableList.add(TB.INCIDENT_REPORT);
		tableList.add(TB.ENTRIES);
		//tableList.add(TB.EXPENSE);
		SQLiteQuery query = new SQLiteQuery();
		for(TB tb : tableList) {
			query.clearAll();
			query.add(new Condition("isSync", false));
			switch(tb) {
				case ENTRIES:
					query.add(new Condition("isDelete", false));
					query.add(new Condition("isSubmit", true));
					break;
				case EXPENSE:
					query.add(new Condition("isDelete", false));
					break;
			}
			String table = Tables.getName(tb);
			String sql = "SELECT COUNT(ID) FROM " + table + " WHERE " +
					query.getConditions();
			count += db.getInt(sql);
		}
		return count;
	}

	public static int getCountPhotoUpload(SQLiteAdapter db) {
		int count = 0;
		ArrayList<TB> tableList = new ArrayList<>();
		tableList.add(TB.TIME_IN);
		tableList.add(TB.TIME_OUT);
		tableList.add(TB.CHECK_IN);
		tableList.add(TB.CHECK_OUT);
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
				case CHECK_IN:
				case CHECK_OUT:
					query.add(new Condition("isUpload", false));
					query.add(new Condition("photo", Operator.NOT_NULL));
					break;
				case TIME_IN:
				case TIME_OUT:
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

	public static int getCountWebUpdate(SQLiteAdapter db) {
		int count = 0;
		ArrayList<TB> tableList = new ArrayList<>();
		tableList.add(TB.TASK);
		//tableList.add(TB.EXPENSE);
		SQLiteQuery query = new SQLiteQuery();
		for(TB tb : tableList) {
			query.clearAll();
			query.add(new Condition("isSync", true));
			query.add(new Condition("isUpdate", true));
			query.add(new Condition("isWebUpdate", false));
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
		int updateCount = getCountWebUpdate(db);
		int photoCount = getCountPhotoUpload(db);
		int signatureCount = getCountSignatureUpload(db);
		return syncCount + updateCount + photoCount + signatureCount;
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

	public static String savePhoto(SQLiteAdapter db, PhotoObj photo) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String syncBatchID = getSyncBatchID(db);
		String empID = getEmployeeID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("dDate", photo.dDate));
		query.add(new FieldValue("dTime", photo.dTime));
		query.add(new FieldValue("fileName", photo.fileName));
		query.add(new FieldValue("isSignature", photo.isSignature));
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

	public static boolean deletePhoto(Context context, SQLiteAdapter db, PhotoObj obj) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("isDelete", true));
		binder.update(Tables.getName(PHOTO), query, obj.ID);
		String path = context.getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() + "/" + obj.fileName;
		CodePanUtils.deleteFile(path);
		return binder.finish();
	}

	public static boolean deletePhotos(Context context, SQLiteAdapter db, ArrayList<PhotoObj> deleteList) {
		SQLiteBinder binder = new SQLiteBinder(db);
		SQLiteQuery query = new SQLiteQuery();
		for(PhotoObj obj : deleteList) {
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
						if(answer.photoList != null) {
							if(answer.photoList.isEmpty()) {
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

	public static boolean addContact(SQLiteAdapter db, ContactObj contact) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String empID = getEmployeeID(db);
		SQLiteQuery query = new SQLiteQuery();
		StoreObj store = contact.store;
		query.add(new FieldValue("storeID", store.ID));
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("name", contact.name));
		query.add(new FieldValue("designation", contact.designation));
		query.add(new FieldValue("mobile", contact.mobile));
		query.add(new FieldValue("landline", contact.landline));
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
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("name", obj.name));
		query.add(new FieldValue("address", obj.address));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		binder.insert(Tables.getName(TB.STORES), query);
		return binder.finish();
	}

	public static String getStoreName(SQLiteAdapter db, String ID) {
		String table = Tables.getName(TB.STORES);
		String query = "SELECT name FROM " + table + " WHERE ID = " + ID;
		return db.getString(query);
	}
}

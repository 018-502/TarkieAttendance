package com.mobileoptima.core;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.codepan.database.Condition;
import com.codepan.database.Condition.Operator;
import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.codepan.database.SQLiteQuery;
import com.codepan.model.GpsObj;
import com.codepan.model.TimeObj;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.constant.AnswerType;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.FieldType;
import com.mobileoptima.constant.Incident;
import com.mobileoptima.model.AnswerObj;
import com.mobileoptima.model.AttendanceObj;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreakObj;
import com.mobileoptima.model.ChoiceObj;
import com.mobileoptima.model.EntryObj;
import com.mobileoptima.model.FieldObj;
import com.mobileoptima.model.ImageObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;
import com.mobileoptima.schema.Tables;
import com.mobileoptima.schema.Tables.TB;
import com.mobileoptima.tarkieattendance.AlertDialogFragment;
import com.mobileoptima.tarkieattendance.R;

import net.sqlcipher.Cursor;

import java.util.ArrayList;

import static com.codepan.database.SQLiteQuery.DataType;
import static com.mobileoptima.schema.Tables.TB.ANSWERS;
import static com.mobileoptima.schema.Tables.TB.COMPANY;
import static com.mobileoptima.schema.Tables.TB.CREDENTIALS;
import static com.mobileoptima.schema.Tables.TB.EMPLOYEE;
import static com.mobileoptima.schema.Tables.TB.ENTRIES;
import static com.mobileoptima.schema.Tables.TB.FIELDS;
import static com.mobileoptima.schema.Tables.TB.PHOTO;
import static com.mobileoptima.schema.Tables.TB.SYNC_BATCH;

public class TarkieLib {

	public static void alertDialog(final FragmentActivity activity, String title, String message) {
		final FragmentManager manager = activity.getSupportFragmentManager();
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(title);
		alert.setDialogMessage(message);
		alert.setPositiveButton("Ok", new View.OnClickListener() {
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
		alert.setPositiveButton("Ok", new View.OnClickListener() {
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
		db.execQuery(Tables.create(TB.TIME_IN));
		db.execQuery(Tables.create(TB.TIME_OUT));
		db.execQuery(Tables.create(TB.BREAK_IN));
		db.execQuery(Tables.create(TB.BREAK_OUT));
		db.execQuery(Tables.create(TB.INCIDENT));
		db.execQuery(Tables.create(TB.INCIDENT_REPORT));
		db.execQuery(Tables.create(TB.TIME_SECURITY));
		db.execQuery(Tables.create(TB.LOCATION));
		db.execQuery(Tables.create(TB.PHOTO));
		db.execQuery(Tables.create(TB.FORMS));
		db.execQuery(Tables.create(TB.FIELDS));
		db.execQuery(Tables.create(TB.CHOICES));
		db.execQuery(Tables.create(TB.ENTRIES));
		db.execQuery(Tables.create(TB.ANSWERS));
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

	public static String getGroupID(SQLiteAdapter db) {
		String e = Tables.getName(TB.EMPLOYEE);
		String c = Tables.getName(TB.CREDENTIALS);
		String query = "SELECT e.groupID FROM " + e + " e, " + c + " c WHERE c.ID = 1 " +
				"AND e.ID = c.empID";
		return db.getString(query);
	}

	public static String getEmployeeUrl(SQLiteAdapter db, String empID) {
		String table = Tables.getName(EMPLOYEE);
		String query = "SELECT imageUrl FROM " + table + " WHERE ID = '" + empID + "'";
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

	public static boolean hasIncident(SQLiteAdapter db, int incidentID) {
		String timeInID = getTimeInID(db);
		String table = Tables.getName(TB.INCIDENT_REPORT);
		String query = "SELECT ID FROM " + table + " WHERE incidentID = '" +
				incidentID + "' AND timeInID = '" + timeInID + "'";
		return db.isRecordExists(query);
	}

	public static boolean saveTimeIn(SQLiteAdapter db, String photo, GpsObj gps, StoreObj store) {
		SQLiteBinder binder = new SQLiteBinder(db);
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
		query.add(new FieldValue("gpsDate", gps.date));
		query.add(new FieldValue("gpsTime", gps.time));
		query.add(new FieldValue("storeID", store.ID));
		query.add(new FieldValue("gpsLongitude", gps.longitude));
		query.add(new FieldValue("gpsLatitude", gps.latitude));
		query.add(new FieldValue("isGpsEnabled", gps.isEnabled));
		query.add(new FieldValue("withGpsHistory", gps.withHistory));
		query.add(new FieldValue("isGpsValid", gps.isValid));
		query.add(new FieldValue("batteryLevel", battery));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		binder.insert(Tables.getName(TB.TIME_IN), query);
		return binder.finish();
	}

	public static boolean saveTimeOut(SQLiteAdapter db, String dDate, String dTime,
									  String photo, String signature, GpsObj gps) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String empID = getEmployeeID(db);
		String timeInID = getTimeInID(db);
		String syncBatchID = getSyncBatchID(db);
		int battery = CodePanUtils.getBatteryLevel(db.getContext());
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("photo", photo));
		query.add(new FieldValue("timeInID", timeInID));
		query.add(new FieldValue("signature", signature));
		query.add(new FieldValue("gpsDate", gps.date));
		query.add(new FieldValue("gpsTime", gps.time));
		query.add(new FieldValue("gpsLongitude", gps.longitude));
		query.add(new FieldValue("gpsLatitude", gps.latitude));
		query.add(new FieldValue("isGpsEnabled", gps.isEnabled));
		query.add(new FieldValue("withGpsHistory", gps.withHistory));
		query.add(new FieldValue("isGpsValid", gps.isValid));
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
		query.add(new FieldValue("gpsDate", gps.date));
		query.add(new FieldValue("gpsTime", gps.time));
		query.add(new FieldValue("gpsLongitude", gps.longitude));
		query.add(new FieldValue("gpsLatitude", gps.latitude));
		query.add(new FieldValue("isGpsEnabled", gps.isEnabled));
		query.add(new FieldValue("withGpsHistory", gps.withHistory));
		query.add(new FieldValue("isGpsValid", gps.isValid));
		query.add(new FieldValue("batteryLevel", battery));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		query.add(new FieldValue("breakID", obj.ID));
		query.add(new FieldValue("timeInID", timeInID));
		binder.insert(Tables.getName(TB.BREAK_IN), query);
		return binder.finish();
	}

	public static boolean saveBreakOut(SQLiteAdapter db, GpsObj gps, BreakInObj in) {
		SQLiteBinder binder = new SQLiteBinder(db);
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
		query.add(new FieldValue("gpsDate", gps.date));
		query.add(new FieldValue("gpsTime", gps.time));
		query.add(new FieldValue("gpsLongitude", gps.longitude));
		query.add(new FieldValue("gpsLatitude", gps.latitude));
		query.add(new FieldValue("isGpsEnabled", gps.isEnabled));
		query.add(new FieldValue("withGpsHistory", gps.withHistory));
		query.add(new FieldValue("isGpsValid", gps.isValid));
		query.add(new FieldValue("batteryLevel", battery));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		query.add(new FieldValue("timeInID", timeInID));
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
		String empID = getEmployeeID(db);
		String timeInID = getTimeInID(db);
		String syncBatchID = getSyncBatchID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("gpsDate", gps.date));
		query.add(new FieldValue("gpsTime", gps.time));
		query.add(new FieldValue("gpsLongitude", gps.longitude));
		query.add(new FieldValue("gpsLatitude", gps.latitude));
		query.add(new FieldValue("isGpsEnabled", gps.isEnabled));
		query.add(new FieldValue("withGpsHistory", gps.withHistory));
		query.add(new FieldValue("isGpsValid", gps.isValid));
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
		String empID = getEmployeeID(db);
		String timeInID = getTimeInID(db);
		String syncBatchID = getSyncBatchID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("gpsDate", gps.date));
		query.add(new FieldValue("gpsTime", gps.time));
		query.add(new FieldValue("gpsLongitude", gps.longitude));
		query.add(new FieldValue("gpsLatitude", gps.latitude));
		query.add(new FieldValue("isGpsEnabled", gps.isEnabled));
		query.add(new FieldValue("withGpsHistory", gps.withHistory));
		query.add(new FieldValue("isGpsValid", gps.isValid));
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
		String empID = getEmployeeID(db);
		String timeInID = getTimeInID(db);
		String syncBatchID = getSyncBatchID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("gpsDate", gps.date));
		query.add(new FieldValue("gpsTime", gps.time));
		query.add(new FieldValue("gpsLongitude", gps.longitude));
		query.add(new FieldValue("gpsLatitude", gps.latitude));
		query.add(new FieldValue("isGpsEnabled", gps.isEnabled));
		query.add(new FieldValue("withGpsHistory", gps.withHistory));
		query.add(new FieldValue("isGpsValid", gps.isValid));
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
		query.add(new Condition("isDefault", true, Operator.EQUALS));
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
			out.timeInID = timeInID;
			out.dDate = cursor.getString(3);
			out.dTime = cursor.getString(4);
			out.signature = cursor.getString(5);
			attendance.in = in;
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
		query.add(new Condition("timeInID", timeInID, Operator.EQUALS));
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
		for(TB tb : tableList) {
			String table = Tables.getName(tb);
			String query = "SELECT COUNT(ID) FROM " + table + " WHERE isSync = 0";
			count += db.getInt(query);
		}
		return count;
	}

	public static int getCountPhotoUpload(SQLiteAdapter db) {
		int count = 0;
		ArrayList<TB> tableList = new ArrayList<>();
		tableList.add(TB.TIME_IN);
		tableList.add(TB.TIME_OUT);
		for(TB tb : tableList) {
			String table = Tables.getName(tb);
			String query = "SELECT COUNT(ID) FROM " + table + " WHERE isPhotoUpload = 0 " +
					"AND photo NOT NULL";
			count += db.getInt(query);
		}
		return count;
	}

	public static int getCountSignatureUpload(SQLiteAdapter db) {
		int count = 0;
		ArrayList<TB> tableList = new ArrayList<>();
		tableList.add(TB.TIME_OUT);
		for(TB tb : tableList) {
			String table = Tables.getName(tb);
			String query = "SELECT COUNT(ID) FROM " + table + " WHERE isSignatureUpload = 0 " +
					"AND signature NOT NULL";
			count += db.getInt(query);
		}
		return count;
	}

	public static int getCountSyncTotal(SQLiteAdapter db) {
		int syncCount = getCountSync(db);
		int photoCount = getCountPhotoUpload(db);
		int signatureCount = getCountSignatureUpload(db);
		return syncCount + photoCount + signatureCount;
	}

	public static boolean saveEntry(SQLiteAdapter db, String formID, ArrayList<FieldObj> fieldList, boolean isSubmit) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String dDate = CodePanUtils.getDate();
		String dTime = CodePanUtils.getTime();
		String empID = getEmployeeID(db);
		String syncBatchID = getSyncBatchID(db);
		SQLiteQuery query = new SQLiteQuery();
		query.add(new FieldValue("formID", formID));
		query.add(new FieldValue("dDate", dDate));
		query.add(new FieldValue("dTime", dTime));
		query.add(new FieldValue("empID", empID));
		query.add(new FieldValue("isSubmit", isSubmit));
		query.add(new FieldValue("syncBatchID", syncBatchID));
		if(isSubmit) {
			query.add(new FieldValue("dateSubmitted", dDate));
			query.add(new FieldValue("timeSubmitted", dTime));
		}
		String entryID = binder.insert(Tables.getName(ENTRIES), query);
		for(FieldObj field : fieldList) {
			if(field.isQuestion) {
				AnswerObj answer = field.answer;
				String value = answer.value;
				switch(field.type) {
					case FieldType.MS:
						if(answer.choiceList != null && !answer.choiceList.isEmpty()) {
							value = "";
							for(ChoiceObj choiceObj : answer.choiceList) {
								if(choiceObj.isCheck) {
									value += choiceObj.code + ",";
								}
							}
							int length = value.length();
							if(length != 0) {
								value = value.substring(0, length - 1);
							}
						}
						break;
					case FieldType.PHOTO:
						if(answer.imageList != null && !answer.imageList.isEmpty()) {
							value = "";
							for(ImageObj image : answer.imageList) {
								value += image.ID + ",";
							}
							int length = value.length();
							if(length != 0) {
								value = value.substring(0, length - 1);
							}
						}
						break;
					case FieldType.YON:
						value = answer.isCheck ? AnswerType.YES : AnswerType.NO;
						break;
					case FieldType.CB:
						value = answer.isCheck ? AnswerType.CHECK : AnswerType.UNCHECK;
						break;
				}
				query.clearAll();
				query.add(new FieldValue("entryID", entryID));
				query.add(new FieldValue("fieldID", field.ID));
				query.add(new FieldValue("value", value));
				query.add(new FieldValue("isUpdate", true));
				binder.insert(Tables.getName(ANSWERS), query);
			}
		}
		return binder.finish();
	}

	public static boolean updateEntry(SQLiteAdapter db, String entryID, ArrayList<FieldObj> fieldList, boolean isSubmit) {
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
				String value = answer.value;
				switch(field.type) {
					case FieldType.MS:
						if(answer.choiceList != null && !answer.choiceList.isEmpty()) {
							value = "";
							for(ChoiceObj choiceObj : answer.choiceList) {
								if(choiceObj.isCheck) {
									value += choiceObj.code + ",";
								}
							}
							int length = value.length();
							if(length != 0) {
								value = value.substring(0, length - 1);
							}
						}
						break;
					case FieldType.PHOTO:
						if(answer.imageList != null && !answer.imageList.isEmpty()) {
							value = "";
							for(ImageObj image : answer.imageList) {
								value += image.ID + ",";
							}
							int length = value.length();
							if(length != 0) {
								value = value.substring(0, length - 1);
							}
						}
						break;
					case FieldType.YON:
						value = answer.isCheck ? AnswerType.YES : AnswerType.NO;
						break;
					case FieldType.CB:
						value = answer.isCheck ? AnswerType.CHECK : AnswerType.UNCHECK;
						break;
				}
				query.clearAll();
				query.add(new FieldValue("value", value));
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
		query.add(new FieldValue("fileName", obj.fileName));
		query.add(new FieldValue("dDate", obj.dDate));
		query.add(new FieldValue("dTime", obj.dTime));
		query.add(new FieldValue("isSignature", obj.isSignature));
		query.add(new FieldValue("empID", empID));
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
}

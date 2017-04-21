package com.mobileoptima.core;

import android.database.Cursor;
import android.os.SystemClock;

import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.codepan.model.TimeObj;
import com.mobileoptima.schema.Tables;
import com.mobileoptima.schema.Tables.TB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeSecurity {

	private static final long DEFAULT_BOOT_DELAY = 60000L;
	private static final int TIME_RESET_DAY = 180;
	private static final int ALLOWANCE_MIN = 1;

	public enum TimeSecurityType {
		TIME_CHANGED,
		DATE_CHANGED,
		TIME_ZONE_CHANGED
	}

	public static TimeObj getServerTime(SQLiteAdapter db) {
		long currentElapsedTime = SystemClock.elapsedRealtime();
		long serverTime = 0;
		long elapsedTime = 0;
		String timeZoneID = "";
		String table = Tables.getName(TB.TIME_SECURITY);
		String query = "SELECT serverTime, timeZoneID, elapsedTime FROM " + table + " WHERE ID = 1";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			serverTime = cursor.getLong(0);
			timeZoneID = cursor.getString(1);
			elapsedTime = cursor.getLong(2);
		}
		cursor.close();
		long rightTime = serverTime + currentElapsedTime - elapsedTime;
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		timeFormat.setTimeZone(TimeZone.getTimeZone(timeZoneID));
		dateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneID));
		Date phoneTime = new Date(rightTime);
		TimeObj timeObj = new TimeObj();
		timeObj.time = timeFormat.format(phoneTime);
		timeObj.date = dateFormat.format(phoneTime);
		timeObj.timeZoneID = timeZoneID;
		timeObj.timestamp = rightTime;
		return timeObj;
	}

	public static boolean isDateChanged(SQLiteAdapter db) {
		String table = Tables.getName(TB.TIME_SECURITY);
		String query = "SELECT isDateChanged FROM " + table + " WHERE ID = 1";
		return db.getInt(query) == 1;
	}

	public static boolean isRightTime(SQLiteAdapter db) {
		TimeObj obj = getServerTime(db);
		long allowance = ALLOWANCE_MIN * 1000 * 60;
		long currentTime = System.currentTimeMillis();
		long difference = currentTime > obj.timestamp ?
				currentTime - obj.timestamp : obj.timestamp - currentTime;
		if(difference <= allowance) {
			if(obj.timeZoneID != null && !obj.timeZoneID.isEmpty()) {
				TimeZone defaultTimeZone = TimeZone.getDefault();
				TimeZone serverTimeZone = TimeZone.getTimeZone(obj.timeZoneID);
				long defaultOffset = TimeUnit.HOURS.convert(defaultTimeZone.getRawOffset(), TimeUnit.MILLISECONDS);
				long serverOffset = TimeUnit.HOURS.convert(serverTimeZone.getRawOffset(), TimeUnit.MILLISECONDS);
				return defaultOffset == serverOffset;
			}
			else {
				return true;
			}
		}
		else {
			return false;
		}
	}

	public static boolean isTimeChanged(SQLiteAdapter db) {
		String table = Tables.getName(TB.TIME_SECURITY);
		String query = "SELECT isTimeChanged FROM " + table + " WHERE ID = 1";
		return db.getInt(query) == 1;
	}

	public static boolean isTimeSecured(SQLiteAdapter db) {
		String table = Tables.getName(TB.TIME_SECURITY);
		String query = "SELECT ID FROM " + table + " WHERE ID = 1";
		return db.isRecordExists(query);
	}

	/**
	 * @param db
	 * @return true if the RTC clock has been reset or if the user change the
	 * time and did not shut down
	 * which will requires the user to download server time online/gps
	 */
	public static boolean isTimeUnknown(SQLiteAdapter db) {
		String table = Tables.getName(TB.TIME_SECURITY);
		String query = "SELECT isTimeUnknown FROM " + table + " WHERE ID = 1";
		return db.getInt(query) == 1;
	}

	public static boolean isValidated(SQLiteAdapter db) {
		String table = Tables.getName(TB.TIME_SECURITY);
		String query = "SELECT isValidated FROM " + table + " WHERE ID = 1";
		return db.getInt(query) == 1;
	}

	public static boolean isTimeZoneChanged(SQLiteAdapter db) {
		String table = Tables.getName(TB.TIME_SECURITY);
		String query = "SELECT isTimeZoneChanged FROM " + table + " WHERE ID = 1";
		return db.getInt(query) == 1;
	}

	/**
	 * @param db
	 * @return true if the device time should be compared
	 * to server time for pop up validation.
	 */
	public static boolean isTimeCheck(SQLiteAdapter db) {
		String table = Tables.getName(TB.TIME_SECURITY);
		String query = "SELECT isTimeCheck FROM " + table + " WHERE ID = 1";
		return db.getInt(query) == 1;
	}

	public static boolean resetTimeSecurity(SQLiteAdapter db) {
		SQLiteBinder binder = new SQLiteBinder(db);
		long currentTime = System.currentTimeMillis();
		long elapsedTime = SystemClock.elapsedRealtime();
		ArrayList<FieldValue> fieldValueList = new ArrayList<FieldValue>();
		fieldValueList.add(new FieldValue("isTimeChanged", false));
		fieldValueList.add(new FieldValue("isDateChanged", false));
		fieldValueList.add(new FieldValue("isTimeZoneChanged", false));
		fieldValueList.add(new FieldValue("isTimeCheck", false));
		fieldValueList.add(new FieldValue("serverTime", currentTime));
		fieldValueList.add(new FieldValue("elapsedTime", elapsedTime));
		fieldValueList.add(new FieldValue("shutDownTime", 0));
		fieldValueList.add(new FieldValue("updateElapsedTime", 0));
		binder.update(Tables.getName(TB.TIME_SECURITY), fieldValueList, 1);
		return binder.finish();
	}

	public static boolean saveLastKnownTime(SQLiteAdapter db) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String table = Tables.getName(TB.TIME_SECURITY);
		String query = "SELECT elapsedTime FROM " + table + " WHERE ID = 1";
		long elapsedTime = db.getLong(query);
		long shutDownTime = System.currentTimeMillis();
		long currentElapsedTime = SystemClock.elapsedRealtime();
		long updateElapsedTime = currentElapsedTime - elapsedTime;
		ArrayList<FieldValue> fieldValueList = new ArrayList<FieldValue>();
		fieldValueList.add(new FieldValue("shutDownTime", shutDownTime));
		fieldValueList.add(new FieldValue("updateElapsedTime", updateElapsedTime));
		fieldValueList.add(new FieldValue("isBootIncomplete", true));
		binder.update(Tables.getName(TB.TIME_SECURITY), fieldValueList, 1);
		return binder.finish();
	}

	public static boolean updateServerTime(SQLiteAdapter db, long timestamp) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String timeZoneID = TimeZone.getDefault().getID();
		long elapsedTime = SystemClock.elapsedRealtime();
		ArrayList<FieldValue> fieldValueList = new ArrayList<FieldValue>();
		fieldValueList.add(new FieldValue("serverTime", timestamp));
		fieldValueList.add(new FieldValue("elapsedTime", elapsedTime));
		fieldValueList.add(new FieldValue("isTimeUnknown", false));
		fieldValueList.add(new FieldValue("isTimeCheck", true));
		fieldValueList.add(new FieldValue("isValidated", true));
		String table = Tables.getName(TB.TIME_SECURITY);
		String query = "SELECT ID FROM " + table + " WHERE ID = 1";
		if(db.isRecordExists(query)) {
			String recID = db.getString(query);
			binder.update(Tables.getName(TB.TIME_SECURITY), fieldValueList, recID);
		}
		else {
			fieldValueList.add(new FieldValue("timeZoneID", timeZoneID));
			binder.insert(Tables.getName(TB.TIME_SECURITY), fieldValueList);
		}
		return binder.finish();
	}

	public static boolean updateServerTime(SQLiteAdapter db, String date, String time, long timestamp) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String serverTimeZoneID = TimeZone.getDefault().getID();
		long elapsedTime = SystemClock.elapsedRealtime();
		for(String timeZoneID : TimeZone.getAvailableIDs()) {
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			timeFormat.setTimeZone(TimeZone.getTimeZone(timeZoneID));
			dateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneID));
			Date serverTime = new Date(timestamp);
			if(timeFormat.format(serverTime).equals(time) && dateFormat.format(serverTime).equals(date)) {
				serverTimeZoneID = timeZoneID;
			}
		}
		ArrayList<FieldValue> fieldValueList = new ArrayList<FieldValue>();
		fieldValueList.add(new FieldValue("serverTime", timestamp));
		fieldValueList.add(new FieldValue("timeZoneID", serverTimeZoneID));
		fieldValueList.add(new FieldValue("elapsedTime", elapsedTime));
		fieldValueList.add(new FieldValue("isTimeUnknown", false));
		fieldValueList.add(new FieldValue("isTimeCheck", true));
		fieldValueList.add(new FieldValue("isValidated", true));
		String table = Tables.getName(TB.TIME_SECURITY);
		String query = "SELECT ID FROM " + table + " WHERE ID = 1";
		if(db.isRecordExists(query)) {
			String recID = db.getString(query);
			binder.update(Tables.getName(TB.TIME_SECURITY), fieldValueList, recID);
		}
		else {
			binder.insert(Tables.getName(TB.TIME_SECURITY), fieldValueList);
		}
		return binder.finish();
	}

	/**
	 * if the boot complete receiver is not yet called,
	 * it will run the pending process of boot complete
	 * receiver to avoid time discrepancy.
	 *
	 * @param db
	 * @return true if success
	 */
	public static void checkBootComplete(SQLiteAdapter db) {
		long serverTime = 0;
		long shutDownTime = 0;
		long updateElapsedTime = 0;
		boolean isBootInComplete = false;
		String table = Tables.getName(TB.TIME_SECURITY);
		String query = "SELECT serverTime, shutDownTime, updateElapsedTime, isBootIncomplete " +
				"FROM " + table + " WHERE ID = 1";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			serverTime = cursor.getLong(0);
			shutDownTime = cursor.getLong(1);
			updateElapsedTime = cursor.getLong(2);
			isBootInComplete = cursor.getInt(3) == 1;
		}
		cursor.close();
		long elapsedTime = SystemClock.elapsedRealtime();
		long bootDelay = getBootDelay(db);
		bootDelay = bootDelay != 0L ? bootDelay : DEFAULT_BOOT_DELAY;
		if(elapsedTime <= bootDelay && isTimeReset(db)) {
			setTimeToUnknown(db);
		}
		else {
			if(isBootInComplete) {
				long bootCompleteTime = System.currentTimeMillis();
				long turnOffDuration = bootCompleteTime - shutDownTime;
				serverTime += updateElapsedTime + turnOffDuration;
				updateServerTimeAfterBoot(db, serverTime, elapsedTime);
			}
		}
	}

	public static void updateServerTimeAfterBoot(SQLiteAdapter db) {
		long serverTime = 0;
		long shutDownTime = 0;
		long updateElapsedTime = 0;
		boolean isBootIncomplete = false;
		String table = Tables.getName(TB.TIME_SECURITY);
		String query = "SELECT serverTime, shutDownTime, updateElapsedTime, isBootIncomplete " +
				"FROM " + table + " WHERE ID = 1";
		Cursor cursor = db.read(query);
		while(cursor.moveToNext()) {
			serverTime = cursor.getLong(0);
			shutDownTime = cursor.getLong(1);
			updateElapsedTime = cursor.getLong(2);
			isBootIncomplete = cursor.getInt(3) == 1;
		}
		cursor.close();
		long elapsedTime = SystemClock.elapsedRealtime();
		long bootCompleteTime = System.currentTimeMillis();
		long turnOffDuration = 0L;
		if(isTimeReset(db)) {
			setTimeToUnknown(db);
		}
		else {
			if(isBootIncomplete) {
				turnOffDuration = bootCompleteTime - shutDownTime;
				serverTime += updateElapsedTime + turnOffDuration;
				updateServerTimeAfterBoot(db, serverTime, elapsedTime);
			}
			else {
				if(isTimeChanged(db) || isDateChanged(db) || isTimeZoneChanged(db)) {
					setTimeToUnknown(db);
				}
				else {
					updateServerTimeAfterBoot(db, bootCompleteTime, elapsedTime);
				}
			}
		}
	}

	private static boolean updateServerTimeAfterBoot(SQLiteAdapter db, long serverTime, long elapsedTime) {
		SQLiteBinder binder = new SQLiteBinder(db);
		ArrayList<FieldValue> fieldValueList = new ArrayList<FieldValue>();
		fieldValueList.add(new FieldValue("isTimeCheck", true));
		fieldValueList.add(new FieldValue("isBootIncomplete", false));
		fieldValueList.add(new FieldValue("serverTime", serverTime));
		fieldValueList.add(new FieldValue("shutDownTime", 0));
		fieldValueList.add(new FieldValue("updateElapsedTime", 0));
		fieldValueList.add(new FieldValue("elapsedTime", elapsedTime));
		binder.update(Tables.getName(TB.TIME_SECURITY), fieldValueList, 1);
		return binder.finish();
	}

	/**
	 * @param db Checker if the RTC has been reset
	 */
	public static boolean isTimeReset(SQLiteAdapter db) {
		TimeObj timeObj = getServerTime(db);
		if(timeObj.timestamp > System.currentTimeMillis()) {
			long difference = timeObj.timestamp - System.currentTimeMillis();
			long allowance = 86400000L * TIME_RESET_DAY;
			return !isTimeUnknown(db) && difference >= allowance;
		}
		return false;
	}

	public static boolean setTimeToUnknown(SQLiteAdapter db) {
		SQLiteBinder binder = new SQLiteBinder(db);
		ArrayList<FieldValue> fieldValueList = new ArrayList<FieldValue>();
		fieldValueList.add(new FieldValue("isTimeUnknown", true));
		fieldValueList.add(new FieldValue("isValidated", false));
		binder.update(Tables.getName(TB.TIME_SECURITY), fieldValueList, 1);
		return binder.finish();
	}

	public static boolean updateTimeSecurity(SQLiteAdapter db, TimeSecurityType type, boolean value) {
		SQLiteBinder binder = new SQLiteBinder(db);
		String table = Tables.getName(TB.TIME_SECURITY);
		String query = "SELECT ID FROM " + table + " WHERE ID = 1";
		ArrayList<FieldValue> fieldValueList = new ArrayList<FieldValue>();
		if(db.isRecordExists(query)) {
			switch(type) {
				case TIME_CHANGED:
					fieldValueList.add(new FieldValue("isTimeChanged", value));
					break;
				case DATE_CHANGED:
					fieldValueList.add(new FieldValue("isDateChanged", value));
					break;
				case TIME_ZONE_CHANGED:
					fieldValueList.add(new FieldValue("isTimeZoneChanged", value));
					break;
			}
			binder.update(table, fieldValueList, 1);
			return binder.finish();
		}
		else {
			return true;
		}
	}

	public static boolean saveBootDelay(SQLiteAdapter db) {
		SQLiteBinder binder = new SQLiteBinder(db);
		ArrayList<FieldValue> fieldValueList = new ArrayList<FieldValue>();
		fieldValueList.add(new FieldValue("bootDelay", SystemClock.elapsedRealtime()));
		binder.update(Tables.getName(TB.TIME_SECURITY), fieldValueList, 1);
		return binder.finish();
	}

	public static long getBootDelay(SQLiteAdapter db) {
		String table = Tables.getName(TB.TIME_SECURITY);
		String query = "SELECT bootDelay FROM " + table + " WHERE ID = 1";
		return db.getLong(query);
	}
}

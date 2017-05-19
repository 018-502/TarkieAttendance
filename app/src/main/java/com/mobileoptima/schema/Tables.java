package com.mobileoptima.schema;

import com.codepan.database.Field;
import com.codepan.database.SQLiteQuery;
import com.codepan.database.SQLiteQuery.DataType;

public class Tables {

	public enum TB {
		API_KEY,
		SYNC_BATCH,
		CREDENTIALS,
		COMPANY,
		EMPLOYEE,
		BREAK,
		STORES,
		CONVENTION,
		GPS,
		TIME_IN,
		TIME_OUT,
		BREAK_IN,
		BREAK_OUT,
		INCIDENT,
		INCIDENT_REPORT,
		TIME_SECURITY,
		LOCATION,
		PHOTO,
		FORMS,
		FIELDS,
		CHOICES,
		ENTRIES,
		ANSWERS,
		ENTRY_TASK,
		TASK,
		CHECK_IN,
		CHECK_OUT
	}

	public static String create(TB tb) {
		SQLiteQuery query = new SQLiteQuery();
		switch(tb) {
			case API_KEY:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("apiKey", DataType.TEXT));
				query.add(new Field("authorizationCode", DataType.TEXT));
				query.add(new Field("deviceID", DataType.TEXT));
				break;
			case SYNC_BATCH:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("syncBatchID", DataType.TEXT));
				break;
			case CREDENTIALS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("isLogOut", 0));
				query.add(new Field("isNewUser", 0));
				break;
			case COMPANY:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("code", DataType.TEXT));
				query.add(new Field("address", DataType.TEXT));
				query.add(new Field("email", DataType.TEXT));
				query.add(new Field("mobile", DataType.TEXT));
				query.add(new Field("landline", DataType.TEXT));
				query.add(new Field("logoUrl", DataType.TEXT));
				query.add(new Field("expirationDate", DataType.TEXT));
				break;
			case EMPLOYEE:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("employeeNo", DataType.TEXT));
				query.add(new Field("firstName", DataType.TEXT));
				query.add(new Field("lastName", DataType.TEXT));
				query.add(new Field("email", DataType.TEXT));
				query.add(new Field("mobile", DataType.TEXT));
				query.add(new Field("groupID", DataType.INTEGER));
				query.add(new Field("isActive", DataType.INTEGER));
				query.add(new Field("imageUrl", DataType.TEXT));
				break;
			case BREAK:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("duration", DataType.INTEGER));
				break;
			case STORES:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("gpsLongitude", DataType.TEXT));
				query.add(new Field("gpsLatitude", DataType.TEXT));
				query.add(new Field("address", DataType.TEXT));
				query.add(new Field("radius", DataType.TEXT));
				query.add(new Field("isDefault", 0));
				break;
			case CONVENTION:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("convention", DataType.TEXT));
				break;
			case GPS:
				query.add(new Field("ID", true));
				query.add(new Field("gpsDate", DataType.TEXT));
				query.add(new Field("gpsTime", DataType.TEXT));
				query.add(new Field("gpsLongitude", DataType.TEXT));
				query.add(new Field("gpsLatitude", DataType.TEXT));
				query.add(new Field("isGpsEnabled", DataType.INTEGER));
				query.add(new Field("withGpsHistory", DataType.INTEGER));
				query.add(new Field("isGpsValid", DataType.INTEGER));
				break;
			case TIME_IN:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("photo", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("storeID", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("batteryLevel", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("isPhotoUpload", 0));
				query.add(new Field("isTimeOut", 0));
				query.add(new Field("isSync", 0));
				break;
			case TIME_OUT:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("photo", DataType.TEXT));
				query.add(new Field("signature", DataType.TEXT));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("batteryLevel", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("isSignatureUpload", 0));
				query.add(new Field("isPhotoUpload", 0));
				query.add(new Field("isSync", 0));
				break;
			case BREAK_IN:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("batteryLevel", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("breakID", DataType.INTEGER));
				query.add(new Field("isBreakOut", 0));
				query.add(new Field("isSync", 0));
				break;
			case BREAK_OUT:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("batteryLevel", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("breakInID", DataType.INTEGER));
				query.add(new Field("isSync", 0));
				break;
			case INCIDENT:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				break;
			case INCIDENT_REPORT:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("incidentID", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("value", DataType.TEXT));
				query.add(new Field("isPending", 0));
				query.add(new Field("isSync", 0));
				break;
			case TIME_SECURITY:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("isTimeChanged", 0));
				query.add(new Field("isDateChanged", 0));
				query.add(new Field("isTimeZoneChanged", 0));
				query.add(new Field("isTimeUnknown", 0));
				query.add(new Field("isTimeCheck", 0));
				query.add(new Field("isBootIncomplete", 0));
				query.add(new Field("isValidated", 0));
				query.add(new Field("serverTime", DataType.TEXT));
				query.add(new Field("elapsedTime", DataType.TEXT));
				query.add(new Field("shutDownTime", DataType.TEXT));
				query.add(new Field("updateElapsedTime", DataType.TEXT));
				query.add(new Field("timeZoneID", DataType.TEXT));
				query.add(new Field("bootDelay", DataType.TEXT));
				break;
			case LOCATION:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("longitude", DataType.TEXT));
				query.add(new Field("latitude", DataType.TEXT));
				query.add(new Field("provider", DataType.TEXT));
				query.add(new Field("accuracy", DataType.TEXT));
				break;
			case PHOTO:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("fileName", DataType.TEXT));
				query.add(new Field("webPhotoID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("isSignature", 0));
				query.add(new Field("isDelete", 0));
				query.add(new Field("isUpload", 0));
				query.add(new Field("isActive", 0));
				break;
			case FORMS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("description", DataType.TEXT));
				query.add(new Field("dateCreated", DataType.TEXT));
				query.add(new Field("timeCreated", DataType.TEXT));
				query.add(new Field("groupID", DataType.INTEGER));
				query.add(new Field("logoUrl", DataType.TEXT));
				query.add(new Field("category", DataType.TEXT));
				query.add(new Field("isActive", 1));
				break;
			case FIELDS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("description", DataType.TEXT));
				query.add(new Field("type", DataType.TEXT));
				query.add(new Field("formID", DataType.INTEGER));
				query.add(new Field("orderNo", DataType.INTEGER));
				query.add(new Field("isRequired", DataType.INTEGER));
				query.add(new Field("isActive", 1));
				break;
			case CHOICES:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("name", DataType.TEXT));
				query.add(new Field("code", DataType.TEXT));
				query.add(new Field("fieldID", DataType.INTEGER));
				break;
			case ENTRIES:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("formID", DataType.INTEGER));
				query.add(new Field("referenceNo", DataType.TEXT));
				query.add(new Field("dateSubmitted", DataType.TEXT));
				query.add(new Field("timeSubmitted", DataType.TEXT));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("webEntryID", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("isFromWeb", 0));
				query.add(new Field("isDelete", 0));
				query.add(new Field("isSubmit", 0));
				query.add(new Field("isSync", 0));
				break;
			case ANSWERS:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("value", DataType.TEXT));
				query.add(new Field("entryID", DataType.INTEGER));
				query.add(new Field("fieldID", DataType.INTEGER));
				query.add(new Field("isUpdate", 0));
				break;
			case ENTRY_TASK:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("entryID", DataType.INTEGER));
				query.add(new Field("taskID", DataType.INTEGER));
				query.add(new Field("isTag", 1));
				break;
			case TASK:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("storeID", DataType.INTEGER));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("scheduleDate", DataType.TEXT));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("webTaskID", DataType.TEXT));
				query.add(new Field("notesLimit", DataType.INTEGER));
				query.add(new Field("notes", DataType.TEXT));
				query.add(new Field("taskPriority", DataType.TEXT));
				query.add(new Field("taskStatus", DataType.TEXT));
				query.add(new Field("isSync", 0));
				query.add(new Field("isFromWeb", 0));
				query.add(new Field("isCheckIn", 0));
				query.add(new Field("isCheckOut", 0));
				query.add(new Field("isUpdate", 0));
				query.add(new Field("isWebUpdate", 0));
				query.add(new Field("isDelete", 0));
				query.add(new Field("isWebDelete", 0));
				break;
			case CHECK_IN:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("taskID", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("batteryLevel", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("photo", DataType.TEXT));
				query.add(new Field("isSync", 0));
				query.add(new Field("isUpload", 0));
				break;
			case CHECK_OUT:
				query.clearAll();
				query.add(new Field("ID", true));
				query.add(new Field("dDate", DataType.TEXT));
				query.add(new Field("dTime", DataType.TEXT));
				query.add(new Field("empID", DataType.INTEGER));
				query.add(new Field("taskID", DataType.INTEGER));
				query.add(new Field("timeInID", DataType.INTEGER));
				query.add(new Field("checkInID", DataType.INTEGER));
				query.add(new Field("batteryLevel", DataType.INTEGER));
				query.add(new Field("gpsID", DataType.INTEGER));
				query.add(new Field("syncBatchID", DataType.TEXT));
				query.add(new Field("photo", DataType.TEXT));
				query.add(new Field("isSync", 0));
				query.add(new Field("isUpload", 0));
				break;
		}
		return query.createTable(getName(tb));
	}

	public static String getName(TB tb) {
		String name = null;
		switch(tb) {
			case API_KEY:
				name = "api_key_tb";
				break;
			case SYNC_BATCH:
				name = "sync_batch_tb";
				break;
			case CREDENTIALS:
				name = "credentials_tb";
				break;
			case COMPANY:
				name = "company_tb";
				break;
			case EMPLOYEE:
				name = "employee_tb";
				break;
			case BREAK:
				name = "break_tb";
				break;
			case STORES:
				name = "stores_tb";
				break;
			case CONVENTION:
				name = "convention_tb";
				break;
			case GPS:
				name = "gps_tb";
				break;
			case TIME_IN:
				name = "time_in_tb";
				break;
			case TIME_OUT:
				name = "time_out_tb";
				break;
			case BREAK_IN:
				name = "break_in_tb";
				break;
			case BREAK_OUT:
				name = "break_out_tb";
				break;
			case INCIDENT:
				name = "incident_tb";
				break;
			case INCIDENT_REPORT:
				name = "incident_report_tb";
				break;
			case TIME_SECURITY:
				name = "time_security_tb";
				break;
			case LOCATION:
				name = "location_tb";
				break;
			case PHOTO:
				name = "photo_tb";
				break;
			case FORMS:
				name = "forms_tb";
				break;
			case FIELDS:
				name = "fields_tb";
				break;
			case CHOICES:
				name = "choices_tb";
				break;
			case ENTRIES:
				name = "entries_tb";
				break;
			case ANSWERS:
				name = "answers_tb";
				break;
			case ENTRY_TASK:
				name = "entry_task_tb";
				break;
			case TASK:
				name = "task_tb";
				break;
			case CHECK_IN:
				name = "check_in_tb";
				break;
			case CHECK_OUT:
				name = "check_out_tb";
				break;
		}
		return name;
	}
}

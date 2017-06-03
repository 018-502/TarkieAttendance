package com.mobileoptima.core;

import android.content.Context;
import android.util.Log;

import com.codepan.callback.Interface.OnErrorCallback;
import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.codepan.database.SQLiteQuery;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.constant.App;
import com.mobileoptima.model.AnswerObj;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreakOutObj;
import com.mobileoptima.model.EmployeeObj;
import com.mobileoptima.model.EntryObj;
import com.mobileoptima.model.FieldObj;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.IncidentObj;
import com.mobileoptima.model.IncidentReportObj;
import com.mobileoptima.model.PhotoObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;
import com.mobileoptima.schema.Tables;
import com.mobileoptima.schema.Tables.TB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Tx {

	public static boolean syncTimeIn(SQLiteAdapter db, TimeInObj in, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "time-in";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			GpsObj gps = in.gps;
			EmployeeObj emp = in.emp;
			StoreObj store = in.store;
			paramsObj.put("api_key", apiKey);
			paramsObj.put("date_in", in.dDate);
			paramsObj.put("time_in", in.dTime);
			paramsObj.put("gps_date", gps.date);
			paramsObj.put("gps_time", gps.time);
			paramsObj.put("latitude", gps.latitude);
			paramsObj.put("longitude", gps.longitude);
			paramsObj.put("store_id", store.webStoreID);
			paramsObj.put("employee_id", emp.ID);
			paramsObj.put("local_record_id", in.ID);
			paramsObj.put("sync_batch_id", in.syncBatchID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(url, paramsObj, TIMEOUT);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(responseObj.isNull("error")) {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					String status = initObj.getString("status");
					String message = initObj.getString("message");
					if(status.equals("ok")) {
						result = TarkieLib.updateStatusSync(db, TB.TIME_IN, in.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(message, params, response, true);
						}
						return false;
					}
				}
			}
			else {
				JSONObject errorObj = responseObj.getJSONObject("error");
				String message = errorObj.getString("message");
				if(errorCallback != null) {
					errorCallback.onError(message, params, response, true);
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean syncTimeOut(SQLiteAdapter db, TimeOutObj out, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "time-out";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			GpsObj gps = out.gps;
			TimeInObj in = out.timeIn;
			EmployeeObj emp = out.emp;
			paramsObj.put("api_key", apiKey);
			paramsObj.put("date_out", out.dDate);
			paramsObj.put("time_out", out.dTime);
			paramsObj.put("gps_date", gps.date);
			paramsObj.put("gps_time", gps.time);
			paramsObj.put("latitude", gps.latitude);
			paramsObj.put("longitude", gps.longitude);
			paramsObj.put("employee_id", emp.ID);
			paramsObj.put("local_record_id", out.ID);
			paramsObj.put("sync_batch_id", out.syncBatchID);
			paramsObj.put("local_record_id_in", in.ID);
			paramsObj.put("sync_batch_id_in", in.syncBatchID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(url, paramsObj, TIMEOUT);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(responseObj.isNull("error")) {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					String status = initObj.getString("status");
					String message = initObj.getString("message");
					if(status.equals("ok")) {
						result = TarkieLib.updateStatusSync(db, TB.TIME_OUT, out.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(message, params, response, true);
						}
						return false;
					}
				}
			}
			else {
				JSONObject errorObj = responseObj.getJSONObject("error");
				String message = errorObj.getString("message");
				if(errorCallback != null) {
					errorCallback.onError(message, params, response, true);
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean syncBreakIn(SQLiteAdapter db, BreakInObj in, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "break-in";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			EmployeeObj emp = in.emp;
			paramsObj.put("api_key", apiKey);
			paramsObj.put("date_in", in.dDate);
			paramsObj.put("time_in", in.dTime);
			paramsObj.put("gps_date", in.dDate);
			paramsObj.put("gps_time", in.gps.time);
			paramsObj.put("latitude", in.gps.latitude);
			paramsObj.put("longitude", in.gps.longitude);
			paramsObj.put("employee_id", emp.ID);
			paramsObj.put("local_record_id", in.ID);
			paramsObj.put("sync_batch_id", in.syncBatchID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(url, paramsObj, TIMEOUT);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(responseObj.isNull("error")) {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					String status = initObj.getString("status");
					String message = initObj.getString("message");
					if(status.equals("ok")) {
						result = TarkieLib.updateStatusSync(db, TB.BREAK_IN, in.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(message, params, response, true);
						}
						return false;
					}
				}
			}
			else {
				JSONObject errorObj = responseObj.getJSONObject("error");
				String message = errorObj.getString("message");
				if(errorCallback != null) {
					errorCallback.onError(message, params, response, true);
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean syncBreakOut(SQLiteAdapter db, BreakOutObj out, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "break-out";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			BreakInObj in = out.breakIn;
			EmployeeObj emp = out.emp;
			paramsObj.put("api_key", apiKey);
			paramsObj.put("date_out", out.dDate);
			paramsObj.put("time_out", out.dTime);
			paramsObj.put("gps_date", out.gps.date);
			paramsObj.put("gps_time", out.gps.time);
			paramsObj.put("latitude", out.gps.latitude);
			paramsObj.put("longitude", out.gps.longitude);
			paramsObj.put("employee_id", emp.ID);
			paramsObj.put("local_record_id", out.ID);
			paramsObj.put("sync_batch_id", out.syncBatchID);
			paramsObj.put("local_record_id_in", in.ID);
			paramsObj.put("sync_batch_id_in", in.syncBatchID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(url, paramsObj, TIMEOUT);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(responseObj.isNull("error")) {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					String status = initObj.getString("status");
					String message = initObj.getString("message");
					if(status.equals("ok")) {
						result = TarkieLib.updateStatusSync(db, TB.BREAK_OUT, out.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(message, params, response, true);
						}
						return false;
					}
				}
			}
			else {
				JSONObject errorObj = responseObj.getJSONObject("error");
				String message = errorObj.getString("message");
				if(errorCallback != null) {
					errorCallback.onError(message, params, response, true);
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean syncIncidentReport(SQLiteAdapter db, IncidentReportObj ir, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "add-alert";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			EmployeeObj emp = ir.emp;
			IncidentObj incident = ir.incident;
			paramsObj.put("api_key", apiKey);
			paramsObj.put("date", ir.dDate);
			paramsObj.put("time", ir.dTime);
			paramsObj.put("gps_date", ir.gps.date);
			paramsObj.put("gps_time", ir.gps.time);
			paramsObj.put("latitude", ir.gps.latitude);
			paramsObj.put("longitude", ir.gps.longitude);
			paramsObj.put("employee_id", emp.ID);
			paramsObj.put("local_record_id", ir.ID);
			paramsObj.put("sync_batch_id", ir.syncBatchID);
			paramsObj.put("alert_type_id", incident.ID);
			paramsObj.put("value", ir.value);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(url, paramsObj, TIMEOUT);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(responseObj.isNull("error")) {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					String status = initObj.getString("status");
					String message = initObj.getString("message");
					if(status.equals("ok")) {
						result = TarkieLib.updateStatusSync(db, TB.INCIDENT_REPORT, ir.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(message, params, response, true);
						}
						return false;
					}
				}
			}
			else {
				JSONObject errorObj = responseObj.getJSONObject("error");
				String message = errorObj.getString("message");
				if(errorCallback != null) {
					errorCallback.onError(message, params, response, true);
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean uploadTimeInPhoto(SQLiteAdapter db, PhotoObj photo, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		String action = "upload-time-in-photo";
		String url = App.WEB_FILES + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("local_record_id", photo.ID);
			paramsObj.put("sync_batch_id", photo.syncBatchID);
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() +
					"/" + photo.fileName;
			File file = new File(path);
			if(!file.exists() || file.isDirectory()) {
				return TarkieLib.updateStatusPhotoUpload(db, TB.TIME_IN, photo.ID);
			}
			response = CodePanUtils.uploadFile(url, params, "image", "image/png", file);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(responseObj.isNull("error")) {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					String status = initObj.getString("status");
					String message = initObj.getString("message");
					if(status.equals("ok")) {
						result = TarkieLib.updateStatusPhotoUpload(db, TB.TIME_IN, photo.ID);
						if(result) {
							CodePanUtils.deleteFile(db.getContext(), App.FOLDER, photo.fileName);
						}
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(message, params, response, true);
						}
						return false;
					}
				}
			}
			else {
				JSONObject errorObj = responseObj.getJSONObject("error");
				String message = errorObj.getString("message");
				if(errorCallback != null) {
					errorCallback.onError(message, params, response, true);
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean uploadTimeOutPhoto(SQLiteAdapter db, PhotoObj photo, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		String action = "upload-time-out-photo";
		String url = App.WEB_FILES + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("local_record_id", photo.ID);
			paramsObj.put("sync_batch_id", photo.syncBatchID);
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() +
					"/" + photo.fileName;
			File file = new File(path);
			if(!file.exists() || file.isDirectory()) {
				return TarkieLib.updateStatusPhotoUpload(db, TB.TIME_OUT, photo.ID);
			}
			response = CodePanUtils.uploadFile(url, params, "image", "image/png", file);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(responseObj.isNull("error")) {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					String status = initObj.getString("status");
					String message = initObj.getString("message");
					if(status.equals("ok")) {
						result = TarkieLib.updateStatusPhotoUpload(db, TB.TIME_OUT, photo.ID);
						if(result) {
							CodePanUtils.deleteFile(db.getContext(), App.FOLDER, photo.fileName);
						}
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(message, params, response, true);
						}
						return false;
					}
				}
			}
			else {
				JSONObject errorObj = responseObj.getJSONObject("error");
				String message = errorObj.getString("message");
				if(errorCallback != null) {
					errorCallback.onError(message, params, response, true);
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean uploadSignature(SQLiteAdapter db, PhotoObj photo, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		String action = "upload-signature-photo";
		String url = App.WEB_FILES + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("local_record_id", photo.ID);
			paramsObj.put("sync_batch_id", photo.syncBatchID);
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() +
					"/" + photo.fileName;
			File file = new File(path);
			if(!file.exists() || file.isDirectory()) {
				return TarkieLib.updateStatusSignatureUpload(db, TB.TIME_OUT, photo.ID);
			}
			response = CodePanUtils.uploadFile(url, params, "image", "image/png", file);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(responseObj.isNull("error")) {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					String status = initObj.getString("status");
					String message = initObj.getString("message");
					if(status.equals("ok")) {
						result = TarkieLib.updateStatusSignatureUpload(db, TB.TIME_OUT, photo.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(message, params, response, true);
						}
						return false;
					}
				}
			}
			else {
				JSONObject errorObj = responseObj.getJSONObject("error");
				String message = errorObj.getString("message");
				if(errorCallback != null) {
					errorCallback.onError(message, params, response, true);
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean uploadSendBackUp(SQLiteAdapter db, String fileName, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		String phpFile = "backup.php";
		String url = "https://www.tarkie.com/API/2.3/" + phpFile;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			//String apiKey = TarkieLib.getAPIKey(db);
			String empID = TarkieLib.getEmployeeID(db);
			String apiKey = "75TvNCip314ts6l1Q1N9i2F3BcRWr090y31W54G279UxaoQx5Z";
			paramsObj.put("action", "upload-backup");
			paramsObj.put("api_key", apiKey);
			paramsObj.put("user_id", empID);
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() +
					"/" + fileName;
			File file = new File(path);
			response = CodePanUtils.uploadFile(url, params, "backup", "application/zip", file);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(responseObj.isNull("error")) {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					String status = initObj.getString("status");
					String message = initObj.getString("message");
					if(status.equals("ok")) {
						result = true;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(message, params, response, true);
						}
						return false;
					}
				}
			}
			else {
				JSONObject errorObj = responseObj.getJSONObject("error");
				String message = errorObj.getString("message");
				if(errorCallback != null) {
					errorCallback.onError(message, params, response, true);
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean syncEntry(SQLiteAdapter db, EntryObj entry, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "add-form-answers";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			String empID = TarkieLib.getEmployeeID(db);
			String groupID = TarkieLib.getGroupID(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("employee_id", empID);
			paramsObj.put("team_id", groupID);
			paramsObj.put("form_id", entry.form.ID);
			paramsObj.put("reference_number", entry.referenceNo);
			paramsObj.put("date_created", entry.dDate);
			paramsObj.put("time_created", entry.dTime);
			paramsObj.put("date_submitted", entry.dateSubmitted);
			paramsObj.put("time_submitted", entry.timeSubmitted);
			paramsObj.put("local_record_id", entry.ID);
			paramsObj.put("sync_batch_id", entry.syncBatchID);
			JSONArray detailsArray = new JSONArray();
			FormObj form = entry.form;
			for(FieldObj field : form.fieldList) {
				JSONObject detailsObj = new JSONObject();
				AnswerObj answer = field.answer;
				detailsObj.put("field_id", field.ID);
				detailsObj.put("field_type", field.type);
				detailsObj.put("answer", answer.value);
				detailsObj.put("local_record_id", answer.ID);
				detailsObj.put("sync_batch_id", answer.syncBatchID);
				detailsArray.put(detailsObj);
			}
			paramsObj.put("question_answers", detailsArray);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(url, paramsObj, TIMEOUT);
			Log.e("syncEntry PARAMS", params);
			Log.e("syncEntry RESPONSE", response);
			JSONObject responseObj = new JSONObject(response);
			if(responseObj.isNull("error")) {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					String status = initObj.getString("status");
					String message = initObj.getString("message");
					int recNo = initObj.getInt("recno");
					if(status.equals("ok")) {
						hasData = recNo > 0;
						result = recNo == 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(message, params, response, true);
						}
						return false;
					}
				}
			}
			else {
				JSONObject errorObj = responseObj.getJSONObject("error");
				String message = errorObj.getString("message");
				if(errorCallback != null) {
					errorCallback.onError(message, params, response, true);
				}
			}
			if(hasData) {
				SQLiteBinder binder = new SQLiteBinder(db);
				JSONArray dataArray = responseObj.getJSONArray("data");
				for(int i = 0; i < dataArray.length(); i++) {
					try {
						SQLiteQuery query = new SQLiteQuery();
						JSONObject dataObj = dataArray.getJSONObject(i);
						query.add(new FieldValue("webEntryID", dataObj.getString("form_answer_id")));
						if(!entry.isFromWeb) {
							query.add(new FieldValue("referenceNo", dataObj.getString("reference_number")));
						}
						query.add(new FieldValue("isSync", true));
						binder.update(Tables.getName(TB.ENTRIES), query, entry.ID);
					}
					catch(Exception e) {
						e.printStackTrace();
						if(errorCallback != null) {
							errorCallback.onError(e.getMessage(), params, response, false);
						}
						return false;
					}
				}
				result = binder.finish();
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean uploadEntryPhoto(SQLiteAdapter db, PhotoObj photo, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		String action = "upload-form-photo";
		String url = App.WEB_FILES + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			String empID = TarkieLib.getEmployeeID(db);
			String groupID = TarkieLib.getGroupID(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("employee_id", empID);
			paramsObj.put("team_id", groupID);
			paramsObj.put("local_record_id", photo.ID);
			paramsObj.put("sync_batch_id", photo.syncBatchID);
			paramsObj.put("is_signature", photo.isSignature ? 1 : 0);
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() +
					"/" + photo.fileName;
			String table = Tables.getName(TB.PHOTO);
			File file = new File(path);
			if(!file.exists() || file.isDirectory()) {
				return TarkieLib.updateStatusUpload(db, TB.PHOTO, photo.ID);
			}
			response = CodePanUtils.uploadFile(url, params, "image", "image/png", file);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(responseObj.isNull("error")) {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					String status = initObj.getString("status");
					String message = initObj.getString("message");
					int recNo = initObj.getInt("recno");
					if(status.equals("ok")) {
						hasData = recNo > 0;
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(message, params, response, true);
						}
						return false;
					}
				}
			}
			else {
				JSONObject errorObj = responseObj.getJSONObject("error");
				String message = errorObj.getString("message");
				if(errorCallback != null) {
					errorCallback.onError(message, params, response, true);
				}
			}
			if(hasData) {
				SQLiteBinder binder = new SQLiteBinder(db);
				JSONArray dataArray = responseObj.getJSONArray("data");
				for(int i = 0; i < dataArray.length(); i++) {
					try {
						SQLiteQuery query = new SQLiteQuery();
						JSONObject dataObj = dataArray.getJSONObject(i);
						query.add(new FieldValue("webPhotoID", dataObj.getString("photo_id")));
						query.add(new FieldValue("isUpload", true));
						binder.update(table, query, photo.ID);
					}
					catch(Exception e) {
						e.printStackTrace();
						if(errorCallback != null) {
							errorCallback.onError(e.getMessage(), params, response, false);
						}
						return false;
					}
				}
				result = binder.finish();
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}

	public static boolean updateTask(SQLiteAdapter db, TaskObj task, OnErrorCallback errorCallback) {
		boolean result = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "edit-itinerary";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			StoreObj store = task.store;
			EmployeeObj emp = task.emp;
			paramsObj.put("api_key", apiKey);
			paramsObj.put("itinerary_id", task.webTaskID);
			paramsObj.put("store_id", store.webStoreID);
			paramsObj.put("employee_id", emp.ID);
			paramsObj.put("start_date", task.startDate);
			paramsObj.put("end_date", task.endDate);
			paramsObj.put("notes", task.notes);
			JSONArray formArray = new JSONArray();
			for(FormObj form : task.formList) {
				formArray.put(form.ID);
			}
			paramsObj.put("forms", formArray);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpPost(url, paramsObj, TIMEOUT);
			CodePanUtils.logHttpRequest(params, response);
			JSONObject responseObj = new JSONObject(response);
			if(responseObj.isNull("error")) {
				JSONArray initArray = responseObj.getJSONArray("init");
				for(int i = 0; i < initArray.length(); i++) {
					JSONObject initObj = initArray.getJSONObject(i);
					String status = initObj.getString("status");
					String message = initObj.getString("message");
					if(status.equals("ok")) {
						result = TarkieLib.updateStatusWebUpdate(db, TB.TASK, task.ID);
					}
					else {
						if(errorCallback != null) {
							errorCallback.onError(message, params, response, true);
						}
						return false;
					}
				}
			}
			else {
				JSONObject errorObj = responseObj.getJSONObject("error");
				String message = errorObj.getString("message");
				if(errorCallback != null) {
					errorCallback.onError(message, params, response, true);
				}
			}
		}
		catch(JSONException je) {
			je.printStackTrace();
			if(errorCallback != null) {
				errorCallback.onError(je.getMessage(), params, response, false);
			}
		}
		return result;
	}
}
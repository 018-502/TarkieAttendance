package com.mobileoptima.core;

import android.content.Context;

import com.codepan.callback.Interface.OnErrorCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.constant.App;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreakOutObj;
import com.mobileoptima.model.ImageObj;
import com.mobileoptima.model.IncidentReportObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;
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
			paramsObj.put("api_key", apiKey);
			paramsObj.put("date_in", in.dDate);
			paramsObj.put("time_in", in.dTime);
			paramsObj.put("gps_date", in.gpsDate);
			paramsObj.put("gps_time", in.gpsTime);
			paramsObj.put("latitude", in.gpsLatitude);
			paramsObj.put("longitude", in.gpsLongitude);
			paramsObj.put("store_id", in.storeID);
			paramsObj.put("employee_id", in.empID);
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
			String syncBatchID = TarkieLib.getSyncBatchID(db, TB.TIME_IN, out.timeInID);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("date_out", out.dDate);
			paramsObj.put("time_out", out.dTime);
			paramsObj.put("gps_date", out.gpsDate);
			paramsObj.put("gps_time", out.gpsTime);
			paramsObj.put("latitude", out.gpsLatitude);
			paramsObj.put("longitude", out.gpsLongitude);
			paramsObj.put("employee_id", out.empID);
			paramsObj.put("local_record_id", out.ID);
			paramsObj.put("sync_batch_id", out.syncBatchID);
			paramsObj.put("local_record_id_in", out.timeInID);
			paramsObj.put("sync_batch_id_in", syncBatchID);
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
			paramsObj.put("api_key", apiKey);
			paramsObj.put("date_in", in.dDate);
			paramsObj.put("time_in", in.dTime);
			paramsObj.put("gps_date", in.gpsDate);
			paramsObj.put("gps_time", in.gpsTime);
			paramsObj.put("latitude", in.gpsLatitude);
			paramsObj.put("longitude", in.gpsLongitude);
			paramsObj.put("employee_id", in.empID);
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
			String syncBatchID = TarkieLib.getSyncBatchID(db, TB.BREAK_IN, out.breakInID);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("date_out", out.dDate);
			paramsObj.put("time_out", out.dTime);
			paramsObj.put("gps_date", out.gpsDate);
			paramsObj.put("gps_time", out.gpsTime);
			paramsObj.put("latitude", out.gpsLatitude);
			paramsObj.put("longitude", out.gpsLongitude);
			paramsObj.put("employee_id", out.empID);
			paramsObj.put("local_record_id", out.ID);
			paramsObj.put("sync_batch_id", out.syncBatchID);
			paramsObj.put("local_record_id_in", out.breakInID);
			paramsObj.put("sync_batch_id_in", syncBatchID);
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
			paramsObj.put("api_key", apiKey);
			paramsObj.put("date", ir.dDate);
			paramsObj.put("time", ir.dTime);
			paramsObj.put("gps_date", ir.gpsDate);
			paramsObj.put("gps_time", ir.gpsTime);
			paramsObj.put("latitude", ir.gpsLatitude);
			paramsObj.put("longitude", ir.gpsLongitude);
			paramsObj.put("employee_id", ir.empID);
			paramsObj.put("local_record_id", ir.ID);
			paramsObj.put("sync_batch_id", ir.syncBatchID);
			paramsObj.put("alert_type_id", ir.incidentID);
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

	public static boolean uploadTimeInPhoto(SQLiteAdapter db, ImageObj image, OnErrorCallback errorCallback) {
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
			paramsObj.put("local_record_id", image.ID);
			paramsObj.put("sync_batch_id", image.syncBatchID);
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() +
					"/" + image.fileName;
			File file = new File(path);
			if(!file.exists() || file.isDirectory()) {
				return TarkieLib.updateStatusPhotoUpload(db, TB.TIME_IN, image.ID);
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
						result = TarkieLib.updateStatusPhotoUpload(db, TB.TIME_IN, image.ID);
						if(result) {
							CodePanUtils.deleteFile(db.getContext(), App.FOLDER, image.fileName);
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

	public static boolean uploadTimeOutPhoto(SQLiteAdapter db, ImageObj image, OnErrorCallback errorCallback) {
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
			paramsObj.put("local_record_id", image.ID);
			paramsObj.put("sync_batch_id", image.syncBatchID);
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() +
					"/" + image.fileName;
			File file = new File(path);
			if(!file.exists() || file.isDirectory()) {
				return TarkieLib.updateStatusPhotoUpload(db, TB.TIME_OUT, image.ID);
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
						result = TarkieLib.updateStatusPhotoUpload(db, TB.TIME_OUT, image.ID);
						if(result) {
							CodePanUtils.deleteFile(db.getContext(), App.FOLDER, image.fileName);
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

	public static boolean uploadSignature(SQLiteAdapter db, ImageObj image, OnErrorCallback errorCallback) {
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
			paramsObj.put("local_record_id", image.ID);
			paramsObj.put("sync_batch_id", image.syncBatchID);
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() +
					"/" + image.fileName;
			File file = new File(path);
			if(!file.exists() || file.isDirectory()) {
				return TarkieLib.updateStatusSignatureUpload(db, TB.TIME_OUT, image.ID);
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
						result = TarkieLib.updateStatusSignatureUpload(db, TB.TIME_OUT, image.ID);
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
		String action = "backup.php";
		String url = "https://www.tarkie.com/API/2.3/" + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			String empID = TarkieLib.getEmployeeID(db);
			apiKey = "75TvNCip314ts6l1Q1N9i2F3BcRWr090y31W54G279UxaoQx5Z";
			paramsObj.put("action", "upload-backup");
			paramsObj.put("api_key", apiKey);
			paramsObj.put("user_id", empID);
			params = paramsObj.toString(INDENT);
			String path = db.getContext().getDir(App.FOLDER, Context.MODE_PRIVATE).getPath() +
					"/" + fileName;
			File file = new File(path);
//			if(!file.exists() || file.isDirectory()) {
//				return TarkieLib.updateStatusSignatureUpload(db, TB.TIME_OUT, image.ID);
//			}
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
//						result = TarkieLib.updateStatusSignatureUpload(db, TB.TIME_OUT, image.ID);
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
}
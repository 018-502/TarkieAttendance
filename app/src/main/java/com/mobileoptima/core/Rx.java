package com.mobileoptima.core;

import com.codepan.callback.Interface.OnErrorCallback;
import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.codepan.database.SQLiteQuery;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.constant.App;
import com.mobileoptima.schema.Tables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Rx {

	public static boolean authorizeDevice(SQLiteAdapter db, String authorizationCode, String deviceID,
										  OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "authorization-request";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			paramsObj.put("tablet_id", deviceID);
			paramsObj.put("authorization_code", authorizationCode);
			paramsObj.put("api_key", App.API_KEY);
			paramsObj.put("device_type", App.OS_TYPE);
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
				String table = Tables.getName(Tables.TB.API_KEY);
				try {
					SQLiteQuery query = new SQLiteQuery();
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String apiKey = dataObj.getString("api_key");
						query.clearAll();
						query.add(new FieldValue("apiKey", apiKey));
						query.add(new FieldValue("authorizationCode", authorizationCode));
						query.add(new FieldValue("deviceID", deviceID));
						String sql = "SELECT ID FROM " + table + " WHERE ID = 1";
						if(!db.isRecordExists(sql)) {
							binder.insert(table, query);
						}
						else {
							binder.update(table, query, 1);
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
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

	public static boolean getSyncBatchID(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-sync-batch-id";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(url, paramsObj, TIMEOUT);
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
				String table = Tables.getName(Tables.TB.SYNC_BATCH);
				try {
					SQLiteQuery query = new SQLiteQuery();
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String sql = "SELECT ID FROM " + table + " WHERE ID = 1";
						query.clearAll();
						query.add(new FieldValue("syncBatchID", dataObj.getString("sync_batch_id")));
						if(!db.isRecordExists(sql)) {
							binder.insert(table, query);
						}
						else {
							binder.update(table, query, 1);
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
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

	public static boolean getCompany(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-company";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(url, paramsObj, TIMEOUT);
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
				String table = Tables.getName(Tables.TB.COMPANY);
				try {
					SQLiteQuery query = new SQLiteQuery();
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String coID = dataObj.getString("company_id");
						String company = CodePanUtils.handleUniCode(dataObj.getString("company_name"));
						String address = CodePanUtils.handleUniCode(dataObj.getString("address"));
						query.clearAll();
						query.add(new FieldValue("ID", coID));
						query.add(new FieldValue("name", company));
						query.add(new FieldValue("address", address));
						query.add(new FieldValue("code", dataObj.getString("company_code")));
						query.add(new FieldValue("mobile", dataObj.getString("mobile")));
						query.add(new FieldValue("landline", dataObj.getString("landline")));
						query.add(new FieldValue("expirationDate", dataObj.getString("expiration_date")));
						String sql = "SELECT ID FROM " + table + " WHERE ID = '" + coID + "'";
						if(!db.isRecordExists(sql)) {
							binder.insert(table, query);
						}
						else {
							binder.update(table, query, coID);
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
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

	public static boolean getIncidents(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-alert-types";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(url, paramsObj, TIMEOUT);
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
				String table = Tables.getName(Tables.TB.INCIDENT);
				try {
					SQLiteQuery query = new SQLiteQuery();
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String incidentID = dataObj.getString("alert_type_id");
						String name = CodePanUtils.handleUniCode(dataObj.getString("alert_type"));
						query.clearAll();
						query.add(new FieldValue("ID", incidentID));
						query.add(new FieldValue("name", name));
						String sql = "SELECT ID FROM " + table + " WHERE ID = '" + incidentID + "'";
						if(!db.isRecordExists(sql)) {
							binder.insert(table, query);
						}
						else {
							binder.update(table, query, incidentID);
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
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

	public static boolean login(SQLiteAdapter db, String username, String password,
								OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "login";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("employee_number", username);
			paramsObj.put("password", password);
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
				String table = Tables.getName(Tables.TB.CREDENTIALS);
				try {
					SQLiteQuery query = new SQLiteQuery();
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String empID = dataObj.getString("employee_id");
						query.clearAll();
						query.add(new FieldValue("dDate", CodePanUtils.getDate()));
						query.add(new FieldValue("dTime", CodePanUtils.getTime()));
						query.add(new FieldValue("isLogOut", false));
						query.add(new FieldValue("empID", empID));
						String sql = "SELECT ID FROM " + table + " WHERE ID = 1";
						if(!db.isRecordExists(sql)) {
							binder.insert(table, query);
						}
						else {
							binder.update(table, query, 1);
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
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

	public static boolean getEmployees(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-employees";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(url, paramsObj, TIMEOUT);
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
				String table = Tables.getName(Tables.TB.EMPLOYEE);
				try {
					SQLiteQuery query = new SQLiteQuery();
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String empID = dataObj.getString("employee_id");
						String firstName = CodePanUtils.handleUniCode(dataObj.getString("firstname"));
						String lastName = CodePanUtils.handleUniCode(dataObj.getString("lastname"));
						String employeeNo = CodePanUtils.handleUniCode(dataObj.getString("employee_number"));
						//String imageUrl = CodePanUtils.handleUniCode(dataObj.getString("profile_picture"));
						//CodePanUtils.clearImageUrl(db.getContext(), imageUrl);
						query.clearAll();
						query.add(new FieldValue("ID", empID));
						query.add(new FieldValue("firstName", firstName));
						query.add(new FieldValue("lastName", lastName));
						query.add(new FieldValue("employeeNo", employeeNo));
						query.add(new FieldValue("groupID", dataObj.getString("team_id")));
						query.add(new FieldValue("mobile", dataObj.getString("mobile")));
						query.add(new FieldValue("email", dataObj.getString("email")));
						query.add(new FieldValue("isActive", dataObj.getString("is_active").equals("yes")));
						//query.add(new FieldValue("imageUrl", imageUrl));
						String sql = "SELECT ID FROM " + table + " WHERE ID = '" + empID + "'";
						if(!db.isRecordExists(sql)) {
							binder.insert(table, query);
						}
						else {
							binder.update(table, query, empID);
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
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

	public static boolean getServerTime(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-server-time";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(url, paramsObj, TIMEOUT);
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
				try {
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String array[] = dataObj.getString("date_time").split(" ");
						String date = CodePanUtils.formatDate(array[0]);
						String time = CodePanUtils.formatTime(array[1]);
						long timestamp = dataObj.getLong("timestamp") * 1000;
						result = TimeSecurity.updateServerTime(db, date, time, timestamp);
					}
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
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

	public static boolean getBreaks(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-breaks";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(url, paramsObj, TIMEOUT);
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
				String table = Tables.getName(Tables.TB.BREAK);
				binder.truncate(table);
				try {
					SQLiteQuery query = new SQLiteQuery();
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String name = CodePanUtils.handleUniCode(dataObj.getString("break_name"));
						query.clearAll();
						query.add(new FieldValue("ID", dataObj.getString("break_id")));
						query.add(new FieldValue("duration", dataObj.getInt("duration")));
						query.add(new FieldValue("name", name));
						binder.insert(table, query);
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
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

	public static boolean getStores(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-stores";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(url, paramsObj, TIMEOUT);
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
				String table = Tables.getName(Tables.TB.STORES);
				try {
					SQLiteQuery query = new SQLiteQuery();
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String storeID = dataObj.getString("store_id");
						String name = CodePanUtils.handleUniCode(dataObj.getString("store_name"));
						String address = CodePanUtils.handleUniCode(dataObj.getString("address"));
						query.clearAll();
						query.add(new FieldValue("ID", storeID));
						query.add(new FieldValue("name", name));
						query.add(new FieldValue("address", address));
						query.add(new FieldValue("gpsLongitude", dataObj.getDouble("longitude")));
						query.add(new FieldValue("gpsLatitude", dataObj.getDouble("latitude")));
						query.add(new FieldValue("radius", dataObj.getInt("geo_fence_radius")));
						String sql = "SELECT ID FROM " + table + " WHERE ID = '" + storeID + "'";
						if(!db.isRecordExists(sql)) {
							binder.insert(table, query);
						}
						else {
							binder.update(table, query, storeID);
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
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

	public static boolean getForms(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-forms";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			String groupID = TarkieLib.getGroupID(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("team_id", groupID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(url, paramsObj, TIMEOUT);
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
				String table = Tables.getName(Tables.TB.FORMS);
				try {
					SQLiteQuery query = new SQLiteQuery();
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String formID = dataObj.getString("form_id");
						String name = CodePanUtils.handleUniCode(dataObj.getString("form_name"));
						String description = CodePanUtils.handleUniCode(dataObj.getString("form_description"));
						String logoUrl = dataObj.getString("form_logo");
						CodePanUtils.clearImageUrl(db.getContext(), logoUrl);
						query.clearAll();
						query.add(new FieldValue("ID", formID));
						query.add(new FieldValue("name", name));
						query.add(new FieldValue("groupID", groupID));
						query.add(new FieldValue("description", description));
						query.add(new FieldValue("dateCreated", dataObj.getString("form_date_created")));
						query.add(new FieldValue("timeCreated", dataObj.getString("form_time_created")));
						query.add(new FieldValue("isActive", dataObj.getString("form_is_active")));
						query.add(new FieldValue("logoUrl", logoUrl));
						String sql = "SELECT ID FROM " + table + " WHERE ID = '" + formID + "'";
						if(!db.isRecordExists(sql)) {
							binder.insert(table, query);
						}
						else {
							binder.update(table, query, formID);
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
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

	public static boolean getFields(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-form-fields";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			String groupID = TarkieLib.getGroupID(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("team_id", groupID);
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(url, paramsObj, TIMEOUT);
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
				try {
					SQLiteQuery query = new SQLiteQuery();
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String fieldID = dataObj.getString("field_id");
						String type = dataObj.getString("field_type");
						String name = CodePanUtils.handleUniCode(dataObj.getString("field_name"));
						String uniCode = CodePanUtils.handleUniCode(dataObj.getString("field_description"));
						String description = CodePanUtils.handleHTMLEntities(uniCode, false);
						query.clearAll();
						query.add(new FieldValue("ID", fieldID));
						query.add(new FieldValue("name", name));
						query.add(new FieldValue("type", type));
						query.add(new FieldValue("description", description));
						query.add(new FieldValue("formID", dataObj.getString("field_form_id")));
						query.add(new FieldValue("orderNo", dataObj.getString("field_order_number")));
						query.add(new FieldValue("isRequired", dataObj.getInt("field_is_required")));
						query.add(new FieldValue("isActive", dataObj.getInt("field_is_active")));
						String table = Tables.getName(Tables.TB.FIELDS);
						String sql = "SELECT ID FROM " + table + " WHERE ID = '" + fieldID + "'";
						if(!db.isRecordExists(sql)) {
							binder.insert(table, query);
						}
						else {
							binder.update(table, query, fieldID);
						}
						if(!dataObj.isNull("field_choices")) {
							JSONArray choicesArray = dataObj.getJSONArray("field_choices");
							for(int c = 0; c < choicesArray.length(); c++) {
								JSONObject choicesObj = choicesArray.getJSONObject(c);
								String code = choicesObj.getString("field_choice_id");
								String choice = CodePanUtils.handleUniCode(choicesObj.getString("field_choice_name"));
								query.clearAll();
								query.add(new FieldValue("code", code));
								query.add(new FieldValue("name", choice));
								query.add(new FieldValue("fieldID", fieldID));
								table = Tables.getName(Tables.TB.CHOICES);
								sql = "SELECT ID FROM " + table + " WHERE code = '" + code + "'";
								if(!db.isRecordExists(sql)) {
									binder.insert(table, query);
								}
								else {
									String choiceID = db.getString(sql);
									binder.update(table, query, choiceID);
								}
							}
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
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

	public static boolean getEntries(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-enter-data";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			String employeeID = TarkieLib.getEmployeeID(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("employee_id", employeeID);
			paramsObj.put("get_details", "yes");
			params = paramsObj.toString(INDENT);
			response = CodePanUtils.doHttpGet(url, paramsObj, TIMEOUT);
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
				try {
					SQLiteQuery query = new SQLiteQuery();
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String webEntryID = dataObj.getString("enterdata_id");
						query.clearAll();
						query.add(new FieldValue("empID", employeeID));
						query.add(new FieldValue("formID", dataObj.getString("form_id")));
						query.add(new FieldValue("dDate", dataObj.getString("date_created")));
						query.add(new FieldValue("dTime", dataObj.getString("time_created")));
						query.add(new FieldValue("referenceNo", dataObj.getString("reference_number")));
						query.add(new FieldValue("webEntryID", webEntryID));
						query.add(new FieldValue("isFromWeb", true));
						String table = Tables.getName(Tables.TB.ENTRIES);
						String sql = "SELECT ID FROM " + table + " WHERE webEntryID = '" + webEntryID + "'";
						String entryID;
						if(!db.isRecordExists(sql)) {
							entryID = binder.insert(table, query);
						}
						else {
							entryID = db.getString(sql);
							binder.update(table, query, entryID);
						}
						if(!dataObj.isNull("enterdata_details")) {
							JSONArray detailsArray = dataObj.getJSONArray("enterdata_details");
							for(int c = 0; c < detailsArray.length(); c++) {
								JSONObject detailsObj = detailsArray.getJSONObject(c);
								String fieldID = detailsObj.getString("field_id");
								String value = CodePanUtils.handleUniCode(detailsObj.getString("field_data_value"));
								query.clearAll();
								query.add(new FieldValue("value", value));
								query.add(new FieldValue("entryID", entryID));
								query.add(new FieldValue("fieldID", fieldID));
								table = Tables.getName(Tables.TB.ANSWERS);
								sql = "SELECT ID FROM " + table + " WHERE isUpdate = 0 AND fieldID = '" + fieldID + "' " +
										"AND entryID = '" + entryID + "'";
								if(!db.isRecordExists(sql)) {
									binder.insert(table, query);
								}
								else {
									String answerID = db.getString(sql);
									binder.update(table, query, answerID);
								}
							}
						}
					}
					result = binder.finish();
				}
				catch(JSONException je) {
					je.printStackTrace();
					if(errorCallback != null) {
						errorCallback.onError(je.getMessage(), params, response, false);
					}
					binder.finish();
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
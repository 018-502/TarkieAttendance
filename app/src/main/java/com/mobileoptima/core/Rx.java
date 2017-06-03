package com.mobileoptima.core;

import com.codepan.callback.Interface.OnErrorCallback;
import com.codepan.database.Condition;
import com.codepan.database.FieldValue;
import com.codepan.database.SQLiteAdapter;
import com.codepan.database.SQLiteBinder;
import com.codepan.database.SQLiteQuery;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.constant.App;
import com.mobileoptima.schema.Tables;

import org.apache.commons.lang3.StringEscapeUtils;
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
						String company = dataObj.getString("company_name");
						String address = dataObj.getString("address");
						String logoUrl = dataObj.getString("company_logo");
						CodePanUtils.clearImageUrl(db.getContext(), logoUrl);
						company = CodePanUtils.handleUniCode(company);
						address = CodePanUtils.handleUniCode(address);
						query.clearAll();
						query.add(new FieldValue("ID", coID));
						query.add(new FieldValue("name", company));
						query.add(new FieldValue("address", address));
						query.add(new FieldValue("logoUrl", logoUrl));
						query.add(new FieldValue("mobile", dataObj.getString("mobile")));
						query.add(new FieldValue("code", dataObj.getString("company_code")));
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
						String name = dataObj.getString("alert_type");
						name = CodePanUtils.handleUniCode(name);
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
						String firstName = dataObj.getString("firstname");
						String lastName = dataObj.getString("lastname");
						String employeeNo = dataObj.getString("employee_number");
						//String imageUrl = dataObj.getString("profile_picture");
						firstName = CodePanUtils.handleUniCode(firstName);
						lastName = CodePanUtils.handleUniCode(lastName);
						employeeNo = CodePanUtils.handleUniCode(employeeNo);
						//imageUrl = CodePanUtils.handleUniCode(imageUrl);
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
						String name = dataObj.getString("break_name");
						name = CodePanUtils.handleUniCode(name);
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

	public static boolean getConvention(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-naming-convention";
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
				String table = Tables.getName(Tables.TB.CONVENTION);
				binder.truncate(table);
				try {
					SQLiteQuery query = new SQLiteQuery();
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						JSONArray nameArray = dataObj.names();
						for(int n = 0; n < nameArray.length(); n++) {
							String name = nameArray.getString(n);
							query.clearAll();
							query.add(new FieldValue("name", name));
							query.add(new FieldValue("convention", dataObj.getString(name)));
							binder.insert(table, query);
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
						String name = dataObj.getString("store_name");
						String address = dataObj.getString("address");
						name = CodePanUtils.handleUniCode(name);
						address = CodePanUtils.handleUniCode(address);
						query.clearAll();
						query.add(new FieldValue("name", name));
						query.add(new FieldValue("address", address));
						query.add(new FieldValue("webStoreID", storeID));
						query.add(new FieldValue("gpsLongitude", dataObj.getDouble("longitude")));
						query.add(new FieldValue("gpsLatitude", dataObj.getDouble("latitude")));
						query.add(new FieldValue("radius", dataObj.getInt("geo_fence_radius")));
						String sql = "SELECT ID FROM " + table + " WHERE webStoreID = '" + storeID + "'";
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
						String name = dataObj.getString("form_name");
						String description = dataObj.getString("form_description");
						String category = dataObj.getString("form_type");
						String logoUrl = dataObj.getString("form_logo");
						name = CodePanUtils.handleUniCode(name);
						description = CodePanUtils.handleUniCode(description);
						category = CodePanUtils.handleUniCode(category);
						CodePanUtils.clearImageUrl(db.getContext(), logoUrl);
						query.clearAll();
						query.add(new FieldValue("ID", formID));
						query.add(new FieldValue("name", name));
						query.add(new FieldValue("groupID", groupID));
						query.add(new FieldValue("description", description));
						query.add(new FieldValue("category", category));
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
						String name = dataObj.getString("field_name");
						String description = dataObj.getString("field_description");
						name = CodePanUtils.handleUniCode(name);
						description = CodePanUtils.handleUniCode(description);
						description = CodePanUtils.handleHTMLEntities(description, false);
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
			String empID = TarkieLib.getEmployeeID(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("employee_id", empID);
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
						query.add(new FieldValue("empID", empID));
						query.add(new FieldValue("formID", dataObj.getString("form_id")));
						query.add(new FieldValue("dDate", dataObj.getString("date_created")));
						query.add(new FieldValue("dTime", dataObj.getString("time_created")));
						query.add(new FieldValue("referenceNo", dataObj.getString("reference_number")));
						query.add(new FieldValue("isFromWeb", true));
						String table = Tables.getName(Tables.TB.ENTRIES);
						String sql = "SELECT ID FROM " + table + " WHERE webEntryID = '" + webEntryID + "'";
						String entryID;
						if(!db.isRecordExists(sql)) {
							String syncBatchID = TarkieLib.getSyncBatchID(db);
							query.add(new FieldValue("webEntryID", webEntryID));
							query.add(new FieldValue("syncBatchID", syncBatchID));
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

	public static boolean getTasks(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-itinerary";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			String empID = TarkieLib.getEmployeeID(db);
			String start = CodePanUtils.getDate();
			String end = CodePanUtils.getDateAfter(start, 15);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("emp_id", empID);
			paramsObj.put("start_date", start);
			paramsObj.put("end_date", end);
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
				String t = Tables.getName(Tables.TB.TASK);
				String tf = Tables.getName(Tables.TB.TASK_FORM);
				try {
					SQLiteQuery query = new SQLiteQuery();
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String webTaskID = dataObj.getString("itinerary_id");
						String name = dataObj.getString("store_name");
						String notes = dataObj.getString("notes");
						notes = CodePanUtils.handleUniCode(notes);
						notes = CodePanUtils.handleHTMLEntities(notes, false);
						name = CodePanUtils.handleUniCode(name);
						String unescapeNotes = StringEscapeUtils.unescapeHtml4(notes);
						int notesLimit = 0;
						if(unescapeNotes != null) {
							notesLimit = unescapeNotes.replace("''", "'").length();
						}
						query.clearAll();
						query.add(new FieldValue("name", name));
						query.add(new FieldValue("notes", notes));
						query.add(new FieldValue("notesLimit", notesLimit));
						query.add(new FieldValue("empID", dataObj.getString("employee_id")));
						query.add(new FieldValue("storeID", dataObj.getString("store_id")));
						query.add(new FieldValue("startDate", dataObj.getString("start_date")));
						query.add(new FieldValue("endDate", dataObj.getString("end_date")));
						String sql = "SELECT ID FROM " + t + " WHERE webTaskID = '" + webTaskID + "'";
						String taskID = null;
						if(!db.isRecordExists(sql)) {
							query.add(new FieldValue("dateCreated", dataObj.getString("date_created")));
							query.add(new FieldValue("timeCreated", dataObj.getString("time_created")));
							query.add(new FieldValue("webTaskID", webTaskID));
							query.add(new FieldValue("isWebUpdate", true));
							query.add(new FieldValue("isFromWeb", true));
							query.add(new FieldValue("isUpdate", true));
							query.add(new FieldValue("isSync", true));
							taskID = binder.insert(t, query);
						}
						else {
							taskID = db.getString(sql);
							binder.update(t, query, taskID);
						}
						JSONArray formArray = dataObj.getJSONArray("forms");
						for(int f = 0; f < formArray.length(); f++) {
							JSONObject formObj = formArray.getJSONObject(f);
							String formID = formObj.getString("form_id");
							query.clearAll();
							query.add(new FieldValue("taskID", taskID));
							query.add(new FieldValue("formID", formID));
							sql = "SELECT ID FROM " + tf + " WHERE taskID = '" + taskID + "' AND " +
									"formID = '" + formID + "'";
							if(!db.isRecordExists(sql)) {
								query.add(new FieldValue("isFromWeb", true));
								binder.insert(tf, query);
							}
							else {
								String taskFormID = db.getString(sql);
								query.add(new FieldValue("isTag", true));
								binder.update(tf, query, taskFormID);
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

	public static boolean getExpenseTypeCategories(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-expense-categories";
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
				String table = Tables.getName(Tables.TB.EXPENSE_TYPE_CATEGORY);
				try {
					SQLiteQuery query = new SQLiteQuery();
					JSONArray dataArray = responseObj.getJSONArray("data");
					query.clearAll();
					query.add(new FieldValue("isActive", false));
					binder.update(table, query);
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String ID = dataObj.getString("expense_category_id");
						String name = dataObj.getString("expense_category_name");
						query.clearAll();
						query.add(new FieldValue("ID", ID));
						query.add(new FieldValue("name", name));
						String sql = "SELECT ID FROM " + table + " WHERE ID = " + ID;
						if(!db.isRecordExists(sql)) {
							binder.insert(table, query);
						}
						else {
							binder.update(table, query, ID);
						}
						result = getExpenseTypes(db, ID, errorCallback);
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

	public static boolean getExpenseTypes(SQLiteAdapter db, String categoryID, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-expense-types";
		String url = App.WEB_API + action;
		String response = null;
		String params = null;
		try {
			JSONObject paramsObj = new JSONObject();
			String apiKey = TarkieLib.getAPIKey(db);
			paramsObj.put("api_key", apiKey);
			paramsObj.put("expense_category_id", categoryID);
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
				String table = Tables.getName(Tables.TB.EXPENSE_TYPE);
				try {
					SQLiteQuery query = new SQLiteQuery();
					query.clearAll();
					query.add(new Condition("categoryID", categoryID));
					query.add(new FieldValue("isActive", false));
					binder.update(table, query);
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);


						String ID = dataObj.getString("expense_type_id");
						String name = dataObj.getString("expense_type_name");
						boolean isRequired = dataObj.getString("is_required").equals("yes");
						query.clearAll();
						query.add(new FieldValue("ID", ID));
						query.add(new FieldValue("name", name));
						query.add(new FieldValue("categoryID", categoryID));
						query.add(new FieldValue("isRequired", isRequired));
						String sql = "SELECT ID FROM " + table + " WHERE ID = " + ID;
						if(!db.isRecordExists(sql)) {
							binder.insert(table, query);
						}
						else {
							binder.update(table, query, ID);
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

	public static boolean getSettings(SQLiteAdapter db, OnErrorCallback errorCallback) {
		boolean result = false;
		boolean hasData = false;
		final int INDENT = 4;
		final int TIMEOUT = 5000;
		String action = "get-settings";
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
				String s = Tables.getName(Tables.TB.SETTINGS);
				String sg = Tables.getName(Tables.TB.SETTINGS_GROUP);
				String groupID = TarkieLib.getGroupID(db);
				try {
					SQLiteQuery query = new SQLiteQuery();
					JSONArray dataArray = responseObj.getJSONArray("data");
					for(int d = 0; d < dataArray.length(); d++) {
						JSONObject dataObj = dataArray.getJSONObject(d);
						String code = dataObj.getString("settings_code");
						String settingsID;
						String sql = "SELECT ID FROM " + s + " WHERE code = '" + code + "'";
						query.clearAll();
						query.add(new FieldValue("code", code));
						if(!db.isRecordExists(sql)) {
							settingsID = binder.insert(s, query);
						}
						else {
							settingsID = TarkieLib.getSettingsID(db, code);
							binder.update(s, query, settingsID);
						}
						query.clearAll();
						query.add(new Condition("settingsID", settingsID));
						query.add(new FieldValue("value", false));
						binder.update(sg, query);
						JSONArray groupArray = new JSONArray();
						JSONArray teamArray = dataObj.optJSONArray("team_id");
						if(teamArray != null) {
							groupArray = teamArray;
						}
						else if(dataObj.getString("team_id").equals("allteams")) {
							groupArray.put(groupID);
						}
						for(int g = 0; g < groupArray.length(); g++) {
							String teamID = groupArray.getString(g);
							sql = "SELECT ID FROM " + sg + " WHERE settingsID = " + settingsID + " AND groupID = " + teamID;
							if(!db.isRecordExists(sql)) {
								query.clearAll();
								query.add(new FieldValue("settingsID", settingsID));
								query.add(new FieldValue("groupID", teamID));
								query.add(new FieldValue("value", true));
								binder.insert(sg, query);
							}
							else {
								query.clearAll();
								query.add(new Condition("settingsID", settingsID));
								query.add(new Condition("groupID", groupID));
								query.add(new FieldValue("value", true));
								binder.update(sg, query);
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
package com.mobileoptima.tarkieattendance;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanButton;
import com.mobileoptima.adapter.LocationAdapter;
import com.mobileoptima.core.Data;
import com.mobileoptima.model.LocationObj;

import java.util.ArrayList;

public class LocationFragment extends Fragment implements OnClickListener {

	private ArrayList<LocationObj> locationList;
	private FragmentTransaction transaction;
	private CodePanButton btnBackLocation;
	private LocationAdapter adapter;
	private FragmentManager manager;
	private ListView lvLocation;
	private SQLiteAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		manager = getActivity().getSupportFragmentManager();
		db = ((MainActivity) getActivity()).getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.location_layout, container, false);
		btnBackLocation = (CodePanButton) view.findViewById(R.id.btnBackLocation);
		lvLocation = (ListView) view.findViewById(R.id.lvLocation);
		btnBackLocation.setOnClickListener(this);
		lvLocation.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				LocationObj location = locationList.get(i);
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?daddr=" +
								location.latitude + "," + location.longitude));
				startActivity(intent);
			}
		});
		loadLocation(db);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackLocation:
				manager.popBackStack();
				break;
		}
	}

	public void loadLocation(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					locationList = Data.loadLocations(db);
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			adapter = new LocationAdapter(getActivity(), locationList);
			lvLocation.setAdapter(adapter);
			return true;
		}
	});
}

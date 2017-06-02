package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanButton;
import com.mobileoptima.adapter.AddFormsAdapter;
import com.mobileoptima.core.Data;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.VisitObj;

import java.util.ArrayList;

public class AddFormsFragment extends Fragment implements OnClickListener{

	private CodePanButton btnSaveAddForms;
	private ArrayList<FormObj> formList;
	private FragmentManager manager;
	private AddFormsAdapter adapter;
	private ListView lvAddForms;
	private SQLiteAdapter db;
	private VisitObj visit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_forms_layout, container, false);
		btnSaveAddForms = (CodePanButton) view.findViewById(R.id.btnSaveAddForms);
		lvAddForms = (ListView) view.findViewById(R.id.lvAddForms);
		btnSaveAddForms.setOnClickListener(this);
		lvAddForms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				FormObj obj = formList.get(i);
				CheckBox cbAddForms = (CheckBox) view.findViewById(R.id.cbAddForms);
				obj.isCheck = !obj.isCheck;
				cbAddForms.setChecked(obj.isCheck);
			}
		});
		loadForms(db);
		return view;
	}

	public void loadForms(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					formList = Data.loadForms(db, visit.ID);
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message message) {
			adapter = new AddFormsAdapter(getActivity(), formList);
			lvAddForms.setAdapter(adapter);
			return false;
		}
	});

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnSaveAddForms:
				break;
		}
	}

	public void setVisit(VisitObj visit) {
		this.visit = visit;
	}
}

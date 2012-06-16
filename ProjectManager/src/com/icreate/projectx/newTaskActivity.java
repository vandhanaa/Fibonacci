package com.icreate.projectx;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.icreate.projectx.datamodel.ProjectxGlobalState;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;


public class newTaskActivity extends Activity  implements AdapterView.OnItemSelectedListener {

	private EditText taskNameTextBox ,taskAboutTextBox , taskDateTextBox ;
	private DatePicker taskDate;
	private Button createTask;
	private List<String> memberList= new ArrayList<String>();
	private List<String> prioriList= new ArrayList<String>();
	private Spinner Assignto, Priority;
	private ArrayAdapter<String> dataAdapter, prioriAdapter;
	private int position;
	
	private String status;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().penaltyLog().penaltyDeath().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
				.penaltyLog().penaltyDeath().build());
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.newtask);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logo1);
		final Context cont = this;
		final Activity currentActivity = this;
		
		Bundle extras = getIntent().getExtras();
		ProjectxGlobalState global = (ProjectxGlobalState)getApplication();
		
		if(extras!=null)
		{
			position=extras.getInt("position");
			System.out.println(position);
			//get members of project and store in memberlist
			//memberList=global.getProjectList().getProjects().get(position).getMembers();
		}
		
		taskNameTextBox = (EditText) findViewById(R.id.taskNameBox);
		taskAboutTextBox=(EditText) findViewById(R.id.taskAboutBox);
		taskDateTextBox=(EditText) findViewById(R.id.taskDeadlineBox);
		Assignto = (Spinner) findViewById(R.id.taskAssignedBox);
		taskDate =(DatePicker)findViewById(R.id.taskDate);
		createTask=(Button)findViewById(R.id.TaskButton);
		Priority= (Spinner)findViewById(R.id.taskPriorityBox);
		
		RelativeLayout tasklayout=(RelativeLayout)findViewById(R.id.newTaskLayout);
		status="Open";
		memberList.add("Assign Task to");
		prioriList.add("Low");
		prioriList.add("Medium");
		prioriList.add("High");
		prioriList.add("Critical");
		
		prioriAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, prioriList); 
		prioriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Priority.setAdapter(prioriAdapter);
		
				
		dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, memberList) {
			@Override
			public boolean isEnabled(int position) {
				if (position == 0) {
					return false;
				} else {
					return true;
				}
			}

			@Override
			public boolean areAllItemsEnabled() {
				return false;
			}
		};
		;
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Assignto.setAdapter(dataAdapter);
		Assignto.setOnItemSelectedListener(this);
		
		taskDateTextBox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				taskDate.setVisibility(View.VISIBLE);
			}

		});
		
		tasklayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				taskDateTextBox.setText(taskDate.getYear() + "-"
						+ (taskDate.getMonth() + 1) + "-" + taskDate.getDayOfMonth());
				taskDate.setVisibility(View.INVISIBLE);
			}
		});
		
		createTask.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				JSONObject json1 = new JSONObject();
				ProjectxGlobalState glob_data = (ProjectxGlobalState)getApplication();
				try {
					
					json1.put("projectId",glob_data.getProjectList().getProjects().get(position).getProject_id() );
					json1.put("name", taskNameTextBox.getText());
					json1.put("description", taskAboutTextBox.getText());
					json1.put("createdBy",glob_data.getUserid());
					json1.put("duedate", taskDateTextBox.getText());
					if(Assignto.getSelectedItem().equals("Assign Task to"))
					{
						json1.put("assignee",44);
					}
					else
						json1.put("assignee",Assignto.getSelectedItem());
					json1.put("status", status);
					json1.put("priority", Priority.getSelectedItem());

					Log.d("JSON string", json1.toString());
					ProgressDialog dialog = new ProgressDialog(cont);
					dialog.setMessage("Create Task...");
					CreateTask createTask = new CreateTask(
							cont, currentActivity, json1, dialog);
					createTask
							.execute("http://ec2-54-251-4-64.ap-southeast-1.compute.amazonaws.com/api/createTask.php");
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});

	}
	
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		String Assignee =(String)Assignto.getSelectedItem();
		if(position!=0)
		{
			status="Assigned";
		}			
	}

	public void onNothingSelected(AdapterView<?> parent) {
	}
	
	public class CreateTask extends AsyncTask<String, Void, String> {
		private final Context context;
		private final Activity callingActivity;
		private final ProgressDialog dialog;
		private final JSONObject requestJson;

		public CreateTask(Context context, Activity callingActivity,
				JSONObject requestData, ProgressDialog dialog) {
			this.context = context;
			this.callingActivity = callingActivity;
			this.requestJson = requestData;
			this.dialog = dialog;
		}

		@Override
		protected void onPreExecute() {
			System.out.println(this.dialog.isShowing());
			if (!(this.dialog.isShowing())) {
				this.dialog.show();
			}
		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				HttpClient client = new DefaultHttpClient();
				HttpPut httpPut = new HttpPut(url);
				try {
					httpPut.setEntity(new StringEntity(requestJson.toString()));
					HttpResponse execute = client.execute(httpPut);
					InputStream content = execute.getEntity().getContent();
					Log.d("inside",requestJson.toString());
					BufferedReader buffer = new BufferedReader(
							new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			System.out.println(result);
			try {
				JSONObject resultJson = new JSONObject(result);
				System.out.println(resultJson.toString());
				if (resultJson.getString("msg").equals("success")) {
					context.startActivity(new Intent(context,
							homeActivity.class));   // on success which activity to call ???
				} else {
					Toast.makeText(context, "error in creation",
							Toast.LENGTH_LONG).show();
				}
				callingActivity.finish();
			} catch (JSONException e) {
				Toast.makeText(context, R.string.server_error,
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

}

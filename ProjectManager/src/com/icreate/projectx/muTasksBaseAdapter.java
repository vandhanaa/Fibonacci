package com.icreate.projectx;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.icreate.projectx.TaskListBaseAdapter.ViewHolder;
import com.icreate.projectx.datamodel.Task;

public class muTasksBaseAdapter extends BaseAdapter {
	
	private static ArrayList<Task> taskList;

	private final LayoutInflater mInflater;

	public muTasksBaseAdapter(Context context, ArrayList<Task> taskList) {
		this.taskList = taskList;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return taskList.size();
	}

	public Object getItem(int position) {
		return taskList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.tasklistitem, null);
			holder = new ViewHolder();
			holder.txtName = (TextView) convertView
					.findViewById(R.id.tasklistname);
			holder.txtProjectName = (TextView) convertView
					.findViewById(R.id.taskParentName);
			holder.txtdate = (TextView) convertView
					.findViewById(R.id.tasklistduedate);
			holder.TaskProgress = (ProgressBar) convertView
					.findViewById(R.id.taskProgress);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtName.setText(taskList.get(position).getTask_name());

		holder.txtProjectName.setText(taskList.get(position).getProject_name());
		
		holder.txtdate.setText(taskList.get(position).getDue_date());

		holder.TaskProgress.setProgress((int) taskList.get(position)
				.getProgress());

		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtProjectName;
		TextView txtdate;
		ProgressBar TaskProgress;
	}

}

package DialogFragments;

import gupta.ashutosh.avatar.R;

import java.util.ArrayList;

import sate2012.avatar.android.augmentedrealityview.AugRelPointManager;
import sate2012.avatar.android.googlemaps.MarkerPlus;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

public class ActivePointDialogFragment extends DialogFragment {

	ListView list;
	AugRelPointManager manager;
	Button saveButton;
	
	public ActivePointDialogFragment(){
		
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.active_points_layout, container, false);
		getDialog().setTitle("Set Active Points");
		manager = (AugRelPointManager) getArguments().get("POINT_MANAGER");
		
		list = (ListView) view.findViewById(R.id.points_list);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		saveButton = (Button) view.findViewById(R.id.show_checked);
		
		final CustomArrayAdapter adapter = new CustomArrayAdapter(getActivity(), manager.getAllPoints());
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//arg1.setEnabled(true);
				
			}
			
		});
		
		
		saveButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ArrayList<MarkerPlus> tempArray = adapter.getCheckedItems();
				
				manager.setActivePoints(tempArray);
				getDialog().cancel();
				
			}
			
		});
		
		
		return view;
	}
	
	class CustomArrayAdapter extends BaseAdapter{

		Context mContext;
		LayoutInflater mInflater;
		ArrayList<MarkerPlus> points;
		SparseBooleanArray mSparseBooleanArray;
		
		public CustomArrayAdapter(Context context,ArrayList<MarkerPlus> objects) {
			this.mContext = context;
			mInflater = LayoutInflater.from(mContext);
			mSparseBooleanArray = new SparseBooleanArray();
			points = new ArrayList<MarkerPlus>();
			this.points = objects;
			
			for(int i = 0; i < manager.getActivePoints().size(); i++){
				
				if(this.points.contains(manager.getActivePoints().get(i))){
					mSparseBooleanArray.put(this.points.indexOf(manager.getActivePoints().get(i)), true);
				}
			}
			
		}
		
		public ArrayList<MarkerPlus> getCheckedItems(){
			ArrayList<MarkerPlus> tempArray = new ArrayList<MarkerPlus>();
			
			for(int i = 0; i < points.size(); i++){
				if(mSparseBooleanArray.get(i)){
					tempArray.add(points.get(i));
				}
			}
			
			return tempArray;
		}
		
		
		@Override
		public int getCount(){
			return points.size();
		}
		
		@Override
		public MarkerPlus getItem(int position){
			return points.get(position);
		}
		
		@Override
		public long getItemId(int position){
			return position;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.list_view_points_row, null);
			}
			
			TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			tvTitle.setText(points.get(position).getName());
			
			
			CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.chkEnable);
			mCheckBox.setTag(position);
			mCheckBox.setChecked(mSparseBooleanArray.get(position));
			mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
			
			return convertView;
		}
		
		OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				System.out.println(buttonView.getTag());
				mSparseBooleanArray.put((Integer)buttonView.getTag(), isChecked);
				
			}
			
		};
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(manager.getAllPoints().size() == 0){
			Toast.makeText(getActivity(), "There are no points on the server.", Toast.LENGTH_SHORT).show();
			getDialog().cancel();
		}
	}
	
}

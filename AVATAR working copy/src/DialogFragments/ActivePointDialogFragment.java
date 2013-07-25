package DialogFragments;

import gupta.ashutosh.avatar.R;

import java.util.ArrayList;

import sate2012.avatar.android.googlemaps.MarkerPlus;
import sate2012.avatar.android.googlemaps.augmentedreality.AugRelPointManager;
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

/**
 * 
 * @author Garrett - emrickgarrett@gmail.com
 * 
 * This class is a dialog fragment that is used to select the active
 * points in the AVATAR augmented Reality. 
 *
 */
public class ActivePointDialogFragment extends DialogFragment {

	ListView list;
	AugRelPointManager manager;
	Button saveButton;
	Button checkAll;
	
	public ActivePointDialogFragment(){
		
	}
	
	/**
	 * When the view for the Dialog is created
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.active_points_layout, container, false);
		getDialog().setTitle("Set Active Points");
		manager = (AugRelPointManager) getArguments().get("POINT_MANAGER");
		
		list = (ListView) view.findViewById(R.id.points_list);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		saveButton = (Button) view.findViewById(R.id.show_checked);
		checkAll = (Button) view.findViewById(R.id.check_all);
		
		//Adapter to populate the list and record which items are seleted
		final CustomArrayAdapter adapter = new CustomArrayAdapter(getActivity(), manager.getAllPoints());
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener(){

			/**
			 * Not currently used.
			 */
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//arg1.setEnabled(true);
				
			}
			
		});
		
		
		saveButton.setOnClickListener(new OnClickListener(){

			/**
			 * What happens when the save button is clicked.
			 */
			@Override
			public void onClick(View v) {
				if(adapter.getCheckedItems().size() > 0){
					ArrayList<MarkerPlus> tempArray = adapter.getCheckedItems();
					
					manager.setActivePoints(tempArray);
				}else{
					ArrayList<MarkerPlus> dummyArray = new ArrayList<MarkerPlus>();
					manager.setActivePoints(dummyArray);
				}
				getDialog().cancel();
				
			}
			
		});
		checkAll.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				System.out.println("MAEHTSEAI");
				adapter.checkAllPoints();
			}
			
		});
		
		return view;
	}
	
	/**
	 * 
	 * @author Garrett -emrickgarrett@gmail.com
	 *
	 *
	 *This class is used as an adapter for a ListView that we need in order
	 *to display the points correctly. Normally, Android re-uses views and will
	 *highlight the wrong ones upon scrolling and causes a lot of problems. This fixes
	 *that problem.
	 */
	class CustomArrayAdapter extends BaseAdapter{

		Context mContext;
		LayoutInflater mInflater;
		ArrayList<MarkerPlus> points;
		SparseBooleanArray mSparseBooleanArray;
		
		/**
		 * Constructor for the Adapter
		 * @param context : The context of the fragment (Activity)
		 * @param objects : The marker plus points.
		 */
		public CustomArrayAdapter(Context context,ArrayList<MarkerPlus> objects) {
			this.mContext = context;
			mInflater = LayoutInflater.from(mContext);
			mSparseBooleanArray = new SparseBooleanArray();
			points = new ArrayList<MarkerPlus>();
			this.points = objects;
			if(points == null) points = new ArrayList<MarkerPlus>();
			
			/**
			 * If this these points contains the managers active points,
			 * highlight them.
			 */
			for(int i = 0; i < manager.getActivePoints().size(); i++){
				
				if(this.points.contains(manager.getActivePoints().get(i))){
					mSparseBooleanArray.put(this.points.indexOf(manager.getActivePoints().get(i)), true);
				}
			}
			
		}
		
		/**
		 * Return which items are checked.
		 * @return : An array list of Marker Plus items.
		 */
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
		
		/**
		 * Used to determine which items are checked. This is important!
		 * If you don't use this, Android will mess everything up as it recycles views.
		 */
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.dialog_list_view_points_row, null);
			}
			
			TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			tvTitle.setText(points.get(position).getName());
			
			
			CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.chkEnable);
			mCheckBox.setTag(position);
			mCheckBox.setChecked(mSparseBooleanArray.get(position));
			mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
			
			return convertView;
		}
		
		public void checkAllPoints(){
			for(int i = 0; i < points.size(); i++){
				mSparseBooleanArray.put(i, true);
			}
			this.notifyDataSetChanged();
		}
		
		/**
		 * Listener for when an Items checked state is changed.
		 */
		OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				System.out.println(buttonView.getTag());
				mSparseBooleanArray.put((Integer)buttonView.getTag(), isChecked);
				
			}
			
		};
		
	}
	
	/**
	 * If there are no points, don't even use the dialog. Cancel it and alert the user.
	 */
	@Override
	public void onResume(){
		super.onResume();
		if(manager != null && manager.getAllPoints() != null){
			if(manager.getAllPoints().size() == 0){
				Toast.makeText(getActivity(), "There are no points on the server.", Toast.LENGTH_SHORT).show();
				getDialog().cancel();
			}
		}else{
			Toast.makeText(getActivity(), "Error connecting to the Server.", Toast.LENGTH_SHORT).show();
			getDialog().cancel();
		}
	}
	
}

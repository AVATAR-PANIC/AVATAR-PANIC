package DialogFragments;

import gupta.ashutosh.avatar.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import sate2012.avatar.android.HandleID;
import sate2012.avatar.android.googlemaps.GoogleMapsViewer;
import sate2012.avatar.android.googlemaps.MarkerPlus;
import android.annotation.SuppressLint;
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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class AvatarMapSettingsDialogFragment extends DialogFragment {

	private GoogleMapsViewer map;
	private ListView list;
	private Button saveButton;
	private EditText tagArea;
	private String FILENAME = "AVATAR_UNIQUE_ID";
	
	public AvatarMapSettingsDialogFragment(GoogleMapsViewer map){
		this.map = map;
	}
	
	public AvatarMapSettingsDialogFragment(){
		
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.avatar_map_settings, container, false);
		getDialog().setTitle("AVATAR Map Settings");
		
		tagArea = (EditText) view.findViewById(R.id.avatar_tag);
		list = (ListView) view.findViewById(R.id.user_locations);
		saveButton = (Button) view.findViewById(R.id.avatar_map_done);
		tagArea.setHint(HandleID.Tag);
		
		final CustomArrayAdapter adapter = new CustomArrayAdapter(getActivity());
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		saveButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String tag = "";
				
				tag = tagArea.getText().toString();
				
				if(!tag.equals("")){
					System.out.println("Re-writing the tag");
					FileOutputStream fos;
					try {
						File file = getActivity().getFileStreamPath(FILENAME);
						boolean isDeleted = file.delete();
						
						System.out.println("Delete Successful? " + isDeleted);
						
						fos = getActivity().openFileOutput(FILENAME, Context.MODE_APPEND);
						fos.write((HandleID.ID + " " + tag).getBytes());
						fos.flush();
						fos.close();
						HandleID.Tag = tag;
					} catch (Exception ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}finally{
						//Bleh.
					}
				}
				
				if(adapter.getCheckedItems().size() > 0){
					
					Toast.makeText(getActivity(), "Tracking User. . .", Toast.LENGTH_SHORT).show();
					map.map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(adapter.getCheckedItems().get(0).getLatitude()
							, adapter.getCheckedItems().get(0).getLongitude()), 15));
					
				}
				
				getDialog().cancel();
				
				
				
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
		
		public CustomArrayAdapter(Context context) {
			this.mContext = context;
			mInflater = LayoutInflater.from(mContext);
			mSparseBooleanArray = new SparseBooleanArray();
			this.points = new ArrayList<MarkerPlus>();
			ArrayList<MarkerPlus> temp = map.getPoints();
			
			for(int i = 0; i < temp.size(); i++){
				if(temp.get(i).getData().contains(" is the ID of this user.")){
					points.add(temp.get(i));
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
		
		OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				System.out.println(buttonView.getTag());
				mSparseBooleanArray = new SparseBooleanArray();
				mSparseBooleanArray.put((Integer)buttonView.getTag(), isChecked);
				
			}
			
		};
		
	}
	
}

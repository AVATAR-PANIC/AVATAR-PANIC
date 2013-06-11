package sate2012.avatar.android.googlemaps;

import java.util.ArrayList;

public class GoogleMapsCluster {

private ArrayList<MarkerPlus> clusters = new ArrayList<MarkerPlus>();
private int MAXDISTANCE = 100;


/**
* Default Constructor
*/

public GoogleMapsCluster(){

}

public void setClusters(ArrayList<MarkerPlus> clusters){
this.clusters = clusters;
}

public ArrayList<MarkerPlus> getClusters(){
return this.clusters;
}

public ArrayList<MarkerPlus> generateClusters(int zoomLevel){

for(int i = 0; i < this.clusters.size(); i++){
System.out.println(this.clusters.get(i).toString());
}

return this.clusters;
}

}

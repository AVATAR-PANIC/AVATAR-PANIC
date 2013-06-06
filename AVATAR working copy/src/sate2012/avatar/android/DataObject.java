package sate2012.avatar.android;

import java.io.File;
import java.net.URL;

public class DataObject {
	@Override
	public String toString() {
		return "DataObject [lat=" + lat + ", lon=" + lon + ", video=" + video
				+ ", text=" + text + ", image=" + image + ", IVAURL=" + IVAURL
				+ "]";
	}

	private double lat;
	private double lon;
	private File video;
	private String text;
	private File image;
	private URL IVAURL;

	public URL getIVAURL() {
		return IVAURL;
	}

	public void setIVAURL(URL iVAURL) {
		IVAURL = iVAURL;
	}

	public File getVideo() {
		return video;
	}

	public void setVideo(File video) {
		this.video = video;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}
}

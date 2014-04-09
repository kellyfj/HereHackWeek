package com.here;

import java.io.Serializable;
import java.util.List;

public class Tweet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String language;
	private String createdAt;
	private String text;
	private String userID;
	private Double latitude;
	private List<String> tags;
	private String placeCountry;
	private String placeName;
	private String placeType;
	private Double longitude;

	public void setLanguage(String string) {
		this.language = string;

	}

	public void setCreatedAt(String string) {
		this.createdAt = string;

	}

	public void setText(String string) {
		this.text = string;

	}

	public void setUserID(String string) {
		this.userID = string;

	}

	public void setLongitude(Double double1) {
		this.longitude = double1;

	}

	public void setPlaceType(String string) {
		this.placeType = string;

	}

	public void setPlaceName(String string) {
		this.placeName = string;

	}

	public void setPlaceCountry(String string) {
		this.placeCountry = string;

	}

	public void setHashtags(List<String> tags) {
		this.tags = tags;

	}

	public void setLatitude(Double double1) {
		this.latitude = double1;

	}

	public String getLanguage() {
		return language;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getText() {
		return text;
	}

	public String getUserID() {
		return userID;
	}

	public Double getLatitude() {
		return latitude;
	}

	public List<String> getTags() {
		return tags;
	}

	public String getPlaceCountry() {
		return placeCountry;
	}

	public String getPlaceName() {
		return placeName;
	}

	public String getPlaceType() {
		return placeType;
	}

	public Double getLongitude() {
		return longitude;
	}

}

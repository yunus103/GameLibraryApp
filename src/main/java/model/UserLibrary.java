package model;

import java.time.LocalDate;
import java.util.List;

public class UserLibrary {
	private int user_id;
	private int igdb_id;
	private int hours_played;
	private double price_paid;
	private LocalDate added_date;
	private String status;
	private boolean is_favorite;
	private List<String> platforms;
	

	public List<String> getPlatforms() {
		return platforms;
	}


	public void setPlatforms(List<String> platforms) {
		this.platforms = platforms;
	}


	
	public UserLibrary() {
		
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getIgdb_id() {
		return igdb_id;
	}

	public void setIgdb_id(int igdb_id) {
		this.igdb_id = igdb_id;
	}

	public int getHours_played() {
		return hours_played;
	}

	public void setHours_played(int hours_played) {
		this.hours_played = hours_played;
	}

	public double getPrice_paid() {
		return price_paid;
	}

	public void setPrice_paid(double price_paid) {
		this.price_paid = price_paid;
	}

	public LocalDate getAdded_date() {
		return added_date;
	}

	public void setAdded_date(LocalDate added_date) {
		this.added_date = added_date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isIs_favorite() {
		return is_favorite;
	}

	public void setIs_favorite(boolean is_favorite) {
		this.is_favorite = is_favorite;
	}
	
	
}

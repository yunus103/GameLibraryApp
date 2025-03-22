package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Game {
	private int igdb_id;
	private String title;
	private int platform_id;
	private LocalDate release_date;
	private String company;
	private String cover_image_path;
	private double rating;
	private String normalized_title;
	private String storyline;
	private String summary;
	private List<Integer> genres;  // List of genre IDs

    
    public List<Integer> getGenres() {
    	return genres != null ? genres : new ArrayList<>();
    }

    public void setGenres(List<Integer> genres) {
        this.genres = genres;
    }
	    
	public String getStoryline() {
		return storyline;
	}

	public void setStoryline(String storyline) {
		this.storyline = storyline;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getNormalized_title() {
		return normalized_title;
	}

	public void setNormalized_title(String normalized_title) {
		this.normalized_title = normalized_title;
	}

	public Game() {
		
	}

	public int getIgdb_id() {
		return igdb_id;
	}

	public void setIgdb_id(int igdb_id) {
		this.igdb_id = igdb_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getPlatform_id() {
		return platform_id;
	}

	public void setPlatform_id(int platform_id) {
		this.platform_id = platform_id;
	}

	public LocalDate getRelease_date() {
		return release_date;
	}

	public void setRelease_date(LocalDate release_date) {
		this.release_date = release_date;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCover_image_path() {
		return cover_image_path;
	}

	public void setCover_image_path(String cover_image_path) {
		this.cover_image_path = cover_image_path;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}
	
	
}

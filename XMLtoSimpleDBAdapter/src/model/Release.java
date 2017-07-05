package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Release {

	private String id;
	private String title;
	private String country;
	private String released;
	private String notes;
	private String masterId;
	private Collection<Map<String, String>> artists;
	private Collection<Map<String, String>> extraArtists;
	private Collection<String> labels;
	private Collection<Map<String, String>> companies;
	private Collection<Map<String, Object>> formats;
	private Collection<String> genres;
	private Collection<String> styles;
	private Collection<Map<String, Object>> tracklist;
	private Collection<Map<String, String>> videos;
	private Collection<Map<String, String>> identifiers;

	public Release() {
		id = "";
		title = "";
		country = "";
		released = "";
		notes = "";
		masterId = "";
		this.artists = new ArrayList<Map<String, String>>();
		this.extraArtists = new ArrayList<Map<String, String>>();
		this.labels = new ArrayList<String>();
		this.companies = new ArrayList<Map<String, String>>();
		this.formats = new ArrayList<Map<String, Object>>();
		this.genres = new ArrayList<String>();
		this.styles = new ArrayList<String>();
		this.tracklist = new ArrayList<Map<String, Object>>();
		this.videos = new ArrayList<Map<String, String>>();
		this.identifiers = new ArrayList<Map<String, String>>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getReleased() {
		return released;
	}

	public void setReleased(String released) {
		this.released = released;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

	public Collection<Map<String, String>> getArtists() {
		return artists;
	}

	public void setArtists(Collection<Map<String, String>> artists) {
		this.artists = artists;
	}

	public Collection<Map<String, String>> getExtraArtists() {
		return extraArtists;
	}

	public void setExtraArtists(Collection<Map<String, String>> extraArtists) {
		this.extraArtists = extraArtists;
	}

	public Collection<String> getLabels() {
		return labels;
	}

	public void setLabels(Collection<String> labels) {
		this.labels = labels;
	}

	public Collection<Map<String, String>> getCompanies() {
		return companies;
	}

	public void setCompanies(Collection<Map<String, String>> companies) {
		this.companies = companies;
	}

	public Collection<Map<String, Object>> getFormats() {
		return formats;
	}

	public void setFormats(Collection<Map<String, Object>> formats) {
		this.formats = formats;
	}

	public Collection<String> getGenres() {
		return genres;
	}

	public void setGenres(Collection<String> genres) {
		this.genres = genres;
	}

	public Collection<String> getStyles() {
		return styles;
	}

	public void setStyles(Collection<String> styles) {
		this.styles = styles;
	}

	public Collection<Map<String, Object>> getTracklist() {
		return tracklist;
	}

	public void setTracklist(Collection<Map<String, Object>> tracklist) {
		this.tracklist = tracklist;
	}

	public Collection<Map<String, String>> getVideos() {
		return videos;
	}

	public void setVideos(Collection<Map<String, String>> videos) {
		this.videos = videos;
	}

	public Collection<Map<String, String>> getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(Collection<Map<String, String>> identifiers) {
		this.identifiers = identifiers;
	}
}

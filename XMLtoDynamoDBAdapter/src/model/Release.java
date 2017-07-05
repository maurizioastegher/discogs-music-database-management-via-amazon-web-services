package model;

import java.util.ArrayList;
import java.util.Collection;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class Release {

	private AttributeValue id;
	private AttributeValue title;
	private AttributeValue country;
	private AttributeValue released;
	private AttributeValue notes;
	private AttributeValue master_id;
	private Collection<AttributeValue> artists;
	private Collection<AttributeValue> labels;
	private Collection<AttributeValue> extraartists;
	private Collection<AttributeValue> formats;
	private Collection<AttributeValue> genres;
	private Collection<AttributeValue> styles;
	private Collection<AttributeValue> tracklist;
	private Collection<AttributeValue> videos;
	private Collection<AttributeValue> identifiers;
	private Collection<AttributeValue> companies;

	public Release() {
		this.artists = new ArrayList<AttributeValue>();
		this.labels = new ArrayList<AttributeValue>();
		this.extraartists = new ArrayList<AttributeValue>();
		this.formats = new ArrayList<AttributeValue>();
		this.genres = new ArrayList<AttributeValue>();
		this.styles = new ArrayList<AttributeValue>();
		this.tracklist = new ArrayList<AttributeValue>();
		this.videos = new ArrayList<AttributeValue>();
		this.identifiers = new ArrayList<AttributeValue>();
		this.companies = new ArrayList<AttributeValue>();
	}

	public AttributeValue getId() {
		return id;
	}

	public void setId(AttributeValue id) {
		this.id = id;
	}

	public AttributeValue getTitle() {
		return title;
	}

	public void setTitle(AttributeValue title) {
		this.title = title;
	}

	public AttributeValue getCountry() {
		return country;
	}

	public void setCountry(AttributeValue country) {
		this.country = country;
	}

	public AttributeValue getReleased() {
		return released;
	}

	public void setReleased(AttributeValue released) {
		this.released = released;
	}

	public AttributeValue getNotes() {
		return notes;
	}

	public void setNotes(AttributeValue notes) {
		this.notes = notes;
	}

	public AttributeValue getMaster_id() {
		return master_id;
	}

	public void setMaster_id(AttributeValue master_id) {
		this.master_id = master_id;
	}

	public Collection<AttributeValue> getArtists() {
		return artists;
	}

	public void setArtists(Collection<AttributeValue> artists) {
		this.artists = artists;
	}

	public Collection<AttributeValue> getLabels() {
		return labels;
	}

	public void setLabels(Collection<AttributeValue> labels) {
		this.labels = labels;
	}

	public Collection<AttributeValue> getExtraartists() {
		return extraartists;
	}

	public void setExtraartists(Collection<AttributeValue> extraartists) {
		this.extraartists = extraartists;
	}

	public Collection<AttributeValue> getFormats() {
		return formats;
	}

	public void setFormats(Collection<AttributeValue> formats) {
		this.formats = formats;
	}

	public Collection<AttributeValue> getGenres() {
		return genres;
	}

	public void setGenres(Collection<AttributeValue> genres) {
		this.genres = genres;
	}

	public Collection<AttributeValue> getStyles() {
		return styles;
	}

	public void setStyles(Collection<AttributeValue> styles) {
		this.styles = styles;
	}

	public Collection<AttributeValue> getTracklist() {
		return tracklist;
	}

	public void setTracklist(Collection<AttributeValue> tracklist) {
		this.tracklist = tracklist;
	}

	public Collection<AttributeValue> getVideos() {
		return videos;
	}

	public void setVideos(Collection<AttributeValue> videos) {
		this.videos = videos;
	}

	public Collection<AttributeValue> getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(Collection<AttributeValue> identifiers) {
		this.identifiers = identifiers;
	}

	public Collection<AttributeValue> getCompanies() {
		return companies;
	}

	public void setCompanies(Collection<AttributeValue> companies) {
		this.companies = companies;
	}


}

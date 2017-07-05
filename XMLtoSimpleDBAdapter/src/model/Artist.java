package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Artist {

	private String id;
	private String artistName;
	private String realName;
	private String profile;
	private Collection<String> nameVariations;
	private Collection<String> aliases;
	private Collection<String> groups;
	private Collection<Map<String, String>> members;
	private Collection<String> urls;

	public Artist() {
		id = "";
		artistName = "";
		realName = "";
		profile = "";
		this.nameVariations = new ArrayList<String>();
		this.aliases = new ArrayList<String>();
		this.groups = new ArrayList<String>();
		this.members = new ArrayList<Map<String, String>>();
		this.urls = new ArrayList<String>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public Collection<String> getNameVariations() {
		return nameVariations;
	}

	public void setNameVariations(Collection<String> nameVariations) {
		this.nameVariations = nameVariations;
	}

	public Collection<String> getAliases() {
		return aliases;
	}

	public void setAliases(Collection<String> aliases) {
		this.aliases = aliases;
	}

	public Collection<String> getGroups() {
		return groups;
	}

	public void setGroups(Collection<String> groups) {
		this.groups = groups;
	}

	public Collection<Map<String, String>> getMembers() {
		return members;
	}

	public void setMembers(Collection<Map<String, String>> members) {
		this.members = members;
	}

	public Collection<String> getUrls() {
		return urls;
	}

	public void setUrls(Collection<String> urls) {
		this.urls = urls;
	}
}

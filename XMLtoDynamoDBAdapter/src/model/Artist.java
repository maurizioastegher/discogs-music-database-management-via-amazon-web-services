package model;

import java.util.ArrayList;
import java.util.Collection;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class Artist {

	private AttributeValue id;
	private AttributeValue name;
	private AttributeValue realname;
	private AttributeValue profile;
	private Collection<AttributeValue> namevariations;
	private Collection<AttributeValue> aliases;
	private Collection<AttributeValue> groups;
	private Collection<AttributeValue> members;
	private Collection<AttributeValue> urls;

	public Artist() {
		this.namevariations = new ArrayList<AttributeValue>();
		this.aliases = new ArrayList<AttributeValue>();
		this.groups = new ArrayList<AttributeValue>();
		this.members = new ArrayList<AttributeValue>();
		this.urls = new ArrayList<AttributeValue>();
	}

	public AttributeValue getId() {
		return id;
	}

	public void setId(AttributeValue id) {
		this.id = id;
	}

	public AttributeValue getName() {
		return name;
	}

	public void setName(AttributeValue name) {
		this.name = name;
	}

	public AttributeValue getRealname() {
		return realname;
	}

	public void setRealname(AttributeValue realname) {
		this.realname = realname;
	}

	public AttributeValue getProfile() {
		return profile;
	}

	public void setProfile(AttributeValue profile) {
		this.profile = profile;
	}

	public Collection<AttributeValue> getNamevariations() {
		return namevariations;
	}

	public void setNamevariations(Collection<AttributeValue> namevariations) {
		this.namevariations = namevariations;
	}

	public Collection<AttributeValue> getAliases() {
		return aliases;
	}

	public void setAliases(Collection<AttributeValue> aliases) {
		this.aliases = aliases;
	}

	public Collection<AttributeValue> getGroups() {
		return groups;
	}

	public void setGroups(Collection<AttributeValue> groups) {
		this.groups = groups;
	}

	public Collection<AttributeValue> getMembers() {
		return members;
	}

	public void setMembers(Collection<AttributeValue> members) {
		this.members = members;
	}

	public Collection<AttributeValue> getUrls() {
		return urls;
	}

	public void setUrls(Collection<AttributeValue> urls) {
		this.urls = urls;
	}
}

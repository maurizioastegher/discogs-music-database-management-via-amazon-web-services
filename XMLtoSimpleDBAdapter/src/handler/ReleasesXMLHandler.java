package handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import model.Release;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import simpleDB.ReleasesAdapter;

public class ReleasesXMLHandler extends DefaultHandler {

	Release release;
	Stack<String> tags = new Stack<String>();
	StringBuilder characters;

	Map<String, String> artist;
	Map<String, Object> format;
	Collection<String> formatsDescription;
	Map<String, Object> track = new HashMap<String, Object>();
	Map<String, String> sub_track;
	Collection<Map<String, String>> sub_tracks = new ArrayList<Map<String, String>>();
	Map<String, String> video;
	Map<String, String> company;
	Map<String, String> identifier;

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		switch (qName) {
		case "release":
			release = new Release();
			release.setId(attributes.getValue("id"));
			break;
		case "format":
			formatsDescription = new ArrayList<String>();
			format = new HashMap<String, Object>();
			if (attributes.getValue("name") != null && !attributes.getValue("name").equals(""))
				format.put("name", attributes.getValue("name"));
			if (attributes.getValue("text") != null && !attributes.getValue("text").equals(""))
				format.put("text", attributes.getValue("text"));
			break;
		case "video":
			video = new HashMap<String, String>();
			if (attributes.getValue("duration") != null && !attributes.getValue("duration").equals(""))
				video.put("duration", attributes.getValue("duration"));
			break;
		case "identifier":
			identifier = new HashMap<String, String>();
			if (attributes.getValue("description") != null && !attributes.getValue("description").equals(""))
				identifier.put("description", attributes.getValue("description"));
			if (attributes.getValue("type") != null && !attributes.getValue("type").equals(""))
				identifier.put("type", attributes.getValue("type"));
			if (attributes.getValue("value") != null && !attributes.getValue("value").equals(""))
				identifier.put("value", attributes.getValue("value"));
			release.getIdentifiers().add(identifier);
			break;
		case "label":
			release.getLabels().add(attributes.getValue("name"));
			break;
		case "track":
			if (tags.peek().equals("sub_tracks")) {
				if (sub_track != null)
					sub_tracks.add(sub_track);
				if (sub_tracks != null)
					track.put("sub_tracks", sub_tracks);
				sub_track = new HashMap<String, String>();
			}
			break;
		default:
			break;
		}

		tags.push(qName);
		characters = new StringBuilder();
	}

	public void characters(char ch[], int start, int length) throws SAXException {
		characters.append(new String(ch, start, length));
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		tags.pop();
		String content = characters.toString().trim();

		if (!content.equals("")) {
			switch (qName) {
			case "id":
				if (tags.peek().equals("artist")) {
					artist = new HashMap<String, String>();
					artist.put("id", content);
				} else if (tags.peek().equals("company")) {
					company = new HashMap<String, String>();
					company.put("id", content);
				}
				break;
			case "name":
				if (tags.peek().equals("artist")) {
					// artist.put("name",content);
				} else if (tags.peek().equals("company")) {
					company.put("name", content);
				}
				break;
			case "anv":
				if (tags.peek().equals("artist")) {
					// artist.put("anv",content);
				}
				break;
			case "join":
				if (tags.peek().equals("artist")) {
					// artist.put("join", content);
				}
				break;
			case "role":
				if (tags.peek().equals("artist")) {
					// artist.put("role", content);
				}
				break;
			case "tracks":
				if (tags.peek().equals("artist")) {
					// artist.put("tracks", content);
				}
				break;
			case "title":
				if (peekTwo(tags).equals("tracklist")) {
					track.put("title", content);
				} else if (tags.peek().equals("video")) {
					video.put("title", content);
				} else if (peekTwo(tags).equals("sub_tracks")) {
					sub_track.put("title", content);
				} else {
					release.setTitle(content);
				}
				break;
			case "genre":
				release.getGenres().add(content);
				break;
			case "style":
				release.getStyles().add(content);
				break;
			case "country":
				release.setCountry(content);
				break;
			case "released":
				release.setReleased(content);
				break;
			case "notes":
				release.setNotes(content);
				break;
			case "master_id":
				release.setMasterId(content);
				break;
			case "position":
				if (peekTwo(tags).equals("tracklist")) {
					track.put("position", content);
				} else if (peekTwo(tags).equals("sub_tracks")) {
					sub_track.put("position", content);
				}
				break;
			case "duration":
				if (peekTwo(tags).equals("tracklist")) {
					track.put("duration", content);
				} else if (peekTwo(tags).equals("sub_tracks")) {
					sub_track.put("duration", content);
				}
				break;
			case "description":
				if (tags.peek().equals("video")) {
					video.put("description", content);
				} else if (tags.peek().equals("descriptions")) {
					formatsDescription.add(content);
				} else if (peekTwo(tags).equals("sub_tracks")) {
					sub_track.put("description", content);
				}
				break;
			case "entity_type_name":
				company.put("entity_type_name", content);
				break;
			default:
				break;
			}
		}

		switch (qName) {
		case "tracks":
			if (tags.peek().equals("artist")) {
				if (peekTwo(tags).equals("artists"))
					release.getArtists().add(artist);
				if (peekTwo(tags).equals("extraartists"))
					release.getExtraArtists().add(artist);
			}
			break;
		case "description":
			if (tags.peek().equals("video")) {
				release.getVideos().add(video);
			}
			break;
		case "entity_type_name":
			release.getCompanies().add(company);
			break;
		case "format":
			if (formatsDescription != null)
				format.put("descriptions", formatsDescription);
			if (format != null)
				release.getFormats().add(format);
			break;
		case "track":
			if (tags.peek().equals("tracklist")) {
				if (track != null)
					release.getTracklist().add(track);
				track = new HashMap<String, Object>();
				sub_tracks = new ArrayList<Map<String, String>>();
			}
			break;
		default:
			break;
		}

		if (qName.equals("release")) {
			try {
				ReleasesAdapter.createParsedItemList(release);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static String peekTwo(Stack<String> tags) {
		String temp = tags.pop();
		String peeked = tags.peek();
		tags.push(temp);
		return peeked;
	}
}
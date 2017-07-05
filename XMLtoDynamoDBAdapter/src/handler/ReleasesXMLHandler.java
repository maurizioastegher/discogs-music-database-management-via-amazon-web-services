package parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import model.Release;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import dynamoDB.AmazonDynamoDBAdapter;

public class ReleasesXMLHandler extends DefaultHandler {

	Release release;
	Stack<String> tags = new Stack<String>();
	StringBuilder characters;

	Map<String, AttributeValue> artist;
	Map<String, AttributeValue> format;
	Map<String, AttributeValue> track  = new HashMap<String, AttributeValue>();
	Map<String, AttributeValue> sub_track;
	Map<String, AttributeValue> video;
	Map<String, AttributeValue> company;
	Map<String, AttributeValue> identifier;
	Collection<AttributeValue> formatsDescription;
	Collection<AttributeValue> sub_tracks = new ArrayList<AttributeValue>();

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("release")) {
			release = new Release();
			release.setId(new AttributeValue().withN(attributes.getValue("id")));
		} else if(qName.equals("format")) {
			formatsDescription = new ArrayList<AttributeValue>();
			format = new HashMap<String, AttributeValue>();
			if(attributes.getValue("name")!= null && !attributes.getValue("name").equals("")) format.put("name", new AttributeValue(attributes.getValue("name")));
			if(attributes.getValue("text")!= null && !attributes.getValue("text").equals("")) format.put("text", new AttributeValue(attributes.getValue("text")));
		} else if(qName.equals("video")) {
			video = new HashMap<String, AttributeValue>();
			if(attributes.getValue("duration")!= null && !attributes.getValue("duration").equals("")) video.put("duration", new AttributeValue(attributes.getValue("duration")));
		} else if(qName.equals("identifier")) {
			identifier = new HashMap<String, AttributeValue>();
			if(attributes.getValue("description")!= null && !attributes.getValue("description").equals("")) identifier.put("description", new AttributeValue(attributes.getValue("description")));
			if(attributes.getValue("type")!= null && !attributes.getValue("type").equals("")) identifier.put("type", new AttributeValue(attributes.getValue("type")));
			if(attributes.getValue("value")!= null && !attributes.getValue("value").equals("")) identifier.put("value", new AttributeValue(attributes.getValue("value")));
			release.getIdentifiers().add(new AttributeValue().withM(identifier));
		} else if(qName.equals("label")) {
			release.getLabels().add(new AttributeValue(attributes.getValue("name")));
		} else if(qName.equals("track")) {
			if(tags.peek().equals("sub_tracks")) {
				if(sub_track != null) sub_tracks.add(new AttributeValue().withM(sub_track));
				if(sub_tracks != null) track.put("sub_tracks", new AttributeValue().withL(sub_tracks));
				sub_track = new HashMap<String, AttributeValue>();
			}
			
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
					artist = new HashMap<String, AttributeValue>();
					artist.put("id", new AttributeValue().withN(content));
				} else if(tags.peek().equals("company")) {
					company = new HashMap<String, AttributeValue>();
					company.put("id", new AttributeValue().withN(content));
				}
				break;
			case "name":
				if (tags.peek().equals("artist")) {
					artist.put("name", new AttributeValue(content));
				} else if(tags.peek().equals("company")) {
					company.put("name", new AttributeValue(content));
				}
				break;
			case "anv":
				if (tags.peek().equals("artist")) {
					artist.put("anv", new AttributeValue(content));
				}
				break;
			case "join":
				if (tags.peek().equals("artist")) {
					artist.put("join", new AttributeValue(content));
				}
				break;
			case "role":
				if (tags.peek().equals("artist")) {
					artist.put("role", new AttributeValue(content));
				}
				break;
			case "tracks":
				if (tags.peek().equals("artist")) {
					artist.put("tracks", new AttributeValue(content));
				}
				break;
			case "title":
				if(peekTwo(tags).equals("tracklist")) {
					track.put("title", new AttributeValue(content));
				} else if(tags.peek().equals("video")) {
					video.put("title", new AttributeValue(content));
				} else if(peekTwo(tags).equals("sub_tracks")) {
					sub_track.put("title", new AttributeValue(content));
				} else {
					release.setTitle(new AttributeValue(content));
				}
				break;
			case "genre":
				release.getGenres().add(new AttributeValue(content));
				break;
			case "style":
				release.getStyles().add(new AttributeValue(content));
				break;
			case "country":
				release.setCountry(new AttributeValue(content));
				break;
			case "released":
				release.setReleased(new AttributeValue(content));
				break;
			case "notes":
				release.setNotes(new AttributeValue(content));
				break;
			case "master_id":
				release.setMaster_id(new AttributeValue().withN(content));
				break;
			case "position":
				if(peekTwo(tags).equals("tracklist")) {
					track.put("position", new AttributeValue(content));
				} else if(peekTwo(tags).equals("sub_tracks")) {
					sub_track.put("position", new AttributeValue(content));
				}
				break;
			case "duration":
				if(peekTwo(tags).equals("tracklist")) {
					track.put("duration", new AttributeValue(content));
				} else if(peekTwo(tags).equals("sub_tracks")) {
					sub_track.put("duration", new AttributeValue(content));
				}
				break;
			case "description":
				if(tags.peek().equals("video")) {
					video.put("description", new AttributeValue(content));
				} else if(tags.peek().equals("descriptions")) {
					formatsDescription.add(new AttributeValue(content));
				} else if(peekTwo(tags).equals("sub_tracks")) {
					sub_track.put("description", new AttributeValue(content));
				}
				break;
			case "entity_type_name":
				company.put("entity_type_name", new AttributeValue(content));
				break;
			default:
				break;
			}
		}
		
		switch (qName) {
			case "tracks":
				if (tags.peek().equals("artist")) {
					if(peekTwo(tags).equals("artists")) release.getArtists().add(new AttributeValue().withM(artist));
					if(peekTwo(tags).equals("extraartists")) release.getExtraartists().add(new AttributeValue().withM(artist));
				}
				break;
			case "description":
				if(tags.peek().equals("video")) {
					release.getVideos().add(new AttributeValue().withM(video));
				}
				break;
			case "entity_type_name":
				release.getCompanies().add(new AttributeValue().withM(company));
				break;
			case "format":
				if(formatsDescription != null) format.put("descriptions", new AttributeValue().withL(formatsDescription));
				if(format != null) release.getFormats().add(new AttributeValue().withM(format));
				break;
			case "track":
				if(tags.peek().equals("tracklist")) {
					if(track!= null) release.getTracklist().add(new AttributeValue().withM(track));
					track = new HashMap<String, AttributeValue>();
					sub_tracks = new ArrayList<AttributeValue>();
				} 
				break;
			default:
				break;
		}
		
		if (qName.equals("release")) {
			try {
				//AmazonDynamoDBAdapter.insertIntoDynamoDB(release);
				AmazonDynamoDBAdapter.createParsedItemList(release);
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
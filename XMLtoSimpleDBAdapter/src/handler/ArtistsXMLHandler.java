package handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import model.Artist;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import simpleDB.ArtistsAdapter;

public class ArtistsXMLHandler extends DefaultHandler {

	Artist artist;
	Stack<String> tags = new Stack<String>();
	StringBuilder characters;

	Map<String, String> member;

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("artist")) {
			artist = new Artist();
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
				if (tags.peek().equals("members")) {
					member = new HashMap<String, String>();
					member.put("id", content);
				} else {
					artist.setId(content);
				}
				break;
			case "name":
				if (tags.peek().equals("namevariations")) {
					artist.getNameVariations().add(content);
				} else if (tags.peek().equals("aliases")) {
					artist.getAliases().add(content);
				} else if (tags.peek().equals("groups")) {
					artist.getGroups().add(content);
				} else if (tags.peek().equals("members")) {
					member.put("name", content);
					artist.getMembers().add(member);
				} else {
					artist.setArtistName(content);
				}
				break;
			case "realname":
				artist.setRealName(content);
				break;
			case "profile":
				artist.setProfile(content);
				break;
			case "url":
				artist.getUrls().add(content);
				break;
			default:
				break;
			}
		}

		if (qName.equals("artist")) {
			try {
				ArtistsAdapter.createParsedItemList(artist);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
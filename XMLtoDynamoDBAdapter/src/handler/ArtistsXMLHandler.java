package parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import model.Artist;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import dynamoDB.AmazonDynamoDBAdapter;

public class ArtistsXMLHandler extends DefaultHandler {

	Artist artist;
	Stack<String> tags = new Stack<String>();
	StringBuilder characters;

	Map<String, AttributeValue> member;

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
					member = new HashMap<String, AttributeValue>();
					member.put("id", new AttributeValue().withN(content));
				} else {
					artist.setId(new AttributeValue().withN(content));
				}
				break;
			case "name":
				if (tags.peek().equals("namevariations")) {
					artist.getNamevariations().add(new AttributeValue(content));
				} else if (tags.peek().equals("aliases")) {
					artist.getAliases().add(new AttributeValue(content));
				} else if (tags.peek().equals("groups")) {
					artist.getGroups().add(new AttributeValue(content));
				} else if (tags.peek().equals("members")) {
					member.put("name", new AttributeValue(content));
					artist.getMembers().add(new AttributeValue().withM(member));
				} else {
					artist.setName(new AttributeValue(content));
				}
				break;
			case "realname":
				artist.setRealname(new AttributeValue(content));
				break;
			case "profile":
				artist.setProfile(new AttributeValue(content));
				break;
			case "url":
				artist.getUrls().add(new AttributeValue(content));
				break;
			default:
				break;
			}
		}
		
		if (qName.equals("artist")) {
			try {
				//AmazonDynamoDBAdapter.insertIntoDynamoDB(artist);
				AmazonDynamoDBAdapter.createParsedItemList(artist);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
package simpleDB;

import handler.ArtistsXMLHandler;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import model.Artist;

import org.json.JSONArray;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;

public class ArtistsAdapter {

	static AmazonSimpleDBClient simpleDB;
	static String artistsDomainName = "artists";
	static String artistsXMLFile = "C:\\Users\\maurizio\\Desktop\\discogs_20150601_artists.xml";

	static boolean debugArtists = false;
	static String artistsS3File = "C:\\Users\\maurizio\\Desktop\\artistsS3.txt";
	static Writer bw;

	private static Collection<ReplaceableItem> writeRequestList = new ArrayList<ReplaceableItem>();
	private static int requestSizeInItems = 24;

	private static void init() throws Exception {
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("Mauri").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (C:\\Users\\Mauri\\.aws\\credentials), and is in valid format.", e);
		}
		simpleDB = new AmazonSimpleDBClient(credentials);
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		simpleDB.setRegion(usWest2);
	}

	public static void main(String[] args) throws Exception {

		if (debugArtists) {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(artistsS3File), "UTF-8"));
			System.out.println("Printing artists..." + "\n");
		} else {
			init();
			try {
				if (simpleDB.listDomains().getDomainNames().contains(artistsDomainName)) {
					System.out.println("Domain " + artistsDomainName + " is already ACTIVE");
				} else {
					CreateDomainRequest createDomainRequest = new CreateDomainRequest().withDomainName(artistsDomainName);
					simpleDB.createDomain(createDomainRequest);
					System.out.println("Created Domain: " + createDomainRequest.getDomainName());
				}
			} catch (AmazonServiceException ase) {
				System.out.println("Caught an AmazonServiceException, which means your request made it "
						+ "to AWS, but was rejected with an error response for some reason.");
				System.out.println("Error Message:    " + ase.getMessage());
				System.out.println("HTTP Status Code: " + ase.getStatusCode());
				System.out.println("AWS Error Code:   " + ase.getErrorCode());
				System.out.println("Error Type:       " + ase.getErrorType());
				System.out.println("Request ID:       " + ase.getRequestId());
			} catch (AmazonClientException ace) {
				System.out.println("Caught an AmazonClientException, which means the client encountered "
						+ "a serious internal problem while trying to communicate with AWS, "
						+ "such as not being able to access the network.");
				System.out.println("Error Message: " + ace.getMessage());
			}
		}

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(artistsXMLFile, new ArtistsXMLHandler());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (debugArtists) {
			bw.close();
			System.out.println("DONE!");
		} else {
			if (!writeRequestList.isEmpty()) {
				writeMultipleItemsBatchWrite(artistsDomainName);
			}
		}
	}

	public static void createParsedItemList(Artist artist) {
		ReplaceableItem item = new ReplaceableItem();
		item.withName(artist.getId());

		if (!debugArtists) {
			if (artist.getProfile().length() > 800) {
				artist.setProfile(artist.getProfile().substring(0, 800));
			}

			if (artist.getNameVariations().size() > 10) {
				artist.setNameVariations(reduceCollectionSize(artist.getNameVariations(), 10));
			}

			if (artist.getAliases().size() > 10) {
				artist.setAliases(reduceCollectionSize(artist.getAliases(), 10));
			}

			if (artist.getGroups().size() > 10) {
				artist.setGroups(reduceCollectionSize(artist.getGroups(), 10));
			}

			if (artist.getUrls().size() > 10) {
				artist.setUrls(reduceCollectionSize(artist.getUrls(), 10));
			}

			if (artist.getMembers().size() > 10) {
				artist.setMembers(reduceMapSize(artist.getMembers(), 10));
			}
		}

		item.withAttributes(
				new ReplaceableAttribute().withValue(artist.getArtistName()).withName("artistName"),
				new ReplaceableAttribute().withValue(artist.getRealName()).withName("realName"),
				new ReplaceableAttribute().withValue(artist.getProfile()).withName("profile"),
				new ReplaceableAttribute().withValue(convertCollectionOfStrings(artist.getNameVariations())).withName(
						"nameVariations"), new ReplaceableAttribute().withValue(convertCollectionOfStrings(artist.getAliases()))
						.withName("aliases"), new ReplaceableAttribute()
						.withValue(convertCollectionOfStrings(artist.getGroups())).withName("groups"), new ReplaceableAttribute()
						.withValue(convertCollectionOfStringMaps(artist.getMembers())).withName("members"),
				new ReplaceableAttribute().withValue(convertCollectionOfStrings(artist.getUrls())).withName("urls"));

		if (debugArtists) {
			try {
				bw.write(item.toString().replace("\n", "") + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			writeRequestList.add(item);
			if (writeRequestList.size() > requestSizeInItems) {
				writeMultipleItemsBatchWrite(artistsDomainName);
			}
		}
	}

	private static void writeMultipleItemsBatchWrite(String domainName) {
		try {
			BatchPutAttributesRequest batchPutAttributesRequest = new BatchPutAttributesRequest().withItems(writeRequestList)
					.withDomainName(domainName);
			simpleDB.batchPutAttributes(batchPutAttributesRequest);

		} catch (Exception e) {
			System.err.println("Failed to retrieve items: ");
			e.printStackTrace(System.err);
		}
		writeRequestList = new ArrayList<ReplaceableItem>();
	}

	private static String convertCollectionOfStrings(Collection<String> attributeValue) {
		JSONArray jo = new JSONArray(attributeValue);
		// System.out.println(jo.toString());
		return jo.toString();
	}

	private static String convertCollectionOfStringMaps(Collection<Map<String, String>> attributeValue) {
		JSONArray jo = new JSONArray(attributeValue);
		// System.out.println(jo.toString());
		return jo.toString();
	}

	private static Collection<String> reduceCollectionSize(Collection<String> collection, int max) {
		Collection<String> temp = new ArrayList<String>();
		int count = 0;
		for (String s : collection) {
			if (count < max) {
				temp.add(s);
				count++;
			}
		}
		return temp;
	}

	private static Collection<Map<String, String>> reduceMapSize(Collection<Map<String, String>> collection, int max) {
		Collection<Map<String, String>> temp = new ArrayList<Map<String, String>>();
		int count = 0;
		for (Map<String, String> m : collection) {
			if (count < max) {
				temp.add(m);
				count++;
			}
		}
		return temp;
	}
}
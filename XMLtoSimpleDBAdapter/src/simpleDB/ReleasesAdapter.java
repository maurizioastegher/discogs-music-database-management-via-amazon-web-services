package simpleDB;

import handler.ReleasesXMLHandler;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import model.Release;

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

public class ReleasesAdapter {

	static AmazonSimpleDBClient simpleDB;
	static String releasesDomainName = "releases";
	static String releasesXMLFile = "";

	static boolean debugReleases = false;
	static String releasesS3File = "";
	static BufferedWriter bw;
	static int chunk = 1;
	static int count = 0;

	private static Collection<ReplaceableItem> writeRequestList = new ArrayList<ReplaceableItem>();
	private static int requestSizeInItems = 24;

	private static void init() throws Exception {
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (), and is in valid format.", e);
		}
		simpleDB = new AmazonSimpleDBClient(credentials);
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		simpleDB.setRegion(usWest2);
	}

	public static void main(String[] args) throws Exception {

		if (debugReleases) {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(releasesS3File + "-chunk" + chunk + ".txt"),
					"UTF-8"));
			System.out.println("Printing releases..." + "\n");
		} else {
			init();
			try {
				if (simpleDB.listDomains().getDomainNames().contains(releasesDomainName)) {
					System.out.println("Domain " + releasesDomainName + " is already ACTIVE");
				} else {
					CreateDomainRequest createDomainRequest = new CreateDomainRequest().withDomainName(releasesDomainName);
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
			saxParser.parse(releasesXMLFile, new ReleasesXMLHandler());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (debugReleases) {
			bw.close();
			System.out.println("DONE!");
		} else {
			if (!writeRequestList.isEmpty()) {
				writeMultipleItemsBatchWrite(releasesDomainName);
			}
		}
	}

	public static void createParsedItemList(Release release) {

		Collection<String> artists = new HashSet<String>();
		Iterator<Map<String, String>> artistsIterator = release.getArtists().iterator();
		while (artistsIterator.hasNext()) {
			artists.add(artistsIterator.next().get("id"));
		}

		ReplaceableItem item = new ReplaceableItem();
		item.withName(release.getId());

		if (!debugReleases) {
			if (release.getNotes().length() > 800) {
				release.setNotes(release.getNotes().substring(0, 800));
			}

			if (artists.size() > 100) {
				artists = reduceCollectionSize(artists, 100);
			}

			if (release.getCompanies().size() > 10) {
				release.setCompanies(reduceStringMapSize(release.getCompanies(), 10));
			}

			if (release.getTracklist().size() > 5) {
				release.setTracklist(reduceObjectMapSize(release.getTracklist(), 5));
			}

			if (release.getVideos().size() > 5) {
				release.setVideos(reduceStringMapSize(release.getVideos(), 5));
			}
		}

		item.withAttributes(
				new ReplaceableAttribute().withValue(release.getTitle()).withName("title"),
				new ReplaceableAttribute().withValue(release.getCountry()).withName("country"),
				new ReplaceableAttribute().withValue(release.getReleased()).withName("released"),
				new ReplaceableAttribute().withValue(release.getNotes()).withName("notes"),
				new ReplaceableAttribute().withValue(release.getMasterId()).withName("masterId"),
				new ReplaceableAttribute().withValue(convertCollectionOfStrings(artists)).withName("artists"),
				// ReplaceableAttribute().withValue(convertCollectionOfStringMaps(release.getExtraArtists())).withName("extraArtists"),
				new ReplaceableAttribute().withValue(convertCollectionOfStrings(release.getLabels())).withName("labels"),
				new ReplaceableAttribute().withValue(convertCollectionOfStringMaps(release.getCompanies())).withName("companies"),
				new ReplaceableAttribute().withValue(convertCollectionOfObjectMaps(release.getFormats())).withName("formats"),
				new ReplaceableAttribute().withValue(convertCollectionOfStrings(release.getGenres())).withName("genres"),
				new ReplaceableAttribute().withValue(convertCollectionOfStrings(release.getStyles())).withName("styles"),
				new ReplaceableAttribute().withValue(convertCollectionOfObjectMaps(release.getTracklist())).withName("tracklist"),
				// new
				// ReplaceableAttribute().withValue(convertCollectionOfStringMaps(release.getIdentifiers())).withName("identifiers"),
				new ReplaceableAttribute().withValue(convertCollectionOfStringMaps(release.getVideos())).withName("videos"));

		if (debugReleases) {
			try {
				if (count > 500000) {
					count = 0;
					chunk++;
					bw.close();
					bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(releasesS3File + "-chunk" + chunk
							+ ".txt"), "UTF-8"));
				}
				count++;
				bw.write(item.toString().replace("\n", "") + "\n");

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			writeRequestList.add(item);
			if (writeRequestList.size() > requestSizeInItems) {
				writeMultipleItemsBatchWrite(releasesDomainName);
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

	private static String convertCollectionOfObjectMaps(Collection<Map<String, Object>> attributeValue) {
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

	private static Collection<Map<String, String>> reduceStringMapSize(Collection<Map<String, String>> collection, int max) {
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

	private static Collection<Map<String, Object>> reduceObjectMapSize(Collection<Map<String, Object>> collection, int max) {
		Collection<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
		int count = 0;
		for (Map<String, Object> m : collection) {
			if (count < max) {
				temp.add(m);
				count++;
			}
		}
		return temp;
	}
}
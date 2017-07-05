package simpleDB;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;

public class SimpleDBtoS3Adapter {

	static AmazonSimpleDBClient simpleDB;
	static String artistsDomainName = "artists";
	static String releasesDomainName = "releases";
	static String artistsOutputFile = "";
	static String releasesOutputFile = "";

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
		init();

		System.out.println("Printing artists..." + "\n");
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(artistsOutputFile), "utf-8"))) {
			String nextToken = null;
			do {
				SelectRequest selectRequest = new SelectRequest("select * from artists");
				selectRequest.setConsistentRead(false);

				if (nextToken != null) {
					selectRequest.setNextToken(nextToken);
				}

				SelectResult selectResult = simpleDB.select(selectRequest);
				nextToken = selectResult.getNextToken();

				for (Item i : selectResult.getItems()) {
					String s = i.toString().replaceAll("\n", "") + "\n";
					writer.write(s);
				}
			} while (nextToken != null);
		}
		System.out.println("DONE!" + "\n");

		System.out.println("Printing releases..." + "\n");
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(releasesOutputFile), "utf-8"))) {
			String nextToken = null;
			do {
				SelectRequest selectRequest = new SelectRequest("select * from releases");
				selectRequest.setConsistentRead(false);

				if (nextToken != null) {
					selectRequest.setNextToken(nextToken);
				}

				SelectResult selectResult = simpleDB.select(selectRequest);
				nextToken = selectResult.getNextToken();

				for (Item i : selectResult.getItems()) {
					String s = i.toString().replaceAll("\n", "") + "\n";
					writer.write(s);
				}
			} while (nextToken != null);
		}
		System.out.println("DONE!");
	}
}
package dynamoDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import model.Artist;
import model.Release;
import parser.ArtistsXMLHandler;
import parser.ReleasesXMLHandler;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.dynamodbv2.util.Tables;

public class AmazonDynamoDBAdapter {

	static AmazonDynamoDBClient dynamoDB;
	static String artistsTableName = "artists";
	static String releasesTableName = "releases";
	static String artistsXMLFile = "";
	static String releasesXMLFile = "";
	private static List<WriteRequest> writeRequestList = new ArrayList<WriteRequest>();
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
		dynamoDB = new AmazonDynamoDBClient(credentials);
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		dynamoDB.setRegion(usWest2);
	}

	public static void main(String[] args) throws Exception {
		init();

		// ARTISTS TABLE
		try {
			if (Tables.doesTableExist(dynamoDB, artistsTableName)) {
				System.out.println("Table " + artistsTableName
						+ " is already ACTIVE");
			} else {
				CreateTableRequest createTableRequest = new CreateTableRequest()
						.withTableName(artistsTableName)
						.withKeySchema(
								new KeySchemaElement().withAttributeName("id")
										.withKeyType(KeyType.HASH))
						.withAttributeDefinitions(
								new AttributeDefinition().withAttributeName(
										"id").withAttributeType(
										ScalarAttributeType.N))
						.withProvisionedThroughput(
								new ProvisionedThroughput()
										.withReadCapacityUnits(25L)
										.withWriteCapacityUnits(25L));
				TableDescription createdTableDescription = dynamoDB
						.createTable(createTableRequest).getTableDescription();
				System.out.println("Created Table: " + createdTableDescription);

				System.out.println("Waiting for " + artistsTableName
						+ " to become ACTIVE...");
				Tables.awaitTableToBecomeActive(dynamoDB, artistsTableName);
			}
		} catch (AmazonServiceException ase) {
			System.out
					.println("Caught an AmazonServiceException, which means your request made it "
							+ "to AWS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out
					.println("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with AWS, "
							+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(artistsXMLFile, new ArtistsXMLHandler());
			if(!writeRequestList.isEmpty()) writeMultipleItemsBatchWrite(artistsTableName); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		// RELEASES TABLE
		try {

			if (Tables.doesTableExist(dynamoDB, releasesTableName)) {
				System.out.println("Table " + releasesTableName + " is already ACTIVE");
			} else {
				CreateTableRequest createTableRequest = new CreateTableRequest()
						.withTableName(releasesTableName)
						.withKeySchema(
								new KeySchemaElement().withAttributeName("id")
										.withKeyType(KeyType.HASH))
						.withAttributeDefinitions(
								new AttributeDefinition().withAttributeName(
										"id").withAttributeType(
										ScalarAttributeType.N))
						.withProvisionedThroughput(
								new ProvisionedThroughput()
										.withReadCapacityUnits(25L)
										.withWriteCapacityUnits(25L));
				TableDescription createdTableDescription = dynamoDB
						.createTable(createTableRequest).getTableDescription();
				System.out.println("Created Table: " + createdTableDescription);

				System.out.println("Waiting for " + releasesTableName
						+ " to become ACTIVE...");
				Tables.awaitTableToBecomeActive(dynamoDB, releasesTableName);
			}
		} catch (AmazonServiceException ase) {
			System.out
					.println("Caught an AmazonServiceException, which means your request made it "
							+ "to AWS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out
					.println("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with AWS, "
							+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(releasesXMLFile, new ReleasesXMLHandler());
			if(!writeRequestList.isEmpty()) writeMultipleItemsBatchWrite(releasesTableName); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertIntoDynamoDB(Artist artist) throws Exception {
		try {
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
			item.put("id", artist.getId());
			item.put("name", artist.getName());
			item.put("realname", artist.getRealname());
			item.put("profile", artist.getProfile());
			item.put("namevariations", new AttributeValue().withL(artist.getNamevariations()));
			item.put("aliases", new AttributeValue().withL(artist.getAliases()));
			item.put("groups", new AttributeValue().withL(artist.getGroups()));
			item.put("members", new AttributeValue().withL(artist.getMembers()));
			item.put("urls", new AttributeValue().withL(artist.getUrls()));

			PutItemRequest putItemRequest = new PutItemRequest(artistsTableName, item);
			PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
			System.out.println("Result: " + putItemResult);

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
					+ "a serious internal problem while trying to communicate with AWS, " + "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}

	}
	
	public static void insertIntoDynamoDB(Release release) throws Exception {
		try {
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
			item.put("id", release.getId());
			item.put("country", release.getCountry());
			item.put("master_id", release.getMaster_id());
			item.put("notes", release.getNotes());
			item.put("released", release.getReleased());
			item.put("title", release.getTitle());
			item.put("artists", new AttributeValue().withL(release.getArtists()));
			item.put("companies", new AttributeValue().withL(release.getCompanies()));
			item.put("extraartists", new AttributeValue().withL(release.getExtraartists()));
			item.put("formats", new AttributeValue().withL(release.getFormats()));
			item.put("genres", new AttributeValue().withL(release.getGenres()));
			item.put("identifiers", new AttributeValue().withL(release.getIdentifiers()));
			item.put("labels", new AttributeValue().withL(release.getLabels()));
			item.put("styles", new AttributeValue().withL(release.getStyles()));
			item.put("tracklist", new AttributeValue().withL(release.getTracklist()));
			item.put("videos", new AttributeValue().withL(release.getVideos()));

			PutItemRequest putItemRequest = new PutItemRequest(releasesTableName, item);
			PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
			System.out.println("Result: " + putItemResult);

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
					+ "a serious internal problem while trying to communicate with AWS, " + "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}

	}
	
	public static void createParsedItemList(Artist artist) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("id", artist.getId());
		item.put("name", artist.getName());
		item.put("realname", artist.getRealname());
		item.put("profile", artist.getProfile());
		item.put("namevariations", new AttributeValue().withL(artist.getNamevariations()));
		item.put("aliases", new AttributeValue().withL(artist.getAliases()));
		item.put("groups", new AttributeValue().withL(artist.getGroups()));
		item.put("members", new AttributeValue().withL(artist.getMembers()));
		item.put("urls", new AttributeValue().withL(artist.getUrls()));

		// System.out.println("Making the request.");
		PutRequest putRequest = new PutRequest(item);
		WriteRequest wr = new WriteRequest().withPutRequest(putRequest);

		writeRequestList.add(wr);

		if (writeRequestList.size() > requestSizeInItems) {
			writeMultipleItemsBatchWrite(artistsTableName);
		}
	}
	
	public static void createParsedItemList(Release release) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("id", release.getId());
		item.put("country", release.getCountry());
		item.put("master_id", release.getMaster_id());
		item.put("notes", release.getNotes());
		item.put("released", release.getReleased());
		item.put("title", release.getTitle());
		item.put("artists", new AttributeValue().withL(release.getArtists()));
		item.put("companies", new AttributeValue().withL(release.getCompanies()));
		item.put("extraartists", new AttributeValue().withL(release.getExtraartists()));
		item.put("formats", new AttributeValue().withL(release.getFormats()));
		item.put("genres", new AttributeValue().withL(release.getGenres()));
		item.put("identifiers", new AttributeValue().withL(release.getIdentifiers()));
		item.put("labels", new AttributeValue().withL(release.getLabels()));
		item.put("styles", new AttributeValue().withL(release.getStyles()));
		item.put("tracklist", new AttributeValue().withL(release.getTracklist()));
		item.put("videos", new AttributeValue().withL(release.getVideos()));

		// System.out.println("Making the request.");
		PutRequest putRequest = new PutRequest(item);
		WriteRequest wr = new WriteRequest().withPutRequest(putRequest);

		writeRequestList.add(wr);

		if (writeRequestList.size() > requestSizeInItems) {
			writeMultipleItemsBatchWrite(releasesTableName);
		}
	}
	
	private static void writeMultipleItemsBatchWrite(String tableName) {
		try {
			Map<String, List<WriteRequest>> tableWriteRequestList = new HashMap<String, List<WriteRequest>>();
			tableWriteRequestList.put(tableName, writeRequestList);
			BatchWriteItemRequest batchWriteItemRequest = new BatchWriteItemRequest()
					.withRequestItems(tableWriteRequestList);
			BatchWriteItemResult result = dynamoDB
					.batchWriteItem(batchWriteItemRequest);

			do { // Check for unprocessed keys which could happen if you exceed
					// provisioned throughput
				Map<String, List<WriteRequest>> unprocessedItems = result
						.getUnprocessedItems();

				if (result.getUnprocessedItems().size() == 0) {
					System.out.println("No unprocessed items found");
				} else {
					System.out.println("Retrieving the unprocessed items");
					result = dynamoDB.batchWriteItem(unprocessedItems);
				}

			} while (result.getUnprocessedItems().size() > 0);

			writeRequestList = new ArrayList<WriteRequest>();
		} catch (Exception e) {
			System.err.println("Failed to retrieve items: ");
			e.printStackTrace(System.err);
		}
	}
}
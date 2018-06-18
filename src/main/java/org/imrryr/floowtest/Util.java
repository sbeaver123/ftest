/**
 * (c) Simon Beaver 2018
 */
package org.imrryr.floowtest;

import static com.mongodb.client.model.Filters.eq;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.MD5;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Utility class holding constants and utility methods.
 *
 * @author Simon Beaver
 */
public class Util {

	/** Name of test database. */
	public static final String DATABASE = "test1";

	/** Name of control collection used to hold status records. */
	public static final String CONTROL = "control";

	/** Status indicating that a file is being processed. */
	public static final String LOADING = "loading";

	/** Status indicating that a file has completely loaded. */
	public static final String LOADCOMPLETE = "load complete";

	/** Status indicating that analysis for the file is complete. */
	public static final String COMPLETE = "complete";

	/**
	 * Create a hash of the input string to use as a collection name.
	 * We add 'c' to the front as MongoDB requires collection names
	 * to start with a letter.
	 * @param input String to hash
	 * @return SHA-224 hex string.
	 */
	public static String hash(String input) {
		String hex = new DigestUtils(MD5).digestAsHex(input);
		String hash = "c" + hex;
		System.out.println(hash);
		return hash;
	}

	public static Document getControlRecord(MongoClient mongo, String filehash) {
		Document doc = null;
		MongoDatabase db = mongo.getDatabase(DATABASE);
		MongoCollection<Document> control = db.getCollection(CONTROL);
		List<Document> record = control.find(eq("key", filehash)).into(new ArrayList<>());
		if(record.size() > 0) {
			doc = record.get(0);
		}
		return doc;
	}
}
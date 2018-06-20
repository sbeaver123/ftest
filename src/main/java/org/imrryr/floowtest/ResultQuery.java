/**
 * (c) Simon Beaver 2018
 */
package org.imrryr.floowtest;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;

/**
 * Class to run queries against map-reduce results.
 *
 * @author Simon Beaver
 */
public class ResultQuery {

	/** Results collection to use for queries. */
	private MongoCollection<Document> results;

	/**
	 * Constructor.
	 * @param db MongoDatabase instance.
	 * @param filehash Hash string denoting the collection to query.
	 */
	public ResultQuery(MongoDatabase db, String filehash) {
		String collectionName = filehash + "results";
		results = db.getCollection(collectionName);
		System.out.println("\nThe full list of word counts can be found in the '" + collectionName + "' collection on the 'test1' database.");
	}

	/**
	 * Output the single most common word in the specified file.
	 */
	public void printMostCommon() {
		FindIterable<Document> top = results.find().limit(1).sort(Sorts.descending("value.count"));
		Document word = (Document) top.first().get("_id");
		Document count = (Document) top.first().get("value");
		System.out.println("\nMost common word is '" + word.get("word") + "'" +
				" which occurred " + count.get("count") + " times.");
	}

	/**
	 * Print out the least common words in the file.
	 * 
	 * In practice, many words will be tied for least common. 
	 * So print out the least number of times a word occurred, almost always 1,
	 * then display the first five words that occurred that many times.
	 */
	public void printLeastCommon() {
		FindIterable<Document> least = results.find().limit(1).sort(Sorts.ascending("value.count"));
		Document count = (Document) least.first().get("value");
		double i = count.getDouble("count");
		List<Document> bottom = results.find(eq("value.count", i)).into(new ArrayList<>());
		System.out.println("\nThere are " + bottom.size() + " words which only occur " + i + " time(s), including:");
		
		int size = bottom.size();
		int bound = 5;
		if (size < 5) {
			bound = size;
		}
		for(int x=0;x<bound;x++) {
			Document d = (Document) bottom.get(x).get("_id");
			System.out.println(d.get("word"));
		}
	}

	/**
	 * Print out the five words that appear most often, aside from the
	 * actual most common.
	 */
	public void printTopFive() {
		System.out.println("\nNext top five words.");
		FindIterable<Document> list = results.find().skip(1).limit(5).sort(Sorts.descending("value.count"));
		for (Document d : list) {
			Document w = (Document) d.get("_id");
			Document c = (Document) d.get("value");
			System.out.println("  " + w.get("word") + ": " + c.get("count") + " times.");
		}
	}
}

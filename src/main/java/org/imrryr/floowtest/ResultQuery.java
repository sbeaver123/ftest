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
 *
 *
 * @author Simon Beaver
 */
public class ResultQuery {

	/** Results collection to use for queries. */
	private MongoCollection<Document> results;

	public ResultQuery(MongoDatabase db, String filehash) {

		String collectionName = filehash + "results";
		results = db.getCollection(collectionName);
		System.out.println("\nThe full list of word counts can be found in the '" + collectionName + "' collection on the 'test1' database.");
	}

	public void printMostCommon() {
		FindIterable<Document> top = results.find().limit(1).sort(Sorts.descending("value.count"));
		Document word = (Document) top.first().get("_id");
		Document count = (Document) top.first().get("value");
		System.out.println("Most common word is '" + word.get("word") + "'" +
				" which occurred " + count.get("count") + " times.");
	}

	public void printLeastCommon() {
		FindIterable<Document> least = results.find().limit(1).sort(Sorts.ascending("value.count"));
		Document count = (Document) least.first().get("value");
		double i = count.getDouble("count");
		System.out.println("Least count = " + i);
		List<Document> bottom = results.find(eq("value.count", i)).into(new ArrayList<>());
		System.out.println("There are " + bottom.size() + " words which only occur " + i + " time(s), including:");
		
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

	public void printTopFive() {
		System.out.println("Next top five words.");
		FindIterable<Document> list = results.find().skip(1).limit(5).sort(Sorts.descending("value.count"));
		for (Document d : list) {
			Document w = (Document) d.get("_id");
			Document c = (Document) d.get("value");
			System.out.println("  " + w.get("word") + ": " + c.get("count") + " times.");
		}
	}
}

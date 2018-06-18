/**
 * (c) Simon Beaver 2018
 */
package org.imrryr.floowtest;

import org.bson.Document;

import com.mongodb.MongoClient;
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

	public void readResults(MongoClient mongo) {
		MongoDatabase db = mongo.getDatabase("test1");
		MongoCollection<Document> results = db.getCollection("results");
			
		FindIterable<Document> top = results.find().limit(1).sort(Sorts.descending("value.count"));
		Document word = (Document) top.first().get("_id");
		Document count = (Document) top.first().get("value");
		System.out.println("Most common word is '" + word.get("word") + "'" +
				" which occurred " + count.get("count") + " times.");
		
		System.out.println("Next top five words.");
		FindIterable<Document> list = results.find().skip(1).limit(5).sort(Sorts.descending("value.count"));
		for (Document d : list) {
			Document w = (Document) d.get("_id");
			Document c = (Document) d.get("value");
			System.out.println("  " + w.get("word") + ": " + c.get("count") + " times.");
		}
		System.out.println("\nThe full list of word counts can be found in the 'results' schema on the 'test1' database.");
	}
}

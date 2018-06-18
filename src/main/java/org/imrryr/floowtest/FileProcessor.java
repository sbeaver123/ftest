/**
 * (c) Simon Beaver 2018
 */
package org.imrryr.floowtest;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 *
 *
 * @author Simon Beaver
 */
public class FileProcessor {

	public void readFile(MongoDatabase db, String filehash, String filename) {
		
		/* Drop collection to clean it out then recreate. */
		db.getCollection(filename).drop();
		MongoCollection<Document> coll = db.getCollection(filehash);
		
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine()) != null) {
				Document d = new Document("txt", line)
						.append("loaded", new Date());
				coll.insertOne(d);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		MongoCollection<Document> control = db.getCollection(Util.CONTROL);
		control.updateOne(
				eq("key", filehash), 
				combine(set("status", Util.LOADCOMPLETE)));
	}
}

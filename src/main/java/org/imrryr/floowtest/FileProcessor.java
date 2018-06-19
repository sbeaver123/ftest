/**
 * (c) Simon Beaver 2018
 */
package org.imrryr.floowtest;

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

		/* Update control record to show file processing is complete. */
		Util.updateControlStatus(db, filehash, Util.LOADCOMPLETE);
	}
}

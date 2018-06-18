/**
 * (c) Simon Beaver 2018
 */
package org.imrryr.floowtest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 *
 *
 * @author Simon Beaver
 */
public class FileProcessor {

	public void readFile(MongoDatabase db, String filename) {
		
		/* Drop collection to clean it out then recreate. */
		db.getCollection("testfiles").drop();
		MongoCollection<Document> coll = db.getCollection("testfiles");
		
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine()) != null) {
				Document d = new Document();
				d.append("txt", line);
				coll.insertOne(d);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

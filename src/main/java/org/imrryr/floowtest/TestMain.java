/**
 * (c) Simon Beaver 2018
 */
package org.imrryr.floowtest;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

/**
 * Launcher for test program.
 *
 * @author Simon Beaver
 */
public class TestMain {

	/**
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {

		MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
		MongoClient mongo = new MongoClient(uri);
		MongoDatabase db = mongo.getDatabase("test1");
		String filename = "C://Temp/test1.txt";
		
		FileProcessor fr = new FileProcessor();
		fr.readFile(db, filename);

		mongo.close();
	}

}

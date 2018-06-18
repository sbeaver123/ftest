/**
 * (c) Simon Beaver 2018
 */
package org.imrryr.floowtest;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

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
		
		String filename = "C://Temp/test1.txt";
		
		FileProcessor fr = new FileProcessor();
		fr.readFile(mongo, filename);
		
		WordCounter count = new WordCounter();
		count.doCount(mongo);

		ResultQuery query = new ResultQuery();
		query.readResults(mongo);

		mongo.close();
	}

}

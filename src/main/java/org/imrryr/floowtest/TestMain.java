/**
 * (c) Simon Beaver 2018
 */
package org.imrryr.floowtest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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

		Options options = new Options();
		options.addOption("source", true, "The name of the file to be processed");
		options.addOption("mongo", true, "The URL of the MongoDB server to use <hostname:port>");
		
		String uri = null;
		String filename = null;

		CommandLineParser clp = new DefaultParser();
		try {
			CommandLine cl = clp.parse(options, args);
			if (cl.hasOption("source") && cl.hasOption("mongo")) {
				filename = cl.getOptionValue("source");
				uri = cl.getOptionValue("mongo");
			} else {
				System.out.println("Required options missing");
				return;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		MongoClientURI muri = new MongoClientURI("mongodb://" + uri);
		MongoClient mongo = new MongoClient(muri);
		
		FileProcessor fr = new FileProcessor();
		fr.readFile(mongo, filename);
		
		WordCounter count = new WordCounter();
		count.doCount(mongo);

		ResultQuery query = new ResultQuery();
		query.readResults(mongo);

		mongo.close();
	}

}

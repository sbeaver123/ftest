/**
 * (c) Simon Beaver 2018
 */
package org.imrryr.floowtest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
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
		MongoDatabase db = mongo.getDatabase(Util.DATABASE);
		String filehash = Util.hash(filename);
		
		/*
		 * If there isn't an existing record in the control collection for this file,
		 * then we need to load it. If a record exists, another instance of the program
		 * is loading or has loaded it.
		 */
		if(!existingFile(mongo, filehash, filename)) {
				FileProcessor fr = new FileProcessor();
				fr.readFile(db, filehash, filename);
		}
		
		WordCounter count = new WordCounter();
		count.doCount(db, filehash);

		ResultQuery query = new ResultQuery(db, filehash);
		query.printMostCommon();
		query.printTopFive();
		query.printLeastCommon();

		mongo.close();
	}

	/**
	 * Check whether or not the supplied filename has an entry in the control collection.
	 * 
	 * @param mongo MongoClient instance
	 * @param filename Name of file to check
	 * @return true if file is already being loaded.
	 */
	private static boolean existingFile(MongoClient mongo, String filehash, String filename) {
		boolean retval = true;

		Document record = Util.getControlRecord(mongo, filehash);
		if (record == null) {
			System.out.println("New file so do the loading.");
			Document d = new Document("key", filehash)
					.append("filename", filename)
					.append("status", Util.LOADING);
			MongoDatabase db = mongo.getDatabase(Util.DATABASE);
			MongoCollection<Document> control = db.getCollection(Util.CONTROL);
			control.insertOne(d);
			retval = false;
		} else {
			System.out.println("Existing file, just run map-reduce.");
			System.out.println(record.toString());
		}

		return retval;
	}
}

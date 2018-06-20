
package org.imrryr.floowtest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger logger = LoggerFactory.getLogger(TestMain.class);

	/**
	 * Main execution method
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {

		logger.debug("Program start.");
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
				System.out.println("Usage: java -jar floowtest-0.1.jar -source <filename> -mongo <hostname:port>");
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
		 * then we need to create one and load the file. 
		 * If a record exists, another instance of the program is loading or has loaded it.
		 */
		Document ctrl = Util.getControlRecord(db, filehash);
		if (ctrl == null) {

			/* Check that file exists before we do anything else. */
			Path path = Paths.get(filename);
			if (!Files.exists(path) || !Files.isRegularFile(path)) {
				System.out.println("File not present or not regular file.");
				mongo.close();
				return;
			}
			
			logger.debug("Processing new file {}", filename);
			Document d = new Document("key", filehash)
					.append("filename", filename)
					.append("status", Util.LOADING);

			MongoCollection<Document> control = db.getCollection(Util.CONTROL);
			control.insertOne(d);
			
			FileProcessor fr = new FileProcessor();
			try {
				fr.readFile(db, filehash, filename);
			} catch (TestException e) {
				System.out.println(e.getMessage());
				mongo.close();
				return;
			}
		}

		if(ctrl == null || 
			(ctrl != null && !ctrl.getString("status").equals(Util.COMPLETE))) {
			WordCounter count = new WordCounter(db);
			count.doCount(filehash);
		}

		ResultQuery query = new ResultQuery(db, filehash);
		query.printMostCommon();
		query.printTopFive();
		query.printLeastCommon();

		mongo.close();
		logger.debug("Program end.");
	}
}

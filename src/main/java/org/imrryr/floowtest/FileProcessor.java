
package org.imrryr.floowtest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Class to handle uploading text data from the specified file.
 * 
 * The class reads the specified file line by line and creates a
 * new MongoDB Document for each one. Not ideal, but does preserve
 * word boundaries while handling large unstructured data.
 *
 * @author Simon Beaver
 */
public class FileProcessor {

	/** Standard SLF4J logger. */
	private static final Logger logger = LoggerFactory.getLogger(FileProcessor.class);

	/**
	 * Main file processing method.
	 * @param db MongoDatabase instance.
	 * @param filehash Hash string to use as collection name.
	 * @param filename Path of file to process.
	 * @throws TestException if a file error occurs.
	 */
	public void readFile(MongoDatabase db, String filehash, String filename) throws TestException {
		
		logger.debug("Loading file {}", filename);
		
		/* Drop collection to clean it out then recreate. */
		db.getCollection(filehash).drop();
		MongoCollection<Document> coll = db.getCollection(filehash);
		
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine()) != null) {
				Document d = new Document("txt", line)
						.append("loaded", new Date());
				coll.insertOne(d);
			}
		} catch (IOException e) {
			logger.error("Error processing file {}", filename, e);
			/* Normally would throw some kind of application-specific exception,
			 * but since this is the only place we need it, just using a standard exception.
			 */
			throw new TestException("Error processing file " + filename);
		}

		/* Update control record to show file processing is complete. */
		Util.updateControlStatus(db, filehash, Util.LOADCOMPLETE);
		logger.debug("File loading complete.");
	}
}

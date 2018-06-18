/**
 * (c) Simon Beaver 2018
 */
package org.imrryr.floowtest;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.MongoClient;

/**
 *
 *
 * @author Simon Beaver
 */
public class WordCounter {

	public void doCount(MongoClient mongo) {
		/* Need deprecated DB class to use MapReduceCommand. */
		@SuppressWarnings("deprecation")
		DB db = mongo.getDB("test1");

		DBCollection collection = db.getCollection("testfiles");

		MapReduceCommand cmd = new MapReduceCommand(
				collection,	getMapCmd(), getReduceCmd(), 
				"results", MapReduceCommand.OutputType.REPLACE, null);
		
		MapReduceOutput out = collection.mapReduce(cmd);
		int proc_count = out.getInputCount();
		System.out.println("Documents processed: " + proc_count);
	}

	private String getMapCmd() {
		String cmd = "function map() {" + 
				"var cnt = this.txt;" + 
				"var words = cnt.match(/\\w+/g);" + 
				"if (words == null) {" + 
				"return;" + 
				"}" + 
				"for (var i = 0; i < words.length; i++) {" + 
				"    emit({ word:words[i] }, { count:1 });" + 
				"}" + 
				"}";
		return cmd;
	}

	private String getReduceCmd() {
		String cmd = "function reduce(key, counts) {" + 
				"var cnt = 0;" + 
				"for (var i = 0; i < counts.length; i++) {" + 
				"    cnt = cnt + counts[i].count;" + 
				"}" + 
				"return { count:cnt };" +
				"}";
		return cmd;
	}
}

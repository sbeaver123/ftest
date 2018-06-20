
TECHNOLOGY USED

Gradle – my preferred build system these days. It is more flexible than Maven and produces more compact build files. 

Apache Commons CLI and Codec – The Apache Commons libraries are a set of utility classes to help with various standard functions. The Commons-CLI library assists with parsing command line arguments, while the Codec library makes it easy to work with encryption libraries, in this case to generate hash strings.

SLF4J/Logback – Increasingly the standard approach to logging these days. I’ve used this on several projects and found it superior to all other frameworks. It also has the flexibility to integrate logging from multiple sources, and to consolidate output. In this case it’s being used partially to capture debug logging, and partly to redirect the messages coming from the MongoDB driver.



PRE-REQUISITES

This program needs a machine with Java and MongDB, obviously enough. I used Java 8r144 as that was what I had installed already, and MongoDB v3.6.5. The jar file can be built from source using the command line

gradlew build

There is no need to install Gradle, this will be done automatically for you, as will the downloading of the dependency files. This does mean that you need to build the program on a machine with internet access.

The Wikidump file I downloaded had an issue in that it contained a very long string of the digit ‘9’ repeated so many times that it broke the MongoDB limit for key sizes. It was therefore necessary to run MongoDB with the command line

mongod --setParameter failIndexKeyTooLong=false



RUNNING THE PROGRAM

The program used the command line mentioned in the challenge documentation

java -jar floowtest-0.1.jar -source <filename> - mongo <hostname:port>



DISCUSSION

Coming to this challenge, I had never before used MongoDB, so the learning curve has been very steep. The differences in the APIs between v2.x and v3.x of MongoDB meant that a lot of the documentation on the internet is out of date and a bit misleading, but I did manage to work out the proper v3 way to do everything in the end.

I’m also new to Github, never having used it before, and I initially focussed on getting started with MongoDB. So you may notice that the early commits are very close together in terms of time. Once I’d created my Github project, I tried to replicate the stages I went through developing the program up to that point. 

Probably the most difficult issue was looking at importing data into MongoDB. The program reads the specified data file in line by line and creates a MongoDB Document for each line. This is slow, but at least does the job. I did look at things like mongoimport and GridFS, but neither seemed appropriate for the task in hand.  I’m guessing there’s probably a much better way to do this, but I don’t know what it is. Given the time constraints on this exercise, I just stuck with the solution which worked.

The program uses a control collection to record the state of a given file import. Multiple instances of the program use that record to determine the state of a file and what action needs to be taken on it. This allows for map-reduce operations to be conducted on a subset of the data while the file is still being loaded. The control record stores the date when map-reduce processing was last performed, allowing map-reduce processing to be performed incrementally. 

The contents of each file are loaded into a collection, the name of which is an MD5 hash of the filename. This is a quick way to generate a unique collection name, and allows for things like processing files with the same name in different directories. I used MD5 hashing simply because it produces the shortest string. Obviously MD5 is flawed from a cryptographic point of view, but since here I’m just using it to generate a unique string, that doesn’t matter.

The results of the map-reduce operations are added to a results collection which contains all the word counts generated for the file being processed. This is added to incrementally as map-reduce operations are performed. 

When the program has finished its map-reduce operation, it runs a set of queries to show the most common word, and the five next most common. Since there are likely to be many words which tie for least common, it simply tells you how many occur only once, and displays the first five of them. For the purposes of this test, the output of the queries is just printed to the screen as simply as possible. The results collection can of course be queried in the standard way to gain any other desired information. A message is displayed explaining this and showing the database and collection names required.

When run, if the file specified is new, the program will start to load it into the database, and keep running until the load operation is complete. If run against a file which is currently being loaded by another instance of the program, it will run a map-reduce operation on any unprocessed documents, then run the standard queries against the current results database, and then exit. The query results are based on the map-reduce results generated to date, and will obviously change over time as fresh map-reduce operations are run. If you run the program for a file which has already been fully processed, it will simply re-run the queries and print out the results. 

The only thing the program doesn’t do is recover gracefully on failure. If an instance of the program is interrupted, then a file may be left partially imported or partially processed. I believe MongoDB has some kind of two-phase commit mechanism that might make this achievable, but I haven’t found enough about it to think that I would be able to implement it in time.

The program generates a debug log, which is output to a file called floowtest.log which is generated in the directory in which the program is run.




# Discogs Music Database Management via Amazon Web Services

This project consists in the development of a set of services meant for the management of Discogs music database (Big Data). Discogs is a website about audio recordings, which includes both commercial and off-label releases, and it is especially known as the largest online database of electronic music releases, in particular on vinyl media. In 2007, Discogs data became publicly accessible via a REST-ful XML-based API. Since then, monthly data dumps are provided in XML format (https://data.discogs.com).

## Project Workflow (picture1.png)

* XMLtoSimpleDBAdapter: parses artists.xml and releases.xml in order to discard irrelevant or redundant information; cleaned results are converted into key-value pairs and inserted into Amazon SimpleDB domains;
* XMLtoDynamoDBAdapter: same as above; cleaned results are converted into key-value pairs and inserted into Amazon DynamoDB (NoSQL);
* SimpleDBtoS3Adapter: Amazon EMR (Elastic MapReduce) requires the input of MapReduce jobs to be stored inside Amazon S3 (Simple Storage Service); this adapter parses the simpleDB domains and save the information in TXT file format; Amazon EMR can then be adopted to perform MapReduce operations on this input, provided that the source code of the job is stored (in JAR format) inside Amazon S3;
* MapReduceJoin: MapReduce operation computing the natural join between artists and releases (picture2.png);
* MapReduceCount: MapReduce operation counting the number of releases per artist (picture3.png).

## Authors

* Astegher Maurizio
* Videsott Pierluigi

# Discogs Music Database Management via Amazon Web Services

This project consists in the development of a set of services meant for the management of Discogs music database (Big Data). Discogs is a website about audio recordings, which includes both commercial and off-label releases, and it is especially known as the largest online database of electronic music releases, in particular on vinyl media. In 2007, Discogs data became publicly accessible via a REST-ful XML-based API. Since then, monthly data dumps are provided in XML format (https://data.discogs.com).

## Project Structure 

* XMLtoSimpleDBAdapter: parses artists.xml and releases.xml in order to discard irrelevant or redundant information; cleaned results are converted into key-value pairs and inserted into Amazon SimpleDB domains;
* XMLtoDynamoDBAdapter: same as above; cleaned results are converted into key-value pairs and inserted into Amazon DynamoDB (NoSQL);

## Authors

* Astegher Maurizio
* Videsott Pierluigi

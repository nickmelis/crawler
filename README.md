# crawler
A dead simple crawler to generate a site map of your website

![Build](https://github.com/nickmelis/crawler/workflows/Build/badge.svg)

This project uses Gradle as dependency manager, so in order to build it, it's enough to check out the code and do 

``gradle build``

The app JAR can be run directly with 

``java -jar crawler-0.0.1-SNAPSHOT.jar https://your-website.com`` 

where `your-website.com` is the website you want to get a site map for.

The app produces a JSON array of results for every link found in your website, starting from the URL provided at startup.
For every link found, the app will print information about:
- links to pages in the same domain
- static assets contained in the current page (images, CSS and JS links) 

The result is printed to the console once all pages are processed. If your intention is to run this in production, we strongly recommend to implement a file writer and make it write into a file of your choice.

The app has a default maximum number to links to fetch of 100. This means the app will not fetch more than 100 links. If you expect your target website to have more than 100 links in total, we recommend you change this value by setting the environment variable ``maxPagesToSearch=n`` where `n` is an integer number greater than zero.
The value can be passed as JVM argument as well, with ``-DmaxPagesToSearch=n``
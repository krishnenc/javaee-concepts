demo-server: Java EE concepts
=============================

What is it?
-----------

The `demo-server` illustrates some common patterns i have used in previous Java EE projects:

* DemoAPI.java

  A REST service which accepts a GET request and sends the data to be processed to a stateless EJB instance

* DemoService.java

  A EJB whcih contains a method to push incoming requests from the API on a Redis queue

* FileUpload.java

 A servlet which accepts a file uploads and starts a batch job to process the file asynchronously

* JobTask.java

A callable which has a method that does a blocking pop on a redis list to process any message pushed on the list.
The class can be instantiated as part of a managed executor thread pool.

* Metrics.java

A class to create and manage an instance of the Metrics library and is configured to send the stats to Graphite

* Redis.java

A class to manage a pool of Redis instances

* Singleton.java

On init a number of JobTasks are created and added to the executor pool.
This class instantiates a timer that re-creates any job tasks that have failed.
This allows the system to be resilient to unexpected failures.

System requirements
-------------------

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform 7 or later. 

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later.

Build the project using command :

mvn clean package

to deploy

mvn wildfly:deploy


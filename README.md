[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)
<img src="src/main/resources/static/logo_transparent.png" width="200" align="right" />

# Air Quality Project
The idea of the project is to have a system that allows the users
to query the air quality for their locations as well to get
automatic notification (via email) when the conditions are bad.

# The Hackathon
In Start-It academy we are learning Spring Boot. This
project started as excercise in a 1-day hackathon. Most of it was
accomplished in just a day. Yet, we've continued improving
it adding more features and details.

# Building the application

### Compiling
You need java and `maven` in order to compile and run the project.

### REST API
The project uses API-fist approach. It defines the REST API usnig
OpenAPI format. Then it uses openapi-generator to generate the
Data Models, and the server stubs.

If IDE reports that it cannot find some files - please run
`mvn compile` to generate sources.

### Using the app
Open http://localhost:8080 to access the application.




# References

### Used tools
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/maven-plugin/)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-jpa-and-spring-data)

### Guides
Следните ръководства илюстрират как да използвате Spring:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

### Similar Applications
* [Breathe](https://github.com/simplabs/breethe-server)
* [creadeweather](https://github.com/energyandcleanair/creadeweather/)

### Air Quality Data
* https://discomap.eea.europa.eu/map/fme/AirQualityExport.htm
* https://discomap.eea.europa.eu/map/fme/latest/
* https://ec.europa.eu/environment/air/quality/data_reporting.htm
* http://dd.eionet.europa.eu/vocabulary/aq/pollutant/view
* http://aided.apps.eea.europa.eu/
* https://www.eea.europa.eu/data-and-maps/data/aqereporting-8
* http://eea.government.bg/airq/bulletin.jsp


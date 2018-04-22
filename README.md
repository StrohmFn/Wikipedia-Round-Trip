# Wikipedia-Round-Trip
This web application allows to calculate a round trip visiting locations of (german) Wikipedia articles.

This project uses OpenStreetMap (https://www.openstreetmap.de/) data for calculation and Leaflet (http://leafletjs.com/) for visualization. 
For this project the Spring Boot (https://spring.io/) framework is used.

## How to use
For the application to work, a graph file is needed. 
You can download the latest .pbf files containing map data here: https://download.geofabrik.de/
Run the PBFParser.java file with this .pdf files to generate the needed graph data. 
(Warning: probably a lot of RAM is needed for parsing, depending on the size of the region)
Put the generated files in a 'resources' folder at the projects root level.
Now you can start the app using this command:
java -jar -Xmx10G wiki-trip.jar
(Note1: This may take some minutes)
(Note2: The app also runs with less RAM, but to guarantee smooth behavior 10 gigabytes are recommended)
After it finished loading, you should be able to access the app under http://localhost:8080/ using your favorite browser.

## Screenshots
Select location on map.
![Alt text](Images/Screenshot1.png?raw=true)

Show nearby articles.
![Alt text](Images/Screenshot2.png?raw=true)

Show information about specific article.
![Alt text](Images/Screenshot3.png?raw=true)

Select multiple articles and show the shortest round trip.
![Alt text](Images/Screenshot4.png?raw=true)
var description = "Select any location on the map to set a marker. " +
		"Click the 'Get Articles' button to retrieve Wikipedia articles (represented as markers) around your selection. " +
		"Left click on such a marker to get informations of that particular article. Right click a marker to select it. " +
		"If you select more than two articles (at most 21), you can calculate a round trip visiting all selected Wikipedia articles! " +
		"With the button 'More Wikis' you can retrieve more Wikipedia articles than already shown."

var redIcon = new L.Icon({
	  iconUrl: 'https://cdn.rawgit.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png',
	  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
	  iconSize: [25, 41],
	  iconAnchor: [12, 41],
	  popupAnchor: [1, -34],
	  shadowSize: [41, 41]
	});

var greenIcon = new L.Icon({
  iconUrl: 'https://cdn.rawgit.com/pointhi/leaflet-color-markers/master/img/marker-icon-green.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

var map = L.map('leafletMap', {maxBoundsViscosity: 1.0});

$("#leafletMap").bind('contextmenu', function(e){
    return false;
}); 

$(document).ready(function(){
	loadMap();
	locate();
});

function loadMap(){
	map.setView([ 48.745577, 9.1065019369 ], 6);
	L
			.tileLayer(
					'http://{s}.tile.osm.org/{z}/{x}/{y}.png',
					{
						attribution : 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>',
						maxZoom : 18,
						noWrap: true
					}).addTo(map);
}

function locate(){
	map.locate({
		setView : true,
		maxZoom : 15
	})
}

map.on('click', function(e) {
	if (typeof (marker) === 'undefined') {
		map.stopLocate()
		marker = new L.marker(e.latlng, {draggable : true});
		marker.addTo(map);
	} else {
		marker.setLatLng(e.latlng);
	}
	if(selectedMarker != undefined){
		document.getElementById("rankedArticles").style.display='block';
		$("#description").text("");
		var id = selectedMarker.id
		map.removeLayer(selectedMarker)
		var newMarker;
		if(selectedArticles.has(id)){
			newMarker = L.marker([ selectedLat, selectedLon ], {icon: greenIcon})
		}else{
			newMarker = L.marker([ selectedLat, selectedLon ])
		}
		newMarker.addTo(markerGroup).bindTooltip(articles[id]['title']);
		newMarker.id = id;
		selectedMarker = undefined;
	}
	if(displayCounter != 0){
		$("#headline").text("Wikipedia Articles");
	}else{
		$("#headline").text("Wikipedia Round Trip");
	}
	document.getElementById("headline").href = "https://www.wikipedia.de/";
	document.getElementById("thumbnail").src = "https://upload.wikimedia.org/wikipedia/commons/8/80/Wikipedia-logo-v2.svg";
});

function reset(){
	if(polyline != undefined){
		map.removeLayer(polyline)
	}
	markerGroup.clearLayers()
	loadMap();
	locate();
	
	$("#rankedArticles").empty();
	articles = {};
	rankedArticleIDs = [];
	displayCounter = 0;
	selectedArticles = new Set();
	markers = {};
	marker = undefined;
	markerGroup = L.featureGroup().addTo(map);
	markerGroup.on("click", groupLeftClick);
	markerGroup.on("contextmenu", groupRightClick);
	amountOfArticles = undefined;
	selectedLat = undefined;
	selectedLon = undefined;
	selectedMarker = undefined;
	responseCounter = 0;
	
	$("#description").text(description);
	$("#headline").text("Wikipedia Round Trip");
	document.getElementById("thumbnail").src = "https://upload.wikimedia.org/wikipedia/commons/8/80/Wikipedia-logo-v2.svg";
	document.getElementById("headline").href = "https://www.wikipedia.de/";
	document.getElementById("rankedArticles").style.display='none';
	document.getElementById("showMoreArticles").style.visibility='visible';
	document.getElementById("showMoreArticles").style.display='none';
	document.getElementById("reset").style.display='none';
	document.getElementById("calculate").style.display='none';
	document.getElementById("getInitArticles").style.display='block';
}

var lat;
var lng;
var articles = {};
var rankedArticleIDs = [];
var displayCounter = 0;
var selectedArticles = new Set();
var markers = {};
var marker;
var markerGroup = L.featureGroup().addTo(map);
markerGroup.on("click", groupLeftClick);
markerGroup.on("contextmenu", groupRightClick);
var amountOfArticles;
var selectedLat;
var selectedLon;
var selectedMarker;
var responseCounter = 0;

function groupRightClick(event) {
	var marker = event.layer;
	var id = event.layer.id;
	map.removeLayer(marker)
	var coords = articles[id]['coordinates'];
	var newMarker;

	if(selectedArticles.has(id)){
		selectedArticles.delete(id);
		newMarker = L.marker([ coords.lat, coords.lon ])
	}else{
		selectedArticles.add(id);
		newMarker = L.marker([ coords.lat, coords.lon ], {icon: greenIcon})
	}
	
	newMarker.addTo(markerGroup).bindTooltip(articles[id]['title']);
	newMarker.id = id;
	
	if(selectedArticles.size > 1 && selectedArticles.size < 22){
		document.getElementById("calculate").style.visibility="visible";
	}else{
		document.getElementById("calculate").style.visibility="hidden";
	}
}

function groupLeftClick(event) {
	var marker = event.layer;
	showArticle(marker)
}

function showArticle(marker){
	document.getElementById("rankedArticles").style.display='none';
	if(selectedMarker != undefined){
		var id = selectedMarker.id
		map.removeLayer(selectedMarker)
		var newMarker;
		if(selectedArticles.has(id)){
			newMarker = L.marker([ selectedLat, selectedLon ], {icon: greenIcon})
		}else{
			newMarker = L.marker([ selectedLat, selectedLon ])
		}
		newMarker.addTo(markerGroup).bindTooltip(articles[id]['title']);
		newMarker.id = id;
	}
	selectedMarker = marker;
	var id = marker.id;
	var coords = articles[id]['coordinates'];
	selectedLat = coords.lat;
	selectedLon = coords.lon;
	map.removeLayer(selectedMarker)
	selectedMarker = L.marker([ selectedLat, selectedLon ], {icon: redIcon})
	selectedMarker.addTo(markerGroup).bindTooltip(articles[id]['title']);
	selectedMarker.id = id;
	
	var headline = articles[id]['title'];
	var text = articles[id]['extract'];
	$("#description").text(text);
	$("#headline").text(headline);
	var thumbnail = articles[id]['thumbnail'];
	if (thumbnail != undefined) {
		document.getElementById("thumbnail").src = thumbnail['source'];
	} else {
		document.getElementById("thumbnail").src = "https://upload.wikimedia.org/wikipedia/commons/8/80/Wikipedia-logo-v2.svg";
	}
	document.getElementById("headline").href = "https://de.wikipedia.org/?curid="+id;
}

$('body').click(function( event ) {
	var id = event.target.id;
	if (id.startsWith("wiki-")){
		id = id.slice(5)
		showArticle(markers[id])
	}
});

function getArticlesInRange() {
	map.stopLocate()
	document.getElementById("loader").style.display='block';
	document.getElementById("getInitArticles").style.display='none';
	document.getElementById("rankedArticles").style.display='none';
	try {
		lat = marker.getLatLng().lat;
		lng = marker.getLatLng().lng;
		searchArticles(function(articleIDs){
			amountOfArticles = Object.keys(articleIDs).length;
			console.log(amountOfArticles + " found!")
		    getArticle(function(){
				completeGeocoords(articleIDs);
				rankArticles();
				map.removeLayer(marker)
				map.setView([ lat, lng ], 12);
				addMarkers();
				$("#description").text("");
				$("#headline").text("Wikipedia Articles");
				document.getElementById("headline").href = "https://www.wikipedia.de/";
				document.getElementById("loader").style.display='none';
				document.getElementById("showMoreArticles").style.display='inline-block';
				document.getElementById("reset").style.display='inline-block';
				document.getElementById("calculate").style.display='inline-block';
				document.getElementById("calculate").style.visibility="hidden";
				document.getElementById("rankedArticles").style.display='block';
			}, articleIDs);
			if(amountOfArticles == 0){
				document.getElementById("getInitArticles").style.display='inline-block';
				document.getElementById("loader").style.display='none';
			}
		})
	}
	catch(err) {
		document.getElementById("rankedArticles").style.display='block';
		document.getElementById("getInitArticles").style.display='inline-block';
		document.getElementById("loader").style.display='none';
	}
}

function searchArticles(callback){
	$.ajax({
		type : "GET",
		url : "https://de.wikipedia.org/w/api.php?action=query&list=geosearch&gslimit=500&gsradius=10000&gscoord="
				+ lat + "%7C" + lng + "&format=json",
		async : true,
		dataType: 'jsonp',
		success : function(response) {
		callback(response['query']['geosearch']);
		}
});
}

function completeGeocoords(articleIDs) {
	for ( var article in articleIDs) {
		var pageid = articleIDs[article]['pageid'];
		articles[pageid]['coordinates'] = {lat:articleIDs[article]['lat'], lon:articleIDs[article]['lon']};
	}
}

function addMarkers() {
	if(displayCounter <= amountOfArticles){
		var counter = displayCounter;
		while (counter < (displayCounter + 30) && counter < amountOfArticles) {
			var articleID = rankedArticleIDs[counter][0];
			$('#rankedArticles').append("<p id=wiki-" + articleID + ">" + articles[articleID]['title'] + "</p>");
			var coords = articles[articleID]['coordinates'];
			if (coords != undefined) {
				var marker = L.marker([ coords.lat, coords.lon ])
				marker.addTo(markerGroup).bindTooltip(articles[articleID]['title']);
				marker.id = articleID;
				markers[articleID] = marker;
			}else{
				console.log(articleID)
			}
			counter++;
		}
		displayCounter += 30;
	}else{
		document.getElementById("showMoreArticles").style.visibility='hidden';
	}
}

function getArticle(callback, articleIDs) {
	// Wiki API limits to 20 articles per query.
	// Therefore we calculate how many request we have to do.
	var quotient = Math.floor(amountOfArticles/20);
	var remainder = amountOfArticles % 20;
	var numberOfRequests;
	if (remainder > 0){
		numberOfRequests = quotient+1;
	}
	else{
		numberOfRequests = quotient
	}
	var offset = 0;
	for (j = 0; j < quotient; j++) {
		var idString = "";
		for (i = offset; i < (offset + 20); i++ ) {
			idString += articleIDs[i]['pageid'] + "|";
		}
		offset += 20;
		idString = idString.slice(0, -1);
		getContent(callback, idString, numberOfRequests);
	}
	if(remainder > 0){
		var idString = "";
		for (i = offset; i < (offset + remainder); i++){
			idString += articleIDs[i]['pageid'] + "|";
		}
		idString = idString.slice(0, -1);
		getContent(callback, idString, numberOfRequests);
	}
}

function getContent(callback, ids, numberOfRequests) {
	$
			.ajax({
				type : "GET",
				url : "https://de.wikipedia.org/w/api.php?action=query&prop=pageviews|extracts|pageimages&pageids="
						+ ids
						+ "&format=json&exintro=&explaintext=&pithumbsize=300",
				async : true,
				dataType: 'jsonp',
				success : function(response) {
					articles = Object.assign(response['query']['pages'], articles);
					responseCounter++;
					if(responseCounter == numberOfRequests){
						callback();
					}
				}
			});
}

function rankArticles() {
	for ( var article in articles) {
		var pageviews = articles[article]['pageviews']
		var daycount = 0;
		var avgViews = 0;
		for ( var day in pageviews) {
			if (pageviews[day] != null) {
				avgViews += pageviews[day];
				daycount++
			}

		}
		if (avgViews > 0) {
			avgViews = avgViews / daycount;
		}
		articles[article].avgViews = avgViews;
		rankedArticleIDs.push([ article, articles[article]['avgViews'] ]);
	}
	rankedArticleIDs.sort(function(a, b) {
		return b[1] - a[1];
	});
}

var polyline;
function calculateTour() {
	document.getElementById("loader").style.display='block';
	document.getElementById("showMoreArticles").style.display='none';
	document.getElementById("reset").style.display='none';
	document.getElementById("calculate").style.display='none';
	document.getElementById("rankedArticles").style.display='none';
	var visitNodesLat = [];
	var visitNodesLon = [];
	var selctedIDs = Array.from(selectedArticles);
	selctedIDs.forEach(function(id) {
		var coords = articles[id]['coordinates'];
		visitNodesLat.push(coords.lat);
		visitNodesLon.push(coords.lon);
	});
	var urlString = "/calc/" + visitNodesLat + "/" + visitNodesLon;
	$.ajax({
		type : "GET",
		url : urlString,
		timeout : 10000,
		success : function(response) {
			// split the response-string into a path
			var latlngs = response.split(",").map(function(e) {
				return e.split("_").map(Number);
			});
			if(polyline != undefined){
				map.removeLayer(polyline)
			}
			polyline = L.polyline(latlngs, {
				color : '#a80303'
			}).addTo(map);
			map.fitBounds(polyline.getBounds());
			if(selectedMarker == undefined){
				document.getElementById("rankedArticles").style.display='block';
			}
			document.getElementById("loader").style.display='none';
			document.getElementById("showMoreArticles").style.display='inline-block';
			document.getElementById("reset").style.display='inline-block';
			document.getElementById("calculate").style.display='inline-block';
		},
		error: function() {
			if(selectedMarker == undefined){
				document.getElementById("rankedArticles").style.display='block';
			}
			document.getElementById("loader").style.display='none';
			document.getElementById("showMoreArticles").style.display='inline-block';
			document.getElementById("reset").style.display='inline-block';
			document.getElementById("calculate").style.display='inline-block'; 
		}       
	});
}

var map = L.map('leafletMap');

map.setView([ 48.745577, 9.1065019369 ], 6);
L
		.tileLayer(
				'http://{s}.tile.osm.org/{z}/{x}/{y}.png',
				{
					attribution : 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>',
					maxZoom : 18
				}).addTo(map);

var marker;

map.locate({
	setView : true
}).on('locationfound', function(e) {
	marker = new L.marker(e.latlng, {
		draggable : true
	});
	// marker.bindPopup(e.latitude + " " + e.longitude);
	map.addLayer(marker);
})

map.on('click', function(e) {
	if (typeof (marker) === 'undefined') {
		marker = new L.marker(e.latlng, {
			draggable : true
		});
		marker.addTo(map);
	} else {
		marker.setLatLng(e.latlng);
		marker.addTo(map);
	}

});

var lat;
var lng;
var articles = {};
var rankedArticleIDs = [];
var displayCounter = 0;
var markers = [];
var markerGroup = L.featureGroup().addTo(map).on("click", groupClick);

function groupClick(event) {
	var id = event.layer.id;
	console.log(articles[id]);
	var text = articles[id]['extract'];
	$("#description").text(text);
	var thumbnail = articles[id]['thumbnail'];
	if (thumbnail != undefined) {
		document.getElementById("thumbnail").src = thumbnail['source'];
	} else {
		document.getElementById("thumbnail").src = "";
	}
}

function getArticlesInRange() {
	lat = marker.getLatLng().lat;
	lng = marker.getLatLng().lng;
	var articleIDs;
	$
			.ajax({
				type : "GET",
				url : "https://de.wikipedia.org/w/api.php?action=query&prop=pageviews&list=geosearch&gslimit=200&gsradius=10000&gscoord="
						+ lat + "%7C" + lng + "&format=json",
				async : false,
				success : function(response) {
					articleIDs = response['query']['geosearch'];
				}
			});
	getArticle(articleIDs);
	rankArticles();
	map.removeLayer(marker)
	map.setView([ lat, lng ], 14);
	addMarkers();
}

function addMarkers() {
	for (i = displayCounter; i < (displayCounter + 20); i++) {
		var articleID = rankedArticleIDs[i][0];
		if (articles[articleID]['coordinates'] != undefined) {
			var coords = articles[articleID]['coordinates'][0];
			var marker = L.marker([ coords.lat, coords.lon ])
			marker.addTo(markerGroup).bindTooltip(articles[articleID]['title']);
			marker.id = articleID;
			markers.push(marker);
		}
	}
	displayCounter += 20;
}

function getArticle(articleIDs) {
	var counter = 0;
	// We want 200 articles but can only query 20 at once due to wikimedia API
	// limitations.
	for (j = 0; j < 10; j++) {
		var idString = "";
		for (i = counter; i < (counter + 20); i++) {
			idString += articleIDs[i]['pageid'] + "|";
		}
		counter += 20;
		idString = idString.slice(0, -1);
		var content = getContent(idString);
		articles = Object.assign(content, articles);
	}
}

function getContent(ids) {
	var articles;
	$
			.ajax({
				type : "GET",
				url : "https://de.wikipedia.org/w/api.php?action=query&prop=pageviews|extracts|pageimages|coordinates&pageids="
						+ ids
						+ "&format=json&exintro=&explaintext=&pithumbsize=400",
				async : false,
				success : function(response) {
					articles = response;
				}
			});
	return articles['query']['pages']
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

function btnCalculateClick() {
	var latlngs;
	var lat = marker.getLatLng().lat;
	var lng = marker.getLatLng().lng;
	var startNode = [ lat, lng ];
	var roundTripDuration = document.querySelector("#duration").value;
	var visitNodesLat = [ 1, 3, 5 ];
	var visitNodesLng = [ 2, 4, 6 ];
	var roundTripPath;
	var urlString = "/calc/" + startNode + "/" + roundTripDuration + "/"
			+ visitNodesLat + "/" + visitNodesLng;
	$.ajax({
		type : "GET",
		url : urlString,
		async : false,
		success : function(response) {
			// split the response-string into a path
			latlngs = response.split(",").map(function(e) {
				return e.split("_").map(Number);
			});
			var polyline = L.polyline(latlngs, {
				color : 'red'
			}).addTo(map);
			map.fitBounds(polyline.getBounds());
		}
	});
}

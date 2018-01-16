var map = L.map('leafletMap');

map.setView([48.745577, 9.1065019369], 6);
L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>',
    maxZoom: 18
}).addTo(map);

var marker;

map.locate({setView : true})
	.on('locationfound', function(e){
    marker = new L.marker(e.latlng,{ draggable: true});
    //marker.bindPopup(e.latitude + " " + e.longitude);
    map.addLayer(marker);
})

map.on('click', function(e){
	console.log(e.latlng);
	 if(typeof(marker)==='undefined')
	 {
		 marker = new L.marker(e.latlng,{ draggable: true});
		 marker.addTo(map);        
	 }
	 else 
	 {
		 marker.setLatLng(e.latlng);         
	 }

});

function btnCalculateClick(){
	var lat = marker.getLatLng().lat;
	var lng = marker.getLatLng().lng;
	var startNode = [lat, lng];
	var roundTripDuration = document.querySelector("#duration").value;
	var visitNodesLat = [1,3,5];
	var visitNodesLng = [2,4,6];
	var roundTripPath;
	var urlString = "/calc/" + startNode + "/" + roundTripDuration + "/" + visitNodesLat + "/" + visitNodesLng;
	 $.ajax({
		   type: "GET",
		   url: urlString,
		   async: false,
		   success: function(response) {
			   roundTripPath = response;
		  }
	 });
	 console.log(roundTripPath);
}


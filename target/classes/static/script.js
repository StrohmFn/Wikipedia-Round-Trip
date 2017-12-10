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

function bnt1Click(){
	var lat = marker.getLatLng().lat;
	var lng = marker.getLatLng().lng;
	var a;
	 $.ajax({
		   type: "GET",
		   url: '/hello',
		   async: false,
		   success: function(response) {
		     a = response;
		  }
	 });
	 console.log(a);
}


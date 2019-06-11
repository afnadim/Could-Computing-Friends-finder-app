//API keys
var mapBoxApiKey="pk.eyJ1IjoiYWZuYWRpbSIsImEiOiJjam9iN3lkZTQwbGF1M3FtcGV4YTJwOTJvIn0.9giAsyME75UEFG-Krc7rug";
var openWeatherAPIKey="5c573ed76554f9296e23fca7ee32bef9";
var baseURL="api";
//var map=makeMap("map",1,0.0,0.0);
var map;



//the document ready function
try	{
	$(function()
		{
		init();
		}
	);
	} catch (e)
		{
		alert("*** jQuery not loaded. ***");
		}

//
// Initialise page.
//
function init()
{
map=makeMap("map",1,0.0,0.0);	//make map using Leaflet or GoogleMap API
var marker=makeMarker(map,0.0,0.0);	//make and put marker on map, keeping reference

//update the input boxes while marker moves
marker.on('dragend', function (e) {
    document.getElementById('latitude').value = marker.getLatLng().lat;
    document.getElementById('longitude').value = marker.getLatLng().lng;
});



//set click handler of check in button
$("#checkin").click(function()
		
		{
	
		saveUser(marker);	//save city to web service
		});

//set click event for log in
$("#lonIn").click(function()
		
		{
		logUser();
		//update pending and approved requests list once user is logged in
		populatePeningRequests($("#name").val());
		populateApprovedRequests($("#name").val());
		//location.reload($("#subscriptionDIV"))
		});
//set click event for subscription requests
$("#sendsub").click(function()
		
		{
		saveRequest();
		
		});


//show one user

$("#searchUser").click(function()
		
		{
		//alert("searching the user")
		
		 showOneUser($("#username").val());
		});

//refresh pending subscrition requests once clicked
$("#refreshPSR").click(function()
		
		{
		populatePeningRequests($("#name").val());
		});


//refresh Approved subscrition requests once clicked
$("#refreshFriends").click(function()
		
		{
		populateApprovedRequests($("#name").val());
		});


//set click handler of Approve  button to Approve friend requests
$("#Approve").click(function()
				{
				$("#incomingSubReq li.selected").each(function()
											{
											approveUser($(this).attr("id"));
											$(this).remove();
											}
										);
				}
			);

//set click handler of Delete Selected button to delete rejected friend requests
$("#Reject").click(function()
				{
				$("#incomingSubReq li.selected").each(function()
											{
											deleteUser($(this).attr("id"));
											$(this).remove();
											}
										);
				}
			);

//update automatically
//populatePeningRequests($("#name").val());
//populateApprovedRequests($("#name").val());

}




//hide and show divs on click	

function friendsDivFunction() {
    var x = document.getElementById("friendsDIV");
    var y = document.getElementById("subscriptionDIV");
    
    if (x.style.display === "none" && y.style.display === "block") {
        x.style.display = "block";
        y.style.display = "none";
    } else {
        x.style.display = "block";
        
    }
}

function subscriptionDivFunction() {
	
    var x = document.getElementById("subscriptionDIV");
    var y = document.getElementById("friendsDIV");
    if (x.style.display === "none" && y.style.display === "block") {
        x.style.display = "block";
        y.style.display = "none";
    } else {
        x.style.display = "block";
        y.style.display = "none";
    }
}

//
// save users location from the marker
//
function saveUser(marker)
{
var longitude=marker.getLatLng().lng;	//get longitude from position
var latitude=marker.getLatLng().lat;	//get latitude from position
//get the timestamp
var d = new Date();
var timestamp = d.getTime();

var name=$("#name").val();	//get users name input text box value

var url=baseURL+"/user";					//URL of web service
var data={	"name":name,				
			"longitude":longitude,
			"latitude":latitude,
			"updatetime":timestamp
		};

//use jQuery shorthand Ajax POST function
$.post(	url,			//URL of service
		data,			//parameters of request
		function(json)		//successful callback function
		{
		//alert("User saved: "+name+" ("+longitude+","+latitude+")");
		alert(JSON.stringify(json));
		} //end callback function
	); //end post call
} //end function

//-------------------------------------------------------------------------------------------------
//sending sunbscription requests 
function saveRequest()
{
//get the timestamp
var d = new Date();
var timestamp = d.getTime();

var requestFrom=$("#name").val();
var requestTo=$("#subreq").val();

var url=baseURL+"/requests";					//URL of web service
var data={				
			"requestFrom":requestFrom,
			"requestTo":requestTo,
			"updatetime":timestamp
		};

//use jQuery shorthand Ajax POST function
$.post(	url,			//URL of service
		data,			//parameters of request
		function(json)		//successful callback function
		{
	    //alert(JSON.stringify(json));
		alert("Request saved: "+requestFrom+" To  "+ requestTo+" at " +timestamp);
		} //end callback function
	); //end post call
} //end function

//--------------------------------------------------------------------------------------------


//--------------------------------------------------------------------------------------------
//send login details
function logUser()
{

//get the timestamp
var d = new Date();
var timestamp = d.getTime();

var name=$("#name").val();	//get users name input text box value

var url=baseURL+"/user/login";	//URL of web service
var data={	"name":name,				//request parameters as a map	
		};

//use jQuery shorthand Ajax POST function
$.post(	url,			//URL of service
		data,			//parameters of request
		function(json)		//successful callback function
		{

	    alert(JSON.stringify(json));	
		} //end callback function
	); //end post call
} //end function

//--------------------------------------------------------------------------------------------

function showOneUser(name) 
{
	
	var name=$("#username").val();
	var username=$("#name").val();
	alert("searched for "+"   "+name);
	var url=baseURL+"/user/"+name;    //URL of the service

	//use jQuery shorthand Ajax function to get JSON data
	$.getJSON(	url,					//URL of service
			function(reply)	//successful callback function
			{
		name=reply["name"];
		longitude=reply["longitude"];			//get longitude from JSON data
		latitude=reply["latitude"];			//get latitude from JSON data
		LastUpdate=reply["updatetime"];
		var date = new Date(LastUpdate);
		$("#oneUserdetails").html("Name : "+ name +" "+"longitude : "+longitude+ "  "+"latitude : "+latitude +" Last Update : "+date);
		var location=L.latLng({longitude,latitude});
		makeMarker(map,longitude,latitude);
		//zooming the map
		map.setView(new L.LatLng(latitude,longitude), 4);
			}
	); 
}

//--------------------------------------------------------------------------------------------------

//retrive all pending friends requests
function populatePeningRequests(name)
{
var name=$("#name").val();	
var url=baseURL+"/requests/"+name;
//use jQuery shorthand Ajax function to get JSON data
$.getJSON(url,				//URL of service
		function(reply)		//successful callback function
		{
		$("#incomingSubReq").empty();		//find pening requests list and remove its children
		for (var i in reply)
			{
			var user=reply[i]; //get 1 city from the JSON list
		
			var time=user["updatetime"];
			//converting the miilisecnds in date format
			var date = new Date(time);
			var id=user["id"]
			var requestFrom=user["requestFrom"];
			var requestTo=user["requestTo"];
			//compose HTML of a list item using the users ip
			var htmlCode="<li id='"+id+"'>"+"Request from : "+ requestFrom +" "+"On :"+date+"</li>";
			
			$("#incomingSubReq").append(htmlCode);	//add a incomingSubReq the list		
			}
		//look for all list items (requests), set their click handler
			$("#incomingSubReq li").click(function()
								{
							
								userClicked($(this).attr("id"));
								} //end click handler function
						); //end click call
			
		} //end Ajax callback function
	); //end Ajax call
} //end function

//populate approved friends request
//retrive all pending friends request
function populateApprovedRequests(name)
{
var name=$("#name").val();	
var url=baseURL+"/subscriptions/username/"+name;
//use jQuery shorthand Ajax function to get JSON data
$.getJSON(url,				//URL of service
		function(reply)		//successful callback function
		{
		$("#approvedSubReq").empty();		//find approved user list and remove its children
		for (var i in reply)
			{
			var user=reply[i]; //get 1 user from the JSON list
			var time=user["updatetime"];
			var friendName=user["name"];
			var longitude=user["longitude"];			//get longitude from JSON data
			var latitude=user["latitude"];
			marker = makeMarker(map,longitude,latitude);
		    marker.addTo(map);
			
			var date = new Date(time);
			
			var htmlCode="<li friendName ='"+friendName +"'>"+ friendName +"  longitude:"+longitude+"latitude"+latitude+" Last check in time  :"+date+    "</li>";
			
			$("#approvedSubReq").append(htmlCode);	//add a incomingSubReq
			
			
			}
		
		
		//look for all list items (requests), set their click handler
		$("#approvedSubReq li").click(function()
								{
								userClickedApp($(this).attr("friendName"));
								} //end click handler function
						); //end click call
						
		
		} //end Ajax callback function
	); //end Ajax call
} //end function
//============================================================
function userClickedApp(friendName)
{

//$("#incomingSubReq li").removeClass("selected"); //remove all list items from the class "selected, thus clearing previous selection
$("#approvedSubReq li").removeClass("selected");

// Find the selected user (i.e. list item) and add the class "selected" to it.
// This will highlight it according to the "selected" class.
$("#"+friendName).addClass("selected");

//retrieve users coordinates from the service
var url=baseURL+"/subscriptions/requestClick/"+friendName;		//URL of service, notice that ID is part of URL path

//use jQuery shorthand Ajax function to get JSON data

$.getJSON(	url,					//URL of service
		function(reply)	//successful callback function
		{
		name=reply["name"];
		longitude=reply["longitude"];			//get longitude from JSON data
		latitude=reply["latitude"];			//get latitude from JSON data
		LastUpdate=reply["updatetime"];
		var date = new Date(LastUpdate);
		$("#oneUserdetails").html("Name : "+ name +" "+"longitude : "+longitude+ "  "+"latitude : "+latitude +" Last Update : "+date);
		var location=L.latLng({longitude,latitude});
		makeMarker(map,longitude,latitude);
		//zooming the map
		map.setView(new L.LatLng(latitude,longitude), 4);
		}
);  
} //end function


//===========================================================
// parameter ID is the unique city identifier
//
function userClicked(id)
{

$("#incomingSubReq li").removeClass("selected"); //remove all list items from the class "selected, thus clearing previous selection
//$("#approvedSubReq li").removeClass("selected");



// Find the selected user (i.e. list item) and add the class "selected" to it.
// This will highlight it according to the "selected" class.
$("#"+id).addClass("selected");

//retrieve users coordinates from the service
var url=baseURL+"/subscriptions/"+id;		//URL of service, notice that ID is part of URL path

//use jQuery shorthand Ajax function to get JSON data


$.getJSON(	url,					//URL of service
			function(user)	//successful callback function
			{
			time=user["updatetime"];
			id=user["id"]
			requestFrom=user["requestFrom"];//get city ID from JSON data
			requestTo=user["requestTo"];//get city ID from JSON data		//get latitude from JSON data
			// *** Add JS code to update h1 on page to show city name
			//alert("Add JS to show city name on page.\nThere is a h1 inside the section of weather details.");
			//$("#incomingSubReq").html("Name clicked : "+ name +" "+"longitude : "+requestFrom+" Last Update : "+time);
			}
		);
} //end function

//-----------------------------------------------//----------------
function deleteUser(id)
{
var url=baseURL+"/subscriptions/"+id;				//URL pattern of delete service
var settings={type:"DELETE"};	//options to the $.ajax(...) function call

$.ajax(url,settings);
} //end function
//----------------------------------------------
//put method to save the approved friend request into subscription database

function approveUser(id)
{
var url=baseURL+"/subscriptions/"+id;				//URL pattern of delete service
var settings={type:"PUT"};	//options to the $.ajax(...) function call

$.ajax(url,settings);
} //end function

//---------------------------------//--------------------------
//================----------====================================================================
//create map in a given division, given its centre coordinates
//the map is returned as it is need to place the marker
//
function makeMap(divId,zoomLevel,longitude,latitude)
{
var location=L.latLng(longitude,latitude);		//create location
var map=L.map(divId).setView(location,zoomLevel);	//put map into division
L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token='+mapBoxApiKey,
		{attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
		maxZoom: 18,
		id: 'mapbox.streets',
		accessToken: mapBoxApiKey}
		).addTo(map);
return map;	//return map object
} //end function

//
//create a marker on a map
//the marker is returned as we need to get its position later
//
function makeMarker(map,longitude,latitude)
{
var location=L.latLng({lon:longitude,lat:latitude});	//create marker at given position
var marker=L.marker(location,{draggable:true});			//make a draggable marker
marker.addTo(map);	//add marker to map	
return marker;				//return marker object
} //end function


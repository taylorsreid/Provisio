import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

//if user is not logged in. Cookies returns string.
if(Cookies.get("loggedIn") !== "true"){
    document.getElementById("form").innerHTML = `You must be logged in to make a reservation, click <a href="./login.html">here</a> if you are not redirected in 3 seconds...`;
    setTimeout(function(){window.location.href = "./login.html"}, 3000); //callback to wait 3 seconds then redirect
}

// const checkOutIsoDate = params.get("checkOut");
// const checkInLocalDate = checkInIsoDate.split("-")[1] + "/" + checkInIsoDate.split("-")[2] + "/" + checkInIsoDate.split("-")[0];
// const checkOutLocalDate = checkOutIsoDate.split("-")[1] + "/" + checkOutIsoDate.split("-")[2] + "/" + checkOutIsoDate.split("-")[0];

let requestBody = JSON.parse(sessionStorage.getItem("requestBody"));
const checkInLocalDate = requestBody.checkIn.split("-")[1] + "/" + requestBody.checkIn.split("-")[2] + "/" + requestBody.checkIn.split("-")[0];
const checkOutLocalDate = requestBody.checkOut.split("-")[1] + "/" + requestBody.checkOut.split("-")[2] + "/" + requestBody.checkOut.split("-")[0];

document.getElementById("location").insertAdjacentHTML("beforeend", location)
document.getElementById("checkIn").insertAdjacentHTML("beforeend", checkInLocalDate);
document.getElementById("checkOut").insertAdjacentHTML("beforeend", checkOutLocalDate);
document.getElementById("roomSize").insertAdjacentHTML("beforeend", roomSize);
document.getElementById("wifi").insertAdjacentHTML("beforeend", wifi);
document.getElementById("breakfast").insertAdjacentHTML("beforeend", breakfast);
document.getElementById("parking").insertAdjacentHTML("beforeend", parking);

document.getElementById("submitButton").addEventListener("click", function(){

    fetch(apiLocation + 'reservations/new', {
        method : "POST",
        headers: {
            'Content-Type': 'application/json',
            Authorization : 'Bearer ' + Cookies.get('jwt'),
        },
        body : JSON.stringify({
            location : location,
            checkIn : checkInIsoDate, //you must send the date as ISO format
            checkOut : checkOutIsoDate, //you must send the date as ISO format
            roomSize : roomSize,
            wifi : wifi,
            breakfast : breakfast,
            parking : parking,
            guests : guests
        })
    })
    .then(resp => resp.json())
    .then(json => {
        if (json.success == true) {
            document.getElementById("submitButton").disabled = true;
        }
        else {
            document.getElementById("formMessage").innerHTML = json.message;
        }
    });
});
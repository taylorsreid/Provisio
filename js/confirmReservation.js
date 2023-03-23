import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

let submitButton = document.getElementById("submitButton");

//if user is not logged in. Cookies returns string.
if(Cookies.get("loggedIn") !== "true"){
    document.getElementById("form").innerHTML = `You must be logged in to make a reservation, click <a href="./login.html">here</a> if you are not redirected in 3 seconds...`;
    setTimeout(function(){window.location.href = "./login.html"}, 3000); //callback to wait 3 seconds then redirect
}

const urlParams = new URLSearchParams(window.location.search);

const checkInLocal = urlParams.get("checkIn");
const checkOutLocal = urlParams.get("checkOut");
const checkInIso = new Date(checkInLocal).toISOString().split("T")[0]; //API only accepts ISO dates, split("T")[0] removes the time
const checkOutIso = new Date(checkOutLocal).toISOString().split("T")[0]; //API only accepts ISO dates, split("T")[0] removes the time
const roomSize = urlParams.get("roomSize");
const wifi = urlParams.get("wifi");
const breakfast = urlParams.get("breakfast");
const parking = urlParams.get("parking");
const guests = urlParams.get("guests");

document.getElementById("checkIn").insertAdjacentHTML("beforeend", checkInLocal);
document.getElementById("checkOut").insertAdjacentHTML("beforeend", checkOutLocal);
document.getElementById("roomSize").insertAdjacentHTML("beforeend", roomSize);
document.getElementById("wifi").insertAdjacentHTML("beforeend", wifi);
document.getElementById("breakfast").insertAdjacentHTML("beforeend", breakfast);
document.getElementById("parking").insertAdjacentHTML("beforeend", parking);
document.getElementById("guests").insertAdjacentHTML("beforeend", guests);


submitButton.addEventListener("click", function(){

    fetch(apiLocation + '/reservation', {
        method : "POST",
        headers: {
            'Content-Type': 'application/json',
            Authorization : 'Bearer ' + Cookies.get('jwt'),
        },
        body : JSON.stringify({
            checkIn : checkInIso,
            checkOut : checkOutIso,
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
            
        }
        else {
            console.log(json);
        }
    });
});
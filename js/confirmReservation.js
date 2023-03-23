import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

let submitButton = document.getElementById("submitButton");

//if user is not logged in. Cookies returns string.
if(Cookies.get("loggedIn") !== "true"){
    document.getElementById("form").innerHTML = `You must be logged in to make a reservation, click <a href="./login.html">here</a> if you are not redirected in 3 seconds...`;
    setTimeout(function(){window.location.href = "./login.html"}, 3000); //callback to wait 3 seconds then redirect
}

const checkInLocalDate = sessionStorage.getItem("checkInLocalDate");
const checkOutLocalDate = sessionStorage.getItem("checkOutLocalDate");
const checkInIsoDate = new Date(checkInLocalDate).toISOString().split("T")[0]; //API only accepts ISO dates, split("T")[0] removes the time
const checkOutIsoDate = new Date(checkOutLocalDate).toISOString().split("T")[0]; //API only accepts ISO dates, split("T")[0] removes the time
const roomSize = sessionStorage.getItem("roomSize");
const wifi = sessionStorage.getItem("wifi");
const breakfast = sessionStorage.getItem("breakfast");
const parking = sessionStorage.getItem("parking");
const guests = sessionStorage.getItem("guests");

document.getElementById("checkIn").insertAdjacentHTML("beforeend", checkInLocalDate);
document.getElementById("checkOut").insertAdjacentHTML("beforeend", checkOutLocalDate);
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
            sessionStorage.clear();
            document.getElementById("formMessage").innerHTML = json.message;
        }
        else {
            document.getElementById("formMessage").innerHTML = json.message;
        }
    });
});
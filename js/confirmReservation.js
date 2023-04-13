import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

//if user is not logged in. Cookies returns string.
if(Cookies.get("loggedIn") !== "true"){
    document.getElementById("reservationInfo").hidden = true;
    document.getElementById("loginMessage").hidden = false;
    setTimeout(function(){window.location.href = "./login.html"}, 3000); //callback to wait 3 seconds then redirect
}

//retrieve reservation info from session storage
const reservationInfo = JSON.parse(sessionStorage.getItem("reservationInfo"));
const nights = parseInt(sessionStorage.getItem("nights")); //not sent to API but saved for convenience

//convert ISO 8601 date to American format
const checkInLocalDate = reservationInfo.checkIn.split("-")[1] + "/" + reservationInfo.checkIn.split("-")[2] + "/" + reservationInfo.checkIn.split("-")[0];
const checkOutLocalDate = reservationInfo.checkOut.split("-")[1] + "/" + reservationInfo.checkOut.split("-")[2] + "/" + reservationInfo.checkOut.split("-")[0];

document.getElementById("hotelName").innerHTML = reservationInfo.hotelName;
document.getElementById("checkIn").innerHTML = checkInLocalDate;
document.getElementById("checkOut").innerHTML = checkOutLocalDate;
document.getElementById("roomSizeName").innerHTML = reservationInfo.roomSizeName;
document.getElementById("wifi").innerHTML = reservationInfo.wifi;
document.getElementById("breakfast").innerHTML = reservationInfo.breakfast;
document.getElementById("parking").innerHTML = reservationInfo.parking;
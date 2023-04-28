import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

//if user is not logged in then redirect to login page. Cookies returns string.
if(Cookies.get("loggedIn") !== "true"){
    document.getElementById("reservationInfo").hidden = true;
    document.getElementById("loginMessage").hidden = false;
    setTimeout(function(){window.location.href = "./login.html"}, 3000); //callback to wait 3 seconds then redirect
}

//if no reservation stored then redirect the user to make one
if (sessionStorage.getItem("reservationInfo") == null) {
    window.location.href = "./bookReservation.html";
}

//retrieve reservation info from session storage
const reservationInfo = JSON.parse(sessionStorage.getItem("reservationInfo"));
const nights = parseInt(sessionStorage.getItem("nights")); //not sent to API but saved for convenience

//convert ISO 8601 date to American format
const checkInLocalDate = reservationInfo.checkIn.split("-")[1] + "/" + reservationInfo.checkIn.split("-")[2] + "/" + reservationInfo.checkIn.split("-")[0];
const checkOutLocalDate = reservationInfo.checkOut.split("-")[1] + "/" + reservationInfo.checkOut.split("-")[2] + "/" + reservationInfo.checkOut.split("-")[0];

//fill in check in and check out dates
document.getElementById("checkIn").innerHTML = checkInLocalDate;
document.getElementById("checkOut").innerHTML = checkOutLocalDate;

//fill in hotel name
document.getElementById("hotelName").innerHTML = reservationInfo.hotelName;

//fill in room size name
document.getElementById("roomSizeName").innerHTML = reservationInfo.roomSizeName;

//fill in nights
for (const element of document.getElementsByClassName("nights")){
    element.innerHTML = nights;
}

//fill in guests
reservationInfo.guests.forEach(guest => {
    document.getElementById("guests").insertAdjacentHTML("beforeend", `${guest.firstName} ${guest.lastName} <br>`)
});

//retrieve all prices from API
//prices retrieved from API instead of hard coded to allow for future price changes
fetch(apiLocation + 'prices', {
    method : "POST",
    headers: {
        'Content-Type': 'application/json',
    },
    body : sessionStorage.getItem("reservationInfo")
})
.then(resp => resp.json())
.then(json => {
    if (json.success == true) {

        let grandTotal = 0;

        //update room price
        const roomTotal = (nights * parseFloat(json.prices[reservationInfo.roomSizeName])); //per night
        document.getElementById("roomTotal").innerHTML = `$${parseFloat(roomTotal).toFixed(2)}`;
        grandTotal += roomTotal;

        //unhide wifi row and update prices
        if (reservationInfo.wifi) {
            const wifiTotal = parseFloat(json.prices.wifi); //flat rate, not per night
            document.getElementById("wifiRow").hidden = false;
            document.getElementById("wifiTotal").innerHTML = `$${parseFloat(wifiTotal).toFixed(2)}`;
            grandTotal += wifiTotal;
        }

        //unhide breakfast row and update prices
        if (reservationInfo.breakfast) {
            const breakfastTotal = (nights * parseFloat(json.prices.breakfast)); //per night
            document.getElementById("breakfastRow").hidden = false;
            document.getElementById("breakfastTotal").innerHTML = `$${parseFloat(breakfastTotal).toFixed(2)}`;
            grandTotal += breakfastTotal;
        }

        //unhide parking row and update prices
        if (reservationInfo.parking) {
            const parkingTotal = (nights * parseFloat(json.prices.parking)); //per night
            document.getElementById("parkingRow").hidden = false;
            document.getElementById("parkingTotal").innerHTML = `$${parseFloat(parkingTotal).toFixed(2)}`;
            grandTotal += parkingTotal;
        }

        //render grand total
        document.getElementById("grandTotal").innerHTML = `$${parseFloat(grandTotal).toFixed(2)}`;
    }
    else{
        document.getElementById("information").innerHTML = "An internal server error has occured."
    }
});

document.getElementById("submitButton").addEventListener("click", function () {
    fetch(apiLocation + 'reservations/new', {
        method : "POST",
        headers: {
            'Content-Type': 'application/json',
            Authorization : 'Bearer ' + Cookies.get('jwt'),
        },
        body : JSON.stringify(reservationInfo)
    })
    .then(resp => resp.json())
    .then(json => {
        if (json.success == true) {
            document.getElementById("submitButton").hidden = true;
            document.getElementById("cancelButton").hidden = true;
            sessionStorage.removeItem("reservationInfo");
            sessionStorage.removeItem("nights");
        }
        document.getElementById("reservationMessage").innerHTML = json.message;
    });
})

document.getElementById("cancelButton").addEventListener("click", function () {
    sessionStorage.removeItem("reservationInfo");
    sessionStorage.removeItem("nights");
    window.location.href = "./bookReservation.html";
})
import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

//if user is not logged in. Cookies returns string.
if(Cookies.get("loggedIn") !== "true"){
    document.getElementById("reservationInfo").hidden = true;
    document.getElementById("loginMessage").hidden = false;
    setTimeout(function(){window.location.href = "./login.html"}, 3000); //callback to wait 3 seconds then redirect
}

//retrieve reservation info from session storage
const requestedReservation = JSON.parse(sessionStorage.getItem("reservationInfo"));
const nights = parseInt(sessionStorage.getItem("nights")); //not sent to API but saved for convenience

//convert ISO 8601 date to American format
const checkInLocalDate = requestedReservation.checkIn.split("-")[1] + "/" + requestedReservation.checkIn.split("-")[2] + "/" + requestedReservation.checkIn.split("-")[0];
const checkOutLocalDate = requestedReservation.checkOut.split("-")[1] + "/" + requestedReservation.checkOut.split("-")[2] + "/" + requestedReservation.checkOut.split("-")[0];

//fill in check in and check out dates
document.getElementById("checkIn").innerHTML = checkInLocalDate;
document.getElementById("checkOut").innerHTML = checkOutLocalDate;

//fill in hotel name
document.getElementById("hotelName").innerHTML = requestedReservation.hotelName;

//fill in room size name
document.getElementById("roomSizeName").innerHTML = requestedReservation.roomSizeName;

//fill in nights
for (const element of document.getElementsByClassName("nights")){
    element.innerHTML = nights;
}

//retrieve all prices from API
//prices retrieved from API instead of hard coded to allow for future price changes
fetch(apiLocation + 'prices', {
    method : "POST",
    headers: {
        'Content-Type': 'application/json',
    }
})
.then(resp => resp.json())
.then(json => {
    console.log(JSON.stringify(json));
    if (json.success == true) {

        let grandTotal = 0;

        //update room price
        const roomTotal = (nights * parseFloat(json.prices[requestedReservation.roomSizeName])); //per night
        document.getElementById("roomTotal").innerHTML = `$${roomTotal}`;
        grandTotal += roomTotal;

        //unhide wifi row and update prices
        if (requestedReservation.wifi) {
            const wifiTotal = parseFloat(json.prices.wifi); //flat rate, not per night
            document.getElementById("wifiRow").hidden = false;
            document.getElementById("wifiTotal").innerHTML = `$${wifiTotal}`;
            grandTotal += wifiTotal;
        }

        //unhide breakfast row and update prices
        if (requestedReservation.breakfast) {
            const breakfastTotal = (nights * parseFloat(json.prices.breakfast)); //per night
            document.getElementById("breakfastRow").hidden = false;
            document.getElementById("breakfastTotal").innerHTML = `$${breakfastTotal}`;
            grandTotal += breakfastTotal;
        }

        //unhide parking row and update prices
        if (requestedReservation.parking) {
            const parkingTotal = (nights * parseFloat(json.prices.parking)); //per night
            document.getElementById("parkingRow").hidden = false;
            document.getElementById("parkingTotal").innerHTML = `$${parkingTotal}`;
            grandTotal += breakfastTotal;
        }

        //render grand total
        document.getElementById("grandTotal").innerHTML = `$${grandTotal}`;
        
    }
    else{

    }
});
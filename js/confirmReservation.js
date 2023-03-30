import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

//if user is not logged in. Cookies returns string.
if(Cookies.get("loggedIn") !== "true"){
    document.getElementById("reservationInfo").hidden = true;
    document.getElementById("loginMessage").hidden = false;
    setTimeout(function(){window.location.href = "./login.html"}, 3000); //callback to wait 3 seconds then redirect
}

let requestBody = JSON.parse(sessionStorage.getItem("requestBody"));
const checkInLocalDate = requestBody.checkIn.split("-")[1] + "/" + requestBody.checkIn.split("-")[2] + "/" + requestBody.checkIn.split("-")[0];
const checkOutLocalDate = requestBody.checkOut.split("-")[1] + "/" + requestBody.checkOut.split("-")[2] + "/" + requestBody.checkOut.split("-")[0];

document.getElementById("hotel").insertAdjacentHTML("beforeend", requestBody.hotel)
document.getElementById("checkIn").insertAdjacentHTML("beforeend", checkInLocalDate);
document.getElementById("checkOut").insertAdjacentHTML("beforeend", checkOutLocalDate);
document.getElementById("roomSize").insertAdjacentHTML("beforeend", requestBody.roomSize);
document.getElementById("wifi").insertAdjacentHTML("beforeend", requestBody.wifi);
document.getElementById("breakfast").insertAdjacentHTML("beforeend", requestBody.breakfast);
document.getElementById("parking").insertAdjacentHTML("beforeend", requestBody.parking);

let guestsText = "";
for(let i = 0; i < requestBody.guests.length; i++){
    let guest = requestBody.guests[i];
    // console.log(guest.firstName + " " + guest.lastName);
    guestsText += (guest.firstName + " " + guest.lastName);
    if(i != requestBody.guests.length - 1){
        guestsText += ", "
    }
}

document.getElementById("guests").insertAdjacentHTML("beforeend", guestsText);

document.getElementById("submitButton").addEventListener("click", function(){
    fetch(apiLocation + 'reservations/new', {
        method : "POST",
        headers: {
            'Content-Type': 'application/json',
            Authorization : 'Bearer ' + Cookies.get('jwt'),
        },
        body : JSON.stringify(requestBody)
    })
    .then(resp => resp.json())
    .then(json => {
        if (json.success == true) {
            document.getElementById("submitButton").disabled = true;
            sessionStorage.clear();
        }
        document.getElementById("reservationMessage").innerHTML = json.message;
    });
});
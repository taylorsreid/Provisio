import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

document.getElementById("postReservationButton").addEventListener("click", function(){
    fetch(apiLocation + '/reservation', {
        method : "POST",
        headers: {
            'Content-Type': 'application/json',
            Authorization : 'Bearer ' + Cookies.get('jwt'),
        },
        body : JSON.stringify({
            checkIn : "2023-3-25",
            checkOut : "2023-3-26",
            roomSize : "k",
            wifi : true,
            breakfast : true,
            parking : true,
            guests : 3
        })
    })
    .then(resp => resp.json())
    .then(json => {
        if (json.success == true) {
            window.location.href = `./confirmReservation.html?`;
        }
        else {
            console.log(json);
        }
    });
});
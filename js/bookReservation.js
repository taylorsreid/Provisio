import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

let submitButton = document.getElementById("submitButton");
let formMessage = document.getElementById("formMessage");

// document.getElementById("checkIn").addEventListener("change", updatePoints());
// document.getElementById("checkOut").addEventListener("change", updatePoints());

// function updatePoints(){

//     console.log(document.getElementById("checkIn"));
//     console.log(document.getElementById("checkOut"));

//     let checkInDate = new Date(document.getElementById("checkIn").textContent);
//     let checkOutDate = new Date(document.getElementById("checkOut").textContent);

//     console.log(checkInDate);
//     console.log(checkOutDate);

//     let deltaTime = checkOutDate.getTime() - checkInDate.getTime();
//     let deltaDays = deltaTime / (1000 * 3600 * 24);

//     console.log(`typeof deltaTime: ${typeof deltaTime}`);
//     console.log(`typeof deltaDays: ${typeof deltaDays}`);
//     console.log(`deltaTime value: ${deltaTime}`);
//     console.log(`deltaDays value: ${deltaDays}`);

//     formMessage.innerHTML = `You will earn ${deltaDays * 150} points for this reservation.`;
// }

submitButton.addEventListener("click", function(){

    //
    

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
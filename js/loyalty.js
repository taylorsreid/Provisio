import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

fetch(apiLocation + 'reservations/getByUserId', { //api root URL + loyalty query endpoint
    method : "POST",
    headers: {
        'Content-Type': 'application/json',
        Authorization : 'Bearer ' + Cookies.get('jwt')
    }
})
.then(response => response.json()) //turn response into an object
.then(json => {
    if (json.success == true) { //things to do if the API accepted the request and the user successfully logged in
        
        console.log(JSON.stringify(json)); //for testing, remove for prod

        json.reservations.forEach(reservation => {
            document.getElementById("reservationsTable").insertAdjacentHTML('beforeend', `
                <tr>
                    <td class="tablecon">${reservation.reservationId}</td>
                    <td class="tablecon">${reservation.hotelName}</td>
                    <td class="tablecon">${reservation.checkIn}</td>
                    <td class="tablecon">${reservation.checkOut}</td>
                    <td class="tablecon">${reservation.pointsEarned}</td>
                </tr>
            `);
        });

        document.getElementById("totalPoints").innerHTML = json.totalPointsEarned;

        document.getElementById("welcomeMessage").innerText = "Your Reservations";
        document.getElementById("reservationsTable").hidden = false;

    }
    else { //things to do if the API did not accept the request and the login failed

    }
})
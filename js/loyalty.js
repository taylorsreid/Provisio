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
                    <td>${reservation.reservationId}</td>
                    <td>${reservation.hotelName}</td>
                    <td>${reservation.checkIn}</td>
                    <td>${reservation.checkOut}</td>
                    <td>${reservation.pointsEarned}</td>
                </tr>
            `);
        });

        document.getElementById("totalPoints").innerHTML = json.totalPointsEarned;

    }
    else { //things to do if the API did not accept the request and the login failed

    }
})
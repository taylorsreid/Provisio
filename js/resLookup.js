import apiLocation from "./apiLocation.js";

//attach this event listener to your form
document.getElementById("form").addEventListener("submit", function (event) {

    event.preventDefault(); //prevent browser from taking default action, which when a form does not have an action attribute is often to reload the page

    const data = new FormData(event.target); //get the form data on submission

    const reservationIdObject = Object.fromEntries(data.entries()); //convert form data to JS object

    fetch(apiLocation + 'reservations/getByReservationId', { //api root URL + endpoint
        method : "POST",
        headers: { //no authorization header required for this endpoint
            'Content-Type': 'application/json'
        },
        body : JSON.stringify(reservationIdObject) //stringify object for request to API
    })
    .then(response => response.json()) //turn response into an object
    .then(json => {

        // console.log(JSON.stringify(json)); //for debugging, can be removed in prod

        if (json.success == true) { //things to do if the API accepted the request and returned a reservation

            document.getElementById("hotelName").innerHTML = json.hotelName;
            document.getElementById("roomSizeName").innerHTML = json.roomSizeName;
            document.getElementById("numberOfGuests").innerHTML = json.guests.length;

            if (json.wifi) {
                document.getElementById("amenities").insertAdjacentHTML("beforeend", "Wifi<br>")
            }
            if (json.breakfast) {
                document.getElementById("amenities").insertAdjacentHTML("beforeend", "Breakfast<br>")
            }
            if (json.parking) {
                document.getElementById("amenities").insertAdjacentHTML("beforeend", "Parking")
            }

            document.getElementById("checkIn").innerHTML = json.checkIn.split("-")[1] + "/" + json.checkIn.split("-")[2] + "/" + json.checkIn.split("-")[0];
            document.getElementById("checkOut").innerHTML = json.checkOut.split("-")[1] + "/" + json.checkOut.split("-")[2] + "/" + json.checkOut.split("-")[0];
            
            document.getElementById("form").hidden = true;
            document.getElementById("resInfo").hidden = false;
        }

        else { //things to do if the API did not accept the request and the lookup failed
            document.getElementById("formMessage").innerHTML = json.message; //show message sent by API explaining why the request failed
        }

    })
    
})

//attach this event listener to your form
document.getElementById("yourFormIdHere").addEventListener("submit", function (event) {

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

        console.log(JSON.stringify(json)); //for debugging, can be removed in prod

        if (json.success == true) { //things to do if the API accepted the request and returned a reservation
            
        }

        else { //things to do if the API did not accept the request and the lookup failed

        }

    })
    
})
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

const form = document.getElementById("form");
const formMessage = document.getElementById("formMessage");
const addGuestButton = document.getElementById("addGuestButton");

//if user is not logged in. Cookies returns string.
if(Cookies.get("loggedIn") !== "true"){
    document.getElementById("form").innerHTML = `You must be logged in to make a reservation, click <a href="./login.html">here</a> if you are not redirected in 3 seconds...`;
    setTimeout(function(){window.location.href = "./login.html"}, 3000); //callback to wait 3 seconds then redirect
}

//create today and tomorrow date objects
const today = new Date();
const tomorrow =  new Date()
tomorrow.setDate(today.getDate() + 1)

document.getElementById("checkIn").min = today.toISOString().split("T")[0]; //set minimum check in date to today
document.getElementById("checkOut").min = tomorrow.toISOString().split("T")[0]; //set minimum check out date to tomorrow

document.getElementById("checkIn").addEventListener("change", function(){updatePoints();});
document.getElementById("checkOut").addEventListener("change", function(){updatePoints();});

function updatePoints(){
    let checkIn = new Date(document.getElementById("checkIn").value);
    let checkOut = new Date(document.getElementById("checkOut").value);

    if(!isNaN(checkIn.getUTCFullYear()) && !isNaN(checkOut.getUTCFullYear())){ //prevents a null error from being thrown in the console
        let deltaDays = (checkOut - checkIn) / (1000 * 3600 * 24);
        formMessage.innerHTML = `You will earn ${deltaDays * 150} points for this trip.`;
    }
}

let currentGuest = 2;
addGuestButton.addEventListener("click", function(){
    if(currentGuest <= 5){
        addGuestButton.hidden = false;
        document.getElementById(`guest${currentGuest}`).hidden = false;
        currentGuest++;
        if (currentGuest === 6){
            addGuestButton.hidden = true;
        }
    }
});

for (let i = 2; i <= 5; i++) {
    document.getElementById(`removeGuestButton${i}`).addEventListener("click", function() {
        document.getElementById(`guest${i}FirstName`).value = null;
        document.getElementById(`guest${i}LastName`).value = null;
        document.getElementById(`guest${i}`).hidden = true;
        addGuestButton.hidden = false;
        currentGuest--;
    });
}

form.addEventListener("submit", function(){
    let requestBody = {};
    requestBody.location = document.querySelector('input[type=radio][name=location]:checked').value;
    requestBody.checkIn = document.getElementById('checkIn').value;
    requestBody.checkOut = document.getElementById('checkOut').value;
    requestBody.roomSize = document.querySelector('input[type=radio][name=roomSize]:checked').value;
    requestBody.wifi = document.querySelector('#wifi').value;
    requestBody.breakfast = document.querySelector('#breakfast').value;
    requestBody.parking = document.querySelector('#parking').value;
    let guests = [];
    for (let i = 1; i <= 5; i++) {
        let firstName = document.getElementById(`guest${i}FirstName`).value;
        let lastName = document.getElementById(`guest${i}LastName`).value;
        if(firstName !== "" && lastName !== ""){
            let guest = {};
            guest.firstName = firstName;
            guest.lastName = lastName;
            guests.push(guest);
        }
    }
    requestBody.guests = guests;
    sessionStorage.setItem("requestBody", JSON.stringify(requestBody));
    window.location.href = "./confirmReservation.html";
})
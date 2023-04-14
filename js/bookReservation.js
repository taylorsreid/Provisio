import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

const container = document.getElementById("container");
const hotelName = document.getElementById("hotelName");
const roomSizeName = document.getElementById("roomSizeName");
const hotelImages = document.getElementsByClassName("hotelImg");
const roomSizeImages = document.getElementsByClassName("roomSizeImg");
const checkInInput = document.getElementById("checkIn");
const checkOutInput = document.getElementById("checkOut");
const form = document.getElementById("form");
const formMessage = document.getElementById("formMessage");
const addGuestButton = document.getElementById("addGuest");

//if user is not logged in. Cookies returns string.
if(Cookies.get("loggedIn") !== "true"){
    container.innerHTML = `You must be logged in to make a reservation, click <a href="./login.html">here</a> if you are not redirected in 3 seconds...`;
    setTimeout(function(){window.location.href = "./login.html"}, 3000); //callback to wait 3 seconds then redirect
}

const steps = [1, 2, 3, 4];
let currentStep = 1;
function advanceStep() {
    currentStep++;
    document.getElementById(`step${currentStep}`).hidden = false;
    document.getElementById(`step${currentStep}`).scrollIntoView();
}

let nights;
function updatePoints(){
    let checkInDate = new Date(checkInInput.value);
    let checkOutDate = new Date(checkOutInput.value);

    if(!isNaN(checkInDate.getUTCFullYear()) && !isNaN(checkOutDate.getUTCFullYear())){ //prevents a null error from being thrown in the console
        nights = (checkOutDate - checkInDate) / (1000 * 3600 * 24);
        formMessage.innerHTML = `You will earn ${nights * 150} points for this trip.`;
    }
}

//step 1 - choose your hotel
Array.from(hotelImages).forEach(element => {
    element.addEventListener("click", function(){
        hotelName.value = element.getAttribute("hotelName");
        advanceStep();
    })
});

//step 2 -choose dates
//create today and tomorrow date objects
const today = new Date();
const tomorrow =  new Date();
tomorrow.setDate(today.getDate() + 1);
checkInInput.min = today.toISOString().split("T")[0]; //set minimum check in date to today
checkOutInput.min = tomorrow.toISOString().split("T")[0]; //set minimum check out date to tomorrow
//if both date fields are filled out, reveal the next step
checkInInput.addEventListener("change", function(){
    updatePoints();
    if (checkOutInput.value !== "") {
        advanceStep();
    }
});
checkOutInput.addEventListener("change", function(){
    updatePoints();
    if (checkInInput.value !== "") {
        advanceStep();
    }
});

//step 3 - pick your room size
Array.from(roomSizeImages).forEach(element => {
    element.addEventListener("click", function(){
        roomSizeName.value = element.getAttribute("roomSizeName");
        advanceStep();
    })
});

//step 4 - enter guest names
let guestCount = 2;
addGuestButton.addEventListener("click", function () {
        //output additional guest name fields
        document.getElementById("guestsBox").insertAdjacentHTML("beforeend", `
            Guest ${guestCount}:
            <input type="text" id="firstName${guestCount}" placeholder="First Name">
            <input type="text" id="lastName${guestCount}" placeholder="Last Name">
        `)
        guestCount++;
        if (guestCount >= 6) { //hides self (add guest button) if maximum guests is reached
            addGuestButton.hidden = true;
        }
});

//handle submit event
form.addEventListener('submit', function(event){
    event.preventDefault(); //prevent default event, which is reload page if no action is set in the HTML

    const formData = new FormData(event.target);

    let guests = [];

    for (let i = 1; i < guestCount; i++) {
        
        let guest = {
            firstName : "",
            lastName : ""
        };

        let firstName = document.getElementById(`firstName${i}`).value;
        let lastName = document.getElementById(`lastName${i}`).value;

        if (firstName !== "" && lastName !== "") {
            guest.firstName = firstName;
            guest.lastName = lastName;
            guests.push(guest);
        }

    }

    const formJson = Object.fromEntries(formData.entries());
    formJson.guests = guests;

    console.log(JSON.stringify(formJson));

    sessionStorage.setItem("reservationInfo", JSON.stringify(formJson));
    sessionStorage.setItem("nights", nights); //not sent to API but saved for convenience

    window.location.href = "./confirmReservation.html"

})
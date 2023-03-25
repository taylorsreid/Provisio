import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

const formMessage = document.getElementById("formMessage");

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

document.getElementById("checkIn").addEventListener("change", function(event){
    updatePoints();

});
document.getElementById("checkOut").addEventListener("change", function(event){
    updatePoints();
});

function updatePoints(){
    let checkIn = new Date(document.getElementById("checkIn").value);
    let checkOut = new Date(document.getElementById("checkOut").value);

    //prevents a null error from being thrown in the console
    if(!isNaN(checkIn.getUTCFullYear()) && !isNaN(checkOut.getUTCFullYear())){
        let deltaDays = (checkOut - checkIn) / (1000 * 3600 * 24);
        formMessage.innerHTML = `You will earn ${deltaDays * 150} points for this trip.`;
    }
}
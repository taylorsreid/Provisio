import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';
import "https://code.jquery.com/jquery-3.6.4.js";
import "https://code.jquery.com/ui/1.13.2/jquery-ui.js";

const formMessage = document.getElementById("formMessage");
const submitButton = document.getElementById("submitButton")

//if user is not logged in. Cookies returns string.
if(Cookies.get("loggedIn") !== "true"){
    document.getElementById("form").innerHTML = `You must be logged in to make a reservation, click <a href="./login.html">here</a> if you are not redirected in 3 seconds...`;
    setTimeout(function(){window.location.href = "./login.html"}, 3000); //callback to wait 3 seconds then redirect
}

//jquery datepicker adds calendar popout to date inputs
$(".date").datepicker({
    onClose : function(dateText){
        updatePoints();
    }
});

function updatePoints(){

    let checkIn = $('#checkIn').datepicker('getDate');
    let checkOut = $('#checkOut').datepicker('getDate')

    //prevents a null error from being thrown in the console
    if(checkIn !== null && checkOut !== null){

        //ensures that the user is picking a date that's not in the past, new Date().setHours(0,0,0,0) returns today at midnight
        if(checkIn >= new Date().setHours(0,0,0,0)){

            let deltaTime = checkOut.getTime() - checkIn.getTime();
            let deltaDays = deltaTime / (1000 * 3600 * 24);

            //ensures that the user picks at least a one night stay and not a negative number of nights
            if(checkOut > checkIn){
                submitButton.disabled = false;
                formMessage.innerHTML = `You will earn ${deltaDays * 150} points for this trip.`;
            }
            else{
                submitButton.disabled = true;
                formMessage.innerHTML = `You must select at least a one night stay.`;
            }
        }
        else{
            submitButton.disabled = true;
            formMessage.innerHTML = `You must select a check in date greater than or equal to today.`;
        }
    }
}

document.getElementById("submitButton").addEventListener("click", function(){

    sessionStorage.setItem("checkInLocalDate", $("#checkIn").val());
    sessionStorage.setItem("checkOutLocalDate", $("#checkOut").val());
    sessionStorage.setItem("roomSize", document.getElementById("roomSize").value);
    sessionStorage.setItem("wifi", document.getElementById("wifi").value);
    sessionStorage.setItem("breakfast", document.getElementById("breakfast").value);
    sessionStorage.setItem("parking", document.getElementById("parking").value);
    sessionStorage.setItem("guests", document.getElementById("guests").value);

    console.log($('#checkIn').datepicker('getDate'));
    console.log($('#checkOut').datepicker('getDate'));
    window.location.href = "./confirmReservation.html";

});
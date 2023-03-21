//
const apiLocation = "http://192.168.1.219:8080/api"

function register(){

}

function login(){
    
    fetch(apiLocation + '/login', {
        method : "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body : JSON.stringify({
            email : document.getElementById("email").value,
            password : document.getElementById("password").value
        })
    })
    .then(resp => resp.json())
    .then(json => {
        if (json.success == true) {

            //save commonly accessed items to session storage to avoid repeated database calls
            sessionStorage.setItem("loggedIn", true);
            sessionStorage.setItem("customerId", json.customerId);
            sessionStorage.setItem("email", json.email);
            sessionStorage.setItem("firstName", json.firstName);
            sessionStorage.setItem("lastName", json.lastName);

            window.location.href = "./index.html"; //redirect back to home page upon successful login

            document.cookie = "jwt=" + json.jwt + ";"; //save JWT to a cookie because it's the most secure way

        }
        else {
            // do something
        }
    })
    
}

function logout(){

    //falisify or remove session storage upon logout
    sessionStorage.setItem("loggedIn", false);
    sessionStorage.removeItem("customerId", null);
    sessionStorage.removeItem("email", null);
    sessionStorage.removeItem("firstName", " ");
    sessionStorage.removeItem("lastName");

    document.cookie = "jwt=; expires=Thu, 01 Jan 1970 00:00:00 UTC;"; //the only way to delete cookies is by setting them in the past
}

function makeReservation(){
    fetch(apiLocation + '/reservation', {
        method : "POST",
        headers: {
            'Content-Type': 'application/json',
            Authorization : 'Bearer ' + document.cookie,
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
    .then(json => console.log(json));
}
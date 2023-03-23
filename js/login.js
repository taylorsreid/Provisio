import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

document.getElementById("loginSubmitButton").addEventListener("click", function(){
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
            Cookies.set('jwt', json.jwt) //save JWT to a cookie because it's the most secure way
        }
        else {
            document.getElementById("message").innerHTML = json.message;
        }
    })
});
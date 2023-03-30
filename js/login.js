import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

//assign easy to use variables
let root = document.getElementById("root");
let submitButton = document.getElementById("submitButton");
let formMessage = document.getElementById("formMessage");

//if user is already logged in then replace login form with message. Cookies returns string.
if(Cookies.get("loggedIn") === "true"){
    root.innerHTML = (`You are already logged in as ${Cookies.get("firstName")} ${Cookies.get("lastName")}`);
}
submitButton.addEventListener("click", function(){
    //assign easy to use variables, must be assigned after click event otherwise they're blank
    let email = document.getElementById("email").value;
    let password = document.getElementById("password").value;

    fetch(apiLocation + 'login', {
        method : "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body : JSON.stringify({
            email : email,
            password : password
        })
    })
    .then(resp => resp.json())
    .then(json => {
        if (json.success == true) {
            //save commonly accessed items to a cookie to avoid repeated database calls
            Cookies.set("loggedIn", true, {expires : 1});
            Cookies.set("email", email, {expires : 1}); //not sent back by the API since the user has already entered it
            Cookies.set("firstName", json.firstName, {expires : 1});
            Cookies.set("lastName", json.lastName, {expires : 1});
            Cookies.set('jwt', json.jwt, {expires : 1}) //save JWT to a cookie because it's the most secure way
            window.location.href = "./index.html"; //redirect back to home page upon successful login
        }
        else {
            formMessage.innerHTML = json.message;
        }
    })
});
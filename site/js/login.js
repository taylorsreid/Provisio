import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

//assign easy to use variables
const form = document.getElementById("form");
const formMessage = document.getElementById("formMessage");

//if user is already logged in then replace page body with a message. Cookies returns string so compare to boolean as a string.
if(Cookies.get("loggedIn") === "true"){
    document.getElementById("userFirstName").innerText = Cookies.get("firstName");
    document.getElementById("userLastName").innerText = Cookies.get("lastName");
    document.getElementById("loggedInContent").hidden = false;
    document.getElementById("loggedOutContent").hidden = true;
}

//handles the submission of the form
form.addEventListener('submit', function(event){

    event.preventDefault(); //prevent browser from taking default action, which when a form does not have an action attribute is often to reload the page

    const data = new FormData(event.target); //get the form data on submission

    const loginValues = Object.fromEntries(data.entries()); //convert form data to JS object

    fetch(apiLocation + 'login', { //api root URL + login endpoint
        method : "POST",
        headers: {
            'Content-Type': 'application/json'
            //no authorization header required for this endpoint
        },
        body : JSON.stringify(loginValues) //stringify object for request to API
    })
    .then(response => response.json()) //turn response into an object
    .then(json => {
        if (json.success == true) {
            //things to do if the API accepted the request and the user successfully logged in

            //save commonly accessed items to a cookie to avoid repeated database calls
            //sameSite : strict because the API takes authorization headers, not cookies.  Cookies are only used as a storage mechanism.
            Cookies.set("loggedIn", true, {sameSite : 'strict'});
            Cookies.set("email", loginValues.email, {sameSite : 'strict'}); //not sent back by the API since the user has already entered it
            Cookies.set("firstName", json.firstName, {sameSite : 'strict'}); //can be used later
            Cookies.set("lastName", json.lastName, {sameSite : 'strict'});
            Cookies.set('jwt', json.jwt, {sameSite : 'strict'}) //save JWT to a cookie because it's the most secure way to store them
            window.location.href = "./index.html"; //redirect back to home page upon successful login

            //more things can go here

        }
        else {
            //things to do if the API did not accept the request and the login failed
            formMessage.innerHTML = json.message; //show message sent by API explaining why the login failed
        }
    })
});
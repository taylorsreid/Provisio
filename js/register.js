import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

//assign easy to use variables
let form = document.getElementById("form");
let submitButton = document.getElementById("submitButton");
let formMessage = document.getElementById("formMessage");

//if user is already logged in then replace page body with a message. Cookies returns string so compare to boolean as a string.
if(Cookies.get("loggedIn") === "true"){
    form.innerHTML = `You are already logged in as ${Cookies.get("firstName")} ${Cookies.get("lastName")}.`;
}

form.addEventListener('submit', function(event){

    event.preventDefault(); //prevent browser from taking default action, which when a form does not have an action attribute is often to reload the page

    const data = new FormData(event.target); //get the form data on submission

    const registerValues = Object.fromEntries(data.entries()); //convert form data to JS object


    //build request to API
    fetch(apiLocation + 'register', {
        method : "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body : JSON.stringify(registerValues) //stringify object for request to API
    })
    .then(resp => resp.json())
    .then(json => {
        //if the API responds that the account was created successfully, then save commonly accessed items to a cookie to avoid repeated database calls
        //API responds to successful register calls with a login request response
        if (json.success == true) {
             //save commonly accessed items to a cookie to avoid repeated database calls, {expires : 1} sets the cookie to be valid for one day
             Cookies.set("loggedIn", true, {expires : 1});
             Cookies.set("email", email, {expires : 1}); //not sent back by the API since the user has already entered it
             Cookies.set("firstName", json.firstName, {expires : 1});
             Cookies.set("lastName", json.lastName, {expires : 1});
             Cookies.set('jwt', json.jwt, {expires : 1}) //save JWT to a cookie because it's the most secure way to store them
             window.location.href = "./index.html"; //redirect back to home page upon successful login
        }
        else {
            formMessage.innerHTML = json.message;
        }
    })
});
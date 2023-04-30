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

form.addEventListener('submit', function(event){

    event.preventDefault(); //prevent browser from taking default action, which when a form does not have an action attribute is often to reload the page

    const data = new FormData(event.target); //get the form data on submission

    const registerValues = Object.fromEntries(data.entries()); //convert form data to JS object

    //if first password entry and confirm password entry match, build and send request to API
    //all other validation is done on backend
    if (registerValues.password === document.getElementById("confirmPassword").value) {
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
                //save commonly accessed items to a cookie to avoid repeated database calls
                //sameSite : strict because the API takes authorization headers, not cookies.  Cookies are only used as a storage mechanism.
                Cookies.set("loggedIn", true, {sameSite : 'strict'});
                Cookies.set("email", registerValues.email, {sameSite : 'strict'}); //not sent back by the API since the user has already entered it
                Cookies.set("firstName", json.firstName, {sameSite : 'strict'});
                Cookies.set("lastName", json.lastName, {sameSite : 'strict'});
                Cookies.set('jwt', json.jwt, {sameSite : 'strict'}) //save JWT to a cookie because it's the most secure way to store them
                window.location.href = "./index.html"; //redirect back to home page upon successful login
            }
            else {
                formMessage.innerHTML = json.message;
            }
        })
    } else {
        formMessage.innerHTML = "Passwords do not match."
    }

});

//clear warning message upon form reset
document.getElementById("cancelButton").addEventListener('click', function(){
    formMessage.innerHTML = "";
});
import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

//assign easy to use variables
let form = document.getElementById("form");
let submitButton = document.getElementById("submitButton");
let formMessage = document.getElementById("formMessage");

//if user is already logged in then replace login form with message. Cookies returns string.
if(Cookies.get("loggedIn") === "true"){
    form.innerHTML = `You are already logged in as ${Cookies.get("firstName")} ${Cookies.get("lastName")}.`;
}

submitButton.addEventListener("click", function(){

    //assign easy to use variables, must be assigned after click event otherwise they're blank
    let email = document.getElementById("email").value;
    let firstName = document.getElementById("firstName").value;
    let lastName = document.getElementById("lastName").value;
    let password = document.getElementById("password").value;

    //validate email and password via regex - credit to https://www.ocpsoft.org/tutorials/regular-expressions/password-regular-expression/ for the explanation of how to write regex
    let validEmail = email.match("@{1}") !== null; //only checks for the @ symbol
    let validPassword = password.match("^(?=.*[a-z])(?=.*[A-Z]).{8,}$") !== null; //checks if the password is at least 8 characters in length and includes one uppercase and one lowercase letter

    //validate that first name and last name are not blank since HTML required attribute does not work correctly with a JS EventListener
    let validFirstName = firstName !== "";
    let validLastName = lastName !== "";

    //if everything matches then build request to API
    if (validEmail && validPassword && validFirstName && validLastName) {
        fetch(apiLocation + 'register', {
            method : "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body : JSON.stringify({
                email : email,
                firstName : firstName,
                lastName : lastName,
                password : password
            })
        })
        .then(resp => resp.json())
        .then(json => {
            //if the API responds that the account was created successfully, then save commonly accessed items to a cookie to avoid repeated database calls
            if (json.success == true) {
                Cookies.set("loggedIn", true, {expires : 1});
                Cookies.set("customerId", json.customerId, {expires : 1});
                Cookies.set("email", json.email, {expires : 1});
                Cookies.set("firstName", json.firstName, {expires : 1});
                Cookies.set("lastName", json.lastName, {expires : 1});
                Cookies.set('jwt', json.jwt, {expires : 1}) //save JWT to a cookie because it's the most secure way
                window.location.href = "index.html"; //redirect back to home page upon successful login
            }
            else {
                formMessage.innerHTML = json.message;
            }
        })
    }
    //if the email or password doesn't meet requirements
    else {
        formMessage.innerHTML = ""; //blank out message box so messages don't pile up
        if(!validEmail){
            formMessage.insertAdjacentHTML('beforeend', `"${email}" is not a valid email.<br>`);
        }
        if(!validFirstName){
            formMessage.insertAdjacentHTML('beforeend', `First name cannot be blank.<br>`);
        }
        if(!validLastName){
            formMessage.insertAdjacentHTML('beforeend', `Last name cannot be blank.<br>`);
        }
        if(!validPassword){
            formMessage.insertAdjacentHTML('beforeend', `
                Your password does not meet requirements.
                It must be least 8 characters in length and include one uppercase letter and one lowercase letter.<br>
            `);
        }
    }

});
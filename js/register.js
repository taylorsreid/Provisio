import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

document.getElementById("registerSubmitButton").addEventListener("click", function(){

    //assign easy to use variables
    let email = document.getElementById("email").value;
    let firstName = document.getElementById("firstName").value;
    let lastName = document.getElementById("lastName").value;
    let password = document.getElementById("password").value;

    //validate email and password via regex - credit to https://www.ocpsoft.org/tutorials/regular-expressions/password-regular-expression/ for the explanation of how to write regex
    let validEmail = email.match("@{1}") !== null; //only checks for the @ symbol
    let validPassword = password.match("^(?=.*[a-z])(?=.*[A-Z]).{8,}$") !== null; //checks if the password is at least 8 characters in length and includes one uppercase and one lowercase letter

    //validate that first name and last name are not blank since HTML required attribute does not work correctly
    let validFirstName = firstName !== "";
    let validLastName = lastName !== "";

    //if everything matches then build request to API
    if (validEmail && validPassword && validFirstName && validLastName) {
        fetch(apiLocation + '/register', {
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
            //if the API responds that the account was created successfully, then save commonly accessed items to session storage to avoid repeated database calls
            if (json.success == true) {
                Cookies.set("loggedIn", true);
                Cookies.set("customerId", json.customerId);
                Cookies.set("email", json.email);
                Cookies.set("firstName", json.firstName);
                Cookies.set("lastName", json.lastName);
                Cookies.set('jwt', json.jwt) //save JWT to a cookie because it's the most secure way
                window.location.href = "index.html"; //redirect back to home page upon successful login
            }
            else {
                document.getElementById("message").innerHTML = json.message;
            }
        })
    }
    //if the email or password doesn't meet requirements
    else {
        document.getElementById("message").innerHTML = ""; //blank out message box so messages don't pile up
        if(!validEmail){
            document.getElementById("message").insertAdjacentHTML('beforeend', `"${email}" is not a valid email.<br>`);
        }
        if(!validFirstName){
            document.getElementById("message").insertAdjacentHTML('beforeend', `First name cannot be blank.<br>`);
        }
        if(!validLastName){
            document.getElementById("message").insertAdjacentHTML('beforeend', `Last name cannot be blank.<br>`);
        }
        if(!validPassword){
            document.getElementById("message").insertAdjacentHTML('beforeend', `
                Your password does not meet requirements.
                It must be least 8 characters in length and include one uppercase letter and one lowercase letter.<br>
            `);
        }
    }

});
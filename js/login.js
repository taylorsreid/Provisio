import apiLocation from "./apiLocation.js";
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

if(Cookies.get("loggedIn") === "true"){
    document.getElementById("root").innerHTML = (`
        You are already logged in as ${Cookies.get("firstName")} ${Cookies.get("lastName")}
    `);
}
else{
    document.getElementById("root").innerHTML = (`
        <label for="email">Email:</label>
        <input type="text" id="email" name="email">

        <br>

        <label for="password">Password:</label>
        <input type="password" id="password" name="password" minlength="8" required>

        <br>
        
        <input type="submit" value="Login" id="loginSubmitButton">
        
        <br>

        <div id="message"></div>
    `);
}

document.getElementById("loginSubmitButton").addEventListener("click", function(){

    let email = document.getElementById("email").value;
    let password = document.getElementById("password").value;

    fetch(apiLocation + '/login', {
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
            //save commonly accessed items to session storage to avoid repeated database calls
            Cookies.set("loggedIn", true);
            Cookies.set("customerId", json.customerId);
            Cookies.set("email", email); //not sent back by the API since the user has already entered it
            Cookies.set("firstName", json.firstName);
            Cookies.set("lastName", json.lastName);
            window.location.href = "./index.html"; //redirect back to home page upon successful login
            Cookies.set('jwt', json.jwt) //save JWT to a cookie because it's the most secure way
        }
        else {
            document.getElementById("message").innerHTML = json.message;
        }
    })
});
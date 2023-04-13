import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

if(Cookies.get("loggedIn") === "true"){ //cookies returns a string not a boolean
    //echo users name if they're logged in
    document.getElementById("welcomeMessage").innerHTML = `Welcome ${Cookies.get('firstName')} ${Cookies.get('lastName')}`;
}
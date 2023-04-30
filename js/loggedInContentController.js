import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

if(Cookies.get("loggedIn") === "true"){
    document.getElementById("loggedOutContent").hidden = true;
    document.getElementById("loggedInContent").hidden = false;
}
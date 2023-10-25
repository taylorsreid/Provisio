import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

if(Cookies.get("loggedIn") !== "true"){
    document.getElementById("footer").hidden = true;
}
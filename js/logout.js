import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';
//falisify or remove session storage upon logout
sessionStorage.setItem("loggedIn", false);
sessionStorage.removeItem("customerId");
sessionStorage.removeItem("email");
sessionStorage.removeItem("firstName");
sessionStorage.removeItem("lastName");
Cookies.remove("jwt")
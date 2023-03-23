import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';
//falisify or remove session storage upon logout
Cookies.set("loggedIn", false);
Cookies.remove("customerId");
Cookies.remove("email");
Cookies.remove("firstName");
Cookies.remove("lastName");
Cookies.remove("jwt")
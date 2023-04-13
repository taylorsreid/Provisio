import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';
//remove site related cookies and clear session storage upon logout
Cookies.remove("loggedIn");
Cookies.remove("email");
Cookies.remove("firstName");
Cookies.remove("lastName");
Cookies.remove("jwt")
sessionStorage.clear(); //for future use
import Cookies from 'https://cdn.jsdelivr.net/npm/js-cookie@3.0.1/+esm';

if(Cookies.get("loggedIn") === "true"){
    // 
    document.getElementById("welcomeMessage").innerHTML = `Welcome ${Cookies.get('firstName')} ${Cookies.get('lastName')}`; //super buggy
    console.log(`${Cookies.get('firstName')} ${Cookies.get('lastName')}`);
}
else{
    document.getElementById("welcomeMessage").innerHTML = "Welcome, please log in.";
}
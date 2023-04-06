function validate() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
  
    if (username == "" || password == "") {
      alert("Please fill in all fields");
      return false;
    }
  
    if (username == "admin" && password == "password") {
      alert("Login successful");
      window.location = "success.html"; // Redirect to success page
      return true;
    } else {
      alert("Invalid username or password");
      return false;
    }
  }
  
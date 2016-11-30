############### Code

<?php

$host="mysql.hostinger.co.uk"; // Host name 
$username=""; // Mysql username 
$password=""; // Mysql password 
$db_name=""; // Database name 
$tbl_name="DroneMembers"; // Table name 

// Connect to server and select databse.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");

// username and password sent from form 
$myemail=$_POST['myemail'];
$myusername=$_POST['myusername']; 
$mypassword=$_POST['mypassword']; 
$mypassword2=$_POST['mypassword2'];

$myusername = strtolower($myusername);

// To protect MySQL injection (more detail about MySQL injection)
$myemail = stripslashes($myemail);
$myusername = stripslashes($myusername);
$mypassword = stripslashes($mypassword);
$mypassword2 = stripslashes($mypassword2);

$mypassword = md5("Encryption" . $mypassword);
$mypassword2 = md5("Encryption" . $mypassword2);

if (strcmp($mypassword, $mypassword2) !== 0) {
    header("location:error2.html");
}

$myCheck = "SELECT Username FROM DroneMembers WHERE Username = '$myusername'";
$result = mysql_query($myCheck); 

$numResults = mysql_num_rows($result);
if($numResults==1){
	header("location:error3.html");
}
else{
	$sql = "INSERT INTO DroneMembers (Username, Password, Email)
	VALUES ('$myusername','$mypassword','$myemail')";	
	$result2=mysql_query($sql);
        header("location:index.html");
}
?>		
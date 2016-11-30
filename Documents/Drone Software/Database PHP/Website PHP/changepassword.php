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
$myusername=$_POST['myusername']; 
$mypassword=$_POST['mypassword']; 
$mypassword2=$_POST['mypassword2'];

$myusername = strtolower($myusername);

// To protect MySQL injection (more detail about MySQL injection)
$myusername = stripslashes($myusername);
$mypassword = stripslashes($mypassword);

$mypassword = md5("Encryption" . $mypassword);
$mypassword2 = md5("Encryption" . $mypassword2);

$myCheck = "SELECT Username FROM DroneMembers WHERE Username = '$myusername' And Password = '$mypassword'";
$result = mysql_query($myCheck); 

$numResults = mysql_num_rows($result);
if($numResults==1){
        $sql = "UPDATE DroneMembers SET Password = '$mypassword2' WHERE Username = '$myusername' And Password = '$mypassword'";	
	$result2=mysql_query($sql);
        header("location:index.html");
}
else{
        header("location:error1.html");
}
?>				
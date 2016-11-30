<?php

error_reporting(E_ALL ^ E_DEPRECATED);

$host="mysql.hostinger.co.uk"; // Host name 
$username=""; // Mysql username 
$password=""; // Mysql password 
$db_name=""; // Database name 
$tbl_name="DroneMembers"; // Table name 

// Connect to server and select database.
mysql_connect("$host", "$username", "$password")or die("cannot connect"); 
mysql_select_db("$db_name")or die("cannot select DB");

// username and email sent from form 
$myemail=$_POST['myemail']; 
$myusername=$_POST['myusername']; 

$myusername = strtolower($myusername);

// To protect MySQL injection (more detail about MySQL injection)
$myusername = stripslashes($myusername);
$myemail = stripslashes($myemail);

$myCheck = "SELECT Username FROM DroneMembers WHERE Username = '$myusername' And Email = '$myemail'";
$result = mysql_query($myCheck); 

$numResults = mysql_num_rows($result);
if($numResults==1){
		$newPass = rand(10000,99999);
                $newPass2 = md5("Encryption" . $newPass);
        $sql = "UPDATE DroneMembers SET Password = '$newPass2' WHERE Username = '$myusername' And Email = '$myemail'";	
	    $result2=mysql_query($sql);
echo "
<!DOCTYPE html>
<html >
  <head>
    <meta charset=\"UTF-8\">
    <title>CENG Caged Micro Drone</title>
    
    <link rel=\"stylesheet\" href=\"css/reset.css\">
    <link rel='stylesheet prefetch' href='http://fonts.googleapis.com/css?family=Roboto:400,100,300,500,700,900|RobotoDraft:400,100,300,500,700,900'>
    <link rel='stylesheet prefetch' href='http://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css'>
    <link rel=\"stylesheet\" href=\"css/style.css\"> 
  </head>

  <body>
    <!-- Mixins-->
    <!-- Pen Title-->
    <div class=\"pen-title\"><h1>Caged Micro Drone</h1></div>
    <div class=\"container\"><div class=\"card\"></div>
    <div class=\"card\">";
 echo "<h1 class=\"title\">Your New Password is: " . $newPass . "</h1>";
 echo "</div>

    <script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>
    <script src=\"js/index.js\"></script>
  </body>
</html>";
}
else{
echo $myCheck;
        header("location:error5.html");
}
?>				
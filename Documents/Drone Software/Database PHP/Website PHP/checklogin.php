<?php
session_start();
?>

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

$myusername = strtolower($myusername);

// To protect MySQL injection (more detail about MySQL injection)
$myusername = stripslashes($myusername);
$mypassword = stripslashes($mypassword);

$mypassword = md5("Encryption" . $mypassword);

$sql="SELECT * FROM $tbl_name WHERE username='$myusername' and password='$mypassword'";
$result=mysql_query($sql);

// Mysql_num_row is counting table row
$count=mysql_num_rows($result);

// If result matched $myusername and $mypassword, table row must be 1 row
if($count==1)
{
   // Register $myusername, $mypassword and redirect to file "login_success.php"
   $_SESSION['myusername'] = $myusername;
   $_SESSION['mypassword'] = $mypassword;
   header("location:userinfo.php");
}
else {
header("location:error1.html");
}
?>	
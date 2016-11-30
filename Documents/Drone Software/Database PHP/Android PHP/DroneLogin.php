<?php
/* CONNECTION SETTINGS */
$DB_HOST = "mysql.hostinger.co.uk";
$DB_UNAME = "";
$DB_PWD = "";
$DB_DATABASE = "";

/* Connecting to mysql database */
$con = mysqli_connect($DB_HOST, $DB_UNAME, $DB_PWD, $DB_DATABASE);

if (mysqli_connect_errno($con))
{
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

$username = $_GET['username'];
$password = $_GET['password'];

$username = strtolower($username);
$username = stripslashes($username);
$password = stripslashes($password);

$password = md5("Encryption" . $password);

$result = mysqli_query($con,"SELECT Username FROM DroneMembers where Username='$username' and Password='$password'");
$row = mysqli_fetch_array($result);
$data = $row[0];

if($data){
echo $data;
}

mysqli_close($con);
?>
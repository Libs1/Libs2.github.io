<?php
/* CONNECTION SETTINGS */
$DB_HOST = "mysql.hostinger.co.uk";
$DB_UNAME = "";
$DB_PWD = "";
$DB_DATABASE = "";

/* Connecting to mysql database */
$con = new mysqli($DB_HOST, $DB_UNAME, $DB_PWD, $DB_DATABASE);

if (mysqli_connect_errno($con))
{
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

$username = $_GET['username'];
$password = $_GET['password'];
$email = $_GET['email'];

$password = md5("Encryption" . $password);

$myCheck = "SELECT Username FROM DroneMembers WHERE Username = '$username';";
$result = mysqli_query($con,$myCheck);
$numResults = mysqli_num_rows($result);
if($numResults>=1)
{
	echo "recordexists";
}
else
{
	$sql = "INSERT INTO DroneMembers (Username, Password, Email)
	VALUES ('$username','$password','$email')";	

	if ($con->query($sql) === TRUE) {
		echo "New record created successfully";
	}
}

mysqli_close($con);
?>		
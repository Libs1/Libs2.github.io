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
$duration = $_GET['duration'];
$username = strtolower($username);

$myCheck = "SELECT User FROM DroneRuntime WHERE User = '$username';";
$result = mysqli_query($con,$myCheck);
$numResults = mysqli_num_rows($result);

if($numResults % 2 == 0)
{
	echo "Data Error";
}
else
{
	/*Replace drone with a getted username */
	$sql = mysqli_query($con, "INSERT INTO DroneRuntime(User,Info,Duration)
	VALUES ('$username','End','$duration')");

	if ($con->query($sql) === TRUE) {
		echo "Data Submitted";
	}
}

mysqli_close($con);
?>
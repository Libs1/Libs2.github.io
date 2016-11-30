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
$username = strtolower($username);

/*Replace drone with a getted username */
$myCheck = "SELECT User FROM DroneRuntime WHERE User = '$username';";
$result = mysqli_query($con,$myCheck);
$numResults = mysqli_num_rows($result);

if($numResults % 2 == 0)
{
	$sql = mysqli_query($con, "INSERT INTO DroneRuntime(User,Info)
	VALUES ('$username','Start')");
	if ($con->query($sql) === TRUE) {
		echo "Data Submitted";
	}
}
else{
	echo "Data Error";
}

mysqli_close($con);
?>
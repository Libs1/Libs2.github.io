<?php
/* array for JSON response */
$response = array();

/* CONNECTION SETTINGS */
$DB_HOST = "mysql.hostinger.co.uk";
$DB_UNAME = "";
$DB_PWD = "";
$DB_DATABASE = "";

/* Connecting to mysql database */
$mysqli = new mysqli($DB_HOST, $DB_UNAME, $DB_PWD, $DB_DATABASE);

if (mysqli_connect_errno()) {
    printf("Connect failed: %s\n", mysqli_connect_error());
    exit();
}

$username = $_GET['username'];
$username = strtolower($username);

/* CONSTRUCT THE QUERY change Drone to user database*/
$query="SELECT User,Info,Time,Duration,Id FROM DroneRuntime Where User = '$username' ORDER BY ID ASC";
$result = $mysqli->query($query) or die($mysqli->error.__LINE__);

if ($result === false)  {
    trigger_error('Wrong SQL: ' . $sql . ' Error: ' . $conn->error, E_USER_ERROR);
} else  {

    $response["stuff"] = array();

    while($row = $result->fetch_assoc())    {

        $stuff= array();

        /* ADD THE TABLE COLUMNS TO THE JSON OBJECT CONTENTS */
		$stuff["User"] = $row['User'];
		$stuff["Info"] = $row['Info'];
                $stuff["Time"] = $row['Time'];
                $stuff["Duration"] = $row['Duration'];
                $stuff["Id"] = $row['Id'];

        array_push($response["stuff"], $stuff);

        // $response[] = $row;
    }
    // success
    $response["success"] = 1;
    echo(json_encode($response));
}

/* CLOSE THE CONNECTION */
mysqli_close($mysqli);
?>			
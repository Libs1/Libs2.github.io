<?php
session_start();
?>

<?php
$DB_HOST = "mysql.hostinger.co.uk";
$DB_UNAME = "";
$DB_PWD = "";
$DB_DATABASE = "";

/* Connecting to mysql database */
$con = mysqli_connect($DB_HOST, $DB_UNAME, $DB_PWD, $DB_DATABASE);

if (mysqli_connect_errno()) {
    header("location:error4.html");
}

$myusername= $_SESSION['myusername'];

$result = mysqli_query($con, "SELECT User,Info,Time,ID,Duration FROM DroneRuntime Where User = '$myusername' ORDER BY ID DESC LIMIT 20");

echo "
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
  <div class=\"pen-title\"><h1>Caged Micro Drone</h1></div>
  <div class=\"container\"><div class=\"card\"></div>
  <div class=\"card\">
	<table border='3' cellpadding='5' cellspacing='5' bordercolor=\"black\" width=\"100%\">
	<tr>
	<th>Username</th>
        <th>Information</th>
	<th>Time</th>
        <th>Duration</th>
        <th>Battery Life</th>
	</tr>";

while($row = mysqli_fetch_array($result))
{
echo "<tr>";
echo "<td align=\"center\">" . $row['User'] . "</td>";
echo "<td align=\"center\">" . $row['Info'] . "</td>";
echo "<td align=\"center\">" . $row['Time'] . "</td>";
echo "<td align=\"center\">" . $row['Duration'] . "</td>";
$BL = $row['Duration'];
$BL = $BL/480 * 100;
$BL = 100 - $BL;
if($BL < 0){
$BL = 0;
}
echo "<td align=\"center\">" . round($BL, 2) . "%</td>";
echo "</tr>";
}

echo "
	</div>
	<script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>
    <script src=\"js/index.js\"></script>
  </body>
</html>";

mysqli_close($con);
?>
				
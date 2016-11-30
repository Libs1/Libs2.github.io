############### Code

// Check if session is not registered, redirect back to main page. 
// Put this code in first line of web page. 

<?php
session_start();
{
header("location:userinfo.php");
}
?>

<html>
<body>
Login Successful
</body>
</html>
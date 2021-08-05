<?php

$host="127.0.0.1";
$user ="root";
$password="Srikrishna66";
$database = "Facebook" 

$connect  = mysqli_connect($host,$user,$password,$database);

if(mysqli_connect_errno()){
    die("cannot connect to DB".mysqli_connect_error());
}

?>
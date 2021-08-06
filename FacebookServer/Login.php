<?php

//callServiceToNewRegister
//http://192.168.0.104/Login.php?email=mad@gmail.com&pword=1234456

require("DBinfo.inc");

$query ="select * from log_in where email='" . $_GET['email'] ."' and pword='" . $_GET['password'] . "'" ;

$result = mysqli_query($connect,$query);

if(!$result){
   die('Error Cannot run the query');
}

$userInfo = array();

while( $row= mysqli_fetch_assoc($result)){
    $userInfo[]= $row;
    break; //to be saved
}

if($userInfo) {
 print("{'msg':'pass login','info':". json_encode($userInfo) ."}");
}else{
    print("{'msg':'cannot login'}");
}

mysqli_free_result($result);
mysqli_close($connect);

?>
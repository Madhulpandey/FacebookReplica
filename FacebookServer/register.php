<?php

require("DBinfo.inc");

$query ="insert into log_in(first_name,email,pword,picture_path) values ('" . $_GET['first_name'] . "', '" . $_GET['email'] . "','" . $_GET['pword'] . "','" . $_GET['picture_path'] . "');" ;

$result = mysqli_query($connect,$query);

if(!$result){
    $output = "{'msg':'fail'}"
}else{
    $output = "{'msg':'user is added'}"
}

print($output)

mysqli_close($connect);

?>
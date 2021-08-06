<?php

require("DBinfo.inc");

//callServiceToNewRegister
//http://192.168.0.104/Posts.php?user_id=2&post_text=hello&post_picture=home/u.png

$query ="insert into posts(user_id,post_text,post_picture) values ('" . $_GET['user_id'] . "', '" . $_GET['post_text'] . "','" . $_GET['post_picture'] . "');" ;

$result = mysqli_query($connect,$query);

if(!$result){
    $output = "{'msg':'fail'}";
}else{
    $output = "{'msg':'post is added'}";
}

print($output);

mysqli_close($connect);

?>
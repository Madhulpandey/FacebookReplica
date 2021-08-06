<?php

//CallService
//http://127.0.0.1/user_following.php?op=1&user_id=1&following_user_id=2

require("DBinfo.inc");
if($_GET['user_id']==1){
    $query ="insert into following(user_id,following_user_id) values (".$_GET['user_id'].",".$_GET['following_user_id'].")" ;
}   

$result = mysqli_query($connect,$query);

if(!$result){
    $output = "{'msg':'fail'}";
}else{
    $output = "{'msg':'user is added'}";
}

print($output);

mysqli_close($connect);

?>
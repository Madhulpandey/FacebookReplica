<?php

//callServiceToNewRegister
//email=mad@gmail.com&pword=1234456

require("DBinfo.inc");
if ( $_GET['op']==1) { // my following
// case 1: search op=2, user_id =? , StartFrom=0
//http://192.168.0.105/Tweetlist.php?op=1&user_id=1&StartFrom=0
$query="select * from posts where user_id in (select friend_user_id from friends where user_id=". $_GET['user_id'] . ") or user_id=" . $_GET['user_id'] . " order by post_date DESC". 
" LIMIT 20 OFFSET ". $_GET['StartFrom']  ;  // $usename=$_GET['username'];
}

elseif ( $_GET['op']==2) { // specific person post
// case 2: search op=2, user_id =? , StartFrom=0
//http://192.168.0.105/Tweetlist.php?op=2&user_id=1&StartFrom=0
$query="select * from posts where user_id=" . $_GET['user_id'] . " order by post_date DESC" . 
" LIMIT 20 OFFSET ". $_GET['StartFrom'] ;  // $usename=$_GET['username'];
}
elseif($_GET['op']==3){
//case 3:Search op=3,query? , startFrom=0
//http://http://192.168.0.105/Tweetlist.php?op=3&query=fin&StartFrom=0
$query ="select * from user_posts where post_text like '%". $_GET['query'] ."%' LIMIT 20 OFFSET ". $_GET['StartFrom'] ;
}
    


$result = mysqli_query($connect,$query);

if(!$result){
   die('Error Cannot run the query');
}

$userTweets= array();

while( $row= mysqli_fetch_assoc($result)){
    $userTweets[]= $row;
    // break; //to be saved
}

if($userTweets) {
 print("{'msg':'has tweet','info':". json_encode($userTweets) ."}");
}else{
    print("{'msg':'no tweet'}");
}

mysqli_free_result($result);
mysqli_close($connect);

?>

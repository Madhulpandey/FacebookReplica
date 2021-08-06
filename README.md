# Social Media Replica App

A replica of a generic social media app hosted on a local server which uses mysql as it's database and firebase's storage services to store and access image files </br>
It is a practise app with basic UI developed in Kotlin.

## Features of the App:
* It allows user to Register as a new user with basic details like name,email id,password and profile picture
* It allows users to Login if they are a old user with their email id and passwords
* Users can post pictures with text along with it which can further be viewed by other users
* The user can follow or unfollow another user 
* The user can search through the feed by entering string values on the search bar.


## Permissions:
* READ EXTERNAL STORAGE
* READ IMAGE FILES


## DATABASE STRUCTURE:
### Tables:
* Friends table-->user_id , friend_user_id
* Login Table -->user_id,first_name,email,password,picture_path
* Posts Table --> user_id,post_id,post_text,post_picture,post_date

### Views:
* USER_POSTS -->post_id,post_text,post_picture,post_date,first_name,picture_path,user_id




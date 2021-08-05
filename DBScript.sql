drop database Facebook;
create Database Facebook;

use Facebook

create table log_in(
user_id int AUTO_INCREMENT PRIMARY KEY,
first_name Varchar(50),
email Varchar(50),
pword Varchar(50),
picture_path Varchar(350)
);

create table posts(
user_id int ,
post_id int AUTO_INCREMENT PRIMARY KEY,
post_text Varchar(250),
post_picture Varchar(350),
post_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (user_id) REFERENCES log_in(user_id) 
);

create table friends(
user_id int,
friend_user_id int,
FOREIGN KEY (user_id) REFERENCES log_in(user_id),
FOREIGN KEY (friend_user_id) REFERENCES log_in(user_id)     
);

create view user_posts as 
    
    select posts.post_id, posts.post_text, posts.post_picture, posts.post_date,
    log_in.first_name, log_in.picture_path , posts.user_id
    from posts 
    inner join log_in
    on posts.user_id=log_in.user_id;
 
--  describe user_posts;

package com.example.facebook

class Ticket {
    var tweetID:String?=null
    var tweetText:String?=null
    var tweetImageURL:String?=null
//    var tweetPersonUID:String?=null
    var tweetDate:String?=null
    var personName:String?=null
    var personImage:String?=null
    var personID:String?=null



    constructor(tweetID:String,tweetText:String,tweetImageURL:String,tweetDate:String,personName:String,personImage:String,personID:String){
        this.tweetID=tweetID
        this.tweetText=tweetText
        this.tweetImageURL=tweetImageURL
      //  this.tweetPersonUID=tweetPersonUID
        this.personImage=personImage
        this.personName=personName
        this.tweetDate=tweetDate
        this.personID=personID
    }
}
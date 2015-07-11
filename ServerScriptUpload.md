#This page show how to upload Server Script
# Deploy instruction for our course(wsmlby wyl .etc) #
## Server ##

We use node.js server provided by Aliyun.

To get account information details to upload to the server. please Contact me.



## Details ##

Upload script  index.js with ftp

ftp.ace.aliyun.com:2222


# Deploy instruction for public #

Aliyun is not needed, any node.js server is okay.

1 Just deploy the server code

2 change the url0 in Android Code in
```
client/Android/v1/src/com/me/whiteboard/http/JsonTransfer.java	
url0 = "http://whiteboard.aliapp.com";
```
> to you server's base url.
3 build the android project

4 ready to go
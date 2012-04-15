var http = require("http");
var urlparse=require('url');
var storage={};
var _time;
function log(s)
{
    var t=new Date();
    console.log(s+":"+(t-_time));
    _time=t;

}
function hasuser(name)
{
    if(storage[name])
        return true;
    return false;
}
function appendmsgforuser(name,msg)
{
    if(hasmsg(name))
    {
        storage[name].msg=storage[name].msg+","+msg;
        return;
    }
    if(!hasuser(name))
    {
        storage[name]={};
    }
    storage[name].hasmsg=true
    storage[name].msg=msg
}
function hasmsg(name)
{
    if(storage[name])
        if(storage[name].hasmsg)
            if(storage[name].hasmsg==true)
                return true;
    return false;
}
function outputmsg(name,r2)
{
    r2.write(storage[name].msg);
    storage[name].hasmsg=false
    storage[name].msg=null
    r2.end();
}
function getmsg(r1,r2,querys,ttv)
{
    if(ttv);
    else ttv=60;
    if(ttv===0)return;
        if(hasmsg(querys['name']))
        {  
            outputmsg(querys['name'],r2);
            
            return;
        }
    setTimeout(function(){getmsg(r1,r2,querys,ttv-1);},3000);   
    
}
function savemsg(r1,r2,querys)
{
    appendmsgforuser(querys['name'],querys['msg']);
    r2.write('saved');
    r2.end();
}
http.createServer(function(request, response) {
log("go");
  var querys=urlparse.parse(request.url,true);
log("parsed");
  response.writeHead(200, {"Content-Type": "text/plain"});
  if(querys.pathname=="/get")
    getmsg(request,response,querys.query);
  else if(querys.pathname=="/post")
    savemsg(request,response,querys.query);
log("done");
  
}).listen(10080);
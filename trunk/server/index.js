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
function getmsg(r1,r2,querys,ttv)
{
    if(ttv);
    else ttv=60;
    if(ttv===0)return;
        if(storage[querys['name']])
        {  r2.write(""+storage[querys['name']][querys['key']]);
            storage[querys['name']]=undefined;
            r2.end();
            return;
        }
    setTimeout(function(){getmsg(r1,r2,querys,ttv-1);},1000);   
    
}
function savemsg(r1,r2,querys)
{
    r2.write('saving');
    log("save");
    storage[querys['name']]={};
    storage[querys['name']][querys['key']]=querys['val'];
    r2.write('ok');
    log("saveover");
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
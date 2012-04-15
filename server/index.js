var http = require("http");
var urlparse=require('url');
function Storage()
{
    this.max_id=0;
}
function MsgPool()
{
    //this.Storage=storage;
    this.base_id=0;
    this.next_id=0;
    this.msg=[];
}
MsgPool.prototype.AppendMsg=function(msg)
{
    this.msg.push(msg);
    this.next_id=this.msg.length+this.base_id;
}
MsgPool.prototype.HasNewMsg=function(fromid)
{
    return fromid<this.next_id;
}
MsgPool.prototype.GetMsg=function(fromid)
{
    return this.msg.slice(fromid);
}
Storage.prototype.HasMsgPool=function(name)
{
    if(this[name])
        return true;
    return false;
}
Storage.prototype.HasNewMsg=function(name,fromid)
{
    if(this.HasMsgPool(name))
        return this[name].HasNewMsg(fromid);
    return false;
}
Storage.prototype.GetMsg=function(pool,fromid)
{
    if(this.HasNewMsg(pool,fromid))
    {
        return this[pool].GetMsg(fromid);
    }
    return [];
}
Storage.prototype.AppendMsg=function(pool,msg)
{
    if(!this.HasMsgPool(pool))
        this[pool]=new MsgPool();
    this[pool].AppendMsg(msg);
}
var storage=new Storage();
var _time;
function log(s)
{
    var t=new Date();
    console.log(s+":"+(t-_time));
    _time=t;

}
function outputmsg(name,fromid,r2)
{
    r2.write(""+storage.GetMsg(name,fromid));
    //log();
    r2.end();
}
function getmsg(r1,r2,querys)
{
    var fromid=parseInt(querys['fromid']);
    log("fromid:"+fromid);
    var name=querys['name'];
    getmsg_loop(r2,name,fromid,60);
}
function getmsg_loop(r2,name,fromid,ttv)
{
    if(ttv===0)return;
        if(storage.HasNewMsg(name,fromid))
        {  
            outputmsg(name,fromid,r2);
            return;
        }
    setTimeout(function(){getmsg_loop(r2,name,fromid,ttv-1);},2000);   
    
}
function savemsg(r1,r2,querys)
{
    r1.on('data',function(chunk){
        log("daads");
        storage.AppendMsg(querys['name'],chunk);
        r2.write('saved');
        r2.end();
        });
    
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
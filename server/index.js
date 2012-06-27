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
    this.n=0;
    this.wl=[];
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
Storage.prototype.GetWait=function(pool)
{
    if(!this.HasMsgPool(pool))
        return [];
    return this[pool].wl;
}
Storage.prototype.AppendMsg=function(pool,msg)
{
    if(!this.HasMsgPool(pool))
        this[pool]=new MsgPool();
    this[pool].AppendMsg(msg);
}
Storage.prototype.AddPool=function(pool)
{
    if(!this.HasMsgPool(pool))
        this[pool]=new MsgPool();
}
Storage.prototype.AddWait=function(wait,from,pool)
{
    if(!this.HasMsgPool(pool))
        return;
        log("AddWait:"+from+pool);
    this[pool].wl.push({r:wait,f:from});
}
Storage.prototype.removePool=function(pool)
{
    if(this.HasMsgPool(pool))
        delete(this[pool]);
}
Storage.prototype.EnterPool=function(pool)
{
    if(this.HasMsgPool(pool))
      {
        return ++(this[pool].n);
      }
     return 0;
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
    var pp=storage.GetMsg(name,fromid);
    if(pp.length==0)
    {
        storage.AddWait(r2,fromid,name);
        return;
    }
    r2.write(""+pp);

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
    //if(ttv===0)return;
        if(storage.HasNewMsg(name,fromid))
        {  
            outputmsg(name,fromid,r2);
            return;
        }
        else
        {
            storage.AddWait(r2,fromid,name);
        }
    //setTimeout(function(){getmsg_loop(r2,name,fromid,ttv-1);},800);   
    
}
function savemsg(r1,r2,querys)
{
    r1.on('data',function(chunk){
        if(chunk.length>0)
        {
            var msgs=(""+chunk).split(",");
            for(var i=0;i<msgs.length;i++)
                storage.AppendMsg(querys['name'],msgs[i]);
        }
        r2.write('saved');
        var wl=storage.GetWait(querys['name']).slice(0);
        while(wl.length>0)
        {
            var ff=wl.pop();
            outputmsg(querys['name'],ff.f,ff.r);
        }
        r2.end();
        });
    
}
function getifexist(r1,r2,q)
{
    if(storage.HasMsgPool(q['name']))
    {
        r2.write('1');
        r2.end();
    }
    else
    {
        r2.write('0');
        r2.end();
    }
    
}
function createroom(r1,r2,q)
{
    if(storage.HasMsgPool(q['name']))
    {
        r2.write('0');
        r2.end();
    }
    else
    {
        storage.AddPool(q['name']);
        r2.write('1');
        r2.end();
    }
        
}
function rmroom(r,query)
{
    storage.removePool(query['name']);
    r.write("1");
    r.end();
}
function enterroom(r,query)
{
    
    r.write(""+storage.EnterPool(query['name']));
    r.end();
}
http.createServer(function(request, response) {
log("go");
  var querys=urlparse.parse(request.url,true);
log("parsed");
  response.writeHead(200, {"Content-Type": "text/plain"});
  if(querys.pathname=="/ifexists")
    getifexist(request,response,querys.query);
  else if(querys.pathname=="/create")
    createroom(request,response,querys.query);
  else if(querys.pathname=="/get")
    getmsg(request,response,querys.query);
  else if(querys.pathname=="/post")
    savemsg(request,response,querys.query);
  else if(querys.pathname=="/rmroom")
    rmroom(response,querys.query);
  else if(querys.pathname=="/enter")
    enterroom(response,querys.query);
log("done");
  
}).listen(10080);
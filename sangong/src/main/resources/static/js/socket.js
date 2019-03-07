var websocket = null;

//判断当前浏览器是否支持WebSocket
if('WebSocket' in window){
    var token = getToken("nb_token");
    websocket = new WebSocket("ws://127.0.0.1:9095/gamesocket?token="+token);
}
else{
    alert('Not support websocket')
}

//读取cookies
function getToken(name)
{
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");

    if(arr=document.cookie.match(reg))

        return unescape(arr[2]);
    else
        return null;
}


//连接成功建立的回调方法
websocket.onopen = function(event){
    setMessageInnerHTML("open");
}

//接收到消息的回调方法
websocket.onmessage = function(event){
    setMessageInnerHTML(event.data);
}

//连接关闭的回调方法
websocket.onclose = function(){
    setMessageInnerHTML("close");
}

//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
window.onbeforeunload = function(){
    websocket.close();
}

//关闭连接
function closeWebSocket(){
    websocket.close();
}

//login(1, "登录"), logout(2, "登出"),
//add(3, "进房间"), leave(4, "离开房间"),
//confirm(5, "准备"), unconfirm(6, "取消准备"),
//begin(7, "开始"), createRoom(8, "创建房间"),
//selectPeopleType(9, "选择币类型");
//发送消息
function send(){
    var message = document.getElementById('text').value;
    var str = {"messageType":"菜鸟教程", "site":"http://www.runoob.com"}
    var strJson = JSON.stringify(str)
    websocket.send(message);
}
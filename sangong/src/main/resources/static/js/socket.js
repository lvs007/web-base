var websocket = null;

//判断当前浏览器是否支持WebSocket
if('WebSocket' in window){
    var token = getToken("nb_token");
    websocket = new WebSocket("ws://127.0.0.1:9095/gamesocket?token="+token);
}
else{
    alert('Not support websocket')
}

//连接发生错误的回调方法
websocket.onerror = function(){
};

//连接关闭的回调方法
websocket.onclose = function(){
}

//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
window.onbeforeunload = function(){
    websocket.close();
}

//解析message
function parseMessage(message){
  var obj = JSON.parse(message);
  return obj;
}

//关闭连接
function closeWebSocket(){
    websocket.close();
}

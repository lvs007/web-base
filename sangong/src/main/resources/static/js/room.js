//login(1, "登录"), logout(2, "登出"),
//add(3, "进房间"), leave(4, "离开房间"),
//confirm(5, "准备"), unconfirm(6, "取消准备"),
//begin(7, "开始"), createRoom(8, "创建房间"),
//selectPeopleType(9, "选择币类型"), invite(10, "邀请用户加入房间")
//sendInvite(11, "发送邀请"),
//发送消息
function send(){
    var str = {"messageType":"leave", "site":"http://www.runoob.com"}
    var strJson = JSON.stringify(str)
    websocket.send(strJson);
}

function invite(){
    var token = getToken("nb_token");
    var name = document.getElementById('inviteName').value;
    var str = {"messageType":"sendInvite", "name":name, "token":token}
    var strJson = JSON.stringify(str)
    websocket.send(strJson);
    closeDialog('inviteNameWindow');
}

function getRoom(roomId){
    var token = getToken("nb_token");
    var str = {"messageType":"getRoom", "roomId":roomId, "token":token}
    var strJson = JSON.stringify(str)
    websocket.send(strJson);
}

function addRoom(roomId,token,peopleType){
    var str = {"messageType":"add", "roomId":roomId, "token":token,"peopleType":peopleType}
    var strJson = JSON.stringify(str)
    websocket.send(strJson);
}

function leave(){
    var truthBeTold = window.confirm("是否离开房间");
    if (!truthBeTold) {
      return;
    }
    var token = getToken("nb_token");
    var roomId = document.getElementById('roomId').innerText;
    var str = {"messageType":"leave", "roomId":roomId, "token":token}
    var strJson = JSON.stringify(str)
    websocket.send(strJson);
}

function confirmGame(){
    var token = getToken("nb_token");
    var coin = document.getElementById('coin').value;
    var str = {"messageType":"confirm", "coin":coin, "token":token}
    var strJson = JSON.stringify(str)
    websocket.send(strJson);
    document.getElementById('confirmDiv').style.display="none";//隐藏
    document.getElementById('unconfirmDiv').style.display="";//显示
}

function unconfirmGame(){
    var token = getToken("nb_token");
    var str = {"messageType":"unconfirm", "token":token}
    var strJson = JSON.stringify(str)
    websocket.send(strJson);
    document.getElementById('confirmDiv').style.display="";//显示
    document.getElementById('unconfirmDiv').style.display="none";//隐藏
}

function zuozhuang(){
    var token = getToken("nb_token");
    var str = {"messageType":"zuozhuang", "token":token}
    var strJson = JSON.stringify(str)
    websocket.send(strJson);
}

function unzuozhuang(){
    var token = getToken("nb_token");
    var str = {"messageType":"unzuozhuang", "token":token}
    var strJson = JSON.stringify(str)
    websocket.send(strJson);
}

function begin(){
    var token = getToken("nb_token");
    var str = {"messageType":"begin", "token":token}
    var strJson = JSON.stringify(str)
    websocket.send(strJson);
}

function recharge(){
    var token = getToken("nb_token");
    var coin = document.getElementById('coin').value;
    var peopleType = getPeopleType();
    var str = {"messageType":"recharge", "coin":coin, "token":token, "peopleType":peopleType}
    var strJson = JSON.stringify(str)
    websocket.send(strJson);
    closeDialog('recharge');
}

function tirenWindow(){
    getRequest("/v1/door/get-room-people",2000,function(data){
      var data = JSON.parse(data);
      var div = document.getElementById("tirenWindow");
      div.innerHTML = "";
      var table = document.createElement("table");

      var titleName=['编号','姓名','选中'];
      //创建tr,td项
      var tr=document.createElement('tr');
      for(var i=0;i<titleName.length;i++){
          var td=document.createElement('td');
          td.innerHTML=titleName[i];
          tr.appendChild(td);
      }
      table.appendChild(tr);
      var peopleList = data.data.result;
      for(var i=0;i<peopleList.length;i++){
         var newRow=table.insertRow();
         var newcellname=newRow.insertCell(0);
         newcellname.innerHTML=peopleList[i].userId;
         newcellname=newRow.insertCell(1);
         newcellname.innerHTML=peopleList[i].name;
         newcellname=newRow.insertCell(2);
         newcellname.innerHTML='<input type="radio" name="radio_userId" value="'+peopleList[i].userId+'"/>';
      }
      var newRow=table.insertRow();
      var newcellname=newRow.insertCell(0);
      newcellname=newRow.insertCell(1);
      newcellname.innerHTML='<button onclick="closeDialog(\'tirenWindow\')">取消</button>';
      newcellname=newRow.insertCell(2);
      newcellname.innerHTML='<button onclick="tiren()">确认</button>';
      div.appendChild(table);
      openDialog('tirenWindow');
    });
}

function tiren(){
   var radio=document.getElementsByName("radio_userId");
   var userId=null;   //  selectvalue为radio中选中的值
   for(var i=0;i<radio.length;i++){
      if(radio[i].checked==true) {
         userId=radio[i].value;
         break;
     }
  }
  if(userId == null){
    alert("请选择一个用户");
    return;
  }
  var token = getToken("nb_token");
  var str = {"messageType":"tiren", "token":token,"userId":userId}
  var strJson = JSON.stringify(str)
  websocket.send(strJson);
  closeDialog('tirenWindow');
}

function logOut(){
  getRequest("/v1/door/log-out",1000,function(data){
  });
}

function quickJoin(){
  var form = document.getElementById('quickForm');
  var coin = document.getElementById('coinShow').innerText;
  if(coin < 10){
    alert("金币不足，请充值。进入房间最小额度是：10TRX。");
    return;
  }
  var input = createPeopleType();
  form.appendChild(input);
  form.submit();
  document.body.removeChild(input);
}

function createRoom(){
  var form = document.getElementById('createForm');
  var coin = document.getElementById('coinShow').innerText;
  if(coin < 10000){
    alert("金币不足，请充值。创建房间的最小额度是：10000TRX。");
    return;
  }
  var input = createPeopleType();
  form.appendChild(input);
  form.submit();
  document.body.removeChild(input);
}

function createPeopleType(){
  var input = document.createElement("input");
  input.type = "hidden";
  input.name = "peopleType";
  input.value = getPeopleType();
  return input;
}

function getPeopleType(){
  var obj=document.getElementById('peopleType');
  var index=obj.selectedIndex; //序号，取当前选中选项的序号
  return obj.options[index].value;
}

function peopleTypeChange(){
  var peopleType = getPeopleType();
  getRequest("/v1/door/change-people-type?peopleType="+peopleType,1000,function(data){
    var data = JSON.parse(data);
    if(data.success){
      document.getElementById('name').innerText=data.data.name;
      document.getElementById('coinShow').innerText=data.data.coin;
      alert("切换成功");
    }else{
      alert("切换失败");
    }
  });
}

function queryTx(){
  var pk = document.getElementById('pk').value;
  getRequest("/v1/door/query-trx?pk="+pk,2000,function(data){
    var data = JSON.parse(data);
    document.getElementById('trxBalance').innerText = "余额："+data.data.value/1000000;
  });
}
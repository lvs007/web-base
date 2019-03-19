function openDialog(divId){
    document.getElementById(divId).style.display='block';
    document.getElementById('fade').style.display='block';
}
function closeDialog(divId){
    document.getElementById(divId).style.display='none';
    document.getElementById('fade').style.display='none';
}

function jia(id){
  var coin = document.getElementById(id).value;
  document.getElementById(id).value = Number(coin) + 100;
}

function jian(id){
  var coin = document.getElementById(id).value;
  if(coin < 100){
    document.getElementById(id).value = 10;
  }else{
    document.getElementById(id).value = coin - 100;
  }
}

function findWindowHeight(){//函数：获取尺寸
  /* <![CDATA[ */
  var winHeight = 0;
  //获取窗口高度
  if (window.innerHeight)
    winHeight = window.innerHeight;
  else if ((document.body) && (document.body.clientHeight))
    winHeight = document.body.clientHeight;
  //通过深入Document内部对body进行检测，获取窗口大小
  if (document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth)
  {
    winHeight = document.documentElement.clientHeight;
  }
  return winHeight * 94 / 100;
  /* ]]> */
}
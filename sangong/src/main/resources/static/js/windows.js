function openDialog(divId){
    document.getElementById(divId).style.display='block';
    document.getElementById('fade').style.display='block'
}
function closeDialog(divId){
    document.getElementById(divId).style.display='none';
    document.getElementById('fade').style.display='none'
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
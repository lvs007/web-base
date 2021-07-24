document.write("<script type='text/javascript' src='/static/js/bignumber.js'></script>");
/*
* @url: url link
* @action: "get", "post"
* @json: {'key1':'value2', 'key2':'value2'}
*/
function doFormRequest(url, action, json)
{
    var form = document.createElement("form");
    form.action = url;
    form.method = action;

    // append input attribute and valus
    for (var key in json)
    {
        if (json.hasOwnProperty(key))
        {
            var val = json[key];
            input = document.createElement("input");
            input.type = "hidden";
            input.name = key;
            input.value = val;

            // append key-value to form
            form.appendChild(input)
        }
    }

    // send post request
    document.body.appendChild(form);
    form.submit();

    // remove form from document
    document.body.removeChild(form);
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

function getRequest( url, time, callback ){
    var request = new XMLHttpRequest();
    var timeout = false;
    var timer = setTimeout( function(){
        timeout = true;
        request.abort();
    }, time );
    request.open( "GET", url );
    request.onreadystatechange = function(){
        if( request.readyState !== 4 ) return;
        if( timeout ) return;
        clearTimeout( timer );
        if( request.status === 200 ){
            callback( request.responseText );
        }
    }
    request.send( null );
}

// backend.js
const ipfs = "https://ipfs.io/ipfs/";
/*main network*/
const local = "http://gateway.daynft.org/ipfs/"
const ipAddr = "ipfs.daynft.org";//"api.btfs.trongrid.io";
const ipPort = "80";//"443";
const gatePort = "80";
/*local*/
//const local = "http://127.0.0.1:8080/ipfs/"
//const ipAddr = "127.0.0.1";//"api.btfs.trongrid.io";
//const ipPort = "5001";//"443";
//const gatePort = "8080";
let url = "";
async function addFile(file) {
    const ipfs = window.IpfsHttpClient.create({host:ipAddr, port:ipPort, protocol:'http'});
//    const Buffer = window.IpfsHttpClient.constructor().Buffer;
//    const newReader = new FileReader();
//    var movieHash, coverHash;
//    newReader.onloadend = function () {
//        ipfs.add(file, (err, result) => {
//            if(err) {
//                console.error(err);
//                return
//            }
//            let url = "http://"+ipAddr+":"+gatePort+"/ipfs/"+result.hash;
//            console.log(url);
//            document.getElementById("fileUrl").innerText= url;
//        });
//    };
//    newReader.readAsArrayBuffer(file);
    //
    try {
      const result = await ipfs.add(file, {
        progress: (prog) => console.log(`received: ${prog}`),
      });
      // 获取上传文件hash值
      url = result.cid.toString();
      //https://ipfs.io/ipfs/
    } catch (err) {
      console.error(err);
    }
}

const imgContractAdd = "TYffZfNPsTsYnDZPx9Rho3VgJC9SKuksfY";
const vedioContractAdd = "TBHSiLb9rLvx4Po6JzWK8LkPSj9bxdZou8";
const tokenContractAdd = "TDFsqpgihK6kHJ7uh5Mmyu54UQ8D1fXTkV";
const decimals = 1000000000000000000;
async function createNft() {
  if(!validCreateNft()){
    return;
  }
  var desc = document.getElementById('desc').value;
  var title = document.getElementById('title').value;
  var keyword = getRadioBoxValue("keyword");
  var contractAddress = window.tronWeb.address.toHex(imgContractAdd);
  if(keyword == "短视频"){
    contractAddress = window.tronWeb.address.toHex(vedioContractAdd);
  }
  var functions = "create(address,string,string,string)";
  var options = "100000000";
  var parameter=new Array();
  parameter[0] = {type:'address',value:'address'};
  parameter[0].value = window.tronWeb.defaultAddress.base58;
  parameter[1] = {type:'string',value:'url'};
  parameter[1].value = url;
  parameter[2] = {type:'string',value:'desc'};
  parameter[2].value = desc;
  parameter[3] = {type:'string',value:'title'};
  parameter[3].value = title;
  var issuerAddress = window.tronWeb.defaultAddress.hex;
  if (window.tronWeb && window.tronWeb.defaultAddress.base58) {
      //valid token approve
      let userAdress = window.tronWeb.defaultAddress.base58;
      let instance = await tronWeb.contract().at(tokenContractAdd);
      let number = await instance.allowance(userAdress,contractAddress).call();
      if(number < 10000000000000000000){
        try {
            var parameter1 = [{type:'address',value:''},{type:'uint256',value:'1000000000000000000000000000'}]
            parameter1[0].value = tronWeb.address.fromHex(contractAddress);
            var tx = await tronWeb.transactionBuilder.triggerSmartContract(tokenContractAdd,"approve(address,uint256)", {},parameter1,issuerAddress);
            var signedTx = await tronWeb.trx.sign(tx.transaction);
            var broastTx = await tronWeb.trx.sendRawTransaction(signedTx);

//            let res = await instance.approve(contractAddress,'1000000000000000000000000000').send({
//                feeLimit:100000000,
//                callValue:0,
//                shouldPollResponse:true
//            });
          } catch (error) {
              console.log(error);
              alert('approve失败，请重试！');
              return;
          }
      }
      //
      var tx = await tronWeb.transactionBuilder.triggerSmartContract(contractAddress,functions, {},parameter,issuerAddress);
//          var tx = await tronWeb.transactionBuilder.sendTrx('TN9RRaXkCFtTXRso2GdTZxSxxwufzxLQPP', 10, 'TTSFjEG3Lu9WkHdp4JrWYhbGP6K1REqnGQ')
      var signedTx = await tronWeb.trx.sign(tx.transaction);
      var broastTx = await tronWeb.trx.sendRawTransaction(signedTx);
      console.log(broastTx);
      if(keyword == "短视频"){
        document.getElementById("tab-img").className = "ax-item";
        var vedio = document.getElementById('tab-vedio');
        vedio.className += ' ax-active';
        vedioNft(0);
      }else{
        document.getElementById("tab-vedio").className = "ax-item";
        var obj = document.getElementById('tab-img');
        obj.className += ' ax-active';
        imgNft(0);
      }
  }
}

async function imgNft(pageNum){
  document.getElementById("user-sell-img").className = "";
  document.getElementById("user-pm-img").className = "";
  var obj = document.getElementById('all-img');
  obj.className = 'ax-active';
  querycontract(pageNum,imgContractAdd,1,0);
}

async function usersellquery(pageNum){
  document.getElementById("all-img").className = "";
  document.getElementById("user-pm-img").className = "";
  var obj = document.getElementById('user-sell-img');
  obj.className = 'ax-active';
  querycontract(pageNum,imgContractAdd,1,1);
}

async function userpmquery(pageNum){
  document.getElementById("all-img").className = "";
  document.getElementById("user-sell-img").className = "";
  var obj = document.getElementById('user-pm-img');
  obj.className = 'ax-active';
  querycontract(pageNum,imgContractAdd,1,2);
}

//async function isApprove(tokenContractAdd,contractAdd){
//  let userAdress = window.tronWeb.defaultAddress.base58;
//  let instance = await tronWeb.contract().at(tokenContractAdd);
//  let number = await instance.allowance(userAdress,contractAdd).call();
//  if(number < 10000000000000000000){
//    return false;
//  }
//  return true;
//}
//
//async function approve(tokenContractAdd,contractAdd){
//  try {
//    let instance = await tronWeb.contract().at(tokenContractAdd);
//    let res = await instance.approve(contractAdd,1000000000000000000000000000).send({
//        feeLimit:100000000,
//        callValue:0,
//        shouldPollResponse:true
//    });
//    return true;
//  } catch (error) {
//      console.log(error);
//  }
//  return false;
//}

async function querycontract(pageNum,contractAdd,type,select){
  var tmp = pageNum;
  if(pageNum <= 0){
    pageNum = 1;
    tmp = 1;
  }
  //
  var pageNo = 9;
  let userAdress = window.tronWeb.defaultAddress.base58;
  let instance = await tronWeb.contract().at(contractAdd);
  let number;
  if(select == 0){
    number = await instance.balanceOf(userAdress).call();
  }else if(select == 1){
    number = await instance.usersellingnum().call();
  }else if(select == 2){
    number = await instance.userpmingnum().call();
  }
  var count = parseInt(number, 10);
  if(count <= 0){
    document.getElementById('pt-list').innerHTML="";
    return;
  }
  var totalPage = Math.ceil(count/pageNo);
  if(pageNum > totalPage){
    pageNum = totalPage;
    tmp = pageNum;
  }
  pageNum = totalPage - pageNum + 1;
  var begin = count - pageNo*(totalPage - pageNum);
  begin = begin > count ? count : begin;
  var end = begin - pageNo;
  end = end < 0 ? 0: end;
  var j = 0;
  var index = new Array();
  for(var i = begin - 1;i>=end;i--){
    index[j] = i;
    j++;
  }
  let result;
  if(select == 0){
    result = await instance.tokensOfOwnerByIndexs(userAdress,index).call();
  }else if(select == 1){
    result = await instance.queryuserselling(index).call();
  }else if(select == 2){
    result = await instance.queryuserpm(index).call();
  }
  document.getElementById('pt-list').innerHTML="";
  if(type == 1) {
    setImg(result,select);
    if(select == 0){
      setPageSplit(count,totalPage,tmp,'imgNft');
    }else if(select == 1){
      setPageSplit(count,totalPage,tmp,'usersellquery');
    }else if(select == 2){
      setPageSplit(count,totalPage,tmp,'userpmquery');
    }
  } else {
    setVedio(result,select);
    setPageSplit(count,totalPage,tmp,'vedioNft');
  }
}

function setImg(result,select) {
    var context = "";
    var col = 3;
    var length = result.ids.length;
    var line = Math.ceil(length / col);
    var url = "https://src.axui.cn/examples/images/image-7.jpg";
    var title = "欧洲最长屋桥盛不下千年传奇";
    var desc = "埃尔福特是图林根州首府，在中世纪就是该地区的经济重镇。 它位于南北交通要道的中心位置，很多贸易物流要通过这里。格拉河从埃尔福特市中心穿过。";
    var time = "3天前发布";
    for(var k = 0, i = 0; k < line; k++) {
      context += '<ul class="ax-grid-inner">';
      for(var j = 0; j < col && i < length; j++,i++) {
        url = result.urls[i];
        title = result.titles[i];
        desc = result.descs[i];
        time = new Date(parseInt(result.times[i], 10) * 1000).toUTCString();
        var button = "";
        if(select ==0){
          button = '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" id="sell-'+result.ids[i]+'">卖出NFT</a>' +
                   '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" id="pm-'+result.ids[i]+'">拍卖NFT</a>' +
                   '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" id="trans-'+result.ids[i]+'">转给他人</a>';
        }else if(select ==1){
          button = '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" onclick="cancelsell('+result.ids[i]+')">取消卖出</a>';
        }else if(select ==2){
          var endtime = tronWeb.toDecimal(result.endtimes[i]);
          var count = result.counts[i];
          var finaluser = tronWeb.address.fromHex(result.finalusers[i]);
          var finalprice = result.finalprices[i]/decimals;
          if(count > 0 && endtime < Date.parse(new Date())/1000){
            button = '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" onclick="cancelpm('+result.ids[i]+')">取消拍卖</a>';
          }else if(count > 0){
            button = 'Latest bidder: '+finaluser+', Latest bid: '+finalprice+", Number of bids: "+count;
          }else{
            button = "Number of bids: "+count;
          }
        }
        context += '<li class="ax-grid-block ax-col-8">'+
          '<div class="ax-card-block" style="background-color: floralwhite;">'+
            '<div class="ax-img">'+
              '<a target="_blank" href="' + ipfs + url + '" class="ax-figure" style="background-image:url(' + ipfs + url + '),url(' + local + url + '),url(https://src.axui.cn/src/images/loading.gif);"></a>'+
            '</div>'+
            '<div class="ax-title">'+
              '<a href="###" class="ax-ell-title">' + title + '</a>'+
            '</div>'+
            '<div class="ax-des ax-ell-2-des">'+
              desc +
            '</div>'+
            '<div class="ax-keywords">'+
              '<div class="ax-flex-row">'+
                time +
              '</div>'+
            '</div>'+
            '<div class="ax-keywords">'+
              '<div class="ax-flex-row">'+
                button +
              '</div>'+
            '</div>'+
          '</div>'+
        '</li>';
      }
      context += '</ul>';
    }
    document.getElementById('pt-list').innerHTML=context;
    for(var k = 0; k < length; k++) {
        initPop(result.ids[k]);
    }
}

var currentTokenId = -1;
function initPop(id) {
  $('#sell-'+id).axPopup({
    url:'#pop-sell-w',
    width:400,
    padding:false,
  });
  $('#pm-'+id).axPopup({
    url:'#pop-pm-w',
    width:600,
    padding:false,
  });
  $('#trans-'+id).axPopup({
    url:'#pop-zr-w',
    width:600,
    padding:false,
  });
  $('#sell-'+id).on('click',function(e){
  //s.replace(/[^0-9]/ig,"");
    console.log(this.id);
    currentTokenId = this.id.replace(/[^0-9]/ig,"");
  });
  $('#pm-'+id).on('click',function(e){
    console.log(this.id);
    currentTokenId = this.id.replace(/[^0-9]/ig,"");
  });
  $('#trans-'+id).on('click',function(e){
    console.log(this.id);
    currentTokenId = this.id.replace(/[^0-9]/ig,"");
  });

}

async function sell() {
  try{
    var price = new BigNumber(document.getElementById('price').value);
    let instance = await tronWeb.contract().at(imgContractAdd);
    var lock = await instance.lockmap(currentTokenId).call();
    if(lock){
      alert('Already set, please cancel and set again');
      return;
    }
    price = price.multipliedBy(decimals).toFixed();
    let res = await instance.addMarket(currentTokenId,price).send({
        feeLimit:100000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
  } catch (error) {
    console.log(error);
  }
}

async function pm() {
  try{
    var minprice = new BigNumber(document.getElementById('minprice').value);
    var mincall = new BigNumber(document.getElementById('mincall').value);
    var time = Date.parse(document.getElementById('endtime').value) / 1000;
    let instance = await tronWeb.contract().at(imgContractAdd);
    var lock = await instance.lockmap(currentTokenId).call();
    if(lock){
      alert('Already set, please cancel and set again');
      return;
    }
    mincall = mincall.multipliedBy(decimals).toFixed();
    minprice = minprice.multipliedBy(decimals).toFixed();
    let res = await instance.addPMMarket(currentTokenId,minprice,time,mincall).send({
        feeLimit:100000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
  } catch (error) {
    console.log(error);
  }
}

async function transfer() {
  try{
    var toAddress = document.getElementById('toAddress').value;
    let instance = await tronWeb.contract().at(imgContractAdd);
    let res = await instance.transfer(toAddress,currentTokenId).send({
        feeLimit:100000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
    alert('Send success');
  } catch (error) {
    console.log(error);
    alert('Send fail');
  }
}

async function cancelsell(tokenId) {
  try{
    let instance = await tronWeb.contract().at(imgContractAdd);
    let res = await instance.cancel(tokenId).send({
        feeLimit:100000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
    alert('Cancel success');
    usersellquery(1);
  } catch (error) {
    console.log(error);
    alert('Cancel fail');
  }
}

async function cancelpm(tokenId) {
  try{
    let instance = await tronWeb.contract().at(imgContractAdd);
    let res = await instance.receive(tokenId).send({
        feeLimit:100000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
    alert('Cancel success');
    userpmquery(1);
  } catch (error) {
    console.log(error);
    alert('Cancel fail');
  }
}

function setPageSplit(total,totalPage,currentPage,onclickFun){
  var context = '<a class="ax-total">共'+total+'条</a>'+
                '<a href="###" class="ax-first" onclick="'+onclickFun+'(1)">首页</a>'+
                '<a href="###" class="ax-prev" onclick="'+onclickFun+'('+(currentPage - 1)+')">上一页</a>'+
                '<a href="###" class="ax-next" onclick="'+onclickFun+'('+(currentPage + 1)+')">下一页</a>'+
                '<a href="###" class="ax-last" onclick="'+onclickFun+'('+totalPage+')">尾页</a>';
  document.getElementById('page-split').innerHTML=context;
}

function validCreateNft () {
  if(document.getElementById('fileInput').value=="") {
    alert('请选择上传的文件');
    return false;
  }
  var title = document.getElementById('title');
  if(title.value == '') {
    alert('请输入标题');
    return false;
  }
  var desc = document.getElementById('desc');
  if(desc.value == '') {
    alert('请输入简介');
    return false;
  }
  if(url==''){
    alert('请等待文件上传');
    return false;
  }
  return true;
}

function getRadioBoxValue(radioName) {
  var obj = document.getElementsByName(radioName);  //这个是以标签的name来取控件
  for(i=0; i<obj.length;i++)    {
    if(obj[i].checked)    {
      return obj[i].value;
    }
  }
  return "undefined";
}

async function vedioNft(pageNum){
  querycontract(pageNum,vedioContractAdd,2,0);
}

function setVedio(result,type) {
    var context = "";
    var col = 3;
    var length = result.ids.length;
    var line = Math.ceil(length / col);
    var url = "https://src.axui.cn/examples/images/image-7.jpg";
    var title = "欧洲最长屋桥盛不下千年传奇";
    var desc = "埃尔福特是图林根州首府，在中世纪就是该地区的经济重镇。 它位于南北交通要道的中心位置，很多贸易物流要通过这里。格拉河从埃尔福特市中心穿过。";
    var time = "3天前发布";
    for(var k = 0, i = 0; k < line; k++) {
      context += '<ul class="ax-grid-inner">';
      for(var j = 0; j < col && i < length; j++,i++) {
        url = result.urls[i];
        title = result.titles[i];
        desc = result.descs[i];
        time = new Date(parseInt(result.times[i], 10) * 1000);
        var button;
        if(type ==0){
          button = '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" id="sell-'+result.ids[i]+'">卖出NFT</a>' +
                   '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" id="pm-'+result.ids[i]+'">拍卖NFT</a>' +
                   '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" id="trans-'+result.ids[i]+'">转给他人</a>';
        }else if(type ==1){
          button = '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" onclick="cancelsell('+result.ids[i]+')">取消卖出</a>';
        }else if(type ==2){
          button = '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" onclick="cancelpm('+result.ids[i]+')">取消拍卖</a>';
        }
        context += '<li class="ax-grid-block ax-col-8">'+
          '<div class="ax-card-block" style="background-color: floralwhite;">'+
            '<div class="ax-videojs">'+
                '<video id="videojs01" class="video-js" controls preload="auto" height="200">'+
                    '<source src="' + ipfs + url + '"/>'+
                '</video>'+
            '</div>'+
            '<div class="ax-title">'+
              '<a href="###" class="ax-ell-title">' + title + '</a>'+
            '</div>'+
            '<div class="ax-des ax-ell-2-des">'+
              desc +
            '</div>'+
            '<div class="ax-keywords">'+
              '<div class="ax-flex-row">'+
                time +
              '</div>'+
            '</div>'+
          '</div>'+
        '</li>';
      }
      context += '</ul>';
    }
    document.getElementById('pt-list').innerHTML=context;
}
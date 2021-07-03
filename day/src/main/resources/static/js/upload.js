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
const local = "http://gateway.daynft.org/ipfs/"
const ipAddr = "ipfs.daynft.org";//"api.btfs.trongrid.io";
const ipPort = "80";//"443";
const gatePort = "80";
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

const imgContractAdd = "TTib3zwRnzv8mrSucRN2Go1Znds14QKsbR";
const vedioContractAdd = "TGq8WWKifjFTgEoGCPVyCVH4vRnqHvRXeg";
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
  var functions = "create(address,string,string,string,string)";
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
  parameter[4] = {type:'string',value:'keyword'};
  parameter[4].value = keyword;
//  var parameter = [{type:'address',value:'TGQVLckg1gDZS5wUwPTrPgRG4U8MKC4jcP'},
//                  {type:'string',value:'+url+'},
//                  {type:'string',value:\'desc\'},
//                  {type:'string',value:\'title\'},
//                  {type:'string',value:\'keyword\'}];
  var issuerAddress = window.tronWeb.defaultAddress.hex;
  if (window.tronWeb && window.tronWeb.defaultAddress.base58) {
      var tronweb = window.tronWeb;
      var tx = await tronWeb.transactionBuilder.triggerSmartContract(contractAddress,functions, {},parameter,issuerAddress);
//          var tx = await tronweb.transactionBuilder.sendTrx('TN9RRaXkCFtTXRso2GdTZxSxxwufzxLQPP', 10, 'TTSFjEG3Lu9WkHdp4JrWYhbGP6K1REqnGQ')
      var signedTx = await tronweb.trx.sign(tx.transaction);
      var broastTx = await tronweb.trx.sendRawTransaction(signedTx);
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
        querycontract(0);
      }
  }
}

async function imgNft(pageNum){
  querycontract(pageNum,imgContractAdd,1);
}

async function querycontract(pageNum,contractAdd,type){
  var tmp = pageNum;
  if(pageNum <= 0){
    pageNum = 1;
    tmp = 1;
  }
  //
  var pageNo = 9;
  let userAdress = window.tronWeb.defaultAddress.base58;
  let instance = await tronWeb.contract().at(contractAdd);
  let number = await instance.balanceOf(userAdress).call();
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
  let result = await instance.tokensOfOwnerByIndexs(userAdress,index).call();
  document.getElementById('pt-list').innerHTML="";
  if(type == 1) {
    setImg(result);
    setPageSplit(count,totalPage,tmp,'imgNft');
  } else {
    setVedio(result);
    setPageSplit(count,totalPage,tmp,'vedioNft');
  }
}

function setImg(result) {
    var context = "";
    var col = 3;
    var length = result.ids.length;
    var line = Math.ceil(length / col);
    var url = "https://src.axui.cn/examples/images/image-7.jpg";
    var title = "欧洲最长屋桥盛不下千年传奇";
    var desc = "埃尔福特是图林根州首府，在中世纪就是该地区的经济重镇。 它位于南北交通要道的中心位置，很多贸易物流要通过这里。格拉河从埃尔福特市中心穿过。";
    var keyword = "威尼斯";
    var time = "3天前发布";
    for(var k = 0, i = 0; k < line; k++) {
      context += '<ul class="ax-grid-inner">';
      for(var j = 0; j < col && i < length; j++,i++) {
        url = result.urls[i];
        title = result.titles[i];
        desc = result.descs[i];
        keyword = result.keyworks[i];
        time = new Date(parseInt(result.times[i], 10) * 1000);
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
              '<span class="ax-child">' + keyword + '.</span>'+
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
  querycontract(pageNum,vedioContractAdd,2);
}

function setVedio(result) {
    var context = "";
    var col = 3;
    var length = result.ids.length;
    var line = Math.ceil(length / col);
    var url = "https://src.axui.cn/examples/images/image-7.jpg";
    var title = "欧洲最长屋桥盛不下千年传奇";
    var desc = "埃尔福特是图林根州首府，在中世纪就是该地区的经济重镇。 它位于南北交通要道的中心位置，很多贸易物流要通过这里。格拉河从埃尔福特市中心穿过。";
    var keyword = "威尼斯";
    var time = "3天前发布";
    for(var k = 0, i = 0; k < line; k++) {
      context += '<ul class="ax-grid-inner">';
      for(var j = 0; j < col && i < length; j++,i++) {
        url = result.urls[i];
        title = result.titles[i];
        desc = result.descs[i];
        keyword = result.keyworks[i];
        time = new Date(parseInt(result.times[i], 10) * 1000);
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
              '<span class="ax-child">' + keyword + '.</span>'+
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
document.write("<script type='text/javascript' src='/static/js/common.js'></script>");
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
document.getElementById('fileInput').addEventListener('change', function selectedFileChanged() {
  if (this.files.length === 0) {
    console.log('No file selected.');
    return;
  }

  addFile(this.files[0]);
});



let url = "";
async function addFile(file) {
    if (!checkNetwork()) {
      return;
    }
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

async function createNft() {
  if (!checkNetwork()) {
    return;
  }
  if(!validCreateNft()){
    return;
  }
  var title = document.getElementById('title').value;
  var keyword = getRadioBoxValue("keyword");
  var contractAddress = window.tronWeb.address.toHex(imgContractAdd);
  if(keyword == "Video"){
    contractAddress = window.tronWeb.address.toHex(vedioContractAdd);
  }
  var functions = "create(address,bytes32,bytes32,bytes16)";
  var issuerAddress = window.tronWeb.defaultAddress.hex;
  if (window.tronWeb && window.tronWeb.defaultAddress.base58) {
      //valid token approve
      let userAdress = window.tronWeb.defaultAddress.base58;
      let instance = await tronWeb.contract().at(tokenContractAdd);
      let number = await instance.allowance(userAdress,contractAddress).call();
      number = tronWeb.toDecimal(number);
      if(number < 10000000000000000000){
        try {
            var parameter1 = [{type:'address',value:''},{type:'uint256',value:'1000000000000000000000000000'}]
            parameter1[0].value = tronWeb.address.fromHex(contractAddress);
            var tx = await tronWeb.transactionBuilder.triggerSmartContract(tokenContractAdd,"approve(address,uint256)", {},parameter1,issuerAddress);
            var signedTx = await tronWeb.trx.sign(tx.transaction);
            var broastTx = await tronWeb.trx.sendRawTransaction(signedTx);

//            let res = await instance.approve(contractAddress,'1000000000000000000000000000').send({
//                feeLimit:1000000000,
//                callValue:0,
//                shouldPollResponse:true
//            });
          } catch (error) {
              console.log(error);
              alert('Approval failed, please try again!');
              return;
          }
      }
      //
      instance = await tronWeb.contract().at(contractAddress);
      title = tronWeb.toHex(title);
      var len = 64-title.length+2;
      if(len<0){
        title = title.substr(0,62)+"0000";
      }else{
        for(var i = 0;i<len;i++){
          title += "0";
        }
      }
      var url1 = tronWeb.toHex(url.substr(0,32));
      var url2 = tronWeb.toHex(url.substr(32))+"0000";
      let res = await instance.create(userAdress,title,url1,url2).send({
              feeLimit:1000000000,
              callValue:0,
              shouldPollResponse:false
          });
      console.log(res);
      checkTransaction(res,"Create Success.");
      if(keyword == "Video"){
        document.getElementById("tab-img").className = "ax-item";
        var vedio = document.getElementById('tab-vedio');
        vedio.className += ' ax-active';
        vedioNft(1);
      }else{
        document.getElementById("tab-vedio").className = "ax-item";
        var obj = document.getElementById('tab-img');
        obj.className += ' ax-active';
        imgNft(1);
      }
  }
}

async function tag(value){
  var context = "";
  if(value == 1){
    context = '<div class="ax-break-xs"></div>'+
                 '<div class="ax-item ax-flex-row">'+
                   '<span class="ax-head">Category</span>'+
                   '<div class="ax-flex-block">'+
                     '<div class="ax-text">'+
                       '<a id="all-img" href="###" class="ax-active" onclick="imgNft(1)">All</a>'+
                       '<a id="user-sell-img" href="###" onclick="usersellquery(1)">One-bite price</a>'+
                       '<a id="user-pm-img" href="###" onclick="userpmquery(1)">In Auction</a>'+
                       '<input id="search-img-id" class="ax-round ax-sm" type="number" placeholder="NFT ID Search" style="width:200px"/><a href="###" class="ax-iconfont ax-icon-search" onclick="getNftinfo()">Search</a>'+
                     '</div>'+
                   '</div>'+
                 '</div>';
    document.getElementById('type-filter').innerHTML=context;
    imgNft(1);
  }else {
    context = '<div class="ax-break-xs"></div>'+
                   '<div class="ax-item ax-flex-row">'+
                     '<span class="ax-head">Category</span>'+
                     '<div class="ax-flex-block">'+
                       '<div class="ax-text">'+
                         '<a id="all-img" href="###" class="ax-active" onclick="vedioNft(1)">All</a>'+
                         '<a id="user-sell-img" href="###" onclick="uservideosellquery(1)">One-bite price</a>'+
                         '<a id="user-pm-img" href="###" onclick="uservideopmquery(1)">In Auction</a>'+
                         '<input id="search-img-id" class="ax-round ax-sm" type="number" placeholder="NFT ID Search" style="width:200px"/><a href="###" class="ax-iconfont ax-icon-search" onclick="getNftinfo()">Search</a>'+
                       '</div>'+
                     '</div>'+
                   '</div>';
    document.getElementById('type-filter').innerHTML=context;
    vedioNft(1);
  }
//  document.getElementById('type-filter').innerHTML=context;
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

async function uservideosellquery(pageNum){
  document.getElementById("all-img").className = "";
  document.getElementById("user-pm-img").className = "";
  var obj = document.getElementById('user-sell-img');
  obj.className = 'ax-active';
  querycontract(pageNum,vedioContractAdd,2,1);
}

async function uservideopmquery(pageNum){
  document.getElementById("all-img").className = "";
  document.getElementById("user-sell-img").className = "";
  var obj = document.getElementById('user-pm-img');
  obj.className = 'ax-active';
  querycontract(pageNum,vedioContractAdd,2,2);
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
//        feeLimit:1000000000,
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
    result = await instance.tokensOfOwnerByIndexs(userAdress,pageNo,pageNum).call();
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
  setContext(result,1,select);
}

function setContext(result,type,select) {
    var context = "";
    var col = 3;
    var length = result.ids.length;
    var line = Math.ceil(length / col);
    var url = "https://src.axui.cn/examples/images/image-7.jpg";
    var title = "欧洲最长屋桥盛不下千年传奇";
    var time = "3天前发布";
    for(var k = 0, i = 0; k < line; k++) {
      if(result.ids[i]<=0){
        break;
      }
      context += '<ul class="ax-grid-inner">';
      for(var j = 0; j < col && i < length; j++,i++) {
        if(result.ids[i]<=0){
          break;
        }
        url = tronWeb.toUtf8(result.url1s[i])+tronWeb.toUtf8(result.url2s[i]).replaceAll("\u0000","");
        title = tronWeb.toUtf8(result.titles[i]).replaceAll("\u0000","");
        time = new Date(parseInt(result.times[i], 10) * 1000).toUTCString();
        var button = "";
        var liupai = "";
        var createTimeContext = "";
        if(select ==0){
          button = '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round btn-a" id="sell-'+result.ids[i]+'">Sell NFT</a>' +
                   '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round btn-a" id="pm-'+result.ids[i]+'">Auction NFT</a>' +
                   '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round btn-a" id="trans-'+result.ids[i]+'">Transfer to others</a>';
          createTimeContext = '<div class="ax-keywords">'+
                                            '<div class="ax-flex-row">'+
                                              time +
                                            '</div>'+
                                          '</div>';
        }else if(select ==1){
          price = new BigNumber(tronWeb.toDecimal(result.prices[i])).div(decimals).toFixed();
          button = '<span class="ax-child">Price: ' + price + ' DAY. </span>'+
                   '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round btn-a" onclick="cancelsell('+result.ids[i]+')">Cancel Auction</a>';
        }else if(select ==2){
          var endtime = tronWeb.toDecimal(result.endtimes[i]);
          var count = result.counts[i];
          var finaluser = tronWeb.address.fromHex(result.finalusers[i]);
          var finalprice = new BigNumber(tronWeb.toDecimal(result.finalprices[i])).div(decimals).toFixed();
          var minprice = new BigNumber(tronWeb.toDecimal(result.minprices[i])).div(decimals).toFixed();
          var mincall = new BigNumber(tronWeb.toDecimal(result.mincalls[i])).div(decimals).toFixed();
          button = '<div class="ax-flex-row">' +
                      '<span class="ax-child">Minimum Bid: ' + minprice + ' DAY</span>'+
                               '</div><div class="ax-flex-row">' +
                               '<span class="ax-child">Minimum Markup:  ' + mincall + ' DAY</span>'+
                               '</div><div class="ax-flex-row">' +
                               '<span class="ax-child">End Time: ' + new Date(parseInt(endtime, 10) * 1000).toUTCString() + '</span>'+
                               '</div>';
          if(count <= 0 && endtime < Date.parse(new Date())/1000){
            button += '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round btn-a" onclick="cancelpm('+result.ids[i]+')">Cancel Auction</a>';
            liupai = '<div class="wYin-fail">' +
                       '<p>Abortive</p>' +
                     '</div>';
          }else if(count > 0 && endtime < Date.parse(new Date())/1000){
            button += 'Latest bidder: '+finaluser+', Latest bid: '+finalprice+", Number of bids: "+count+
                      '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round btn-a" onclick="cancelpm('+result.ids[i]+')">Transfer to auctioneer</a>';
            liupai = '<div class="wYin-success">' +
                       '<p>Success</p>' +
                     '</div>';
          }else if(count > 0){
            button += 'Latest bidder: '+finaluser+', Latest bid: '+finalprice+", Number of bids: "+count;
          }
        }
        var typeContext = "";
        if(type == 1){
          typeContext = '<div class="ax-img">'+
                          '<a target="_blank" href="' + ipfs + url + '" class="ax-figure" style="background-image:url(' + local + url + '),url(' + ipfs + url + '),url(https://src.axui.cn/src/images/loading.gif);"></a>'+
                        '</div>';
        }else{
          typeContext = '<div class="ax-videojs">'+
                            '<video id="videojs01" class="video-js" controls preload="auto" height="200">'+
                                '<source src="' + ipfs + url + '"/>'+
                            '</video>'+
                        '</div>';
        }
        context += '<li class="ax-grid-block ax-col-8">'+
          '<div class="ax-card-block card-div" style="background-color: floralwhite;">'+
            typeContext +
            liupai +
            '<div class="ax-title">'+
              '<a href="###" class="ax-ell-title"> NFT ID：'+result.ids[i] + '</a>'+
            '</div>'+
            '<div class="ax-title">'+
              '<a href="###" class="ax-ell-title">' + title + '</a>'+
            '</div>'+
//            '<div class="ax-des ax-ell-2-des">'+
//              desc +
//            '</div>'+
            createTimeContext +
            '<div class="">'+
//              '<div class="ax-flex-row">'+
                button +
//              '</div>'+
            '</div>'+
          '</div>'+
        '</li>';
      }
      context += '</ul>';
    }
    document.getElementById('pt-list').innerHTML=context;
    for(var k = 0; k < length; k++) {
        initPop(result.ids[k],type);
    }
}

var currentTokenId = -1;
function initPop(id,type) {
  if(id <= 0){
    return;
  }
  if(type == 1){
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
  }else{
    $('#sell-'+id).axPopup({
      url:'#pop-sell-w-v',
      width:400,
      padding:false,
    });
    $('#pm-'+id).axPopup({
      url:'#pop-pm-w-v',
      width:600,
      padding:false,
    });
    $('#trans-'+id).axPopup({
      url:'#pop-zr-w-v',
      width:600,
      padding:false,
    });
  }

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
//todo: 设置type区分img和video
async function sell(type) {
  if (!checkNetwork()) {
    return;
  }
  try{
    var price;
    let instance;
    if(type == 1){
      price = new BigNumber(document.getElementById('price').value);
      instance = await tronWeb.contract().at(imgContractAdd);
    }else{
      price = new BigNumber(document.getElementById('price-v').value);
      instance = await tronWeb.contract().at(vedioContractAdd);
    }
    var lock = await instance.lockmap(currentTokenId).call();
    if(lock){
      alert('Already set, please cancel and set again');
      return;
    }
    price = price.multipliedBy(decimals).toFixed();
    let res = await instance.addMarket(currentTokenId,price).send({
        feeLimit:1000000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
    checkTransaction(res,"Add To Market Success.");
  } catch (error) {
    console.log(error);
  }
}

async function pm(type) {
  if (!checkNetwork()) {
    return;
  }
  try{
    var minprice;
    var mincall;
    var time;
    let instance;
    if(type == 1){
      minprice = new BigNumber(document.getElementById('minprice').value);
      mincall = new BigNumber(document.getElementById('mincall').value);
      time = Date.parse(document.getElementById('endtime').value) / 1000;
      instance = await tronWeb.contract().at(imgContractAdd);
    }else{
      minprice = new BigNumber(document.getElementById('minprice-v').value);
      mincall = new BigNumber(document.getElementById('mincall-v').value);
      time = Date.parse(document.getElementById('endtime-v').value) / 1000;
      instance = await tronWeb.contract().at(vedioContractAdd);
    }
    var lock = await instance.lockmap(currentTokenId).call();
    if(lock){
      alert('Already set, please cancel and set again');
      return;
    }
    mincall = mincall.multipliedBy(decimals).toFixed();
    minprice = minprice.multipliedBy(decimals).toFixed();
    let res = await instance.addPMMarket(currentTokenId,minprice,time,mincall).send({
        feeLimit:1000000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
    checkTransaction(res,"Add To Action Market Success.");
  } catch (error) {
    console.log(error);
  }
}

async function transfer(type) {
  if (!checkNetwork()) {
    return;
  }
  try{
    var toAddress;
    let instance;
    if(type == 1){
      toAddress = document.getElementById('toAddress').value;
      instance = await tronWeb.contract().at(imgContractAdd);
    }else{
      toAddress = document.getElementById('toAddress-v').value;
      instance = await tronWeb.contract().at(vedioContractAdd);
    }
    let res = await instance.transfer(toAddress,currentTokenId).send({
        feeLimit:1000000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
    checkTransaction(res,"Send Success.");
    imgNft(1);
  } catch (error) {
    console.log(error);
    alert('Send fail');
  }
}

async function cancelsell(tokenId) {
  if (!checkNetwork()) {
    return;
  }
  try{
    let instance = await tronWeb.contract().at(imgContractAdd);
    let res = await instance.cancel(tokenId).send({
        feeLimit:1000000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
    checkTransaction(res,"Cancel Success.");
    usersellquery(1);
  } catch (error) {
    console.log(error);
    alert('Cancel fail');
  }
}

async function cancelpm(tokenId) {
  if (!checkNetwork()) {
    return;
  }
  try{
    let instance = await tronWeb.contract().at(imgContractAdd);
    let res = await instance.receives(tokenId).send({
        feeLimit:1000000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
    checkTransaction(res,"Receive Success.");
    userpmquery(1);
  } catch (error) {
    console.log(error);
    alert('Cancel fail');
  }
}

function setPageSplit(total,totalPage,currentPage,onclickFun){
  if(totalPage <= 1){
    return;
  }
  var context = '<a class="ax-total">Total:'+total+'</a>'+
                  '<a href="###" class="ax-first" onclick="'+onclickFun+'(1)">First</a>'+
                  '<a href="###" class="ax-prev" onclick="'+onclickFun+'('+(currentPage - 1)+')">Previous</a>'+
                  '<a href="###" class="ax-next" onclick="'+onclickFun+'('+(currentPage + 1)+')">Next</a>'+
                  '<a href="###" class="ax-last" onclick="'+onclickFun+'('+totalPage+')">Last</a>';
  document.getElementById('page-split').innerHTML=context;
}

function validCreateNft () {
  if(document.getElementById('fileInput').value=="") {
    alert('Please select the file to be uploaded');
    return false;
  }
  var title = document.getElementById('title');
  if(title.value == '') {
    alert('Please enter a title');
    return false;
  }
//  var desc = document.getElementById('desc');
//  if(desc.value == '') {
//    alert('Please enter a profile');
//    return false;
//  }
  if(url==''){
    alert('Please wait for the file to be uploaded');
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
  document.getElementById("user-sell-img").className = "";
  document.getElementById("user-pm-img").className = "";
  var obj = document.getElementById('all-img');
  obj.className = 'ax-active';
  querycontract(pageNum,vedioContractAdd,2,0);
}

function setVedio(result,select) {
  setContext(result,2,select);
}


async function getNftinfo(){
  try{
    var tokenId = document.getElementById('search-img-id').value;
    let instance;
    var isImg = true;
    if(document.getElementById("tab-img").className.includes('ax-active')){
      instance = await tronWeb.contract().at(imgContractAdd);
    }else{
      isImg = false;
      instance = await tronWeb.contract().at(vedioContractAdd);
    }
    var result;
    if(document.getElementById("user-sell-img").className == 'ax-active'){
      result = await instance.getselldesc(tokenId).call();
      var r = new Object();
      r.ids = new Array(result.id);
      r.url1s = new Array(result.url1);
      r.url2s = new Array(result.url2);
      r.titles = new Array(result.title);
      r.times = new Array(result.time);
      r.prices = new Array(result.price);
      r.sells = new Array(result.sell);
      if(isImg){
        setImg(r,1);
      }else{
        setVedio(r,1);
      }
    }else if(document.getElementById("user-pm-img").className == 'ax-active'){
      result = await instance.getpmdesc(tokenId).call();
      var r = new Object();
      r.ids = new Array(result.param2[0]);
      r.url1s = new Array(result.param1[0]);
      r.url2s = new Array(result.param1[1]);
      r.titles = new Array(result.param1[2]);
      r.times = new Array(result.param2[1]);
      r.minprices = new Array(result.param2[2]);
      r.finalprices = new Array(result.param2[3]);
      r.mincalls = new Array(result.param2[4]);
      r.endtimes = new Array(result.param2[5]);
      r.finalusers = new Array(result.finaluser);
      r.counts = new Array(result.param2[6]);
      r.states = new Array(result.param2[7]);
      if(isImg){
        setImg(r,2);
      }else{
        setVedio(r,2);
      }
    }else{
      result = await instance.getselldesc(tokenId).call();
      var r = new Object();
      r.ids = new Array(result.id);
      r.url1s = new Array(result.url1);
      r.url2s = new Array(result.url2);
      r.titles = new Array(result.title);
      r.times = new Array(result.time);
      r.prices = new Array(result.price);
      r.sells = new Array(result.sell);
      if(isImg){
        setImg(r,0);
      }else{
        setVedio(r,0);
      }
    }

  } catch (error) {
    console.log(error);
    alert(error.message);
  }
}

function changeAgentContent(){
    document.getElementById("inputFileAgent").value = document.getElementById("fileInput").value;
}
document.write("<script type='text/javascript' src='/static/js/common.js'></script>");

async function tag(value){
  var context = "";
  if(value == 1){
    context = '<div class="ax-break-xs"></div>'+
                 '<div class="ax-item ax-flex-row">'+
                   '<span class="ax-head">Category</span>'+
                   '<div class="ax-flex-block">'+
                     '<div class="ax-text">'+
                       '<a id="sell-img" class="ax-active" href="###" onclick="imgNft(1)">One-bite price</a>'+
                       '<a id="pm-img" href="###" onclick="pmquery(1)">In Auction</a>'+
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
                         '<a id="sell-img" class="ax-active" href="###" onclick="vedioNft(1)">One-bite price</a>'+
                         '<a id="pm-img" href="###" onclick="vediopmquery(1)">In Auction</a>'+
                         '<input id="search-img-id" class="ax-round ax-sm" type="number" placeholder="NFT ID Search" style="width:200px"/><a href="###" class="ax-iconfont ax-icon-search" onclick="getNftinfo()">Search</a>'+
                       '</div>'+
                     '</div>'+
                   '</div>';
    document.getElementById('type-filter').innerHTML=context;
    vedioNft(1);
  }

}

async function imgNft(pageNum){
  document.getElementById("pm-img").className = "";
  var obj = document.getElementById('sell-img');
  obj.className = 'ax-active';
  querycontract(pageNum,imgContractAdd,1,1);
}

async function pmquery(pageNum){
  document.getElementById("sell-img").className = "";
  var obj = document.getElementById('pm-img');
  obj.className = 'ax-active';
  querycontract(pageNum,imgContractAdd,1,2);
}



async function getimgpminfo(tokenId){
  try{
    let instance = await tronWeb.contract().at(imgContractAdd);
    var result = await instance.getpmdesc(tokenId).call();

  } catch (error) {
    console.log(error);
  }
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
    if(document.getElementById("sell-img").className == 'ax-active'){
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
    }else{
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
    }

  } catch (error) {
    console.log(error);
  }
}

async function querycontract(pageNum,contractAdd,type,select){
  var tmp = pageNum;
  if(pageNum <= 0){
    pageNum = 1;
    tmp = 1;
  }
  //
  var pageNo = 9;
  let instance = await tronWeb.contract().at(contractAdd);
  let number;
  if(select == 1){
    number = await instance.sellingnum().call();
  }else if(select == 2){
    number = await instance.pmingnum().call();
  }
  var count = parseInt(number, 10);
  if(count <= 0){
    document.getElementById('all-pt-list').innerHTML="";
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
  for(var i = begin-1;i>=end;i--){
    index[j] = i;
    j++;
  }
  let result;
  if(select == 1){
    result = await instance.queryallselling(index).call();
  }else if(select == 2){
    result = await instance.queryallpm(index).call();
  }
  document.getElementById('all-pt-list').innerHTML="";
  if(type == 1) {
    setImg(result,select);
    if(select == 1){
      setPageSplit(count,totalPage,tmp,'imgNft');
    }else if(select == 2){
      setPageSplit(count,totalPage,tmp,'pmquery');
    }
  } else {
    setVedio(result,select);
    if(select == 1){
      setPageSplit(count,totalPage,tmp,'vedioNft');
    }else if(select == 2){
      setPageSplit(count,totalPage,tmp,'vediopmquery');
    }
  }
}

async function buy(tokenId,type,amount) {
  if (!checkNetwork()) {
    return;
  }
  try{
    var contractAddress = selectContract(type);
    let instance = await tronWeb.contract().at(contractAddress);
    var lock = await instance.lockmap(tokenId).call();
    if(!lock){
      alert('This NFT is not for sale');
      return;
    }
    await approve(tokenContractAdd,contractAddress,amount);
    let res = await instance.buy(tokenId).send({
        feeLimit:1000000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
    checkTransaction(res,"Buy Success.");
    imgNft(1);
  } catch (error) {
    console.log(error);
    alert(error);
  }
}

async function receive(tokenId,type){
  if (!checkNetwork()) {
    return;
  }
  try{
    var contractAddress = selectContract(type);
    let instance = await tronWeb.contract().at(contractAddress);
    var lock = await instance.lockmap(tokenId).call();
    if(!lock){
      alert('This NFT is not for auction');
      return;
    }
    let res = await instance.receive(tokenId).send({
        feeLimit:1000000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
    checkTransaction(res,"Receive Success.");
    pmquery(1);
  } catch (error) {
    console.log(error);
    alert(error);
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
        id = result.ids[i];
        if(id<=0){
          break;
        }
        url = id;
        title = tronWeb.toUtf8(result.titles[i]).replaceAll("\u0000","");
        time = new Date(parseInt(result.times[i], 10) * 1000).toUTCString();
        var button = "";
        var liupai = "";
        if(select ==1){
          price = new BigNumber(tronWeb.toDecimal(result.prices[i])).div(decimals).toFixed();
          button = '<span class="ax-child">Price: ' + price + ' DAY. </span>'+
                   '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" onclick="buy('+id+','+type+','+price+')">Buy</a>';
        }else if(select ==2){
          var endtime = tronWeb.toDecimal(result.endtimes[i]);
          var count = result.counts[i];
          var finaluser = tronWeb.address.fromHex(result.finalusers[i]);
          var finalprice = new BigNumber(tronWeb.toDecimal(result.finalprices[i])).div(decimals).toFixed();
          var minprice = new BigNumber(tronWeb.toDecimal(result.minprices[i])).div(decimals).toFixed();
          var mincall = new BigNumber(tronWeb.toDecimal(result.mincalls[i])).div(decimals).toFixed();
          if(endtime > Date.parse(new Date())/1000){
            button = '<div class="ax-flex-row">' +
            '<span class="ax-child">Minimum Bid: ' + minprice + ' DAY</span>'+
                     '</div><div class="ax-flex-row">' +
                     '<span class="ax-child">Minimum Markup:  ' + mincall + ' DAY</span>'+
                     '</div><div class="ax-flex-row">' +
                     '<span class="ax-child">Latest Price: ' + finalprice + ' DAY</span>'+
                     '</div><div class="ax-flex-row">' +
                     '<span class="ax-child">End Time: ' + new Date(parseInt(endtime, 10) * 1000).toUTCString() + '</span>'+
                     '</div>' +
                     '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" id="bid-'+id+'">Bid</a>';
          }else{
            button = '<div class="ax-flex-row">' +
                     '<span class="ax-child">Latest Price: ' + finalprice + ' DAY</span>'+
                     '</div>';
            if(finaluser != "T9yD14Nj9j7xAB4dbGeiX9h8unkKHxuWwb"){
              button += '<div class="ax-flex-row"><span class="ax-child">Winner: ' + finaluser + '</span></div>';
              liupai = '<div class="wYin-success">' +
                         '<p>Success</p>' +
                       '</div>';
            } else{
              liupai = '<div class="wYin-fail">' +
                         '<p>Abortive</p>' +
                       '</div>';
            }
            button += '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" onclick="receive('+id+','+type+')">Receive</a>';
          }
        }
        var typeContext = "";
        if(type == 1){
          typeContext = '<div class="ax-img">'+
                          '<a target="_blank" href="' + ipfs + url + '" class="ax-figure" style="background-image:url(' + ipfs + url + '),url(' + local + url + '),url(https://src.axui.cn/src/images/loading.gif);"></a>'+
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
              '<a href="###" class="ax-ell-title"> NFT ID：'+ id + '</a>'+
            '</div>'+
            '<div class="ax-title">'+
              '<a href="###" class="ax-ell-title">' + title + '</a>'+
            '</div>'+
            '<div class="">'+
              button +
            '</div>'+
          '</div>'+
        '</li>';
      }
      context += '</ul>';
    }
    document.getElementById('all-pt-list').innerHTML=context;
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
    $('#bid-'+id).axPopup({
      url:'#pop-bid-w',
      width:400,
      padding:false,
    });
  }else{
    $('#bid-'+id).axPopup({
      url:'#pop-bid-w-v',
      width:400,
      padding:false,
    });
  }
  $('#bid-'+id).on('click',function(e){
    console.log(this.id);
    currentTokenId = this.id.replace(/[^0-9]/ig,"");
  });
}

async function bid(type) {
  if (!checkNetwork()) {
    return;
  }
  try{
    var bidprice;
    let instance;
    if(type == 1){
      bidprice = new BigNumber(document.getElementById('bidprice').value);
      instance = await tronWeb.contract().at(imgContractAdd);
    }else{
      bidprice = new BigNumber(document.getElementById('bidprice-v').value);
      instance = await tronWeb.contract().at(vedioContractAdd);
    }
    var lock = await instance.lockmap(currentTokenId).call();
    if(!lock){
      alert('This nft is not for auction');
      return;
    }
    bidprice = bidprice.multipliedBy(decimals).toFixed();
    let res = await instance.call(currentTokenId,bidprice).send({
        feeLimit:1000000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
    checkTransaction(res,"Bid Success.");
    pmquery(1);
  } catch (error) {
    console.log(error);
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
  document.getElementById('all-page-split').innerHTML=context;
}

async function vedioNft(pageNum){
  document.getElementById("pm-img").className = "";
  var obj = document.getElementById('sell-img');
  obj.className = 'ax-active';
  querycontract(pageNum,vedioContractAdd,2,1);
}

async function vediopmquery(pageNum){
  document.getElementById("sell-img").className = "";
  var obj = document.getElementById('pm-img');
  obj.className = 'ax-active';
  querycontract(pageNum,vedioContractAdd,2,2);
}

function setVedio(result,select) {
  setContext(result,2,select);
}

function selectContract(type){
  if(type == 1){
    return imgContractAdd;
  }else{
    return vedioContractAdd;
  }
}

async function approve(contractAddress,to,amount){
  var issuerAddress = window.tronWeb.defaultAddress.hex;
  let userAdress = window.tronWeb.defaultAddress.base58;
  let instance = await tronWeb.contract().at(contractAddress);
  let number = await instance.allowance(userAdress,to).call();
  number = tronWeb.toDecimal(number);
  if(number < amount){
    try {
        var parameter1 = [{type:'address',value:''},{type:'uint256',value:'1000000000000000000000000000'}]
        parameter1[0].value = tronWeb.address.fromHex(to);
        var tx = await tronWeb.transactionBuilder.triggerSmartContract(contractAddress,"approve(address,uint256)", {},parameter1,issuerAddress);
        var signedTx = await tronWeb.trx.sign(tx.transaction);
        var broastTx = await tronWeb.trx.sendRawTransaction(signedTx);
      } catch (error) {
          console.log(error);
          alert('Approval failed, please try again!');
          return;
      }
  }
}
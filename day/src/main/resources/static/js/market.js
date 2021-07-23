document.write("<script type='text/javascript' src='/static/js/bignumber.js'></script>");

// backend.js
const ipfs = "https://ipfs.io/ipfs/";
/*main network*/
//const local = "http://gateway.daynft.org/ipfs/"
//const ipAddr = "ipfs.daynft.org";//"api.btfs.trongrid.io";
//const ipPort = "80";//"443";
//const gatePort = "80";
/*local*/
const local = "http://127.0.0.1:8080/ipfs/"
const ipAddr = "127.0.0.1";//"api.btfs.trongrid.io";
const ipPort = "5001";//"443";
const gatePort = "8080";

const imgContractAdd = "TYffZfNPsTsYnDZPx9Rho3VgJC9SKuksfY";
const vedioContractAdd = "TBHSiLb9rLvx4Po6JzWK8LkPSj9bxdZou8";
const tokenContractAdd = "TDFsqpgihK6kHJ7uh5Mmyu54UQ8D1fXTkV";
const decimals = 1000000000000000000;

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
    setPageSplit(count,totalPage,tmp,'vedioNft');
  }
}

async function buy(tokenId) {
  try{
    let instance = await tronWeb.contract().at(imgContractAdd);
    var lock = await instance.lockmap(tokenId).call();
    if(!lock){
      alert('This NFT is not for sale');
      return;
    }

    let res = await instance.buy(tokenId).send({
        feeLimit:100000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
    alert('Buy success.');
    imgNft(1);
  } catch (error) {
    console.log(error);
    alert(error);
  }
}

async function receive(tokenId){
  try{
    let instance = await tronWeb.contract().at(imgContractAdd);
    var lock = await instance.lockmap(tokenId).call();
    if(!lock){
      alert('This NFT is not for auction');
      return;
    }

    let res = await instance.receive(tokenId).send({
        feeLimit:100000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
    alert('Receive success.');
    pmquery(1);
  } catch (error) {
    console.log(error);
    alert(error);
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
        id = result.ids[i];
        url = result.urls[i];
        title = result.titles[i];
        desc = result.descs[i];
        time = new Date(parseInt(result.times[i], 10) * 1000).toUTCString();
        var button = "";
        if(select ==1){
          price = result.prices[i] / decimals;
          button = '<span class="ax-child">Price: ' + price + ' DAY. </span>'+
                   '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" onclick="buy('+id+')">Buy</a>';
        }else if(select ==2){
          var endtime = tronWeb.toDecimal(result.endtimes[i]);
          var count = result.counts[i];
          var finaluser = tronWeb.address.fromHex(result.finalusers[i]);
          var finalprice = result.finalprices[i]/decimals;
          var minprice = result.minprices[i]/decimals;
          var mincall = result.mincalls[i]/decimals;
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
            button = '<span class="ax-child">Latest Price: ' + finalprice + ' DAY. Winner: ' + finaluser + '</span>'+
                     '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" onclick="receive('+id+')">Receive</a>';
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
//            '<div class="ax-keywords">'+
//              '<div class="ax-flex-row">'+
//                'createTime:'+time +
//              '</div>'+
//            '</div>'+
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
    document.getElementById('all-pt-list').innerHTML=context;
    for(var k = 0; k < length; k++) {
        initPop(result.ids[k]);
    }
}
var currentTokenId = -1;
function initPop(id) {
  $('#bid-'+id).axPopup({
    url:'#pop-bid-w',
    width:400,
    padding:false,
  });
  $('#bid-'+id).on('click',function(e){
    console.log(this.id);
    currentTokenId = this.id.replace(/[^0-9]/ig,"");
  });
}

async function bid() {
  try{
    var bidprice = new BigNumber(document.getElementById('bidprice').value);
    let instance = await tronWeb.contract().at(imgContractAdd);
    var lock = await instance.lockmap(currentTokenId).call();
    if(!lock){
      alert('This nft is not for auction');
      return;
    }
    bidprice = bidprice.multipliedBy(decimals).toFixed();
    let res = await instance.call(currentTokenId,bidprice).send({
        feeLimit:100000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
  } catch (error) {
    console.log(error);
  }
}

function setPageSplit(total,totalPage,currentPage,onclickFun){
  var context = '<a class="ax-total">共'+total+'条</a>'+
                '<a href="###" class="ax-first" onclick="'+onclickFun+'(1)">首页</a>'+
                '<a href="###" class="ax-prev" onclick="'+onclickFun+'('+(currentPage - 1)+')">上一页</a>'+
                '<a href="###" class="ax-next" onclick="'+onclickFun+'('+(currentPage + 1)+')">下一页</a>'+
                '<a href="###" class="ax-last" onclick="'+onclickFun+'('+totalPage+')">尾页</a>';
  document.getElementById('all-page-split').innerHTML=context;
}

async function vedioNft(pageNum){
  querycontract(pageNum,vedioContractAdd,2);
}

function setVedio(result,select) {
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
    document.getElementById('all-pt-list').innerHTML=context;

}
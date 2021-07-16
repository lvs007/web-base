

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

const imgContractAdd = "TXkC8kZK779GzPvxHLLyJmjXVn6sJWCYTh";
const vedioContractAdd = "TBHSiLb9rLvx4Po6JzWK8LkPSj9bxdZou8";
const tokenContractAdd = "TDFsqpgihK6kHJ7uh5Mmyu54UQ8D1fXTkV";

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
  let instance = await tronWeb.contract().at(contractAdd);
  let number = await instance.totalSupply().call();
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
  for(var i = begin;i>end;i--){
    index[j] = i;
    j++;
  }
  let result = await instance.tokensByIndexs(index).call();
  document.getElementById('all-pt-list').innerHTML="";
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
    document.getElementById('all-pt-list').innerHTML=context;

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
    document.getElementById('all-pt-list').innerHTML=context;

}
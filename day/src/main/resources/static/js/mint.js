document.write("<script type='text/javascript' src='/static/js/common.js'></script>");

function getContractAddress(type){
  if(type == 1){
    return dayPoolContractAdd;
  }else if(type == 2){
    return nftPoolContractAdd;
  }else{
    return tokenContractAdd;
  }
}

function formatNumber(result){
  var BN = BigNumber.clone();
  BN.config({DECIMAL_PLACES:4});
  return new BN(tronWeb.toDecimal(result)).div(decimals).toNumber();
}

async function balanceOf(type)
{
    if (!checkNetwork()) {
      return;
    }
    var contractAddress = getContractAddress(type);
    try{
      let userAdress = window.tronWeb.defaultAddress.base58;
      let instance = await tronWeb.contract().at(contractAddress);
      var result = await instance.balanceOf(userAdress).call();
      return formatNumber(result);
    }catch (error) {
      console.log(error);
    }
    return 0;
}

async function getReward(type)
{
    if (!checkNetwork()) {
      return;
    }
    var contractAddress = getContractAddress(type);
    try{
      let userAdress = window.tronWeb.defaultAddress.base58;
      let instance = await tronWeb.contract().at(contractAddress);
      var result = await instance.earned(userAdress).call();
      return formatNumber(result);
    }catch (error) {
      console.log(error);
    }
    return 0;
}

async function setValue(){
  var v1 = await getReward(1);
  var v2 = await getReward(2);
  document.getElementById('day_harverst').innerHTML=v1 + ' MH ready to harvest.';
  document.getElementById('day_nft_harverst').innerHTML=v2 + ' MH ready to harvest.';
}

async function setDetailValue(type){
  var v1 = await getReward(type);
  var v2 = await getStakeNum(type);
  document.getElementById('detail_harverst').innerHTML=v1;
  document.getElementById('detail_stake').innerHTML=v2;
}

async function totalSupply(type)
{
    if (!checkNetwork()) {
      return;
    }
    var contractAddress = getContractAddress(type);
    try{
      let instance = await tronWeb.contract().at(contractAddress);
      var result = await instance.totalSupply().call();
      return formatNumber(result);
    }catch (error) {
      console.log(error);
    }
    return 0;
}

async function getStakeNum(type)
{
    return balanceOf(type);
}

async function withdraw(type,amount)
{
    if (!checkNetwork()) {
      return;
    }
    var contractAddress = getContractAddress(type);
    try{
      let instance = await tronWeb.contract().at(contractAddress);
      let res = await instance.withdraw(amount).send({
          feeLimit:100000000,
          callValue:0,
          shouldPollResponse:false
      });
    }catch (error) {
      console.log(error);
    }
}

async function harvest(type)
{
    if (!checkNetwork()) {
      return;
    }
    var contractAddress = getContractAddress(type);
    try{
      let instance = await tronWeb.contract().at(contractAddress);
      let res = await instance.getReward().send({
          feeLimit:100000000,
          callValue:0,
          shouldPollResponse:false
      });
      document.getElementById('detail_harverst').innerHTML="0.0000";
    }catch (error) {
      console.log(error);
    }
}

async function unStake(type){
  var amount = document.getElementById('amount').value;
  var stake = await getStakeNum(type);
  if(amount >= stake){
    await exit(type);
  }else{
    await withdraw(type,amount);
  }
  setDetailValue(type);
}

async function exit(type)
{
    if (!checkNetwork()) {
      return;
    }
    var contractAddress = getContractAddress(type);
    try{
      let instance = await tronWeb.contract().at(contractAddress);
      let res = await instance.exit().send({
          feeLimit:100000000,
          callValue:0,
          shouldPollResponse:false
      });
    }catch (error) {
      console.log(error);
    }
}

async function setMaxAmount(type,select){
  var amount = 0;
  if(select == 2){
    amount = await getStakeNum(type);
  }else{
    amount = await balanceOf(3);
  }
  document.getElementById('amount').value = amount;
}

async function stake(type){
  var amount = document.getElementById('amount').value;
  amount = new BigNumber(amount);
  amount = amount.multipliedBy(decimals).toFixed();
  if(type == 1){
    await stakeDay(type,amount);
  }else if(type == 2){

  }
  setDetailValue(type);
}

async function stakeDay(type,amount) {
  if (!checkNetwork()) {
    return;
  }
  var contractAddress = getContractAddress(type);
  try{
    var issuerAddress = window.tronWeb.defaultAddress.hex;
    let userAdress = window.tronWeb.defaultAddress.base58;
    let instance = await tronWeb.contract().at(tokenContractAdd);
    let number = await instance.allowance(userAdress,contractAddress).call();
    number = tronWeb.toDecimal(number);
    if(number < amount){
      try {
          var parameter1 = [{type:'address',value:''},{type:'uint256',value:'1000000000000000000000000000'}]
          parameter1[0].value = tronWeb.address.fromHex(contractAddress);
          var tx = await tronWeb.transactionBuilder.triggerSmartContract(tokenContractAdd,"approve(address,uint256)", {},parameter1,issuerAddress);
          var signedTx = await tronWeb.trx.sign(tx.transaction);
          var broastTx = await tronWeb.trx.sendRawTransaction(signedTx);
        } catch (error) {
            console.log(error);
            alert('Approval failed, please try again!');
            return;
        }
    }

    instance = await tronWeb.contract().at(contractAddress);
    let res = await instance.stake(amount).send({
        feeLimit:100000000,
        callValue:0,
        shouldPollResponse:false
    });
  }catch (error) {
    console.log(error);
  }
}

async function nftStake(type,amount,nftId) {
  if (!checkNetwork()) {
    return;
  }
  var contractAddress = getContractAddress(type);
  try{
    let instance = await tronWeb.contract().at(contractAddress);
    let res = await instance.stake(amount,nftId).send({
        feeLimit:100000000,
        callValue:0,
        shouldPollResponse:false
    });
  }catch (error) {
    console.log(error);
  }
}

async function mhNft(pageNum){
  querycontract(pageNum,mhNftContractAdd);
}

async function querycontract(pageNum,contractAdd){
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
  setContext(result);
  setPageSplit(count,totalPage,tmp,'mhNft');
}

function setContext(result) {
    var context = "";
    var col = 3;
    var length = result.ids.length;
    var line = Math.ceil(length / col);
    var url = "https://src.axui.cn/examples/images/image-7.jpg";
    var title = "";
    var desc = "";
    var time = "";
    for(var k = 0, i = 0; k < line; k++) {
      context += '<ul class="ax-grid-inner">';
      for(var j = 0; j < col && i < length; j++,i++) {
        url = result.urls[i];
        title = result.titles[i];
        desc = result.descs[i];
        time = new Date(parseInt(result.times[i], 10) * 1000).toUTCString();
        var liupai = "";
        var button = '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" id="sell-'+result.ids[i]+'">Sell NFT</a>' +
                 '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" id="pm-'+result.ids[i]+'">Auction NFT</a>' +
                 '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round" id="trans-'+result.ids[i]+'">Transfer to others</a>';
        var typeContext = '<div class="ax-img">'+
                  '<a target="_blank" href="' + ipfs + url + '" class="ax-figure" style="background-image:url(' + ipfs + url + '),url(' + local + url + '),url(https://src.axui.cn/src/images/loading.gif);"></a>'+
                  '</div>';
        context += '<li class="ax-grid-block ax-col-8">'+
          '<div class="ax-card-block" style="background-color: floralwhite;">'+
            typeContext +
            liupai +
            '<div class="ax-title">'+
              '<a href="###" class="ax-ell-title"> NFT ID：'+result.ids[i] + '</a>'+
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
                button +
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
  $('#trans-'+id).axPopup({
    url:'#pop-zr-w',
    width:600,
    padding:false,
  });

  $('#trans-'+id).on('click',function(e){
    console.log(this.id);
    currentTokenId = this.id.replace(/[^0-9]/ig,"");
  });

}

async function transfer(type) {
  if (!checkNetwork()) {
    return;
  }
  var contractAddress = getContractAddress(type);
  try{
    var toAddress = document.getElementById('toAddress').value;
    let instance = await tronWeb.contract().at(contractAddress);
    let res = await instance.transfer(toAddress,currentTokenId).send({
        feeLimit:100000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
    alert('Send success');
    mhNft(1);
  } catch (error) {
    console.log(error);
    alert('Send fail');
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
  var desc = document.getElementById('desc');
  if(desc.value == '') {
    alert('Please enter a profile');
    return false;
  }
  if(url==''){
    alert('Please wait for the file to be uploaded');
    return false;
  }
  return true;
}
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

async function balanceOf(type)
{
    try{
      var contractAddress = getContractAddress(type);
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
    try{
      var contractAddress = getContractAddress(type);
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
    try{
      var contractAddress = getContractAddress(type);
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
          feeLimit:1000000000,
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
          feeLimit:1000000000,
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
  if(stake <= 0){
    alert('Not stake token');
    return;
  }
  if(amount >= stake || type == 2){
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
          feeLimit:1000000000,
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
    var myselect=document.getElementById("select");
    var index=myselect.selectedIndex;
    var nftId = myselect.options[index].value;
    await nftStake(type,amount,nftId);
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
        feeLimit:1000000000,
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
    await approve(tokenContractAdd,contractAddress,amount);
    await approveNft(mhNftContractAdd,contractAddress,nftId);
    let instance = await tronWeb.contract().at(contractAddress);
    let res = await instance.stake(amount,nftId).send({
        feeLimit:1000000000,
        callValue:0,
        shouldPollResponse:false
    });
  }catch (error) {
    console.log(error);
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

async function transfer() {
  if (!checkNetwork()) {
    return;
  }
  try{
    var toAddress = document.getElementById('toAddress').value;
    let instance = await tronWeb.contract().at(mhNftContractAdd);
    let res = await instance.transfer(toAddress,currentTokenId).send({
        feeLimit:1000000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log(res);
    mhnftlist();
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

async function buyBox(){
  if (!checkNetwork()) {
    return;
  }
  try{
    await approve(mhTokenContractAdd,mhboxContractAdd,amount);
    let instance = await tronWeb.contract().at(mhboxContractAdd);
    let res = await instance.buy().send({
        feeLimit:1000000000,
        callValue:0,
        shouldPollResponse:false
    });
    await sleep(2000);
    boxlist();
  } catch (error) {
    console.log(error);
    alert('Buy fail');
  }
}

async function buyAndOpenBox(){
  if (!checkNetwork()) {
    return;
  }
  try{
    await approve(mhTokenContractAdd,mhboxContractAdd,amount);
    let instance = await tronWeb.contract().at(mhboxContractAdd);
    let res = await instance.buyAndOpen().send({
        feeLimit:1000000000,
        callValue:0,
        shouldPollResponse:false
    });
    await sleep(2000);
    mhnftlist();
  } catch (error) {
    console.log(error);
    alert('Buy fail');
  }
}


async function openBox(boxId){
  if (!checkNetwork()) {
    return;
  }
  try{
    let instance = await tronWeb.contract().at(mhboxContractAdd);
    let res = await instance.open(boxId).send({
        feeLimit:1000000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log("openBox: "+res);
    await sleep(2000);
    boxlist();
  } catch (error) {
    console.log(error);
    alert('Open fail');
  }
}

async function mergeNft(){
  if (!checkNetwork()) {
    return;
  }
  let idArr = [...set];
  if(set.size != 5){
    alert('Please select 5 same level nft');
    return
  }
  try{
    let instance = await tronWeb.contract().at(mhNftContractAdd);
    let res = await instance.merge(idArr).send({
        feeLimit:1000000000,
        callValue:0,
        shouldPollResponse:false
    });
    console.log("mergeNft: "+res);
    await sleep(2000);
    mhnftlist();
  } catch (error) {
    console.log(error);
    alert('Merge Nft fail');
  }
}

async function boxlist(){
  mhnftlist();
  let userAdress = window.tronWeb.defaultAddress.base58;
  let instance = await tronWeb.contract().at(mhboxContractAdd);
  let result = await instance.boxOfOwner(userAdress).call();
  setBoxContext(result);
}

function setSelect(result){
  var context = '<option value="0">Select a NFT</option>';
  var length = result.ids.length;
  for(var i=0;i<length ;i++){
    context += '<option value="'+result.ids[i]+'">NFT ID:'+result.ids[i]+',Level:'+result.levels[i]+'</option>';
  }
  document.getElementById('select').innerHTML = context;
}

async function mhnftlist(){
  let userAdress = window.tronWeb.defaultAddress.base58;
  let instance = await tronWeb.contract().at(mhNftContractAdd);
  let result = await instance.tokenOfOwner(userAdress).call();
  setMHNFTContext(result);
  setSelect(result);
}

function setBoxContext(result) {
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
        var button = '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round btn-a" onclick="openBox('+result.ids[i]+')">Open Box</a>';
        var typeContext = '<div class="ax-img">'+
                  '<a target="_blank" href="' + url + '" class="ax-figure" style="background-image:url(' + url + '),url(https://src.axui.cn/src/images/loading.gif);"></a>'+
                  '</div>';
        context += '<li class="ax-grid-block ax-col-8">'+
          '<div class="ax-card-block" style="background-color: floralwhite;padding: 3px;">'+
            typeContext +
            '<div class="ax-keywords">'+
              '<a href="###" class="">ID：'+result.ids[i] + '</a>'+
            '</div>'+
            '<div class="">'+
                button +
            '</div>'+
          '</div>'+
        '</li>';
      }
      context += '</ul>';
    }
    document.getElementById('pt-list').innerHTML=context;
}

function setMHNFTContext(result) {
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
        var levelAndUrl = getLevelAndUrl(result.levels[i]);
        url =  "http://www.daynft.org/static/images/" + levelAndUrl.url;
        var liupai = '<div class="wYin-success">' +
                       '<p>'+levelAndUrl.l+'</p>' +
                     '</div>';
        var button = '<a href="###" class="ax-btn ax-info ax-gradient ax-primary ax-sm ax-round btn-a" id="trans-'+result.ids[i]+'">Transfer NFT</a>';
        var typeContext = '<div class="ax-img">'+
                  '<a onclick="selectNft('+ result.ids[i] +')" href="###" class="ax-figure" style="background-image:url(' + url + '),url(https://src.axui.cn/src/images/loading.gif);"></a>'+
                  '</div>';
        context += '<li class="ax-grid-block ax-col-8">'+
          '<div id="nft-'+ result.ids[i] +'" class="ax-card-block" style="background-color: floralwhite;padding: 3px;">'+
            typeContext +
            liupai +
            '<div class="ax-keywords">'+
              '<a href="###" class="">ID：' + result.ids[i] + '</a>'+
            '</div>'+
            '<div class="">'+
                button +
            '</div>'+
          '</div>'+
        '</li>';
      }
      context += '</ul>';
    }
    document.getElementById('mhnft-list').innerHTML=context;
    for(var k = 0; k < length; k++) {
        initPop(result.ids[k]);
    }
}
let set = new Set();
function selectNft(id) {
  var img = document.getElementById('nft-'+id);
  if(set.has(id)){
    set.delete(id);
    img.className = "ax-card-block";
  }else{
    set.add(id);
    img.className = "ax-card-block disable-div";
  }
}

function getLevelAndUrl(level){
  if(level == 0){
    return {l:"V1",url:"6.jpeg"};
  }else if(level == 1){
    return {l:"V2",url:"4.jpeg"};
  }else if(level == 2){
    return {l:"V3",url:"1.jpeg"};
  }else if(level == 3){
    return {l:"V4",url:"1.jpeg"};
  }else if(level == 4){
    return {l:"V5",url:"1.jpeg"};
  }
  return {l:"None",url:"logo.png"};
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

async function approveNft(contractAddress,to,nftId){
  try {
      var issuerAddress = window.tronWeb.defaultAddress.hex;
      var parameter1 = [{type:'address',value:''},{type:'uint256',value:'0'}]
      parameter1[0].value = tronWeb.address.fromHex(to);
      parameter1[1].value = nftId;

      var tx = await tronWeb.transactionBuilder.triggerSmartContract(contractAddress,"approve(address,uint256)", {},parameter1,issuerAddress);
      var signedTx = await tronWeb.trx.sign(tx.transaction);
      var broastTx = await tronWeb.trx.sendRawTransaction(signedTx);
    } catch (error) {
        console.log(error);
        alert('Approval failed, please try again!');
        return;
    }
}
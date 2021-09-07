document.write("<script type='text/javascript' src='/static/js/bignumber.js'></script>");

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

const imgContractAdd = "TDBydF2bcpq5UYXVgBBS2pP8Mn1ACBueou";
const vedioContractAdd = "TSn7Ue1E5ECFkxNU6PuEStVwsSt8hgHwCA";
const tokenContractAdd = "TDFsqpgihK6kHJ7uh5Mmyu54UQ8D1fXTkV";
const mhTokenContractAdd = "TBGkxmT4HX83Q4noknuCMiH76c77hTVP3x";
const dayPoolContractAdd = "TCpe9tiEia9jYxvMurursnXiwwU1aemV1A";
const nftPoolContractAdd = "TDzHnbPu8qT4FSEDrj2s8qz5cmkLHrVZ4M";
const mhNftContractAdd = "TVPTTeVBJknKWEk7kH3ThWyaoUtwaJKkZY";
const mhboxContractAdd = "TVuvc4aDcNg38C1rfSUacXD2bBX2rhF6hv";
const decimals = 1000000000000000000;
const interval = 3000;
const boxPrice = 100*decimals;

//const tron-server = "https://api.trongrid.io";
const tron_server = "https://nile.trongrid.io";
beforeAccount = "";
beforeNode = "";

var finish = false;

function init(show,isTime){
  if(!isTime){
    initFooter();
  }

  if(finish){
    return true;
  }

  if(!isTime && !window.tronWeb){
    alert('Please install TronLink plugin first!');
    return false;
  }

  if (!isTime && !checkNetwork()) {
    return false;
  }
  if (window.tronWeb && window.tronWeb.defaultAddress) {
    beforeAccount = window.tronWeb.defaultAddress.base58;
    beforeNode = window.tronWeb.fullNode.host;
    var char = "***";
    var str = window.tronWeb.defaultAddress.base58;
    if(str && str.length>8){
    str = str.substring(0, 8) + char + str.substring(str.length - 8);
    }else{
    str = "";
    }
    document.getElementById('address-home').innerText=str;
    if(show){
      tag(1);
    }
    finish = true;
    return true;
  }
  return false;
}

window.addEventListener('message', function (e) {
    if (e.data.message && (e.data.message.action == "setAccount" || e.data.message.action == "setNode")){
      var nowAccount = window.tronWeb.defaultAddress.base58;
      var nowNode = window.tronWeb.fullNode.host;
      if (e.data.message && e.data.message.action == "setAccount" && beforeAccount != nowAccount) {
          location.reload();
          console.log("setAccount event", e.data.message)
          console.log("current address:", e.data.message.data.address)

      }
      if (e.data.message && e.data.message.action == "setNode" && beforeNode != nowNode) {
          location.reload();
          console.log("setNode event", e.data.message)
          if (e.data.message.data.node.chain == '_'){
              console.log("tronLink currently selects the main chain")
          }else{
              console.log("tronLink currently selects the side chain")
          }
      }
    }
})

function checkNetwork() {
  var host = window.tronWeb.fullNode.host;
  if(host.includes('127.0.0.1')){
    return false;
  }
  if (!host.includes('nile')) {
    alert('Please switch TronLink network to nile test network! The function is still in testing!');
    return false;
  }
  return true;
}

function sleep (time) {
  return new Promise((resolve) => setTimeout(resolve, time));
}

function formatNumber(result){
  var BN = BigNumber.clone();
  BN.config({DECIMAL_PLACES:4});
  return new BN(tronWeb.toDecimal(result)).div(decimals).toNumber();
}

function formatNumberDecimal(result,decimals){
  var BN = BigNumber.clone();
  BN.config({DECIMAL_PLACES:4});
  return new BN(tronWeb.toDecimal(result)).div(decimals).toNumber();
}

async function receiveDay(){
  try{
    let instance = await tronWeb.contract().at("TUCXYV1PoGN659ByWu2Q52atLyxdJxMJuV");
    let res = await instance.receive().send({
        feeLimit:1000000000,
        callValue:0,
        shouldPollResponse:false
    });
  }catch (error) {
    console.log(error);
  }
}

async function wallet() {
  try{
    let userAdress = window.tronWeb.defaultAddress.base58;
    let instance = await tronWeb.contract().at(tokenContractAdd);
    var result = await instance.balanceOf(userAdress).call();
    var dayAmount = formatNumber(result);
    instance = await tronWeb.contract().at(mhTokenContractAdd);
    result = await instance.balanceOf(userAdress).call();
    var mhAmount = formatNumber(result);
    var trxAmount = await tronWeb.trx.getBalance(userAdress);
    trxAmount = formatNumberDecimal(trxAmount,1000000);
    document.getElementById('trx').innerText="TRX balance: "+trxAmount;
    document.getElementById('day').innerText="DAY balance: "+dayAmount;
    document.getElementById('mh').innerText="MH balance: "+mhAmount;
    $("#stake_window").addClass("ax-window-show");
  }catch (error) {
    console.log(error);
  }
}

function initFooter() {
 var footer = '<div class="ax-padding">'+
                  '<div class="ax-row ax-split">'+
                    '<div class="ax-col ax-copyright">'+
                      'DAYNFT Copyright © 2021 DAY Finance Providing you with a silky smooth user experience'+
                    '</div>'+
                    '<div class="ax-operate">'+
                      '<a href="###" class="ax-btn ax-line ax-lg ax-icon ax-round"><i class="ax-iconfont ax-icon-github"></i></a>'+
                      '<a href="###" class="ax-btn ax-line ax-lg ax-icon ax-round"><i class="ax-iconfont ax-icon-paperplane-fill"></i></a>'+
                      '<a href="###" class="ax-btn ax-line ax-lg ax-icon ax-round"><i class="ax-iconfont ax-icon-twitter-fill"></i></a>'+
                      '<a href="###" class="ax-btn ax-line ax-lg ax-icon ax-round"><i class="ax-iconfont ax-icon-email-fill"></i></a>'+
                    '</div>'+
                  '</div>'+
                '</div>';
  document.getElementById('footer').innerHTML=footer;
}

async function checkTransaction(txId,desc){
  await sleep(3000);
  tronWeb.trx.getTransaction(txId).then(function (result) {
      if(result.ret.length > 0 && result.ret[0].contractRet == "SUCCESS") {
        alert(desc);
      } else{
        alert('Fail: '+result.ret[0].contractRet);
      }
  }).catch(function (reason) {
      alert('Fail：' + reason);
      console.log('fail：' + reason);
  });
}






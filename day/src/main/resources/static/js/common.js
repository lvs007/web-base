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

const imgContractAdd = "TPhyBmRfk5RKSYR3Dj2kK74UNTS3RMMzcd";
const vedioContractAdd = "TJn4ohdXDm6HFE4mSfj9jQxj9QnafcP64y";
const tokenContractAdd = "TDFsqpgihK6kHJ7uh5Mmyu54UQ8D1fXTkV";
const decimals = 1000000000000000000;

//const tron-server = "https://api.trongrid.io";
const tron_server = "https://nile.trongrid.io";

window.onload = function() {

  if(!window.tronWeb){
    alert('Please install TronLink plugin first!');
    return;
  }

  if (!checkNetwork()) {
    return;
  }
  if (window.tronWeb && window.tronWeb.defaultAddress) {
    var char = "***";
    var str = window.tronWeb.defaultAddress.base58;
    if(str && str.length>8){
    str = str.substring(0, 8) + char + str.substring(str.length - 8);
    }else{
    str = "";
    }
    document.getElementById('address-home').innerText=str;
    tag(1);
  }
};

window.addEventListener('message', function (e) {
    if (e.data.message && e.data.message.action == "tabReply") {
        location.reload();
        console.log("tabReply event", e.data.message)
        if (e.data.message.data.data.node.chain == '_'){
            console.log("tronLink currently selects the main chain")
        }else{
            console.log("tronLink currently selects the side chain")
        }
    }

    if (e.data.message && e.data.message.action == "setAccount") {
        location.reload();
        console.log("setAccount event", e.data.message)
        console.log("current address:", e.data.message.data.address)

    }
    if (e.data.message && e.data.message.action == "setNode") {
        location.reload();
        console.log("setNode event", e.data.message)
        if (e.data.message.data.node.chain == '_'){
            console.log("tronLink currently selects the main chain")
        }else{
            console.log("tronLink currently selects the side chain")
        }

    }
})

function checkNetwork() {
  var host = window.tronWeb.fullNode.host;
  if (!host.includes('nile')) {
    alert('Please switch TronLink network to nile test network! The function is still in testing');
    return false;
  }
  return true;
}




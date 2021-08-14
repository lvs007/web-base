window.addEventListener('load', function() {
    if (!ethereum || !ethereum.isMetaMask) {
      window.alert('Please install MetaMask first.');
      return;
    }
    if (ethereum.selectedAddress == null) {//这个是判断你有没有登录，coinbase是你此时选择的账号
      connect();
    }
  // Checking if Web3 has been injected by the browser (Mist/MetaMask)
    if (typeof web3 !== 'undefined') {
      // Use the browser's ethereum provider
      web3.personal.sign(web3.fromUtf8("Hello from wanghui!"), web3.eth.coinbase, console.log);
　　 }
});

async function connect() {
  try {
    const accounts = await ethereum.enable()
    // You now have an array of accounts!
    // Currently only ever one:
    // ['0xFDEa65C8e26263F6d9A1B5de9555D2931A33b825']

  } catch (error) {
    // Handle error. Likely the user rejected the login:
    console.log(reason === "User rejected provider access")
  }
}

function send(){
  params: [{
    "from": "0xb60e8dd61c5d32be8058bb8eb970870f07233155",
    "to": "0xd46e8dd67c5d32be8058bb8eb970870f07244567",
    "gas": "0x76c0", // 30400
    "gasPrice": "0x9184e72a000", // 10000000000000
    "value": "0x9184e72a", // 2441406250
    "data": "0xd46e8dd67c5d32be8d46e8dd67c5d32be8058bb8eb970870f072445675058bb8eb970870f072445675"
  }]
  ethereum.send({
    method: 'eth_sendTransaction',
    params: params,
    from: accounts[0], // Provide the user's account to use.
  }).then(function (result) {
    // The result varies by method, per the JSON RPC API.
    // For example, this method will return a transaction hash on success.
  }).catch(function (reason) {
   // Like a typical promise, returns a reason on rejection.
  })
}

function sendAsync() {
  params: [{
    "from": "0xb60e8dd61c5d32be8058bb8eb970870f07233155",
    "to": "0xd46e8dd67c5d32be8058bb8eb970870f07244567",
    "gas": "0x76c0", // 30400
    "gasPrice": "0x9184e72a000", // 10000000000000
    "value": "0x9184e72a", // 2441406250
    "data": "0xd46e8dd67c5d32be8d46e8dd67c5d32be8058bb8eb970870f072445675058bb8eb970870f072445675"
  }]

  ethereum.sendAsync({
    method: 'eth_sendTransaction',
    params: params,
    from: accounts[0], // Provide the user's account to use.
  }, function (err, result) {
    // A typical node-style, error-first callback.
    // The result varies by method, per the JSON RPC API.
    // For example, this method will return a transaction hash on success.
  })
}

ethereum.on('accountsChanged', function (accounts) {
  console.log(accounts[0])
})
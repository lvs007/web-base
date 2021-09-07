initHeader();
function initHeader(){
  var header = '<div class="ax-row">'+
                  '<div class="ax-col">'+
                    '<a href="###" class="ax-logo"><img src="/static/images/logo.png" /></a>'+
                  '</div>'+
                  '<div class="ax-nav ax-scrollnav-h">'+
                    '<div class="ax-item">'+
                      '<li class="ax-scrollnav-item" data-section="scrollnav"><a href="/v1/home/home" class="ax-text ax-scrollnav-link" style="color:#fff">Home</a><span class="ax-line"></span></li>'+
                      '<li class="ax-scrollnav-item" data-section="scrollnav-4"><a href="/v1/mint/mint" class="ax-text ax-scrollnav-link" style="color:#fff">Mint</a><span class="ax-line"></span></li>'+
                      '<li class="ax-scrollnav-item" data-section="scrollnav-2"><a href="/v1/market/market" class="ax-text ax-scrollnav-link" style="color:#fff">NFT Market</a><span class="ax-line"></span></li>'+
                      '<li class="ax-scrollnav-item" data-section="scrollnav-6"><a href="/v1/help/help" class="ax-text ax-scrollnav-link" style="color:#fff">Help</a><span class="ax-line"></span></li>'+
                      '<li class="ax-scrollnav-item" data-section="scrollnav-6"><a href="/v1/my/my" class="ax-text ax-scrollnav-link" style="color:#fff">My NFT</a><span class="ax-line"></span></li>'+
                    '</div>'+
                    '<span class="ax-btns">'+
                      '<button type="button" class="ax-btn ax-sm ax-longest" id="address-home" onclick="wallet()"></button>'+
                    '</span>'+
                  '</div>'+
                '</div>';
  document.getElementById('header').innerHTML=header;
}







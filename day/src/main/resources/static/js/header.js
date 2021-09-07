initHeader();
function initHeader(){
  var header = '<div class="ax-row">'+
                  '<div class="ax-col">'+
                    '<a href="###" class="ax-logo"><img src="/static/images/logo.png" /></a>'+
                  '</div>'+
                  '<div class="ax-nav ax-scrollnav-h">'+
                    '<div class="ax-item">'+
                      '<li class="ax-scrollnav-item" data-section="scrollnav"><a href="/v1/home/home" class="ax-text ax-scrollnav-link" style="color:#fff">Home</a><span class="ax-line"></span></li>'+
                      '<li class="ax-scrollnav-item"><a href="/v1/mint/mint" class="ax-text ax-scrollnav-link" style="color:#fff">Mint</a><span class="ax-line"></span></li>'+
                      '<li class="ax-scrollnav-item"><a href="/v1/market/market" class="ax-text ax-scrollnav-link" style="color:#fff">NFT Market</a><span class="ax-line"></span></li>'+
                      '<li class="ax-scrollnav-item"><a href="/v1/video-view/view" class="ax-text ax-scrollnav-link" style="color:#fff">NFT Video</a><span class="ax-line"></span></li>'+
                      '<li class="ax-scrollnav-item"><a href="/v1/create/createnft" class="ax-text ax-scrollnav-link" style="color:#fff">My NFT</a><span class="ax-line"></span></li>'+
                      '<li class="ax-scrollnav-item"><a href="/v1/help/help" class="ax-text ax-scrollnav-link" style="color:#fff">Help</a><span class="ax-line"></span></li>'+
                    '</div>'+
                    '<span class="ax-btns">'+
                      '<button type="button" class="ax-btn ax-sm ax-longest" id="address-home" onclick="wallet()"></button>'+
                    '</span>'+
                  '</div>'+
                '</div>';
  document.getElementById('header').innerHTML=header;
}

//function initHeader(){
//  var header = '<div class="ax-row">'+
//                  '<div class="ax-col">'+
//                    '<a href="###" class="ax-logo"><img src="/static/images/logo.png" /></a>'+
//                  '</div>'+
//                  '<div class="ax-nav ax-scrollnav-h" id="article-nav1">'+
//                    '<div class="ax-item">'+
//                      '<li class="ax-scrollnav-item" data-section="scrollnav"><a href="/v1/home/home" class="ax-text ax-scrollnav-link" style="color:#fff">Home</a><span class="ax-line"></span></li>'+
//                      '<li class="ax-scrollnav-item" data-section="scrollnav-4"><a href="/v1/home/home#scrollnav-4" class="ax-text ax-scrollnav-link" style="color:#fff">News</a><span class="ax-line"></span></li>'+
//                      '<li class="ax-scrollnav-item" data-section="scrollnav-1"><a href="/v1/home/home#scrollnav-1" class="ax-text ax-scrollnav-link" style="color:#fff">Features</a><span class="ax-line"></span></li>'+
//                      '<li class="ax-scrollnav-item" data-section="scrollnav-2"><a href="/v1/home/home#scrollnav-2" class="ax-text ax-scrollnav-link" style="color:#fff">Token</a><span class="ax-line"></span></li>'+
//                      '<li class="ax-scrollnav-item" data-section="scrollnav-3"><a href="/v1/home/home#scrollnav-3" class="ax-text ax-scrollnav-link" style="color:#fff">Roadmap</a><span class="ax-line"></span></li>'+
//                      '<li class="ax-scrollnav-item" data-section="scrollnav-5"><a href="/v1/home/home#scrollnav-5" class="ax-text ax-scrollnav-link" style="color:#fff">Team</a><span class="ax-line"></span></li>'+
//                      '<li class="ax-scrollnav-item" data-section="scrollnav-6"><a href="/v1/home/home#scrollnav-6" class="ax-text ax-scrollnav-link" style="color:#fff">FAQ</a><span class="ax-line"></span></li>'+
//                      '<li class="ax-scrollnav-item" data-section="scrollnav-7"><a href="/v1/home/home#scrollnav-7" class="ax-text ax-scrollnav-link" style="color:#fff">Partners</a><span class="ax-line"></span></li>'+
//                    '</div>'+
//                    '<div class="ax-item ax-grade"><a href="###" class="ax-text" style="color:#fff">Services<i class="ax-iconfont ax-icon-down"></i></a><span class="ax-line"></span>'+
//                      '<ul class="ax-outer card-div-blue">'+
//                        '<li style="margin: 0px;"><a href="/v1/mint/mint" style="color:#fff">Mint</a></li>'+
//                        '<li style="margin: 0px;"><a href="/v1/create/createnft" style="color:#fff">NFT Creation</a></li>'+
//                        '<li style="margin: 0px;"><a href="/v1/market/market" style="color:#fff">NFT Market</a></li>'+
//                        '<li style="margin: 0px;"><a href="/v1/video-view/view" style="color:#fff">NFT Video</a></li>'+
//                      '</ul>'+
//                    '</div>'+
//                    '<span class="ax-btns">'+
//                      '<button type="button" class="ax-btn ax-sm ax-longest" id="address-home" onclick="wallet()"></button>'+
//                    '</span>'+
//                  '</div>'+
//                '</div>';
//  document.getElementById('header').innerHTML=header;
//}







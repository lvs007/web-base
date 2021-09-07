/**
 *Submitted for verification at Etherscan.io on 2020-12-31
*/

pragma solidity ^0.5.4;
pragma experimental ABIEncoderV2;

interface IERC165 {
    function supportsInterface(bytes4 interfaceId) external view returns (bool);
}


contract IERC721 is IERC165 {
    event Transfer(address indexed from, address indexed to, uint48 indexed tokenId);
    event Approval(address indexed owner, address indexed approved, uint48 indexed tokenId);
    event ApprovalForAll(address indexed owner, address indexed operator, bool approved);

    function balanceOf(address owner) public view returns (uint256 balance);

    function ownerOf(uint48 tokenId) public view returns (address owner);

    function approve(address to, uint48 tokenId) public;

    function getApproved(uint48 tokenId) public view returns (address operator);

    function setApprovalForAll(address operator, bool _approved) public;

    function isApprovedForAll(address owner, address operator) public view returns (bool);

    function transferFrom(address from, address to, uint48 tokenId) public;

    function safeTransferFrom(address from, address to, uint48 tokenId) public;

    function safeTransferFrom(address from, address to, uint48 tokenId, bytes memory data) public;
}


contract IERC721Receiver {
    function onERC721Received(address operator, address from, uint256 tokenId, bytes memory data)
    public returns (bytes4);
}


library Address {
    function isContract(address account) internal view returns (bool) {
        uint256 size;
        assembly {size := extcodesize(account)}
        return size > 0;
    }
}


contract ERC165 is IERC165 {
    bytes4 private constant _INTERFACE_ID_ERC165 = 0x01ffc9a7;
    mapping(bytes4 => bool) private _supportedInterfaces;

    constructor () internal {
        _registerInterface(_INTERFACE_ID_ERC165);
    }

    function supportsInterface(bytes4 interfaceId) external view returns (bool) {
        return _supportedInterfaces[interfaceId];
    }

    function _registerInterface(bytes4 interfaceId) internal {
        require(interfaceId != 0xffffffff);
        _supportedInterfaces[interfaceId] = true;
    }
}


contract ERC721 is ERC165, IERC721 {

    using Address for address;

    bytes4 private constant _ERC721_RECEIVED = 0x150b7a02;

    mapping(address => mapping(address => bool)) private _operatorApprovals;
    bytes4 private constant _INTERFACE_ID_ERC721 = 0x80ac58cd;

    mapping(address => uint48) internal userMap;
    mapping(uint48 => address) private allowance_;

    mapping(uint48 => address) public nftdesc;
    mapping(uint48 => NftBoFinal) public nftFinalData;
    uint48 public total;
    mapping(uint48 => bool) public lockmap;

    struct NftBoFinal {
        bytes32 title;
        bytes32 url1;
        bytes16 url2;
        uint48 createTime;
    }

    event Create(address owner, uint48 nftId);

    constructor () public {
        _registerInterface(_INTERFACE_ID_ERC721);
    }

    modifier isLock(uint48 tokenId) {
        require(!lockmap[tokenId]);
        _;
    }

    function lockNft(uint48 tokenId) internal {
        lockmap[tokenId] = true;
    }

    function unlockNft(uint48 tokenId) internal {
        lockmap[tokenId] = false;
    }

    function balanceOf(address owner) public view returns (uint256) {
        require(owner != address(0));
        return userMap[owner];
    }

    function ownerOf(uint48 tokenId) public view returns (address) {
        address owner = nftdesc[tokenId];
        require(owner != address(0), "not owner");
        return owner;
    }

    function approve(address to, uint48 tokenId) public isLock(tokenId) {
        require(to != msg.sender, "not exist nft");
        require(nftdesc[tokenId] == msg.sender, "this nft are not your");

        allowance_[tokenId] = to;

        emit Approval(msg.sender, to, tokenId);
    }

    function getApproved(uint48 tokenId) public view returns (address) {
        return allowance_[tokenId];
    }

    function setApprovalForAll(address to, bool approved) public {
        require(to != msg.sender);
        _operatorApprovals[msg.sender][to] = approved;
        emit ApprovalForAll(msg.sender, to, approved);
    }

    function isApprovedForAll(address owner, address operator) public view returns (bool) {
        return _operatorApprovals[owner][operator];
    }

    function transferFrom(address from, address to, uint48 tokenId) public isLock(tokenId) {
        _transferFrom(from, to, tokenId);
    }

    function safeTransferFrom(address from, address to, uint48 tokenId) public isLock(tokenId) {
        safeTransferFrom(from, to, tokenId, "");
    }

    function safeTransferFrom(address from, address to, uint48 tokenId, bytes memory _data) public isLock(tokenId) {
        transferFrom(from, to, tokenId);
        require(_checkOnERC721Received(from, to, tokenId, _data));
    }

    function _exists(uint48 tokenId) internal view returns (bool) {
        return nftFinalData[tokenId].createTime > 0;
    }


    function _mint(address owner, bytes32 title, bytes32 url1, bytes16 url2) internal returns (uint48){
        require(owner != address(0));
        total++;
        nftdesc[total] = owner;
        nftFinalData[total] = NftBoFinal(title, url1,url2,uint48(now));//11wan
        userMap[owner] = userMap[owner] + 1;
        emit Create(owner,total);
        return total;
    }

    function _transferFrom(address src, address dst, uint48 id) internal {
        require(dst != address(0));
        require(nftdesc[id] == src, "this nft is not src user");

        if (src != msg.sender && !isApprovedForAll(nftdesc[id], msg.sender)) {
            require(allowance_[id] == msg.sender, "you can not spend this nft");
            delete allowance_[id];
        }

        nftdesc[id] = dst;
        userMap[src] = userMap[src] - 1;
        userMap[dst] = userMap[dst] + 1;

        emit Transfer(src, dst, id);
    }

    function _transferFromNotCheck(address src, address dst, uint48 id) internal {
        require(dst != address(0));
        require(nftdesc[id] == src, "this nft is not src user");
        if (allowance_[id] != address(0)) {
            delete allowance_[id];
        }
        nftdesc[id] = dst;
        userMap[src] = userMap[src] - 1;
        userMap[dst] = userMap[dst] + 1;

        emit Transfer(src, dst, id);
    }

    function _checkOnERC721Received(address from, address to, uint48 tokenId, bytes memory _data)
    internal returns (bool)
    {
        if (!to.isContract()) {
            return true;
        }

        bytes4 retval = IERC721Receiver(to).onERC721Received(msg.sender, from, tokenId, _data);
        return (retval == _ERC721_RECEIVED);
    }

}


contract IERC721Enumerable is IERC721 {

    function tokenOfOwnerByIndex(address owner, uint48 index) public view returns (uint256 tokenId);

    function tokenByIndex(uint48 index) public view returns (uint256);
}


contract ERC721Enumerable is ERC165, ERC721, IERC721Enumerable {

    bytes4 private constant _INTERFACE_ID_ERC721_ENUMERABLE = 0x780e9d63;

    constructor () public {
        _registerInterface(_INTERFACE_ID_ERC721_ENUMERABLE);
    }

    function tokenOfOwnerByIndex(address owner, uint48 index) public view returns (uint256) {
        require(index < userMap[owner], "ERC721Enumerable: owner index out of bounds");

        if(nftdesc[index+1] == owner){
            return index+1;
        }
        return 0;
    }

    function tokenByIndex(uint48 index) public view returns (uint256) {
        if(nftdesc[index+1] != address(0)){
            return index+1;
        }
        return 0;
    }

    function tokensOfOwner(address owner) public view returns (uint48[] memory ids) {
        uint48 count = userMap[owner];
        ids = new uint48[](count);
        uint256 index = 0;
        for(uint48 i=1;i<=total;i++){
            if(nftdesc[i] == owner){
                ids[index] = i;
                index++;
            }

        }
        return ids;
    }

    function tokensOfOwnerByIndexs(address owner, uint pageNum,uint pageNo) public view returns (uint48[] memory ids, uint48[] memory times,
        bytes32[] memory titles,bytes32[] memory url1s,bytes16[] memory url2s) {
        require(pageNum > 0 && pageNo > 0);
        uint start = pageNum*(pageNo-1);
        uint end = pageNum*pageNo;
        uint index = 0;

        ids = new uint48[](pageNum);
        times = new uint48[](pageNum);
        titles = new bytes32[](pageNum);
        url1s = new bytes32[](pageNum);
        url2s = new bytes16[](pageNum);

        for(uint48 i=1;i<=total;i++){
            if(nftdesc[i] == owner){
                if(index >= start && index < end){
                    ids[index] = i;
                    NftBoFinal memory nb = nftFinalData[i];
                    times[index] = nb.createTime;
                    titles[index] = nb.title;
                    url1s[index] = nb.url1;
                    url2s[index] = nb.url2;
                }
                if(index >= end){
                    return (ids,times,titles,url1s,url2s);
                }
                index++;
            }

        }
        return (ids,times,titles,url1s,url2s);
    }

    function tokensByIndexs(uint48[] memory tokenIds) public view returns (uint48[] memory ids, uint48[] memory times,
        bytes32[] memory titles,bytes32[] memory url1s,bytes16[] memory url2s) {
        ids = new uint48[](tokenIds.length);
        times = new uint48[](tokenIds.length);
        titles = new bytes32[](tokenIds.length);
        url1s = new bytes32[](tokenIds.length);
        url2s = new bytes16[](tokenIds.length);
        for (uint128 i = 0; i < tokenIds.length; i++) {
            ids[i] = tokenIds[i];
            NftBoFinal memory nb = nftFinalData[tokenIds[i]];
            times[i] = nb.createTime;
            titles[i] = nb.title;
            url1s[i] = nb.url1;
            url2s[i] = nb.url2;

        }
        return (ids,times,titles,url1s,url2s);
    }

}


contract IERC721Metadata is IERC721 {
    function name() external view returns (string memory);

    function symbol() external view returns (string memory);

    function tokenURI(uint48 tokenId) external view returns (string memory);
}


contract ERC721Metadata is ERC165, ERC721, IERC721Metadata {

    string private _name;
    string private _symbol;

    bytes4 private constant _INTERFACE_ID_ERC721_METADATA = 0x5b5e139f;

    constructor (string memory name, string memory symbol) public {
        _name = name;
        _symbol = symbol;
        _registerInterface(_INTERFACE_ID_ERC721_METADATA);
    }

    function name() external view returns (string memory) {
        return _name;
    }

    function symbol() external view returns (string memory) {
        return _symbol;
    }

    function tokenURI(uint48 tokenId) external view returns (string memory) {
        require(_exists(tokenId));
        return "";
    }
}


contract ERC721Full is ERC721, ERC721Enumerable, ERC721Metadata {
    constructor (string memory name, string memory symbol) public ERC721Metadata(name, symbol) {
    }
}




contract ERC721Mintable is ERC721 {

    function mint(address to,bytes32 title,bytes32 url1, bytes16 url2) public returns (bool) {
        return true;
    }
}


contract Ownable {
    address private _owner;

    event OwnershipTransferred(address indexed previousOwner, address indexed newOwner);

    constructor () internal {
        _owner = msg.sender;
        emit OwnershipTransferred(address(0), _owner);
    }

    function owner() public view returns (address) {
        return _owner;
    }

    modifier onlyOwner() {
        require(isOwner());
        _;
    }

    function isOwner() public view returns (bool) {
        return msg.sender == _owner;
    }

    function renounceOwnership() public onlyOwner {
        emit OwnershipTransferred(_owner, address(0));
        _owner = address(0);
    }

    function transferOwnership(address newOwner) public onlyOwner {
        _transferOwnership(newOwner);
    }

    function _transferOwnership(address newOwner) internal {
        require(newOwner != address(0));
        emit OwnershipTransferred(_owner, newOwner);
        _owner = newOwner;
    }
}

interface Day {
    function totalSupply() external view returns (uint);

    function balanceOf(address account) external view returns (uint);

    function transfer(address recipient, uint amount) external returns (bool);

    function allowance(address owner, address spender) external view returns (uint);

    function approve(address spender, uint amount) external returns (bool);

    function transferFrom(address sender, address recipient, uint amount) external returns (bool);

    event Transfer(address indexed from, address indexed to, uint value);
    event Approval(address indexed owner, address indexed spender, uint value);
}

contract Market is ERC721Full, Ownable {

    mapping(uint48 => NftInfo) private nftprice;
    uint48[] private sellingnft;
    uint48[] private sellednft;
    mapping(address => uint48[]) private userselling;

    Day private mtoken;
    address private feeAddress;

    mapping(uint48 => NftPM) private nftpm;
    uint48[] private pming;
    uint48[] private other;
    mapping(address => uint48[]) private userpm;

    event AddMarket(address from, uint48 tokenId, uint price);
    event Cancel(address from, uint48 tokenId);
    event Buy(address from, address to, uint48 tokenId, uint price);
    event AddPmMarket(address from, uint48 tokenId, uint256 minprice, uint48 endtime, uint256 mincall);
    event Call(address from, uint48 tokenId, uint price);
    event Receive(address from, address to, uint48 tokenId);

    struct NftInfo {
        uint256 price;
        uint48 index;
        uint32 userindex;
        bool sell;
    }

    struct NftPM {
        address finaluser;
        uint48 endtime;
        uint32 count;
        uint32 userindex;
        uint256 finalprice;
        uint256 minprice;
        uint256 mincall;
        uint48 index;
        uint8 state;
    }

    constructor (address _token, address _feeaddress) public {
        mtoken = Day(_token);
        feeAddress = _feeaddress;
    }

    function setFeeAddress(address _address) public onlyOwner {
        feeAddress = _address;
    }

    function sellingnum() public view returns (uint256) {
        return sellingnft.length;
    }

    function usersellingnum() public view returns (uint256) {
        return userselling[msg.sender].length;
    }

    function sellednum() public view returns (uint256) {
        return sellednft.length;
    }

    function pmingnum() public view returns (uint256) {
        return pming.length;
    }

    function userpmingnum() public view returns (uint256) {
        return userpm[msg.sender].length;
    }

    function othernum() public view returns (uint256) {
        return other.length;
    }

    function addMarket(uint48 tokenId, uint256 price) public isLock(tokenId)  returns (bool) {
        require(msg.sender == ownerOf(tokenId), "This nft is not yours");
        require(price > 0, "price must big 0");
        if (nftprice[tokenId].sell) {
            uint48 swaptokenId = sellednft[sellednft.length - 1];
            uint48 newindex = nftprice[tokenId].index;
            sellednft[newindex] = swaptokenId;
            sellednft.length--;
            nftprice[swaptokenId].index = newindex;
        }

        nftprice[tokenId] = NftInfo(price, uint48(sellingnft.length), uint32(userselling[msg.sender].length), false);
        sellingnft.push(tokenId);
        userselling[msg.sender].push(tokenId);
        approve(address(this), tokenId);
        lockNft(tokenId);
        emit AddMarket(msg.sender, tokenId, price);
        return true;
    }

    function cancel(uint48 tokenId) public {
        require(lockmap[tokenId] && sellingnft[nftprice[tokenId].index] == tokenId, "not add to market");
        require(msg.sender == ownerOf(tokenId), "This nft is not yours");
        uint48 sellinglaste = uint48(sellingnft.length - 1);
        uint32 usersellinglaste = uint32(userselling[msg.sender].length - 1);
        uint48 swaptokenId = sellingnft[sellinglaste];
        uint48 newindex = nftprice[tokenId].index;
        uint32 newuserindex = nftprice[tokenId].userindex;
        uint48 swapusertokenid = userselling[msg.sender][usersellinglaste];
        sellingnft[newindex] = swaptokenId;
        sellingnft.length--;
        userselling[msg.sender][newuserindex] = swapusertokenid;
        userselling[msg.sender].length--;
        nftprice[swaptokenId].index = newindex;
        nftprice[swapusertokenid].userindex = newuserindex;
        unlockNft(tokenId);
        emit Cancel(msg.sender,tokenId);
    }

    function buy(uint48 tokenId) public returns (bool){
        require(msg.sender != ownerOf(tokenId), "NFT is your");
        require(mtoken.balanceOf(msg.sender) >= nftprice[tokenId].price, "Day token insufficient");
        // require(mtoken.allowance(msg.sender,address(this)) >= nftprice[tokenId].price, "Please approve Day token to contract");
        require(sellingnft[nftprice[tokenId].index] == tokenId && !nftprice[tokenId].sell && lockmap[tokenId], "This nft is not for sale");

        mtoken.transferFrom(msg.sender, ownerOf(tokenId), nftprice[tokenId].price * 9 / 10);
        mtoken.transferFrom(msg.sender, feeAddress, nftprice[tokenId].price / 10);

        uint48 sellinglaste = uint48(sellingnft.length - 1);
        uint32 usersellinglaste = uint32(userselling[ownerOf(tokenId)].length - 1);
        uint48 swaptokenId = sellingnft[sellinglaste];
        uint48 newindex = nftprice[tokenId].index;
        uint32 newuserindex = nftprice[tokenId].userindex;
        uint48 swapusertokenid = userselling[ownerOf(tokenId)][usersellinglaste];

        sellingnft[newindex] = swaptokenId;
        sellingnft.length--;
        nftprice[swaptokenId].index = newindex;

        userselling[ownerOf(tokenId)][newuserindex] = swapusertokenid;
        userselling[ownerOf(tokenId)].length--;
        nftprice[swapusertokenid].userindex = newuserindex;

        nftprice[tokenId].index = uint48(sellednft.length);
        nftprice[tokenId].sell = true;

        sellednft.push(tokenId);
        unlockNft(tokenId);
        _transferFromNotCheck(ownerOf(tokenId), msg.sender, tokenId);
        emit Buy(msg.sender, ownerOf(tokenId), tokenId,nftprice[tokenId].price);
        return true;
    }

    function queryallselling(uint256[] memory indexs) public view returns (uint48[] memory ids, uint48[] memory times,
        bytes32[] memory titles,bytes32[] memory url1s,bytes16[] memory url2s, uint256[] memory prices){
        return queryselling(indexs, 0);
    }

    function queryuserselling(uint256[] memory indexs) public view returns (uint48[] memory ids, uint48[] memory times,
        bytes32[] memory titles,bytes32[] memory url1s,bytes16[] memory url2s, uint256[] memory prices){
        return queryselling(indexs, 1);
    }

    function queryselling(uint256[] memory indexs, uint256 select) private view returns (uint48[] memory ids, uint48[] memory times,
        bytes32[] memory titles,bytes32[] memory url1s,bytes16[] memory url2s, uint256[] memory prices){
        uint48[] memory tokenIds = new uint48[](indexs.length);
        prices = new uint256[](indexs.length);
        uint48[] memory tokens = sellingnft;
        if (select == 1) {
            tokens = userselling[msg.sender];
        }
        for (uint256 i = 0; i < indexs.length; i++) {
            tokenIds[i] = uint48(tokens[indexs[i]]);
            prices[i] = nftprice[tokenIds[i]].price;
        }
        (ids, times, titles,url1s,url2s) = tokensByIndexs(tokenIds);
        return (ids, times, titles,url1s,url2s, prices);
    }

    function queryselled(uint256[] memory indexs) public view returns (uint48[] memory ids, uint48[] memory times,
        bytes32[] memory titles,bytes32[] memory url1s,bytes16[] memory url2s, uint256[] memory prices){
        uint48[] memory tokenIds = new uint48[](indexs.length);
        prices = new uint256[](indexs.length);
        for (uint256 i = 0; i < indexs.length; i++) {
            tokenIds[i] = uint48(sellednft[indexs[i]]);
            prices[i] = nftprice[tokenIds[i]].price;
        }
        (ids, times, titles,url1s,url2s) = tokensByIndexs(tokenIds);
        return (ids, times, titles,url1s,url2s, prices);
    }

    function getselldesc(uint48 tokenId) public view returns (uint48 id, uint48 time,
        bytes32 title,bytes32 url1,bytes16 url2, uint256 price,bool sell){
        NftInfo memory nftinfo = nftprice[tokenId];
        NftBoFinal memory nftbo = nftFinalData[tokenId];
        require(sellingnft[nftprice[tokenId].index] == tokenId && !nftinfo.sell && lockmap[tokenId], "This nft is not for sale");
        return (tokenId,nftbo.createTime,nftbo.title,nftbo.url1,nftbo.url2,nftinfo.price,nftinfo.sell);
    }

    /************NftPM***************/
    function addPMMarket(uint48 tokenId, uint256 minprice, uint48 endtime, uint256 mincall) public isLock(tokenId) returns (bool) {
        require(msg.sender == ownerOf(tokenId), "This nft is not yours");
        require(endtime > now, "endtime must big now");
        require(minprice > 0 && mincall > 0, "minprice and mincall must big 0");
        if (nftpm[tokenId].state > 0) {
            uint48 swaptokenId = other[other.length - 1];
            uint48 newindex = nftpm[tokenId].index;
            other[nftpm[tokenId].index] = other[other.length - 1];
            other.length--;
            nftprice[swaptokenId].index = newindex;
        }
        nftpm[tokenId] = NftPM(address(0), endtime,0, uint32(userpm[msg.sender].length),0, minprice,mincall, uint48(pming.length),0);
        pming.push(tokenId);
        userpm[msg.sender].push(tokenId);
        approve(address(this), tokenId);
        lockNft(tokenId);
        emit AddPmMarket(msg.sender,tokenId,minprice,endtime,mincall);
        return true;
    }

    function call(uint48 tokenId, uint256 price) public returns (bool){
        require(msg.sender != ownerOf(tokenId), "NFT is your");
        require(nftpm[tokenId].endtime > now, "The auction has ended");
        require(price - nftpm[tokenId].finalprice >= nftpm[tokenId].mincall && price >= nftpm[tokenId].minprice, "call price error");
        require(mtoken.balanceOf(msg.sender) >= price, "Day token insufficient");
        // require(mtoken.allowance(msg.sender,address(this)) >= price, "Please approve Day token to contract");
        require(pming[nftpm[tokenId].index] == tokenId && nftpm[tokenId].state == 0, "This nft is not for auction");

        mtoken.transferFrom(msg.sender, address(this), price);
        if (nftpm[tokenId].finalprice > 0) {
            mtoken.transfer(nftpm[tokenId].finaluser, nftpm[tokenId].finalprice);
        }

        nftpm[tokenId].finalprice = price;
        nftpm[tokenId].finaluser = msg.sender;
        nftpm[tokenId].count = nftpm[tokenId].count + 1;
        emit Call(msg.sender,tokenId,price);
        return true;
    }

    function receives(uint48 tokenId) public returns (bool) {
        require(nftpm[tokenId].endtime <= now, "The auction has not ended");
        require(nftpm[tokenId].state == 0 && lockmap[tokenId], "can not receive");
        require(pming[nftpm[tokenId].index] == tokenId, "This nft is not for auction");

        uint48 pmlaste = uint48(pming.length - 1);
        uint32 userpmlaste = uint32(userpm[ownerOf(tokenId)].length - 1);
        uint48 swaptokenId = pming[pmlaste];
        uint48 swapusertokenid = userpm[ownerOf(tokenId)][userpmlaste];
        uint48 newindex = nftpm[tokenId].index;
        uint32 newuserindex = nftpm[tokenId].userindex;
        address _from = ownerOf(tokenId);

        pming[newindex] = swaptokenId;
        pming.length--;
        nftpm[swaptokenId].index = newindex;

        userpm[_from][newuserindex] = swapusertokenid;
        userpm[_from].length--;
        nftpm[swapusertokenid].userindex = newuserindex;

        nftpm[tokenId].index = uint48(other.length);

        other.push(tokenId);
        unlockNft(tokenId);

        if (nftpm[tokenId].finalprice <= 0) {
            nftpm[tokenId].state = 2;
        } else {
            nftpm[tokenId].state = 1;
            mtoken.transfer(ownerOf(tokenId), nftpm[tokenId].finalprice * 9 / 10);
            mtoken.transfer(feeAddress, nftpm[tokenId].finalprice / 10);
            _transferFromNotCheck(ownerOf(tokenId), nftpm[tokenId].finaluser, tokenId);
        }
        emit Receive(_from,nftpm[tokenId].finaluser,tokenId);
        return true;
    }

    function queryallpm(uint256[] memory indexs) public view returns (uint48[] memory ids,bytes32[] memory url1s,
        bytes16[] memory url2s,bytes32[] memory titles,uint256[] memory times,uint256[] memory minprices, uint256[] memory finalprices,
        uint256[] memory mincalls, uint256[] memory endtimes, address[] memory finalusers, uint256[] memory counts){
        return querypm(indexs, 0);
    }

    function queryuserpm(uint256[] memory indexs) public view returns (uint48[] memory ids,bytes32[] memory url1s,
        bytes16[] memory url2s,bytes32[] memory titles,uint256[] memory times,uint256[] memory minprices, uint256[] memory finalprices,
        uint256[] memory mincalls, uint256[] memory endtimes, address[] memory finalusers, uint256[] memory counts){
        return querypm(indexs, 1);
    }

    function querypm(uint256[] memory indexs, uint256 select) private view returns (uint48[] memory ids,bytes32[] memory url1s,
        bytes16[] memory url2s,bytes32[] memory titles,uint256[] memory times,uint256[] memory minprices, uint256[] memory finalprices,
        uint256[] memory mincalls, uint256[] memory endtimes, address[] memory finalusers, uint256[] memory counts){
        ids = new uint48[](indexs.length);
        minprices = new uint256[](indexs.length);
        finalprices = new uint256[](indexs.length);
        mincalls = new uint256[](indexs.length);
        endtimes = new uint256[](indexs.length);
        finalusers = new address[](indexs.length);
        counts = new uint256[](indexs.length);
        url1s = new bytes32[](ids.length);
        url2s = new bytes16[](ids.length);
        titles = new bytes32[](ids.length);
        times = new uint256[](ids.length);
        uint48[] memory tokens = pming;
        if (select == 1) {
            tokens = userpm[msg.sender];
        }
        for (uint256 i = 0; i < indexs.length; i++) {
            ids[i] = tokens[indexs[i]];
            minprices[i] = nftpm[ids[i]].minprice;
            finalprices[i] = nftpm[ids[i]].finalprice;
            mincalls[i] = nftpm[ids[i]].mincall;
            endtimes[i] = nftpm[ids[i]].endtime;
            finalusers[i] = nftpm[ids[i]].finaluser;
            counts[i] = nftpm[ids[i]].count;
        }
        for (uint256 i = 0; i < ids.length; i++) {
            url1s[i] = nftFinalData[ids[i]].url1;
            url2s[i] = nftFinalData[ids[i]].url2;
            titles[i] = nftFinalData[ids[i]].title;
            times[i] = nftFinalData[ids[i]].createTime;
        }

        return (ids, url1s,url2s, titles, times, minprices,finalprices,mincalls,endtimes,finalusers,counts);
    }

    function getpmdesc(uint48 tokenId) public view returns (bytes32[] memory param1,uint256[] memory param2, address finaluser){
        require(pming[nftpm[tokenId].index] == tokenId && nftpm[tokenId].state == 0, "This nft is not for auction");
        NftPM memory pm = nftpm[tokenId];
        NftBoFinal memory nftbo = nftFinalData[tokenId];
        param1 = new bytes32[](3);
        param1[0] = nftbo.url1;
        param1[1] = nftbo.url2;
        param1[2] = nftbo.title;
        param2 = new uint256[](8);
        param2[0] = tokenId;
        param2[1] = nftbo.createTime;
        param2[2] = pm.minprice;
        param2[3] = pm.finalprice;
        param2[4] = pm.mincall;
        param2[5] = pm.endtime;
        param2[6] = pm.count;
        param2[7] = pm.state;
        return (param1,param2,pm.finaluser);
    }
}


contract CreateNFT is Market, ERC721Mintable {

    Day private token;

    uint256 private fee = 10 * 10 ** 18;

    constructor (string memory _name, string memory _symbol, address _token, address _feeaddress) public
    ERC721Mintable()
    ERC721Full(_name, _symbol)
    Market(_token,_feeaddress){
        token = Day(_token);
    }

    function setToken(address _token) public onlyOwner {
        token = Day(_token);
    }

    function getFee() public view returns (uint256) {
        return fee;
    }

    function setFee(uint256 value) public onlyOwner returns (bool) {
        fee = value;
        return true;
    }

    function transfer(address _to, uint48 _tokenId) public {
        transferFrom(msg.sender, _to, _tokenId);
    }

    function transferAll(address _to, uint48[] memory _tokenId) public {
        for (uint i = 0; i < _tokenId.length; i++) {
            transferFrom(msg.sender, _to, _tokenId[i]);
        }
    }

    function MinterCreate(address owner,  bytes32 title, bytes32 url1, bytes16 url2) public onlyOwner returns (uint256) {
        return _mint(owner, title, url1,url2);
    }

    function create(address owner,  bytes32 title, bytes32 url1, bytes16 url2) public returns (uint256) {
        require(token.balanceOf(msg.sender) >= fee, "Day token insufficient");
        require(token.allowance(msg.sender, address(this)) >= fee, "please approve Day token to contract");
        token.transferFrom(msg.sender, address(this), fee);
        return _mint(owner, title, url1,url2);
    }

    function withdrawFee(address user,uint amount) public onlyOwner {
        token.transfer(user,amount);
    }

}

/**
 *Submitted for verification at Etherscan.io on 2020-12-31
*/

pragma solidity ^0.5.4;
pragma experimental ABIEncoderV2;

interface IERC165 {
    function supportsInterface(bytes4 interfaceId) external view returns (bool);
}


contract IERC721 is IERC165 {
    event Transfer(address indexed from, address indexed to, uint256 indexed tokenId);
    event Approval(address indexed owner, address indexed approved, uint256 indexed tokenId);
    event ApprovalForAll(address indexed owner, address indexed operator, bool approved);

    function balanceOf(address owner) public view returns (uint256 balance);
    function ownerOf(uint128 tokenId) public view returns (address owner);
    function approve(address to, uint128 tokenId) public;
    function getApproved(uint128 tokenId) public view returns (address operator);
    function setApprovalForAll(address operator, bool _approved) public;
    function isApprovedForAll(address owner, address operator) public view returns (bool);
    function transferFrom(address from, address to, uint128 tokenId) public;
    function safeTransferFrom(address from, address to, uint128 tokenId) public;
    function safeTransferFrom(address from, address to, uint128 tokenId, bytes memory data) public;
}


contract IERC721Receiver {
    function onERC721Received(address operator, address from, uint256 tokenId, bytes memory data)
    public returns (bytes4);
}


library SafeMath {
    function mul(uint256 a, uint256 b) internal pure returns (uint256) {
        if (a == 0) {
            return 0;
        }
        uint256 c = a * b;
        require(c / a == b);
        return c;
    }

    function div(uint256 a, uint256 b) internal pure returns (uint256) {
        require(b > 0);
        uint256 c = a / b;
        return c;
    }

    function sub(uint256 a, uint256 b) internal pure returns (uint256) {
        require(b <= a);
        uint256 c = a - b;
        return c;
    }

    function add(uint256 a, uint256 b) internal pure returns (uint256) {
        uint256 c = a + b;
        require(c >= a);
        return c;
    }

    function mod(uint256 a, uint256 b) internal pure returns (uint256) {
        require(b != 0);
        return a % b;
    }
}


library Address {
    function isContract(address account) internal view returns (bool) {
        uint256 size;
        assembly { size := extcodesize(account) }
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
    using SafeMath for uint256;
    using Address for address;

    bytes4 private constant _ERC721_RECEIVED = 0x150b7a02;

    mapping (address => mapping (address => bool)) private _operatorApprovals;
    bytes4 private constant _INTERFACE_ID_ERC721 = 0x80ac58cd;

    mapping(address => uint128[]) internal balanceOf_;
    mapping(uint128 => address) private allowance_;

    mapping(uint128 => NftBo) public nftdesc;
    mapping(uint128 => NftBoFinal) public nftFinalData;
    uint128 private nftid = 0;
    uint256 public totalSupply;
    mapping(uint256 => bool) public lockmap;

    struct NftBo {
        address owner;
        uint48 index;
    }

    struct NftBoFinal {
        uint48 createTime;
        uint128 id;
        bytes32 title;
        string url;
    }

    constructor () public {
        _registerInterface(_INTERFACE_ID_ERC721);
    }

    modifier isLock(uint256 tokenId) {
        require(!lockmap[tokenId]);
        _;
    }


    function lockNft(uint256 tokenId) internal {
        lockmap[tokenId] = true;
    }

    function unlockNft(uint256 tokenId) internal {
        lockmap[tokenId] = false;
    }

    function balanceOf(address owner) public view returns (uint256) {
        require(owner != address(0));
        return balanceOf_[owner].length;
    }

    function ownerOf(uint128 tokenId) public view returns (address) {
        address owner = nftdesc[tokenId].owner;
        require(owner != address(0),"not owner");
        return owner;
    }

    function approve(address to, uint128 tokenId) public isLock(tokenId) {
        require(nftFinalData[tokenId].id > 0, "not exist nft");
        require(to != msg.sender, "not exist nft");
        NftBo memory nftBo = nftdesc[tokenId];
        require(balanceOf_[msg.sender][nftBo.index] == tokenId,"this nft are not your");

        allowance_[tokenId] = to;

        emit Approval(msg.sender, to, tokenId);
    }

    function getApproved(uint128 tokenId) public view returns (address) {
        require(allowance_[tokenId] != address(0));
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

    function transferFrom(address from, address to, uint128 tokenId) public isLock(tokenId) {
        _transferFrom(from, to, tokenId);
    }

    function safeTransferFrom(address from, address to, uint128 tokenId) public isLock(tokenId) {
        safeTransferFrom(from, to, tokenId, "");
    }

    function safeTransferFrom(address from, address to, uint128 tokenId, bytes memory _data) public isLock(tokenId) {
        transferFrom(from, to, tokenId);
        require(_checkOnERC721Received(from, to, tokenId, _data));
    }

    function _exists(uint128 tokenId) internal view returns (bool) {
        return nftFinalData[tokenId].id > 0;
    }

    function _mint(address owner,string memory url,string memory title) internal returns(uint256){
        require(owner != address(0));

        totalSupply++;
        nftid = nftid + 1;
        NftBo memory nftBo = NftBo(owner, uint48(balanceOf_[owner].length));
        NftBoFinal memory finalBo = NftBoFinal(uint48(now),nftid,stringToBytes32(title),url);
        nftdesc[nftid] = nftBo;
        nftFinalData[nftid] = finalBo;

        balanceOf_[owner].push(nftid);

        return nftid;
    }

    function _transferFrom(address src, address dst, uint128 id) internal {
        require(dst != address(0));
        require(nftFinalData[id].id > 0,"not exist nft");
        NftBo memory nftBo = nftdesc[id];

        require(balanceOf_[src][nftBo.index] == id, "this nft is not src user");

        if (src != msg.sender && !isApprovedForAll(nftBo.owner, msg.sender)) {
            require(allowance_[id] == msg.sender, "you can not spend this nft");
            delete allowance_[id];
        }

        uint48 lastTokenIndex = uint48(balanceOf_[src].length) - 1;
        uint48 tokenIndex = nftBo.index;
        if (tokenIndex != lastTokenIndex) {
            nftdesc[balanceOf_[src][lastTokenIndex]].index = tokenIndex;
            balanceOf_[src][tokenIndex] = balanceOf_[src][lastTokenIndex];
        }
        balanceOf_[src].length--;

        nftdesc[id].index = uint48(balanceOf_[dst].length);
        nftdesc[id].owner = dst;
        balanceOf_[dst].push(id);

        emit Transfer(src, dst, id);
    }

    function _checkOnERC721Received(address from, address to, uint256 tokenId, bytes memory _data)
    internal returns (bool)
    {
        if (!to.isContract()) {
            return true;
        }

        bytes4 retval = IERC721Receiver(to).onERC721Received(msg.sender, from, tokenId, _data);
        return (retval == _ERC721_RECEIVED);
    }

    function uint2str(uint i) internal pure returns (string memory){
        if (i == 0) return "0";
        uint j = i;
        uint length;
        while (j != 0){
            length++;
            j /= 10;
        }
        bytes memory bstr = new bytes(length);
        uint k = length - 1;
        while (i != 0){
            bstr[k--] = byte(uint8(48 + i % 10));
            i /= 10;
        }
        return string(bstr);
    }

    function strConcat(string memory _a, string memory _b) internal pure returns (string memory) {
        bytes memory _ba = bytes(_a);
        bytes memory _bb = bytes(_b);
        string memory ab = new string(_ba.length + _bb.length);
        bytes memory bab = bytes(ab);
        uint k = 0;
        for (uint i = 0; i < _ba.length; i++) bab[k++] = _ba[i];
        for (uint j = 0; j < _bb.length; j++) bab[k++] = _bb[j];
        return string(bab);
    }


    function bytes32ToString(bytes32 _bytes32) public pure returns (string memory) {
        uint8 i = 0;
        while(i < 32 && _bytes32[i] != 0) {
            i++;
        }
        bytes memory bytesArray = new bytes(i);
        for (i = 0; i < 32 && _bytes32[i] != 0; i++) {
            bytesArray[i] = _bytes32[i];
        }
        return string(bytesArray);
    }

    function stringToBytes32(string memory _source) public pure returns (bytes32 result) {
        bytes memory tempEmptyStringTest = bytes(_source);
        if (tempEmptyStringTest.length == 0) {
            return 0x0;
        }

        assembly {
            result := mload(add(_source, 32))
        }
    }
}


contract IERC721Enumerable is IERC721 {

    function tokenOfOwnerByIndex(address owner, uint256 index) public view returns (uint256 tokenId);
    function tokenByIndex(uint256 index) public view returns (uint256);
}


contract ERC721Enumerable is ERC165, ERC721, IERC721Enumerable {

    bytes4 private constant _INTERFACE_ID_ERC721_ENUMERABLE = 0x780e9d63;

    constructor () public {
        _registerInterface(_INTERFACE_ID_ERC721_ENUMERABLE);
    }

    function tokenOfOwnerByIndex(address owner, uint256 index) public view returns (uint256) {
        require(index < balanceOf_[owner].length, "ERC721Enumerable: owner index out of bounds");
        return balanceOf_[owner][index];
    }

    function tokenByIndex(uint256 index) public view returns (uint256) {
        return uint256(-1);
    }

    function tokensOfOwner(address owner) public view returns (uint256[] memory ids) {
        uint256 length = balanceOf_[owner].length;
        ids = new uint256[](length);
        for(uint256 i=0;i<length;i++){
            ids[i] = balanceOf_[owner][i];
        }
        return ids;
    }

    function tokensOfOwnerByIndexs(address owner, uint256[] memory indexs) public view returns (uint128[] memory ids,
        string[] memory urls,string[] memory titles,uint48[] memory times) {
        ids = new uint128[](indexs.length);
        urls = new string[](indexs.length);
        titles = new string[](indexs.length);
        times = new uint48[](indexs.length);
        for(uint256 i=0;i<indexs.length;i++){
            ids[i] = balanceOf_[owner][indexs[i]];
            urls[i] = nftFinalData[ids[i]].url;
            titles[i] = bytes32ToString(nftFinalData[ids[i]].title);
            times[i] = nftFinalData[ids[i]].createTime;
        }
        return (ids,urls,titles,times);
    }

    function tokensByIndexs(uint128[] memory tokenIds) public view returns (uint128[] memory ids,
        string[] memory urls,string[] memory titles,uint48[] memory times) {
        ids = new uint128[](tokenIds.length);
        urls = new string[](tokenIds.length);
        titles = new string[](tokenIds.length);
        times = new uint48[](tokenIds.length);
        for(uint256 i=0;i<tokenIds.length;i++){
            ids[i] = nftFinalData[tokenIds[i]].id;
            urls[i] = nftFinalData[tokenIds[i]].url;
            titles[i] = bytes32ToString(nftFinalData[tokenIds[i]].title);
            times[i] = nftFinalData[tokenIds[i]].createTime;
        }
        return (ids,urls,titles,times);
    }

}


contract IERC721Metadata is IERC721 {
    function name() external view returns (string memory);
    function symbol() external view returns (string memory);
    function tokenURI(uint128 tokenId) external view returns (string memory);
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

    function tokenURI(uint128 tokenId) external view returns (string memory) {
        require(_exists(tokenId));
        string memory infoUrl;
        infoUrl = strConcat('https://blockdatanalysis.com/v1/', uint2str(tokenId));
        return infoUrl;
    }
}


contract ERC721Full is ERC721, ERC721Enumerable, ERC721Metadata {
    constructor (string memory name, string memory symbol) public ERC721Metadata(name, symbol) {
    }
}


library Roles {
    struct Role {
        mapping (address => bool) bearer;
    }

    function add(Role storage role, address account) internal {
        require(account != address(0));
        require(!has(role, account));

        role.bearer[account] = true;
    }

    function remove(Role storage role, address account) internal {
        require(account != address(0));
        require(has(role, account));

        role.bearer[account] = false;
    }

    function has(Role storage role, address account) internal view returns (bool) {
        require(account != address(0));
        return role.bearer[account];
    }
}



contract MinterRole {
    using Roles for Roles.Role;
    event MinterAdded(address indexed account);
    event MinterRemoved(address indexed account);

    Roles.Role private _minters;

    constructor () internal {
        _addMinter(msg.sender);
    }

    modifier onlyMinter() {
        require(isMinter(msg.sender));
        _;
    }

    function isMinter(address account) public view returns (bool) {
        return _minters.has(account);
    }

    function addMinter(address account) public onlyMinter {
        _addMinter(account);
    }

    function renounceMinter() public {
        _removeMinter(msg.sender);
    }

    function _addMinter(address account) internal {
        _minters.add(account);
        emit MinterAdded(account);
    }

    function _removeMinter(address account) internal {
        _minters.remove(account);
        emit MinterRemoved(account);
    }
}


contract ERC721Mintable is ERC721, MinterRole {

    function mint(address to,string memory url,string memory title) public onlyMinter returns (bool) {
        _mint(to,url,title);
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

contract Market is ERC721Full,Ownable{
    using SafeMath for uint256;
    mapping(uint256 => NftInfo) private nftprice;
    uint256[] private sellingnft;
    uint256[] private sellednft;
    mapping(address => uint256[]) private userselling;

    Day private mtoken;
    address private feeAddress;

    mapping(uint256 => NftPM) private nftpm;
    uint256[] private pming;
    uint256[] private other;
    mapping(address => uint256[]) private userpm;



    struct NftInfo {
        uint256 price;
        uint256 index;
        uint256 userindex;
        bool sell;
    }

    struct NftPM {
        address finaluser;
        uint256 finalprice;
        uint256 minprice;
        uint256 endtime;
        uint256 mincall;
        uint256 index;
        uint256 state;
        uint256 count;
        uint256 userindex;
    }

    constructor (address _token, address _feeaddress) public {
        mtoken = Day(_token);
        feeAddress = _feeaddress;
    }

    function setFeeAddress(address _address) public onlyOwner {
        feeAddress = _address;
    }

    function sellingnum() public view returns(uint256) {
        return sellingnft.length;
    }

    function usersellingnum() public view returns(uint256) {
        return userselling[msg.sender].length;
    }

    function sellednum() public view returns(uint256) {
        return sellednft.length;
    }

    function pmingnum() public view returns(uint256) {
        return pming.length;
    }

    function userpmingnum() public view returns(uint256) {
        return userpm[msg.sender].length;
    }

    function othernum() public view returns(uint256) {
        return other.length;
    }

    function addMarket(uint128 tokenId, uint256 price) public isLock(tokenId) returns(bool) {
        require(msg.sender == ownerOf(tokenId),"This nft is not yours");
        require(price > 0, "price must big 0");
        if(nftprice[tokenId].sell){
            uint256 swaptokenId = sellednft[sellednft.length-1];
            uint256 newindex = nftprice[tokenId].index;
            sellednft[newindex] = swaptokenId;
            sellednft.length--;
            nftprice[swaptokenId].index = newindex;
        }

        nftprice[tokenId] = NftInfo(price,sellingnft.length,userselling[msg.sender].length,false);
        sellingnft.push(tokenId);
        userselling[msg.sender].push(tokenId);
        approve(address(this), tokenId);
        lockNft(tokenId);
        return true;
    }

    function cancel(uint128 tokenId) public {
        require(lockmap[tokenId] && sellingnft[nftprice[tokenId].index] == tokenId, "not add to market");
        require(msg.sender == ownerOf(tokenId),"This nft is not yours");
        uint256 sellinglaste = sellingnft.length-1;
        uint256 usersellinglaste = userselling[msg.sender].length-1;
        uint256 swaptokenId = sellingnft[sellinglaste];
        uint256 newindex = nftprice[tokenId].index;
        uint256 newuserindex = nftprice[tokenId].userindex;
        uint256 swapusertokenid = userselling[msg.sender][usersellinglaste];
        sellingnft[newindex] = swaptokenId;
        sellingnft.length--;
        userselling[msg.sender][newuserindex] = swapusertokenid;
        userselling[msg.sender].length--;
        nftprice[swaptokenId].index = newindex;
        nftprice[swapusertokenid].userindex = newuserindex;
        unlockNft(tokenId);
    }

    function buy(uint128 tokenId) public returns(bool){
        require(msg.sender != ownerOf(tokenId), "NFT is your");
        require(mtoken.balanceOf(msg.sender) >= nftprice[tokenId].price, "Day token insufficient");
        // require(mtoken.allowance(msg.sender,address(this)) >= nftprice[tokenId].price, "Please approve Day token to contract");
        require(sellingnft[nftprice[tokenId].index] == tokenId && !nftprice[tokenId].sell && lockmap[tokenId],"This nft is not for sale");
        mtoken.approve(address(this), mtoken.totalSupply());
        mtoken.transferFrom(msg.sender,ownerOf(tokenId),nftprice[tokenId].price * 9 / 10);
        mtoken.transferFrom(msg.sender,feeAddress,nftprice[tokenId].price / 10);

        uint256 sellinglaste = sellingnft.length-1;
        uint256 usersellinglaste = userselling[ownerOf(tokenId)].length-1;
        uint256 swaptokenId = sellingnft[sellinglaste];
        uint256 newindex = nftprice[tokenId].index;
        uint256 newuserindex = nftprice[tokenId].userindex;
        uint256 swapusertokenid = userselling[ownerOf(tokenId)][usersellinglaste];

        sellingnft[newindex] = swaptokenId;
        sellingnft.length--;
        nftprice[swaptokenId].index = newindex;

        userselling[ownerOf(tokenId)][newuserindex] = swapusertokenid;
        userselling[ownerOf(tokenId)].length--;
        nftprice[swapusertokenid].userindex = newuserindex;

        nftprice[tokenId].index = sellednft.length;
        nftprice[tokenId].sell = true;

        sellednft.push(tokenId);
        unlockNft(tokenId);
        transferFrom(ownerOf(tokenId), msg.sender, tokenId);
        return true;
    }

    function queryallselling(uint256[] memory indexs) public view returns(uint128[] memory ids,string[] memory urls,
        string[] memory titles,uint48[] memory times,uint256[] memory prices){
        return queryselling(indexs,0);
    }

    function queryuserselling(uint256[] memory indexs) public view returns(uint128[] memory ids,string[] memory urls,
        string[] memory titles,uint48[] memory times,uint256[] memory prices){
        return queryselling(indexs,1);
    }

    function queryselling(uint256[] memory indexs, uint256 select) private view returns(uint128[] memory ids,string[] memory urls,
        string[] memory titles,uint48[] memory times,uint256[] memory prices){
        uint128[] memory tokenIds = new uint128[](indexs.length);
        prices = new uint256[](indexs.length);
        uint256[] memory tokens = sellingnft;
        if(select == 1){
            tokens = userselling[msg.sender];
        }
        for(uint256 i=0;i<indexs.length;i++){
            tokenIds[i] = uint128(tokens[indexs[i]]);
            prices[i] = nftprice[tokenIds[i]].price;
        }
        (ids, urls, titles, times) = tokensByIndexs(tokenIds);
        return (ids,urls,titles,times,prices);
    }

    function queryselled(uint256[] memory indexs) public view returns(uint128[] memory ids,string[] memory urls,
        string[] memory titles,uint48[] memory times,uint256[] memory prices){
        uint128[] memory tokenIds = new uint128[](indexs.length);
        prices = new uint256[](indexs.length);
        for(uint256 i=0;i<indexs.length;i++){
            tokenIds[i] = uint128(sellednft[indexs[i]]);
            prices[i] = nftprice[tokenIds[i]].price;
        }
        (ids, urls, titles, times) = tokensByIndexs(tokenIds);
        return (ids,urls,titles,times,prices);
    }

    function getselldesc(uint128 tokenId) public view returns(uint128 id,string memory url,
        string memory title,uint48 time,uint256 price,bool sell){
        NftInfo memory nftinfo = nftprice[tokenId];
        NftBoFinal memory nftbo = nftFinalData[tokenId];
        require(sellingnft[nftprice[tokenId].index] == tokenId && !nftinfo.sell && lockmap[tokenId],"This nft is not for sale");
        return (tokenId,nftbo.url,bytes32ToString(nftbo.title),nftbo.createTime,nftinfo.price,nftinfo.sell);
    }

    /************NftPM***************/
    function addPMMarket(uint128 tokenId, uint256 minprice, uint256 endtime, uint256 mincall) public isLock(tokenId) returns(bool) {
        require(msg.sender == ownerOf(tokenId),"This nft is not yours");
        require(endtime > now, "endtime must big now");
        require(minprice > 0 && mincall > 0, "minprice and mincall must big 0");
        if(nftpm[tokenId].state > 0){
            uint256 swaptokenId = other[other.length-1];
            uint256 newindex = nftpm[tokenId].index;
            other[nftpm[tokenId].index] = other[other.length-1];
            other.length--;
            nftprice[swaptokenId].index = newindex;
        }
        nftpm[tokenId] = NftPM(address(0),0,minprice,endtime,mincall,pming.length,0,0,userpm[msg.sender].length);
        pming.push(tokenId);
        userpm[msg.sender].push(tokenId);
        approve(address(this), tokenId);
        lockNft(tokenId);
        return true;
    }

    function call(uint128 tokenId, uint256 price) public returns(bool){
        require(msg.sender != ownerOf(tokenId), "NFT is your");
        require(nftpm[tokenId].endtime > now,"The auction has ended");
        require(price-nftpm[tokenId].finalprice >= nftpm[tokenId].mincall && price >= nftpm[tokenId].minprice, "call price error");
        require(mtoken.balanceOf(msg.sender) >= price, "Day token insufficient");
        // require(mtoken.allowance(msg.sender,address(this)) >= price, "Please approve Day token to contract");
        require(pming[nftpm[tokenId].index] == tokenId && nftpm[tokenId].state == 0,"This nft is not for auction");
        mtoken.approve(address(this), mtoken.totalSupply());
        mtoken.transferFrom(msg.sender,address(this),price);
        if(nftpm[tokenId].finalprice > 0){
            mtoken.transfer(nftpm[tokenId].finaluser, nftpm[tokenId].finalprice);
        }

        nftpm[tokenId].finalprice = price;
        nftpm[tokenId].finaluser = msg.sender;
        nftpm[tokenId].count = nftpm[tokenId].count + 1;
        return true;
    }

    function receives(uint128 tokenId) public returns(bool) {
        require(nftpm[tokenId].endtime <= now,"The auction has not ended");
        require(nftpm[tokenId].state == 0 && lockmap[tokenId],"can not receive");
        require(pming[nftpm[tokenId].index] == tokenId,"This nft is not for auction");

        uint256 pmlaste = pming.length-1;
        uint256 userpmlaste = userpm[ownerOf(tokenId)].length-1;
        uint256 swaptokenId = pming[pmlaste];
        uint256 swapusertokenid = userpm[ownerOf(tokenId)][userpmlaste];
        uint256 newindex = nftpm[tokenId].index;
        uint256 newuserindex = nftpm[tokenId].userindex;

        pming[newindex] = swaptokenId;
        pming.length--;
        nftpm[swaptokenId].index = newindex;

        userpm[ownerOf(tokenId)][newuserindex] = swapusertokenid;
        userpm[ownerOf(tokenId)].length--;
        nftpm[swapusertokenid].userindex = newuserindex;

        nftpm[tokenId].index = other.length;

        other.push(tokenId);
        unlockNft(tokenId);

        if(nftpm[tokenId].finalprice <= 0){
            nftpm[tokenId].state = 2;
        }else{
            nftpm[tokenId].state = 1;
            mtoken.transferFrom(msg.sender,ownerOf(tokenId),nftpm[tokenId].finalprice * 9 / 10);
            mtoken.transferFrom(msg.sender,feeAddress,nftpm[tokenId].finalprice / 10);
            transferFrom(ownerOf(tokenId), nftpm[tokenId].finaluser, tokenId);
        }
        return true;
    }

    function queryallpm(uint256[] memory indexs) public view returns(uint128[] memory ids,string[] memory urls,
        string[] memory titles,uint256[] memory times,uint256[] memory minprices, uint256[] memory finalprices,
        uint256[] memory mincalls, uint256[] memory endtimes, address[] memory finalusers, uint256[] memory counts){
        return querypm(indexs,0);
    }

    function queryuserpm(uint256[] memory indexs) public view returns(uint128[] memory ids,string[] memory urls,
        string[] memory titles,uint256[] memory times,uint256[] memory minprices, uint256[] memory finalprices,
        uint256[] memory mincalls, uint256[] memory endtimes, address[] memory finalusers, uint256[] memory counts){
        return querypm(indexs,1);
    }

    function querypm(uint256[] memory indexs,uint256 select) private view returns(uint128[] memory ids,string[] memory urls,
        string[] memory titles,uint256[] memory times,uint256[] memory minprices, uint256[] memory finalprices,
        uint256[] memory mincalls, uint256[] memory endtimes, address[] memory finalusers, uint256[] memory counts){
        minprices = new uint256[](indexs.length);
        finalprices = new uint256[](indexs.length);
        mincalls = new uint256[](indexs.length);
        endtimes = new uint256[](indexs.length);
        finalusers = new address[](indexs.length);
        counts = new uint256[](indexs.length);
        ids = new uint128[](indexs.length);
        uint256[] memory tokens = pming;
        if(select == 1){
            tokens = userpm[msg.sender];
        }
        for(uint256 i=0;i<indexs.length;i++){
            ids[i] = uint128(tokens[indexs[i]]);
            minprices[i] = nftpm[ids[i]].minprice;
            finalprices[i] = nftpm[ids[i]].finalprice;
            mincalls[i] = nftpm[ids[i]].mincall;
            endtimes[i] = nftpm[ids[i]].endtime;
            finalusers[i] = nftpm[ids[i]].finaluser;
            counts[i] = nftpm[ids[i]].count;
        }

        urls = new string[](ids.length);
        titles = new string[](ids.length);
        times = new uint256[](ids.length);
        for(uint256 i=0;i<ids.length;i++){
            urls[i] = nftFinalData[ids[i]].url;
            titles[i] = bytes32ToString(nftFinalData[ids[i]].title);
            times[i] = nftFinalData[ids[i]].createTime;
        }

        return (ids, urls, titles, times, minprices,finalprices,mincalls,endtimes,finalusers,counts);
    }

    function getpmdesc(uint128 tokenId) public view returns(string[] memory param1,uint256[] memory param2, address finaluser){
        require(pming[nftpm[tokenId].index] == tokenId && nftpm[tokenId].state == 0,"This nft is not for auction");
        NftPM memory pm = nftpm[tokenId];
        NftBoFinal memory nftbo = nftFinalData[tokenId];
        param1 = new string[](2);
        param1[0] = nftbo.url;
        param1[1] = bytes32ToString(nftbo.title);
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
    using SafeMath for uint256;

    Day private token;

    uint256 private fee = 10 * 10 ** 18;

    constructor (string memory _name, string memory _symbol, address _token, address _feeaddress) public
    ERC721Mintable()
    ERC721Full(_name, _symbol)
    Market(_token,_feeaddress){
        token = Day(_token);
    }

    function getFee() public view returns(uint256) {
        return fee;
    }

    function setFee(uint256 value) public onlyOwner returns(bool) {
        fee = value;
        return true;
    }

    function transfer(address _to, uint128 _tokenId) public {
        safeTransferFrom(msg.sender, _to, _tokenId);
    }

    function transferAll(address _to, uint128[] memory _tokenId) public {
        for (uint i = 0; i < _tokenId.length; i++) {
            safeTransferFrom(msg.sender, _to, _tokenId[i]);
        }
    }

    function MinterCreate(address owner,string memory url,string memory title) public onlyMinter returns (uint256) {
        return _mint(owner,url,title);
    }

    function create(address owner,string memory url,string memory title) public returns (uint256) {
        require(token.balanceOf(msg.sender) >= fee, "Day token insufficient");
        require(token.allowance(msg.sender,address(this)) >= fee, "please approve Day token to contract");
        token.transferFrom(msg.sender,address(this),fee);
        return _mint(owner,url,title);
    }

    function removeMinter(address account) public onlyOwner {
        _removeMinter(account);
    }
}

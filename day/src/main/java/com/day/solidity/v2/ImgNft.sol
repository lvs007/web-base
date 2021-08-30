/**
 *Submitted for verification at Etherscan.io on 2020-12-31
*/

pragma solidity ^0.5.4;
pragma experimental ABIEncoderV2;

interface IERC165 {
    function supportsInterface(bytes4 interfaceId) external view returns (bool);
}


contract IERC721 is IERC165 {
    event Transfer(address indexed from, address indexed to, uint96 indexed tokenId);
    event Approval(address indexed owner, address indexed approved, uint96 indexed tokenId);
    event ApprovalForAll(address indexed owner, address indexed operator, bool approved);

    function balanceOf(address owner) public view returns (uint256 balance);

    function ownerOf(uint96 tokenId) public view returns (address owner);

    function approve(address to, uint96 tokenId) public;

    function getApproved(uint96 tokenId) public view returns (address operator);

    function setApprovalForAll(address operator, bool _approved) public;

    function isApprovedForAll(address owner, address operator) public view returns (bool);

    function transferFrom(address from, address to, uint96 tokenId) public;

    function safeTransferFrom(address from, address to, uint96 tokenId) public;

    function safeTransferFrom(address from, address to, uint96 tokenId, bytes memory data) public;
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
    using SafeMath for uint256;
    using Address for address;

    bytes4 private constant _ERC721_RECEIVED = 0x150b7a02;

    mapping(address => mapping(address => bool)) private _operatorApprovals;
    bytes4 private constant _INTERFACE_ID_ERC721 = 0x80ac58cd;

    mapping(address => User) internal userMap;
    mapping(uint96 => address) private allowance_;

    mapping(uint96 => address) public nftdesc;
    mapping(uint96 => NftBoFinal) public nftFinalData;
    uint48 public userCount;
    mapping(uint96 => bool) public lockmap;

    struct NftBoFinal {
        uint48 createTime;
        string title;
        string url;
    }

    struct User {
        uint48 id;
        uint48 total;
        uint32 count;
    }

    event Create(address owner, uint96 nftId);

    constructor () public {
        _registerInterface(_INTERFACE_ID_ERC721);
    }

    modifier isLock(uint96 tokenId) {
        require(!lockmap[tokenId]);
        _;
    }

    function isLockToken(uint96 tokenId) public view returns(bool){
        return lockmap[tokenId];
    }

    // compress an NftBo into a 256b integer
    function genId(uint48 userId, uint48 userNftId) internal pure returns (uint96) {
        uint96 n = uint96(userId);
        n = (n<<48) | userNftId;
        return n;
    }
    // extract an NftBo from a 256b integer
    function tokenIdToUserNftId(uint96 n) internal pure returns (uint48) {
        // nftbo.index = uint48(n & ((1<<48)-1));
        n = n >> 48;
        return uint48(n);
    }

    function tokenIdToUserId(uint96 n) internal pure returns (uint48) {
        return uint48(n & ((1<<48)-1));
    }

    function lockNft(uint96 tokenId) public {
        address owner = nftdesc[tokenId];
        if(owner == msg.sender || isApprovedForAll(owner, msg.sender) || allowance_[tokenId] == msg.sender){
            lockmap[tokenId] = true;
        }
    }

    function unlockNft(uint96 tokenId) public {
        address owner = nftdesc[tokenId];
        if(owner == msg.sender || isApprovedForAll(owner, msg.sender) || allowance_[tokenId] == msg.sender){
            lockmap[tokenId] = false;
        }
    }

    function balanceOf(address owner) public view returns (uint256) {
        require(owner != address(0));
        return userMap[owner].count;
    }

    function ownerOf(uint96 tokenId) public view returns (address) {
        address owner = nftdesc[tokenId];
        require(owner != address(0), "not owner");
        return owner;
    }

    function approve(address to, uint96 tokenId) public isLock(tokenId) {
        require(to != msg.sender, "not exist nft");
        require(nftdesc[tokenId] == msg.sender, "this nft are not your");

        allowance_[tokenId] = to;

        emit Approval(msg.sender, to, tokenId);
    }

    function getApproved(uint96 tokenId) public view returns (address) {
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

    function transferFrom(address from, address to, uint96 tokenId) public isLock(tokenId) {
        _transferFrom(from, to, tokenId);
    }

    function safeTransferFrom(address from, address to, uint96 tokenId) public isLock(tokenId) {
        safeTransferFrom(from, to, tokenId, "");
    }

    function safeTransferFrom(address from, address to, uint96 tokenId, bytes memory _data) public isLock(tokenId) {
        transferFrom(from, to, tokenId);
        require(_checkOnERC721Received(from, to, tokenId, _data));
    }

    function _exists(uint96 tokenId) internal view returns (bool) {
        return nftFinalData[tokenId].createTime > 0;
    }

    function _mint(address owner, string memory url, string memory title) internal returns (uint96){
        require(owner != address(0));
        User memory userBo = userMap[owner];
        if(userBo.id <= 0){
            userCount++;
            userBo = User(userCount,1,1);
        }else{
            userBo.total = userBo.total + 1;
            userBo.count = userBo.count + 1;
        }

        uint96 tokenId = genId(userBo.id,userBo.total);
        nftdesc[tokenId] = owner;
        nftFinalData[tokenId] = NftBoFinal(uint48(now), title, url);
        userMap[owner] = userBo;
        emit Create(owner,tokenId);
        return tokenId;
    }

    function _transferFrom(address src, address dst, uint96 id) internal {
        require(dst != address(0));
        require(nftdesc[id] == src, "this nft is not src user");

        if (src != msg.sender && !isApprovedForAll(nftdesc[id], msg.sender)) {
            require(allowance_[id] == msg.sender, "you can not spend this nft");
            delete allowance_[id];
        }

        nftdesc[id] = dst;
        userMap[src].count = userMap[src].count - 1;

        emit Transfer(src, dst, id);
    }

    function _checkOnERC721Received(address from, address to, uint96 tokenId, bytes memory _data)
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
        while (j != 0) {
            length++;
            j /= 10;
        }
        bytes memory bstr = new bytes(length);
        uint k = length - 1;
        while (i != 0) {
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
        while (i < 32 && _bytes32[i] != 0) {
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

    function tokenOfOwnerByIndex(address owner, uint48 index) public view returns (uint256 tokenId);

    function tokenByIndex(uint256 index) public view returns (uint256);
}


contract ERC721Enumerable is ERC165, ERC721, IERC721Enumerable {

    bytes4 private constant _INTERFACE_ID_ERC721_ENUMERABLE = 0x780e9d63;

    constructor () public {
        _registerInterface(_INTERFACE_ID_ERC721_ENUMERABLE);
    }

    function tokenOfOwnerByIndex(address owner, uint48 index) public view returns (uint256) {
        User memory user = userMap[owner];
        require(index <= user.total, "ERC721Enumerable: owner index out of bounds");
        uint96 tokenId = genId(user.id,index);
        if(nftdesc[tokenId] == owner){
            return tokenId;
        }
        return 0;
    }

    function tokenByIndex(uint256 index) public view returns (uint256) {
        return uint256(-1);
    }

    function tokensOfOwner(address owner) public view returns (uint96[] memory ids) {
        User memory userBo = userMap[owner];
        ids = new uint96[](userBo.count);
        uint256 index = 0;
        for(uint48 i=1;i<=userBo.total;i++){
            uint96 tokenId = genId(userBo.id,i);
            if(nftdesc[tokenId] == owner){
                ids[index] = tokenId;
                index++;
            }

        }
        return ids;
    }

    function tokensOfOwnerByIndexs(address owner, uint pageNum,uint pageNo) public view returns (uint96[] memory ids,NftBoFinal[] memory nftBos) {
        require(pageNum > 0 && pageNo > 0);
        User memory user = userMap[owner];
        uint start = pageNum*(pageNo-1)+1;
        uint end = pageNum*pageNo;
        uint index = 0;

        ids = new uint96[](pageNum-1);
        nftBos = new NftBoFinal[](pageNum-1);
        for(uint48 i=1;i<=user.total;i++){
            uint96 tokenId = genId(user.id,i);
            if(nftdesc[tokenId] == owner){
                index++;
                if(index >= start && index <= end){
                    ids[index] = tokenId;
                    nftBos[index] = nftFinalData[tokenId];
                }
                if(index > end){
                    return (ids,nftBos);
                }
            }

        }
        return (ids,nftBos);
    }

    function tokensByIndexs(uint96[] memory tokenIds) public view returns (NftBoFinal[] memory nftBos) {
        nftBos = new NftBoFinal[](tokenIds.length);
        for (uint128 i = 0; i < tokenIds.length; i++) {
            nftBos[i] = nftFinalData[tokenIds[i]];
        }
        return nftBos;
    }

}


contract IERC721Metadata is IERC721 {
    function name() external view returns (string memory);

    function symbol() external view returns (string memory);

    function tokenURI(uint96 tokenId) external view returns (string memory);
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

    function tokenURI(uint96 tokenId) external view returns (string memory) {
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
        mapping(address => bool) bearer;
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

    function mint(address to, string memory url, string memory title) public onlyMinter returns (bool) {
        _mint(to, url, title);
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


contract CreateNFT is ERC721Full, Ownable, ERC721Mintable {
    using SafeMath for uint256;

    Day private token;

    uint256 private fee = 10 * 10 ** 18;

    constructor (string memory _name, string memory _symbol, address _token) public
    ERC721Mintable()
    ERC721Full(_name, _symbol){
        token = Day(_token);
    }

    function getFee() public view returns (uint256) {
        return fee;
    }

    function setFee(uint256 value) public onlyOwner returns (bool) {
        fee = value;
        return true;
    }

    function transfer(address _to, uint96 _tokenId) public {
        safeTransferFrom(msg.sender, _to, _tokenId);
    }

    function transferAll(address _to, uint96[] memory _tokenId) public {
        for (uint i = 0; i < _tokenId.length; i++) {
            safeTransferFrom(msg.sender, _to, _tokenId[i]);
        }
    }

    function MinterCreate(address owner, string memory url, string memory title) public onlyMinter returns (uint256) {
        return _mint(owner, url, title);
    }

    function create(address owner, string memory url, string memory title) public returns (uint256) {
        require(token.balanceOf(msg.sender) >= fee, "Day token insufficient");
        require(token.allowance(msg.sender, address(this)) >= fee, "please approve Day token to contract");
        token.transferFrom(msg.sender, address(this), fee);
        return _mint(owner, url, title);
    }

    function withdrawFee(address user,uint amount) public onlyOwner {
        token.transfer(user,amount);
    }

    function removeMinter(address account) public onlyOwner {
        _removeMinter(account);
    }
}

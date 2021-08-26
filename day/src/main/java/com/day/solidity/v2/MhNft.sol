pragma solidity ^0.5.8;

interface IERC165 {
    function supportsInterface(bytes4 interfaceId) external view returns (bool);
}


contract IERC721 is IERC165 {
    event Transfer(address indexed from, address indexed to, uint96 indexed tokenId);
    event Approval(address indexed owner, address indexed approved, uint96 indexed tokenId);

    function balanceOf(address owner) public view returns (uint256 balance);
    function ownerOf(uint96 tokenId) public view returns (address owner);
    function approve(address to, uint96 tokenId) public;
    function getApproved(uint96 tokenId) public view returns (address operator);
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
    bytes4 private constant _INTERFACE_ID_ERC721 = 0x80ac58cd;

    mapping(address => User) internal user;
    mapping(uint96 => address) private allowance_;

    mapping(uint96 => NftBo) public nftdesc;

    uint48 public userCount;

    struct NftBo {
        address owner;
        uint8 level;
    }

    struct User {
        uint48 id;
        uint48 total;
        uint32 count;
    }

    event Create(address owner, uint96 nftId, uint8 level);
    // event Transfer(address from, address to, uint48 nftId);

    constructor () public {
        _registerInterface(_INTERFACE_ID_ERC721);
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

    function _exists(uint96 tokenId) public view returns (bool) {
        return nftdesc[tokenId].owner != address(0);
    }

    function balanceOf(address owner) public view returns (uint256) {
        require(owner != address(0));
        return user[owner].count;
    }

    function ownerOf(uint96 tokenId) public view returns (address) {
        address owner = nftdesc[tokenId].owner;
        require(owner != address(0));
        return owner;
    }

    function approve(address guy, uint96 id) public {
        require(guy != msg.sender, "not exist nft");
        require(nftdesc[id].owner == msg.sender,"this nft are not your");

        allowance_[id] = guy;

        emit Approval(msg.sender, guy, id);
    }

    function removeAllowance(uint96 id) public returns (bool) {
        require(allowance_[id] != address(0), "not nft allowance");
        require(nftdesc[id].owner == msg.sender,"this nft are not your");

        delete allowance_[id];

        return true;
    }

    function getApproved(uint96 id) public view returns (address){
        require(allowance_[id] != address(0));
        return allowance_[id];
    }

    function safeTransferFrom(address from, address to, uint96 tokenId) public {
        safeTransferFrom(from, to, tokenId, "");
    }

    function safeTransferFrom(address from, address to, uint96 tokenId, bytes memory _data) public {
        transferFrom(from, to, tokenId);
        require(_checkOnERC721Received(from, to, tokenId, _data));
    }

    function _mint(address owner, uint8 level) internal returns (uint96) {

        NftBo memory nftBo = NftBo(owner, level);
        User memory userBo = user[owner];
        if(userBo.id <= 0){
            userCount++;
            userBo = User(userCount,1,1);
        }else{
            userBo.total = userBo.total + 1;
            userBo.count = userBo.count + 1;
        }
        uint96 tokenId = genId(userBo.id,userBo.total);
        nftdesc[tokenId] = nftBo;
        user[owner] = userBo;

        emit Create(owner,tokenId,level);
        return tokenId;
    }

    function transferFrom(address src, address dst, uint96 id) public {
        NftBo memory nftBo = nftdesc[id];
        require(nftBo.owner == src,"this nft are not your");

        if (src != msg.sender) {
            require(allowance_[id] == msg.sender, "you can not spend this nft");
            delete allowance_[id];
        }

        nftBo.owner = dst;

        nftdesc[id].owner = dst;
        user[src].count = user[src].count - 1;

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

}

contract IERC721Enumerable is IERC721 {
    function tokenOfOwnerByIndex(address owner, uint48 index) public view returns (uint96 tokenId);
    function tokenByIndex(uint256 index) public view returns (uint96);
}

contract ERC721Enumerable is ERC165, ERC721, IERC721Enumerable {

    bytes4 private constant _INTERFACE_ID_ERC721_ENUMERABLE = 0x780e9d63;

    constructor () public {
        _registerInterface(_INTERFACE_ID_ERC721_ENUMERABLE);
    }

    function tokenOfOwnerByIndex(address owner, uint48 index) public view returns (uint96) {
        require(index <= user[owner].total, "ERC721Enumerable: owner index out of bounds");
        uint96 tokenId = genId(user[owner].id,index);
        if(nftdesc[tokenId].owner == owner){
            return tokenId;
        }
        return 0;
    }

    function tokenByIndex(uint256 index) public view returns (uint96) {
        return 0;
    }

    function tokenOfOwner(address owner) public view returns (uint96[] memory ids,uint8[] memory levels) {
        User memory userBo = user[owner];
        levels = new uint8[](userBo.count);
        ids = new uint96[](userBo.count);
        uint256 index = 0;
        for(uint48 i=1;i<=userBo.total;i++){
            uint96 tokenId = genId(userBo.id,i);
            NftBo memory nftBo = nftdesc[tokenId];
            if(nftBo.owner == owner){
                levels[index] = nftBo.level;
                ids[index] = tokenId;
                index++;
            }

        }
        return (ids,levels);
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

    function mint(address to, uint8 level) public onlyMinter returns (uint96) {
        return _mint(to, level);
    }

    function merge(uint96[] memory ids) public returns (uint96) {
        uint32 idlength = uint32(ids.length);
        require(idlength == 5,"id count not right");

        uint256 level = nftdesc[ids[0]].level;

        for(uint256 i=0;i<idlength;i++){
            require(nftdesc[ids[i]].level == level,"ids level are not same");
            require(nftdesc[ids[i]].owner == msg.sender,"this nft are not your");
            delete nftdesc[ids[i]];
        }
        user[msg.sender].count = user[msg.sender].count - idlength;

        return mint(msg.sender,uint8(level)+1);
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

    // function renounceOwnership() public onlyOwner {
    //     emit OwnershipTransferred(_owner, address(0));
    //     _owner = address(0);
    // }

    function transferOwnership(address newOwner) public onlyOwner {
        _transferOwnership(newOwner);
    }

    function _transferOwnership(address newOwner) internal {
        require(newOwner != address(0));
        emit OwnershipTransferred(_owner, newOwner);
        _owner = newOwner;
    }
}


contract MHNFT is ERC721Full, ERC721Mintable, Ownable {
    using SafeMath for uint256;

    constructor (string memory _name, string memory _symbol) public
    ERC721Mintable()
    ERC721Full(_name, _symbol){
    }

    function transfer(address _to, uint96 _tokenId) public {
        safeTransferFrom(msg.sender, _to, _tokenId);
    }


    function create(address owner, uint8 level) public onlyMinter returns (uint96) {
        return mint(owner,level);
    }

    function removeMinter(address account) public onlyOwner {
        _removeMinter(account);
    }
}

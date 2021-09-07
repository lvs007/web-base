pragma solidity ^0.5.8;

interface IERC165 {
    function supportsInterface(bytes4 interfaceId) external view returns (bool);
}


contract IERC721 is IERC165 {
    event Transfer(address indexed from, address indexed to, uint48 indexed tokenId);
    event Approval(address indexed owner, address indexed approved, uint48 indexed tokenId);

    function balanceOf(address owner) public view returns (uint256 balance);
    function ownerOf(uint48 tokenId) public view returns (address owner);
    function approve(address to, uint48 tokenId) public;
    function getApproved(uint48 tokenId) public view returns (address operator);
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
    using Address for address;

    bytes4 private constant _ERC721_RECEIVED = 0x150b7a02;
    bytes4 private constant _INTERFACE_ID_ERC721 = 0x80ac58cd;

    mapping(address => uint48) internal user;
    mapping(uint48 => address) private allowance_;

    mapping(uint48 => NftBo) public nftdesc;

    uint48 public total;

    struct NftBo {
        address owner;
        uint8 level;
    }

    event Create(address owner, uint48 nftId, uint8 level);
    // event Transfer(address from, address to, uint48 nftId);

    constructor () public {
        _registerInterface(_INTERFACE_ID_ERC721);
    }

    function _exists(uint48 tokenId) public view returns (bool) {
        return nftdesc[tokenId].owner != address(0);
    }

    function balanceOf(address owner) public view returns (uint256) {
        require(owner != address(0));
        return user[owner];
    }

    function ownerOf(uint48 tokenId) public view returns (address) {
        address owner = nftdesc[tokenId].owner;
        require(owner != address(0));
        return owner;
    }

    function approve(address guy, uint48 id) public {
        require(guy != msg.sender, "not exist nft");
        require(nftdesc[id].owner == msg.sender,"this nft are not your");

        allowance_[id] = guy;

        emit Approval(msg.sender, guy, id);
    }

    function removeAllowance(uint48 id) public returns (bool) {
        require(allowance_[id] != address(0), "not nft allowance");
        require(nftdesc[id].owner == msg.sender,"this nft are not your");

        delete allowance_[id];

        return true;
    }

    function getApproved(uint48 id) public view returns (address){
        require(allowance_[id] != address(0));
        return allowance_[id];
    }

    function safeTransferFrom(address from, address to, uint48 tokenId) public {
        safeTransferFrom(from, to, tokenId, "");
    }

    function safeTransferFrom(address from, address to, uint48 tokenId, bytes memory _data) public {
        transferFrom(from, to, tokenId);
        require(_checkOnERC721Received(from, to, tokenId, _data));
    }

    function _mint(address owner, uint8 level) internal returns (uint48) {

        NftBo memory nftBo = NftBo(owner, level);
        total++;
        nftdesc[total] = nftBo;
        user[owner] = user[owner]+1;

        emit Create(owner,total,level);
        return total;
    }

    function transferFrom(address src, address dst, uint48 id) public {
        NftBo memory nftBo = nftdesc[id];
        require(nftBo.owner == src,"this nft are not your");

        if (src != msg.sender) {
            require(allowance_[id] == msg.sender, "you can not spend this nft");
            delete allowance_[id];
        }

        nftdesc[id].owner = dst;
        user[src] = user[src] - 1;
        user[dst] = user[dst] + 1;

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

}

contract IERC721Enumerable is IERC721 {
    function tokenOfOwnerByIndex(address owner, uint48 index) public view returns (uint48 tokenId);
    function tokenByIndex(uint48 index) public view returns (uint48);
}

contract ERC721Enumerable is ERC165, ERC721, IERC721Enumerable {

    bytes4 private constant _INTERFACE_ID_ERC721_ENUMERABLE = 0x780e9d63;

    constructor () public {
        _registerInterface(_INTERFACE_ID_ERC721_ENUMERABLE);
    }

    function tokenOfOwnerByIndex(address owner, uint48 index) public view returns (uint48) {
        require(index <= user[owner], "ERC721Enumerable: owner index out of bounds");
        uint48 count = 0;
        for(uint48 i=1;i<=total;i++){
            if(nftdesc[i].owner == owner){
                count++;
            }
            if(count==index+1){
                return i;
            }
        }

        return 0;
    }

    function tokenByIndex(uint48 index) public view returns (uint48) {
        if(nftdesc[index+1].owner == address(0)){
            return 0;
        }
        return index+1;
    }

    function tokenOfOwner(address owner) public view returns (uint48[] memory ids,uint8[] memory levels) {
        uint48 count = user[owner];
        levels = new uint8[](count);
        ids = new uint48[](count);
        uint256 index = 0;
        for(uint48 i=1;i<=total;i++){
            NftBo memory nftBo = nftdesc[i];
            if(nftBo.owner == owner){
                levels[index] = nftBo.level;
                ids[index] = i;
                index++;
            }

        }
        return (ids,levels);
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

    function mint(address to, uint8 level) public onlyMinter returns (uint48) {
        return _mint(to, level);
    }

    function merge(uint48[] memory ids) public returns (uint48) {
        uint32 idlength = uint32(ids.length);
        require(idlength == 5,"id count not right");

        uint256 level = nftdesc[ids[0]].level;

        for(uint256 i=0;i<idlength;i++){
            require(nftdesc[ids[i]].level == level,"ids level are not same");
            require(nftdesc[ids[i]].owner == msg.sender,"this nft are not your");
            delete nftdesc[ids[i]];
        }
        user[msg.sender] = user[msg.sender] - idlength;

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

    constructor (string memory _name, string memory _symbol) public
    ERC721Mintable()
    ERC721Full(_name, _symbol){
    }

    function transfer(address _to, uint48 _tokenId) public {
        safeTransferFrom(msg.sender, _to, _tokenId);
    }


    function create(address owner, uint8 level) public onlyMinter returns (uint48) {
        return mint(owner,level);
    }

    function removeMinter(address account) public onlyOwner {
        _removeMinter(account);
    }
}

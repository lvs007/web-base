pragma solidity ^0.5.8;

interface IERC20 {
    function totalSupply() external view returns (uint);
    function balanceOf(address account) external view returns (uint);
    function transfer(address recipient, uint amount) external returns (bool);
    function allowance(address owner, address spender) external view returns (uint);
    function approve(address spender, uint amount) external returns (bool);
    function transferFrom(address sender, address recipient, uint amount) external returns (bool);
    event Transfer(address indexed from, address indexed to, uint value);
    event Approval(address indexed owner, address indexed spender, uint value);
}

contract MHNFT {
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
    function totalSupply() external view returns (uint);
    function create(address owner, uint8 level) public returns (uint96);
}

contract Ownable {
    address public owner;


    event OwnershipRenounced(address indexed previousOwner);
    event OwnershipTransferred(
        address indexed previousOwner,
        address indexed newOwner
    );


    /**
     * @dev The Ownable constructor sets the original `owner` of the contract to the sender
     * account.
     */
    constructor() public {
        owner = msg.sender;
    }

    /**
     * @dev Throws if called by any account other than the owner.
     */
    modifier onlyOwner() {
        require(msg.sender == owner);
        _;
    }

    /**
     * @dev Allows the current owner to transfer control of the contract to a newOwner.
     * @param _newOwner The address to transfer ownership to.
     */
    function transferOwnership(address _newOwner) public onlyOwner {
        _transferOwnership(_newOwner);
    }

    /**
     * @dev Transfers control of the contract to a newOwner.
     * @param _newOwner The address to transfer ownership to.
     */
    function _transferOwnership(address _newOwner) internal {
        require(_newOwner != address(0));
        emit OwnershipTransferred(owner, _newOwner);
        owner = _newOwner;
    }
}

contract Mohe is Ownable {

    mapping(uint96 => address) public moheInfo;
    mapping(address => User) private userMap;

    uint48 public userCount = 0;

    uint256 private key;

    uint16 public nftCount0 = 5000;
    uint16 public nftCount1 = 2000;
    uint16 public nftCount2 = 1000;
    uint16 public nftCount3 = 100;
    uint16 public nftCount4 = 20;

    uint256 private price = 100 * 10 ** 18;

    IERC20 private token;
    address private team;
    MHNFT private nft;

    struct User {
        uint48 id;
        uint48 total;
        uint32 count;
    }

    event Buy(address owner, uint96 moheNum);
    event Open(address owner, uint96 moheNum, uint96 nftId);

    constructor(uint256 _key, address _token, address _team, address _mhnft) public Ownable() {
        key = _key;
        token = IERC20(_token);
        team = _team;
        nft = MHNFT(_mhnft);
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

    function getPrice() public view returns(uint256) {
        return price;
    }

    function setPrice(uint256 _price) public onlyOwner {
        price = _price;
    }

    function boxOfOwner(address _owner) public view returns (uint96[] memory ids) {
        User memory user = userMap[_owner];
        ids = new uint96[](user.count);
        uint48 index = 0;
        for(uint48 i=1; i <= user.total; i++){
            uint96 tokenId = genId(user.id,i);
            if(moheInfo[tokenId] == _owner){
                ids[index] = tokenId;
                index++;
            }
        }
        return ids;
    }

    function withdrawFee() public onlyOwner {
        token.transfer(msg.sender, token.balanceOf(msg.sender));
    }

    function buy() public returns(uint256) {
        require(token.balanceOf(msg.sender) >= price, "token value error, please check the price");//1wan
        token.transferFrom(msg.sender, address(this), price);//2.5wan
        return create();
    }

    function buyAndOpen() public returns(uint256) {
        require(token.balanceOf(msg.sender) >= price, "token value error, please check the price");//1wan
        token.transferFrom(msg.sender, address(this), price);//2.5wan
        User memory user = userMap[msg.sender];
        if(user.id<=0){//new
            userCount++;//2wan
            user = User(userCount,1,1);
        }else{//old
            user.total = user.total + 1;
        }
        userMap[msg.sender] = user;
        uint96 tokenId = genId(user.id,user.total);
        key = random(user.total);
        uint96 nftId = kaijiang(key);
        emit Open(msg.sender,tokenId, nftId);
        return nftId;
    }

    function create() private returns(uint256){
        User memory user = userMap[msg.sender];
        if(user.id<=0){//new
            userCount++;//2wan
            user = User(userCount,1,1);
        }else{//old
            user.total = user.total + 1;
            user.count = user.count + 1;
        }
        uint96 tokenId = genId(user.id,user.total);
        moheInfo[tokenId] = msg.sender;//2wan
        userMap[msg.sender] = user;
        emit Buy(msg.sender, tokenId);
        return tokenId;
    }

    function open(uint96 moheNumP) public returns(uint96){
        require(moheInfo[moheNumP] == msg.sender, "this moheNum are not you");

        delete moheInfo[moheNumP];
        userMap[msg.sender].count = userMap[msg.sender].count - 1;

        key = random(moheNumP);
        uint96 nftId = kaijiang(key);
        emit Open(msg.sender,moheNumP, nftId);
        return nftId;
    }


    function kaijiang(uint256 num) private returns(uint96){
        uint256 a = num % 100000;
        uint96 result;
        if(a > 0 && a <= 30000 && nftCount0 > 0) {
            result = nft.create(msg.sender, 0);
            nftCount0--;
        }else if(a > 30000 && a <= 40000 && nftCount1 > 0){
            result = nft.create(msg.sender, 1);
            nftCount1--;
        }else if(a > 40000 && a <= 42000 && nftCount2 > 0){
            result = nft.create(msg.sender, 2);
            nftCount2--;
        }else if(a > 42000 && a <= 43000 && nftCount3 > 0){
            result = nft.create(msg.sender, 3);
            nftCount3--;
        }else if(a > 99900 && nftCount4 > 0){
            result = nft.create(msg.sender, 4);
            nftCount4--;
        }
        return result;
    }

    function random(uint256 _moheNum) private view returns(uint256){
        return uint256(keccak256(abi.encodePacked(msg.sender, now, key, _moheNum)));
    }

}
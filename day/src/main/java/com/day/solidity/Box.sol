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
    event Transfer(address indexed from, address indexed to, uint256 indexed tokenId);
    event Approval(address indexed owner, address indexed approved, uint256 indexed tokenId);
    event ApprovalForAll(address indexed owner, address indexed operator, bool approved);

    function balanceOf(address owner) public view returns (uint256 balance);
    function ownerOf(uint256 tokenId) public view returns (address owner);
    function approve(address to, uint256 tokenId) public;
    function getApproved(uint256 tokenId) public view returns (address operator);
    function setApprovalForAll(address operator, bool _approved) public;
    function isApprovedForAll(address owner, address operator) public view returns (bool);
    function transferFrom(address from, address to, uint256 tokenId) public;
    function safeTransferFrom(address from, address to, uint256 tokenId) public;
    function safeTransferFrom(address from, address to, uint256 tokenId, bytes memory data) public;
    function totalSupply() external view returns (uint);
    function create(address owner, uint256 level) public returns (uint256);
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

    mapping(address => uint48[]) public moheUser;
    mapping(uint48 => MoheVo) private moheInfo;

    uint48 public moheNum = 0;

    uint256 private key;

    uint256 private nftCount0 = 5000;
    uint256 private nftCount1 = 2000;
    uint256 private nftCount2 = 1000;
    uint256 private nftCount3 = 100;
    uint256 private nftCount4 = 20;

    uint256 private price = 100 * 10 ** 18;

    IERC20 private token;
    address private team;
    MHNFT private nft;

    event Buy(address owner, uint48 moheNum, uint48 blockNum);
    event Open(address owner, uint48 moheNum, uint256 key);

    struct MoheVo {
        address owner;
        uint48 blockNum;
        uint32 index;
        bool open;
    }

    constructor(uint256 _key, address _token, address _team, address _mhnft) public Ownable() {
        key = _key;
        token = IERC20(_token);
        team = _team;
        nft = MHNFT(_mhnft);
    }

    function getPrice() public view returns(uint256) {
        return price;
    }

    function setPrice(uint256 _price) public {
        price = _price;
    }

    function boxOfOwner(address owner) public view returns (uint48[] memory ids) {

        return moheUser[owner];
    }

    function buy() public returns(uint256) {
        require(token.balanceOf(msg.sender) >= price, "token value error, please check the price");//1wan
        token.transferFrom(msg.sender, address(this), price * 9 / 10);//2.5wan
        token.transferFrom(msg.sender, team, price / 10);
        return create();
    }

    function create() private returns(uint256){
        moheNum = moheNum + 1;//2wan
        MoheVo memory moheVo = MoheVo(msg.sender, uint48(block.number + 1),uint32(moheUser[msg.sender].length),false);
        moheUser[msg.sender].push(moheNum);//5wan
        moheInfo[moheNum] = moheVo;//2wan
        emit Buy(msg.sender, moheNum,moheVo.blockNum);
        return moheNum;
    }

    function open(uint48 moheNumP) public returns(uint256){
        require(moheNumP > 0);
        require(moheInfo[moheNumP].owner == msg.sender, "this moheNum are not you");
        require(moheInfo[moheNumP].blockNum <= block.number, "wait next block time open");
        require(!moheInfo[moheNumP].open, "this mohe is open");
        uint48 latest = moheUser[msg.sender][moheUser[msg.sender].length-1];
        uint32 currentIndex = moheInfo[moheNumP].index;
        key = random(moheInfo[moheNumP].blockNum, moheNumP);
        moheInfo[moheNumP].open = true;
        moheUser[msg.sender][currentIndex] = latest;
        moheUser[msg.sender].length--;
        moheInfo[latest].index = uint32(currentIndex);
        emit Open(msg.sender,moheNumP,key);
        return kaijiang(key);
    }


    function kaijiang(uint256 num) private returns(uint256){
        uint256 a = num % 100000;
        uint256 result;
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
        return a;
    }

    function random(uint256 blockNum, uint256 _moheNum) private view returns(uint256){
        return uint256(keccak256(abi.encodePacked(blockhash(blockNum), now, key, _moheNum)));
    }

}
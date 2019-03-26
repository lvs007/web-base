package com.liang.sangong.trx.tron;

import static org.apache.commons.codec.digest.DigestUtils.sha256;

import com.google.protobuf.ByteString;
import com.liang.common.http.api.ApiResponse;
import com.liang.common.http.api.BaseApi;
import com.liang.common.http.api.exception.ApiException;
import com.liang.common.http.api.exception.HttpException;
import com.liang.common.http.api.exception.InternalException;
import com.liang.sangong.bo.TransactionInfo;
import com.liang.sangong.bo.TransactionInfo.TxState;
import com.liang.sangong.common.SystemState;
import com.liang.sangong.service.TransactionInfoService;
import com.liang.sangong.trx.tron.api.GrpcAPI.Return;
import com.liang.sangong.trx.tron.api.GrpcAPI.TransactionExtention;
import com.liang.sangong.trx.tron.api.WalletGrpc;
import com.liang.sangong.trx.tron.api.WalletGrpc.WalletBlockingStub;
import com.liang.sangong.trx.tron.crypto.ByteUtil;
import com.liang.sangong.trx.tron.crypto.ECKey;
import com.liang.sangong.trx.tron.crypto.ECKey.ECDSASignature;
import com.liang.sangong.trx.tron.protos.Contract.TransferContract;
import com.liang.sangong.trx.tron.protos.Protocol.Account;
import com.liang.sangong.trx.tron.protos.Protocol.Transaction;
import com.liang.sangong.trx.tron.protos.Protocol.Transaction.Contract;
import io.grpc.ManagedChannelBuilder;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferService {

  private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

  private WalletBlockingStub walletStub;

  private TrxSolidityHttp trxSolidityHttp;

  private Map<String, TransactionInfo> transactionInfoMap = new ConcurrentHashMap<>();

  private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

  @Autowired
  private TransactionInfoService transactionInfoService;

  @PostConstruct
  public void init() {
    walletStub = WalletGrpc
        .newBlockingStub(ManagedChannelBuilder.forTarget(SystemState.FULLNODE_IP + ":50051")
            .usePlaintext(true).build());
    trxSolidityHttp = new TrxSolidityHttp();
    scheduledExecutorService
        .scheduleWithFixedDelay(new QueryAndUpdateTask(), 2, 2, TimeUnit.MINUTES);
  }

  public boolean updateStub(String ip) {
    try {
      walletStub = WalletGrpc
          .newBlockingStub(ManagedChannelBuilder.forTarget(ip + ":50051")
              .usePlaintext(true).build());
      return true;
    } catch (Exception e) {
      logger.error("", e);
    }
    return false;
  }

  public boolean transferTrx(String pkey, long amount, long userId) {
    try {
      Wallet.setAddressPreFixByte(Wallet.ADD_PRE_FIX_BYTE_MAINNET);
      if (amount <= 0 || queryTrx(pkey) < amount) {
        return false;
      }
      BigInteger priK = new BigInteger(pkey, 16);
      ECKey myKey = ECKey.fromPrivate(priK);
      TransferContract.Builder builder = TransferContract.newBuilder();
      builder.setAmount(amount * 1000000);
      builder.setOwnerAddress(ByteString.copyFrom(myKey.getAddress()));
      builder.setToAddress(ByteString.
          copyFrom(Wallet.decodeFromBase58Check(SystemState.TO_ADDRESS)));
      TransactionExtention transactionExtention = walletStub.createTransaction2(builder.build());
      Transaction transaction = sign(transactionExtention.getTransaction(), myKey);
      Return returns = walletStub.broadcastTransaction(transaction);
      if (returns.getResult()) {
        String txId = ByteUtil.toHexString(transactionExtention.getTxid().toByteArray());
        TransactionInfo transactionInfo = TransactionInfo.build(txId, userId, amount);
        transactionInfoMap.put(txId, transactionInfo);
        return transactionInfoService.insert(transactionInfo);
      }
      return returns.getResult();
    } catch (Exception e) {
      logger.error("", e);
    }
    return false;
  }

  public long queryTrx(String pkey) {
    try {
      Wallet.setAddressPreFixByte(Wallet.ADD_PRE_FIX_BYTE_MAINNET);
      BigInteger priK = new BigInteger(pkey, 16);
      ECKey myKey = ECKey.fromPrivate(priK);
      Account account = Account.newBuilder().setAddress(ByteString.copyFrom(myKey.getAddress()))
          .build();
      account = walletStub.getAccount(account);
      return account == null ? 0 : account.getBalance();
    } catch (Exception e) {
      logger.error("", e);
    }
    return 0;
  }

  public Transaction createTransfer(String meAddress, String toAddress, long amount) {
    Wallet.setAddressPreFixByte(Wallet.ADD_PRE_FIX_BYTE_MAINNET);
    TransferContract.Builder builder = TransferContract.newBuilder();
    builder.setAmount(amount * 1000000);
    builder.setOwnerAddress(ByteString.copyFrom(Wallet.decodeFromBase58Check(meAddress)));
    builder.setToAddress(ByteString.copyFrom(Wallet.decodeFromBase58Check(toAddress)));
    TransactionExtention transactionExtention = walletStub.createTransaction2(builder.build());
    return transactionExtention.getTransaction();
  }

  public Transaction sign(Transaction transaction, ECKey myKey) {
    Transaction.Builder transactionBuilderSigned = transaction.toBuilder();
    byte[] hash = sha256(transaction.getRawData().toByteArray());
    List<Contract> contractList = transaction.getRawData().getContractList();
    for (int i = 0; i < contractList.size(); i++) {
      ECDSASignature signature = myKey.sign(hash);
      ByteString sign = ByteString.copyFrom(signature.toByteArray());
      transactionBuilderSigned.addSignature(sign);
    }
    return transactionBuilderSigned.build();
  }

  public boolean queryTransfer(String txId) throws Exception {
//    BytesMessage bytesMessage = BytesMessage.newBuilder().setValue(ByteString.copyFromUtf8(txId))
//        .build();
//    TransactionInfo transactionInfo = walletStub.getTransactionInfoById(bytesMessage);
//    if (transactionInfo.getResult() == code.SUCESS) {
//      transactionInfo.getBlockNumber();
//    } else {
//
//    }
    return trxSolidityHttp.getTransactionInfo(txId);
  }

  private static class TrxSolidityHttp extends BaseApi {

    public void getSolidityNowBlock() throws InternalException, ApiException, HttpException {
      ApiResponse apiResponse = httpGet("/walletsolidity/getnowblock");
      if (!apiResponse.getJsonObject().isEmpty()) {
        long blockNum = apiResponse.getJsonObject().getJSONObject("block_header")
            .getJSONObject("raw_data").getLong("number");
      }
      System.out.println(apiResponse.getJsonObject());
    }

    public boolean getTransactionInfo(String txId)
        throws InternalException, ApiException, HttpException {
      ApiResponse apiResponse = httpGet("/walletsolidity/gettransactioninfobyid?value=" + txId);
      System.out.println(apiResponse.getJsonObject());
      return !apiResponse.getJsonObject().isEmpty() && StringUtils
          .isBlank(apiResponse.getJsonObject().getString("Error"));
    }

    @Override
    protected String getApiHost() {
      return "http:" + SystemState.FULLNODE_IP + ":8091";
    }

    @Override
    protected String getSignKey() {
      return null;
    }
  }

  private class QueryAndUpdateTask implements Runnable {

    public QueryAndUpdateTask() {
      List<TransactionInfo> transactionInfoList = transactionInfoService.queryInit();
      for (TransactionInfo transactionInfo : transactionInfoList) {
        transactionInfoMap.put(transactionInfo.getTxId(), transactionInfo);
      }
    }

    @Override
    public void run() {
      for (Iterator<Entry<String, TransactionInfo>> iterator = transactionInfoMap.entrySet()
          .iterator(); iterator.hasNext(); ) {
        Entry<String, TransactionInfo> entry = iterator.next();
        try {
          if (trxSolidityHttp.getTransactionInfo(entry.getKey())) {
            transactionInfoService.update(entry.getValue().setState(TxState.success.code));
            iterator.remove();
          } else if (System.currentTimeMillis() - entry.getValue().getCreateTime()
              > SystemState.TX_TIME_OUT) {
            transactionInfoService.update(entry.getValue().setState(TxState.fail.code));
            iterator.remove();
          }
        } catch (Exception e) {
          logger.error("", e);
        }
      }
    }
  }

  public static void main(String[] args) {
    TrxSolidityHttp trxHttp = new TrxSolidityHttp();
    try {
      System.out.println(trxHttp
          .getTransactionInfo("f2f096a1340015e6a98e6e1fa9dc6c6bcb7dce8190ede27e550cd62be7ab109d"));
    } catch (InternalException e) {
      e.printStackTrace();
    } catch (ApiException e) {
      e.printStackTrace();
    } catch (HttpException e) {
      e.printStackTrace();
    }
  }
}

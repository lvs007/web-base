webpackJsonp([1], {
    NHnr: function(t, e, n) {
        "use strict";
        Object.defineProperty(e, "__esModule", {
            value: !0
        });
        var a = n("7+uW")
          , r = {
            render: function() {
                var t = this
                  , e = t.$createElement
                  , n = t._self._c || e;
                return n("div", {
                    attrs: {
                        id: "app"
                    }
                }, [n("div", {
                    attrs: {
                        id: "header"
                    }
                }, [t._m(0), t._v(" "), n("div", {
                    staticClass: "main-menu"
                }, [n("div", [n("router-link", {
                    class: {
                        active: "/" == this.$router.currentRoute.path
                    },
                    attrs: {
                        to: "/"
                    }
                }, [t._v("HOME")])], 1), t._v(" "), n("div", [n("router-link", {
                    class: {
                        active: this.$router.currentRoute.path.indexOf("/farm") > -1
                    },
                    attrs: {
                        to: "/farm"
                    }
                }, [t._v("FARM")])], 1), t._v(" "), n("div", [n("router-link", {
                    class: {
                        active: "/exchange" == this.$router.currentRoute.path
                    },
                    attrs: {
                        to: "/exchange"
                    }
                }, [t._v("EXCHANGE")])], 1), t._v(" "), n("div", [n("router-link", {
                    class: {
                        active: "/about" == this.$router.currentRoute.path
                    },
                    attrs: {
                        to: "/about"
                    }
                }, [t._v("ABOUT")])], 1)])]), t._v(" "), n("router-view"), t._v(" "), t._m(1)], 1)
            },
            staticRenderFns: [function() {
                var t = this.$createElement
                  , e = this._self._c || t;
                return e("div", {
                    staticClass: "main-title"
                }, [e("img", {
                    staticStyle: {
                        "vertical-align": "middle",
                        "margin-right": "15px"
                    },
                    attrs: {
                        src: n("lUua")
                    }
                }), this._v("Dimsum Swap Family")])
            }
            , function() {
                var t = this.$createElement
                  , e = this._self._c || t;
                return e("div", {
                    attrs: {
                        id: "footer"
                    }
                }, [e("div", {
                    staticClass: "content"
                }, [e("div", {
                    staticClass: "contract"
                }, [e("span", [this._v("DIMSUM")]), e("span", [this._v("Token Contract")]), e("a", {
                    staticStyle: {
                        "vertical-align": "middle",
                        "margin-left": "10px"
                    },
                    attrs: {
                        href: "https://tronscan.io/#/token20/TJHbXDUtaoPHwv9S6MDDZ3si4fMVnqfJ4B"
                    }
                }, [e("img", {
                    attrs: {
                        src: n("3+uR")
                    }
                })])]), this._v(" "), e("div", {
                    staticClass: "link"
                }, [e("a", {
                    attrs: {
                        href: "#"
                    }
                }, [e("img", {
                    attrs: {
                        src: n("Zum8")
                    }
                })]), this._v(" "), e("a", {
                    attrs: {
                        href: "#"
                    }
                }, [e("img", {
                    attrs: {
                        src: n("7M42")
                    }
                })]), this._v(" "), e("a", {
                    attrs: {
                        href: "#"
                    }
                }, [e("img", {
                    attrs: {
                        src: n("bmFd")
                    }
                })])])])])
            }
            ]
        };
        var s, i, o, c, u, p, l, d = n("VU/8")({
            name: "App",
            mounted: function() {}
        }, r, !1, function(t) {
            n("Tk6V")
        }, null, null).exports, A = n("/ocq"), m = n("Dd8w"), v = n.n(m), f = n("NYxO"), h = n("Gu7T"), y = n.n(h), x = n("Xxa5"), b = n.n(x), k = n("exGp"), w = n.n(k), g = (s = w()(b.a.mark(function t(e, n) {
            return b.a.wrap(function(t) {
                for (; ; )
                    switch (t.prev = t.next) {
                    case 0:
                        return t.next = 2,
                        window.tronWeb.contract(n).at(e);
                    case 2:
                        return t.abrupt("return", t.sent);
                    case 3:
                    case "end":
                        return t.stop()
                    }
            }, t, this)
        })),
        function(t, e) {
            return s.apply(this, arguments)
        }
        ), T = (i = w()(b.a.mark(function t(e, n, a) {
            var r, s, i;
            return b.a.wrap(function(t) {
                for (; ; )
                    switch (t.prev = t.next) {
                    case 0:
                        return t.next = 2,
                        g(e);
                    case 2:
                        return s = t.sent,
                        t.next = 5,
                        (r = s.methods)[n].apply(r, y()(a)).call();
                    case 5:
                        return i = t.sent,
                        t.abrupt("return", i);
                    case 7:
                    case "end":
                        return t.stop()
                    }
            }, t, this)
        })),
        function(t, e, n) {
            return i.apply(this, arguments)
        }
        ), S = (o = w()(b.a.mark(function t(e, n, a) {
            var r, s, i;
            return b.a.wrap(function(t) {
                for (; ; )
                    switch (t.prev = t.next) {
                    case 0:
                        return t.next = 2,
                        g(e, Q);
                    case 2:
                        return s = t.sent,
                        t.next = 5,
                        (r = s.methods)[n].apply(r, y()(a)).call();
                    case 5:
                        return i = t.sent,
                        t.abrupt("return", i);
                    case 7:
                    case "end":
                        return t.stop()
                    }
            }, t, this)
        })),
        function(t, e, n) {
            return o.apply(this, arguments)
        }
        ), C = (c = w()(b.a.mark(function t(e, n) {
            var a;
            return b.a.wrap(function(t) {
                for (; ; )
                    switch (t.prev = t.next) {
                    case 0:
                        return a = [{
                            type: "address",
                            value: n
                        }],
                        t.next = 3,
                        window.tronWeb.transactionBuilder.triggerConstantContract(R(e), "balanceOf(address)", {}, a, R(n)).then(function(t) {
                            return F(J(t.constant_result))
                        });
                    case 3:
                        return t.abrupt("return", t.sent);
                    case 4:
                    case "end":
                        return t.stop()
                    }
            }, t, this)
        })),
        function(t, e) {
            return c.apply(this, arguments)
        }
        ), M = (u = w()(b.a.mark(function t(e, n, a) {
            var r;
            return b.a.wrap(function(t) {
                for (; ; )
                    switch (t.prev = t.next) {
                    case 0:
                        return r = [{
                            type: "address",
                            value: n
                        }, {
                            type: "address",
                            value: a
                        }],
                        t.next = 3,
                        window.tronWeb.transactionBuilder.triggerConstantContract(R(e), "allowance(address,address)", {}, r, R(n)).then(function(t) {
                            return F(J(t.constant_result))
                        });
                    case 3:
                        return t.abrupt("return", t.sent);
                    case 4:
                    case "end":
                        return t.stop()
                    }
            }, t, this)
        })),
        function(t, e, n) {
            return u.apply(this, arguments)
        }
        ), D = (p = w()(b.a.mark(function t(e, n, a) {
            var r, s;
            return b.a.wrap(function(t) {
                for (; ; )
                    switch (t.prev = t.next) {
                    case 0:
                        return r = window.tronWeb,
                        s = [{
                            type: "address",
                            value: a
                        }, {
                            type: "uint256",
                            value: B
                        }],
                        t.next = 4,
                        r.transactionBuilder.triggerSmartContract(R(e), "approve(address,uint256)", {}, s, R(n)).then(function(t) {
                            return r.trx.sign(t.transaction)
                        }).then(function(t) {
                            return r.trx.sendRawTransaction(t)
                        });
                    case 4:
                        return t.abrupt("return", t.sent);
                    case 5:
                    case "end":
                        return t.stop()
                    }
            }, t, this)
        })),
        function(t, e, n) {
            return p.apply(this, arguments)
        }
        ), U = (l = w()(b.a.mark(function t(e) {
            var n, a;
            return b.a.wrap(function(t) {
                for (; ; )
                    switch (t.prev = t.next) {
                    case 0:
                        return n = window.tronWeb,
                        a = [{
                            type: "uint256",
                            value: "1000000000000000000"
                        }],
                        t.next = 4,
                        n.transactionBuilder.triggerConstantContract(R(e), "getTokenToTrxInputPrice(uint256)", {}, a, R(e)).then(function(t) {
                            return parseInt("0x" + t.constant_result) / Math.pow(10, 6)
                        });
                    case 4:
                        return t.abrupt("return", t.sent);
                    case 5:
                    case "end":
                        return t.stop()
                    }
            }, t, this)
        })),
        function(t) {
            return l.apply(this, arguments)
        }
        ), Q = n("pnvZ"), B = "105792089237316195423570985008687907853269984665640564039457584007913129639935";
        function Y(t) {
            return window.tronWeb.fromSun(tronWeb.fromSun(tronWeb.fromSun(t)))
        }
        function F(t) {
            return window.tronWeb.fromSun(t)
        }
        function R(t) {
            return window.tronWeb.address.toHex(t)
        }
        function J(t) {
            return window.tronWeb.toDecimal("0x" + t)
        }
        var P = {
            comma: function(t) {
                return (t = parseInt(t)).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")
            },
            fromWei: Y,
            toSun: function(t) {
                return window.tronWeb.toSun(t)
            },
            fromSun: F,
            getContract: g,
            callContract: T,
            callStakeContract: S,
            balanceOf: C,
            approve: D,
            allowanceOf: M,
            getValueAddress: function(t) {
                return window.tronWeb.address.fromHex(t)
            },
            getValueNumber: function(t) {
                return t.toString()
            },
            getValueDecimal18: function(t) {
                return Y(t)
            },
            getValueDecimal6: function(t) {
                return F(t)
            },
            getTokenPrice: U
        }
          , I = {
            computed: v()({}, Object(f.b)(["walletAddress", "tokenTotalSupply", "myTokenBalance"]))
        }
          , W = {
            render: function() {
                var t = this
                  , e = t.$createElement
                  , a = t._self._c || e;
                return a("div", {
                    staticClass: "hello"
                }, [a("img", {
                    staticClass: "main-image",
                    attrs: {
                        src: n("LcYe")
                    }
                }), t._v(" "), a("div", {
                    staticClass: "subtitle"
                }, [t._v("Cure COVID with delicious Dim sum!")]), t._v(" "), t._m(0), t._v(" "), t._m(1), t._v(" "), a("div", {
                    staticClass: "info-bar"
                }, [a("div", [t._v("Your Dimsum")]), t._v(" "), a("div", [a("span", [t._v(t._s(t.myTokenBalance))]), t._v(" "), a("img", {
                    staticStyle: {
                        "vertical-align": "middle",
                        "margin-left": "15px"
                    },
                    attrs: {
                        src: n("0h9p")
                    }
                })])]), t._v(" "), a("div", [t._v("\n        " + t._s(t.walletAddress) + "\n    ")])])
            },
            staticRenderFns: [function() {
                var t = this.$createElement
                  , e = this._self._c || t;
                return e("div", {
                    staticClass: "main-text"
                }, [this._v("\n        Stake your LP token and gain healthy Dim sum!\n        "), e("br"), this._v("Be aware of Scam project. Dim sum pursue healthy blockchain !\n    ")])
            }
            , function() {
                var t = this.$createElement
                  , e = this._self._c || t;
                return e("div", {
                    staticClass: "info-bar"
                }, [e("div", [this._v("Total Supply")]), this._v(" "), e("div", [e("span", [this._v("undefined")]), this._v(" "), e("img", {
                    staticStyle: {
                        "vertical-align": "middle",
                        "margin-left": "15px"
                    },
                    attrs: {
                        src: n("0h9p")
                    }
                })])])
            }
            ]
        };
        var V = n("VU/8")(I, W, !1, function(t) {
            n("qO53")
        }, "data-v-2340e7a5", null).exports
          , N = {
            render: function() {
                this.$createElement;
                this._self._c;
                return this._m(0)
            },
            staticRenderFns: [function() {
                var t = this
                  , e = t.$createElement
                  , n = t._self._c || e;
                return n("div", {
                    staticClass: "hello"
                }, [n("div", {
                    staticClass: "big"
                }, [t._v("About")]), t._v(" "), n("div", {
                    staticClass: "middle"
                }), t._v(" "), n("div", {
                    staticClass: "title"
                }, [t._v("What do you know about Dimsum?")]), t._v("\n  Dim sum is one of the best cuisine in the world, "), n("br"), t._v("\n  famous for its outstanding taste and well-balanced nutrition."), n("br"), t._v("\n  Get healthy and defeat COVID by eating Dimsum."), n("br"), t._v("\n  We remember eating Dim sum with family in the past."), n("br"), t._v("\n  Let’s overcome this challenging period with Dimsum!"), n("br")])
            }
            ]
        };
        var j = n("VU/8")({
            name: "HelloWorld",
            data: function() {
                return {}
            }
        }, N, !1, function(t) {
            n("+ctV")
        }, "data-v-06e77882", null).exports
          , z = {
            computed: v()({}, Object(f.b)(["pools"])),
            mounted: function() {
                var t = this;
                fetch("/apy_cache.json").then(function(e) {
                    e.json().then(function(e) {
                        t.$store.commit("setAPY", e)
                    })
                })
            },
            methods: {
                goTrade: function(t) {
                    window.open(t, "_new")
                },
                goDetail: function(t) {
                    this.$router.push("/farm/" + t)
                }
            }
        }
          , Z = {
            render: function() {
                var t = this
                  , e = t.$createElement
                  , n = t._self._c || e;
                return n("div", {
                    staticClass: "hello",
                    staticStyle: {
                        "padding-bottom": "50px"
                    }
                }, [t._m(0), t._v(" "), n("div", {
                    staticClass: "middle"
                }), t._v(" "), n("div", {
                    staticStyle: {
                        display: "flex",
                        width: "100%",
                        "justify-content": "center",
                        "flex-wrap": "wrap"
                    }
                }, t._l(t.pools, function(e) {
                    return n("div", {
                        key: e.pair,
                        staticClass: "pool"
                    }, [n("div", {
                        staticClass: "thumb",
                        class: e.pair
                    }), t._v(" "), n("div", {
                        staticClass: "container"
                    }, [n("div", {
                        staticClass: "name"
                    }, [t._v(t._s(e.display))]), t._v(" "), n("div", {
                        staticClass: "desc"
                    }, [t._v("\n                      Deposit " + t._s(e.pair) + " LP\n                      "), n("br"), t._v("Earn " + t._s(e.earn) + "\n                  ")]), t._v(" "), n("div", {
                        staticClass: "apy"
                    }, [n("div", [t._v("APY")]), t._v(" "), n("div", [n("span", {
                        staticStyle: {
                            "font-size": "1.4em",
                            "line-height": "40px"
                        }
                    }, [t._v(t._s(e.apy))]), t._v("%\n                      ")])]), t._v(" "), n("div", {
                        staticClass: "buttons"
                    }, [n("button", {
                        staticClass: "farm-btn",
                        staticStyle: {
                            "margin-right": "10px"
                        },
                        on: {
                            click: function(n) {
                                return t.goDetail(e.pair)
                            }
                        }
                    }, [t._v("Farm")]), t._v(" "), n("button", {
                        staticClass: "trade-btn",
                        on: {
                            click: function(n) {
                                return t.goTrade(e.tradeLink)
                            }
                        }
                    }, [t._v("Trade")])])])])
                }), 0)])
            },
            staticRenderFns: [function() {
                var t = this.$createElement
                  , e = this._self._c || t;
                return e("div", {
                    staticClass: "title"
                }, [e("div", {
                    staticStyle: {
                        "font-weight": "900",
                        "font-size": "30px",
                        flex: "1 1 50%"
                    }
                }, [this._v("\n              What Kind of Dim sum\n              "), e("br"), this._v("does your family like?\n          ")]), this._v(" "), e("div", {
                    staticStyle: {
                        "text-align": "left",
                        flex: "1 1 50%"
                    }
                }, [this._v("Professional Dim sum chef is waiting for you!")])])
            }
            ]
        };
        var E = n("VU/8")(z, Z, !1, function(t) {
            n("aQEb")
        }, "data-v-501ae527", null).exports
          , G = {
            props: ["id"],
            filters: {
                fix0: function(t) {
                    return parseInt(t)
                },
                fix2: function(t) {
                    return parseFloat(t).toFixed(2)
                }
            },
            computed: v()({}, Object(f.b)(["pools", "walletAddress", "tokenAddress", "stakeAddress"])),
            data: function() {
                var t = this;
                return {
                    pool: this.$store.state.pools.filter(function(e) {
                        return e.pair == t.$props.id
                    })[0],
                    lpBalance: 0,
                    stakedBalance: -1,
                    rewardAvail: -1,
                    approved: !1,
                    timers: {}
                }
            },
            mounted: function() {
                var t = this;
                setTimeout(function() {
                    t.getCurrentUserStaus()
                }, 1e3)
            },
            methods: {
                test66: function() {
                    var t = this;
                    return w()(b.a.mark(function e() {
                        return b.a.wrap(function(e) {
                            for (; ; )
                                switch (e.prev = e.next) {
                                case 0:
                                    return e.next = 2,
                                    P.getAPY(t.pool.lpToken, t.stakeAddress);
                                case 2:
                                    e.sent;
                                case 3:
                                case "end":
                                    return e.stop()
                                }
                        }, e, t)
                    }))()
                },
                test: function() {
                    var t = this;
                    return w()(b.a.mark(function e() {
                        var n, a;
                        return b.a.wrap(function(e) {
                            for (; ; )
                                switch (e.prev = e.next) {
                                case 0:
                                    return n = void 0,
                                    e.next = 3,
                                    P.getContract(t.stakeAddress);
                                case 3:
                                    return a = e.sent,
                                    console.log("t:", t.pool.lpToken),
                                    e.next = 7,
                                    a.poolLength().call();
                                case 7:
                                    n = e.sent,
                                    console.log("result:", n);
                                case 9:
                                case "end":
                                    return e.stop()
                                }
                        }, e, t)
                    }))()
                },
                getStakeContract: function() {
                    var t = this;
                    return w()(b.a.mark(function e() {
                        return b.a.wrap(function(e) {
                            for (; ; )
                                switch (e.prev = e.next) {
                                case 0:
                                    if (t.stakeContact) {
                                        e.next = 4;
                                        break
                                    }
                                    return e.next = 3,
                                    P.getContract(t.stakeAddress);
                                case 3:
                                    t.stakeContract = e.sent;
                                case 4:
                                    return e.abrupt("return", t.stakeContract);
                                case 5:
                                case "end":
                                    return e.stop()
                                }
                        }, e, t)
                    }))()
                },
                getLPBalance: function() {
                    var t = this;
                    return w()(b.a.mark(function e() {
                        var n;
                        return b.a.wrap(function(e) {
                            for (; ; )
                                switch (e.prev = e.next) {
                                case 0:
                                    return e.next = 2,
                                    P.balanceOf(t.pool.lpToken, t.walletAddress);
                                case 2:
                                    return n = e.sent,
                                    t.lpBalance = n,
                                    e.next = 6,
                                    P.allowanceOf(t.pool.lpToken, t.walletAddress, t.stakeAddress);
                                case 6:
                                    "0" != e.sent && (t.approved = !0);
                                case 8:
                                case "end":
                                    return e.stop()
                                }
                        }, e, t)
                    }))()
                },
                getCurrentUserStaus: function() {
                    var t = this;
                    return w()(b.a.mark(function e() {
                        var n, a, r;
                        return b.a.wrap(function(e) {
                            for (; ; )
                                switch (e.prev = e.next) {
                                case 0:
                                    return t.getLPBalance(),
                                    e.next = 3,
                                    t.getStakeContract();
                                case 3:
                                    return n = e.sent,
                                    e.next = 6,
                                    n.userInfo(t.pool.idx, t.walletAddress).call();
                                case 6:
                                    return a = e.sent,
                                    t.stakedBalance = P.getValueDecimal6(a.amount),
                                    e.next = 10,
                                    n.pendingDimsum(t.pool.idx, t.walletAddress).call();
                                case 10:
                                    r = e.sent,
                                    t.rewardAvail = P.getValueDecimal18(r);
                                case 12:
                                case "end":
                                    return e.stop()
                                }
                        }, e, t)
                    }))()
                },
                approveLP: function() {
                    var t = this;
                    return w()(b.a.mark(function e() {
                        var n, a;
                        return b.a.wrap(function(e) {
                            for (; ; )
                                switch (e.prev = e.next) {
                                case 0:
                                    if (!t.approved) {
                                        e.next = 2;
                                        break
                                    }
                                    return e.abrupt("return", alert("Already approved wallet address"));
                                case 2:
                                    return e.next = 4,
                                    P.approve(t.pool.lpToken, t.walletAddress, t.stakeAddress).catch(function(t) {
                                        return alert(t),
                                        !1
                                    });
                                case 4:
                                    n = e.sent,
                                    a = n.txid,
                                    console.log("[TRANSACTION]", a),
                                    t.timers[a] = setInterval(function() {
                                        tronWeb.trx.getTransaction(a).then(function(e) {
                                            void 0 != e.ret && ("SUCCESS" == e.ret[0].contractRet ? t.$toast.open({
                                                type: "success",
                                                message: "Approve transaction confirmed.",
                                                position: "top-right"
                                            }) : t.$toast.open({
                                                type: "error",
                                                message: "Approve transaction failed.",
                                                position: "top-right"
                                            }),
                                            clearInterval(t.timers[a]))
                                        })
                                    }, 2e3);
                                case 8:
                                case "end":
                                    return e.stop()
                                }
                        }, e, t)
                    }))()
                },
                deposit: function() {
                    var t = this;
                    return w()(b.a.mark(function e() {
                        var n, a, r;
                        return b.a.wrap(function(e) {
                            for (; ; )
                                switch (e.prev = e.next) {
                                case 0:
                                    if (0 != t.approved) {
                                        e.next = 2;
                                        break
                                    }
                                    return e.abrupt("return", alert("click approve button first."));
                                case 2:
                                    if (n = prompt("Deposit LP Token Amount?")) {
                                        e.next = 5;
                                        break
                                    }
                                    return e.abrupt("return");
                                case 5:
                                    return e.next = 7,
                                    t.getStakeContract();
                                case 7:
                                    return a = e.sent,
                                    e.next = 10,
                                    a.deposit(t.pool.idx, P.toSun(n)).send({
                                        from: t.walletAddress
                                    });
                                case 10:
                                    r = e.sent,
                                    console.log("[TRANSACTION]", r),
                                    t.timers[r] = setInterval(function() {
                                        tronWeb.trx.getTransaction(r).then(function(e) {
                                            void 0 != e.ret && ("SUCCESS" == e.ret[0].contractRet ? t.$toast.open({
                                                type: "success",
                                                message: "Stake transaction confirmed.",
                                                position: "top-right"
                                            }) : t.$toast.open({
                                                type: "error",
                                                message: "Stake transaction failed.",
                                                position: "top-right"
                                            }),
                                            clearInterval(t.timers[r]))
                                        })
                                    }, 2e3);
                                case 13:
                                case "end":
                                    return e.stop()
                                }
                        }, e, t)
                    }))()
                },
                withdraw: function() {
                    var t = this;
                    return w()(b.a.mark(function e() {
                        var n, a, r;
                        return b.a.wrap(function(e) {
                            for (; ; )
                                switch (e.prev = e.next) {
                                case 0:
                                    if (n = prompt("Withdraw LP Token Amount?")) {
                                        e.next = 3;
                                        break
                                    }
                                    return e.abrupt("return");
                                case 3:
                                    return e.next = 5,
                                    t.getStakeContract();
                                case 5:
                                    return a = e.sent,
                                    e.next = 8,
                                    a.withdraw(t.pool.idx, P.toSun(n)).send({
                                        from: t.walletAddress
                                    });
                                case 8:
                                    r = e.sent,
                                    console.log("[TRANSACTION]", r),
                                    t.timers[r] = setInterval(function() {
                                        tronWeb.trx.getTransaction(r).then(function(e) {
                                            void 0 != e.ret && ("SUCCESS" == e.ret[0].contractRet ? t.$toast.open({
                                                type: "success",
                                                message: "Unstake transaction confirmed.",
                                                position: "top-right"
                                            }) : t.$toast.open({
                                                type: "error",
                                                message: "Unstake transaction failed.",
                                                position: "top-right"
                                            }),
                                            clearInterval(t.timers[r]))
                                        })
                                    }, 2e3);
                                case 11:
                                case "end":
                                    return e.stop()
                                }
                        }, e, t)
                    }))()
                }
            }
        }
          , q = {
            render: function() {
                var t = this
                  , e = t.$createElement
                  , n = t._self._c || e;
                return n("div", {
                    staticStyle: {
                        display: "flex",
                        "justify-content": "center",
                        "padding-top": "90px"
                    }
                }, [n("div", {
                    staticClass: "box"
                }, [n("div", {
                    staticClass: "title"
                }, [n("div", {
                    staticClass: "thumb",
                    class: t.pool.pair
                }), t._v(" "), n("div", {
                    staticStyle: {
                        display: "flex",
                        "flex-direction": "column"
                    }
                }, [n("span", {
                    staticStyle: {
                        "font-size": "23px",
                        "font-weight": "bold",
                        "line-height": "2.0"
                    }
                }, [t._v(t._s(t.pool.display))]), t._v(" "), n("span", {
                    staticStyle: {
                        "font-size": "16px"
                    }
                }, [t._v("\n                      Deposit " + t._s(t.pool.pair) + " LP, Earn " + t._s(t.pool.earn) + "\n                  ")])])]), t._v(" "), n("div", {
                    staticStyle: {
                        "margin-bottom": "40px"
                    }
                }, [n("div", {
                    staticClass: "apy"
                }, [n("div", [t._v("Your Balance")]), t._v(" "), n("div", [n("span", {
                    staticStyle: {
                        "line-height": "40px"
                    }
                }, [n("b", [t._v(t._s(t._f("fix2")(t.lpBalance)))]), t._v(" " + t._s(t.pool.pair) + " LP")])])]), t._v(" "), n("div", {
                    staticClass: "apy"
                }, [n("div", [t._v("Currently Staked")]), t._v(" "), n("div", [n("span", {
                    staticStyle: {
                        "line-height": "40px"
                    }
                }, [n("b", [t._v(t._s(t.stakedBalance))]), t._v(" " + t._s(t.pool.pair) + " LP")])])]), t._v(" "), n("div", {
                    staticClass: "apy"
                }, [n("div", [t._v("Rewards Available")]), t._v(" "), n("div", [n("span", {
                    staticStyle: {
                        "line-height": "40px"
                    }
                }, [n("b", [t._v(t._s(t._f("fix0")(t.rewardAvail)))]), t._v(" " + t._s(t.pool.earn))])])])]), t._v(" "), n("div", {
                    staticClass: "buttons"
                }, [n("button", {
                    staticClass: "approve-btn",
                    staticStyle: {
                        "margin-right": "12px"
                    },
                    on: {
                        click: t.approveLP
                    }
                }, [t._v("Approve")]), t._v(" "), n("button", {
                    staticClass: "stake-btn",
                    staticStyle: {
                        "margin-right": "12px"
                    },
                    on: {
                        click: t.deposit
                    }
                }, [t._v("Stake")]), t._v(" "), n("button", {
                    staticClass: "unstake-btn",
                    on: {
                        click: t.withdraw
                    }
                }, [t._v("Unstake")])]), t._v(" "), n("div", {
                    staticStyle: {
                        "font-size": "12px",
                        "text-align": "center",
                        "padding-top": "10px"
                    }
                }, [t._v("\n              You just press unstake or additionally press stake then dimsum harvest automatically work\n          ")])])])
            },
            staticRenderFns: []
        };
        var L = n("VU/8")(G, q, !1, function(t) {
            n("39pe")
        }, "data-v-46e2c4f4", null).exports
          , X = {
            render: function() {
                this.$createElement;
                this._self._c;
                return this._m(0)
            },
            staticRenderFns: [function() {
                var t = this.$createElement
                  , e = this._self._c || t;
                return e("div", {
                    staticStyle: {
                        "padding-top": "150px"
                    }
                }, [e("div", {
                    staticClass: "big"
                }, [this._v("Coming Soon.")]), this._v(" "), e("div", {
                    staticClass: "middle"
                })])
            }
            ]
        };
        var O = n("VU/8")({
            data: function() {
                return {}
            }
        }, X, !1, function(t) {
            n("361L")
        }, "data-v-174c4cf6", null).exports;
        a.default.use(A.a);
        var K = new A.a({
            routes: [{
                path: "/",
                name: "Main",
                component: V
            }, {
                path: "/about",
                name: "About",
                component: j
            }, {
                path: "/farm",
                name: "Farm",
                component: E
            }, {
                path: "/farm/:id",
                component: L,
                props: !0
            }, {
                path: "/exchange",
                component: O
            }]
        })
          , H = function() {
            var t = w()(b.a.mark(function t() {
                return b.a.wrap(function(t) {
                    for (; ; )
                        switch (t.prev = t.next) {
                        case 0:
                            if (0 != (window.tronWeb && window.tronWeb.defaultAddress.base58)) {
                                t.next = 2;
                                break
                            }
                            return t.abrupt("return");
                        case 2:
                            if (at != window.tronWeb.defaultAddress.base58) {
                                t.next = 4;
                                break
                            }
                            return t.abrupt("return");
                        case 4:
                            at = window.tronWeb.defaultAddress.base58,
                            et.commit("setWalletAdress", at),
                            _();
                        case 7:
                        case "end":
                            return t.stop()
                        }
                }, t, this)
            }));
            return function() {
                return t.apply(this, arguments)
            }
        }()
          , _ = function() {
            var t = w()(b.a.mark(function t() {
                var e, n;
                return b.a.wrap(function(t) {
                    for (; ; )
                        switch (t.prev = t.next) {
                        case 0:
                            return t.next = 2,
                            P.callContract(tt, "totalSupply", []);
                        case 2:
                            return e = t.sent,
                            et.commit("setTokenTotalSupply", P.getValueDecimal18(e)),
                            t.next = 6,
                            P.callContract(tt, "balanceOf", [at]);
                        case 6:
                            n = t.sent,
                            et.commit("setMyTokenBalance", P.getValueDecimal18(n));
                        case 8:
                        case "end":
                            return t.stop()
                        }
                }, t, this)
            }));
            return function() {
                return t.apply(this, arguments)
            }
        }()
          , $ = function() {
            var t = w()(b.a.mark(function t() {
                return b.a.wrap(function(t) {
                    for (; ; )
                        switch (t.prev = t.next) {
                        case 0:
                            fetch("https://api.coingecko.com/api/v3/simple/price?ids=tron&vs_currencies=USD").then(function(t) {
                                t.json().then(function(t) {
                                    et.commit("setTronPrice", t.tron.usd)
                                })
                            });
                        case 1:
                        case "end":
                            return t.stop()
                        }
                }, t, this)
            }));
            return function() {
                return t.apply(this, arguments)
            }
        }();
        a.default.use(f.a);
        var tt = "TJHbXDUtaoPHwv9S6MDDZ3si4fMVnqfJ4B"
          , et = new f.a.Store({
            state: {
                pools: [],
                walletAddress: "-",
                tokenAddress: tt,
                stakeAddress: "TY7z5brcT4VFvfrRTMd6jrFqW4KQ5xvJKf",
                tokenTotalSupply: "-",
                myTokenBalance: "-",
                tronPrice: .029
            },
            getters: {
                walletAddress: function(t) {
                    return t.walletAddress
                },
                tokenAddress: function(t) {
                    return t.tokenAddress
                },
                stakeAddress: function(t) {
                    return t.stakeAddress
                },
                tokenTotalSupply: function(t) {
                    return t.tokenTotalSupply
                },
                myTokenBalance: function(t) {
                    return t.myTokenBalance
                },
                pools: function(t) {
                    return t.pools
                },
                tronPrice: function(t) {
                    return t.tronWeb
                }
            },
            mutations: {
                setWalletAdress: function(t, e) {
                    t.walletAddress = e
                },
                setTokenTotalSupply: function(t, e) {
                    t.tokenTotalSupply = P.comma(e)
                },
                setMyTokenBalance: function(t, e) {
                    t.myTokenBalance = P.comma(e)
                },
                addPool: function(t, e) {
                    t.pools.push(e)
                },
                setTronPrice: function(t, e) {
                    t.tronPrice = e
                },
                setAPY: function(t, e) {
                    t.pools[0].apy = e["DIMSUM-TRX"],
                    t.pools[1].apy = e["USDT-TRX"],
                    t.pools[2].apy = e["USDJ-TRX"]
                }
            }
        });
        [{
            pair: "DIMSUM-TRX",
            display: "DIMSUM/TRX Pair Staking",
            lpToken: "TGDhhAWDrWPAt8dN6R3bSjtfrxtJaFJNfw",
            earn: "DIMSUM",
            apy: "112,128",
            idx: 0,
            tradeLink: "https://justswap.io/#/scan/detail/trx/TJHbXDUtaoPHwv9S6MDDZ3si4fMVnqfJ4B"
        }, {
            pair: "USDT-TRX",
            display: "USDT/TRX Pair Staking",
            lpToken: "TQn9Y2khEsLJW1ChVWFMSMeRDow5KcbLSE",
            earn: "DIMSUM",
            apy: "14,016",
            idx: 1,
            tradeLink: "https://justswap.io/?lang=en-US#/scan/detail/trx/TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t"
        }, {
            pair: "USDJ-TRX",
            display: "USDJ/TRX Pair Staking",
            lpToken: "TQcia2H2TU3WrFk9sKtdK9qCfkW8XirfPQ",
            earn: "DIMSUM",
            apy: "12,181",
            idx: 2,
            tradeLink: "https://justswap.io/?lang=en-US#/scan/detail/trx/TMwFHYXLJaRUPeW6421aqXL4ZEzPRFGkGT"
        }].forEach(function(t) {
            et.commit("addPool", t)
        });
        var nt = et
          , at = null;
        setInterval(function() {
            H()
        }, 1e3),
        $();
        var rt = n("+4za")
          , st = n.n(rt);
        n("9T2W");
        a.default.use(st.a),
        a.default.config.productionTip = !1,
        new a.default({
            el: "#app",
            router: K,
            store: nt,
            components: {
                App: d
            },
            template: "<App/>"
        })
    },
    Tk6V: function(t, e) {},
    Zum8: function(t, e) {
        t.exports = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACkAAAApCAYAAACoYAD2AAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAIGSURBVHgB7ZcxU8JAEIU3joVSiiVjCaW22EKJpda02NI52trhb8DWFloohdJSbLE0llLie+QyZpgkexdJwcx9MzvHkL3cy+b2diPi8Xg8nr0nkBJZr9cVDC1YE3YM+4YtYa9BEHxs+da3/7MSiYlVDNWsycrcSwzXsEqGywQ2hp3D6BtinaEUENk1NxngBp9iL7CG4UHsCc0aYdrFA2UyI8lI9LHwhdhz6+BLYYxqG2u00hxsRBIK7eEmV4r/Zm8l5tlA3xvYESI5kQIit19xByIeYc2cOTVxZwKBz1kXNZHvKf/xybuxWJNcSSriBhPmJc/hUPKZw9qS/vo2YvkDQhnxL4mOl1Nx40dz0LKbUTmB9cU9QrYsEMmnPActkvewlUR7sy7lsNIcNJELiQ7aMlELhZY4MymfpeaQK9KUw4mUR2hTcrVIijkeyhJq1RNYdUHmLGR2szLsMoHusup1Ei1xYlhh1JLoyMxGIFFft2EqUSOwK3ivka2zlUg8MavCQHYndGQbxc364oDZmx1YQ9w6nSQUOHaZUPjzAYJ7GFx6TOIskDiLNM0vs9w1koUEkiBDCI+bY/nrzNlknEn0KeHcisGGRb6TYlKPICYKhFIkj56itZvJxiIwNYlXGPV1m2ThAU7BDdGFsRa/web/FRdTZE9ScLwdYthuLV2OFY/H49kTfgGW+JxVTp7AQwAAAABJRU5ErkJggg=="
    },
    aQEb: function(t, e) {},
    bmFd: function(t, e) {
        t.exports = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACoAAAApCAYAAABDV7v1AAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAKaSURBVHgB7Vgtc9tAEF13SiJYU7dQtC5sYUMT2Ib6P5QWdspSXJjAliZUgTY1dahDHehC971m1VGU3fuQbBS9mR0lq9u757293T2JDBgw4HlhJD2x2+0KPN5C3kBeQQp9tYWsIXeQ29FotJUe6EwUBEs8TiFloskCMgfhW+mAbKIgOMHjTNIJtkHCVyC8yTHKIgqSx3h8lv4gyd8gu0w1SCYKktzmE9kvLkF2njIwieiBSNZIIvsiNgAkp/KYJE/vL0iSJ1p2FeSqpf+ENcYx45ehl5p62jG5hgdu9P21PPwI/pgV5F4JEbTlwSMJHqCbOkVprBeNcTPIuXQlChzrQk3c13/oyb1UyQFzazNrlEx3odQV2/r3chgcGbrTkIFLVGPTip1oPCWgMHRlKFZDHp06+gvpjwvJWzNIdGLoFrkVxYLGojXPa88mRNQyWsv+sDJ0E2+wSVTTkoVeHVALlke9dV2PHsnhUeQM9oj+cfRuDHWANZe7YyZRrSDbxMm7worHPKKKO0NXasPcC5iDhaRIXPMfQkRXjv4scNii0KTuVSG3Pw0RXTh6btnXlI6nDd2NL2JXt02o1rtNCRM7JqZhvdXsbhijJ7rQd7ynB5beIup52vPS907C15fgXSrWlDR7x5k8hEOzU5qq/kNgDraJsTvWRp72qY8QJKpeqvTfsS7Y1NWYO/ZbSWuwq1hpjvWjBJvjupOiVyZKlOWUW7uJXIFjvcES9lVkTPwqol45byw4g7DJZYNS5dwkDTAdJTXdObdQerR5YvkDWMFYbr95W6c5c2a8Yrz/TP2CEvVoDSVCz9YxVyjpLjmVO/Ej5zNPSoz+R31H0rTFpJ2bS+nF666fdTqDCRzyMTJmzDH7KL0DBgzYA/4CD2PVaghmxEQAAAAASUVORK5CYII="
    },
    lUua: function(t, e) {
        t.exports = "data:image/png;base64,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"
    },
    pnvZ: function(t, e) {
        t.exports = [{
            inputs: [{
                name: "_dimsum",
                type: "address"
            }, {
                name: "_devaddr",
                type: "address"
            }, {
                name: "_dimsumPerBlock",
                type: "uint256"
            }, {
                name: "_startBlock",
                type: "uint256"
            }, {
                name: "_bonusEndBlock",
                type: "uint256"
            }],
            stateMutability: "Nonpayable",
            type: "Constructor"
        }, {
            inputs: [{
                indexed: !0,
                name: "user",
                type: "address"
            }, {
                indexed: !0,
                name: "pid",
                type: "uint256"
            }, {
                name: "amount",
                type: "uint256"
            }],
            name: "Deposit",
            type: "Event"
        }, {
            inputs: [{
                indexed: !0,
                name: "user",
                type: "address"
            }, {
                indexed: !0,
                name: "pid",
                type: "uint256"
            }, {
                name: "amount",
                type: "uint256"
            }],
            name: "EmergencyWithdraw",
            type: "Event"
        }, {
            inputs: [{
                indexed: !0,
                name: "previousOwner",
                type: "address"
            }, {
                indexed: !0,
                name: "newOwner",
                type: "address"
            }],
            name: "OwnershipTransferred",
            type: "Event"
        }, {
            inputs: [{
                indexed: !0,
                name: "user",
                type: "address"
            }, {
                indexed: !0,
                name: "pid",
                type: "uint256"
            }, {
                name: "amount",
                type: "uint256"
            }],
            name: "Withdraw",
            type: "Event"
        }, {
            outputs: [{
                type: "uint256"
            }],
            constant: !0,
            name: "BONUS_MULTIPLIER",
            stateMutability: "View",
            type: "Function"
        }, {
            inputs: [{
                name: "_allocPoint",
                type: "uint256"
            }, {
                name: "_lpToken",
                type: "address"
            }, {
                name: "_withUpdate",
                type: "bool"
            }],
            name: "add",
            stateMutability: "Nonpayable",
            type: "Function"
        }, {
            outputs: [{
                type: "uint256"
            }],
            constant: !0,
            name: "bonusEndBlock",
            stateMutability: "View",
            type: "Function"
        }, {
            inputs: [{
                name: "_pid",
                type: "uint256"
            }, {
                name: "_amount",
                type: "uint256"
            }],
            name: "deposit",
            stateMutability: "Nonpayable",
            type: "Function"
        }, {
            inputs: [{
                name: "_devaddr",
                type: "address"
            }],
            name: "dev",
            stateMutability: "Nonpayable",
            type: "Function"
        }, {
            outputs: [{
                type: "address"
            }],
            constant: !0,
            name: "devaddr",
            stateMutability: "View",
            type: "Function"
        }, {
            outputs: [{
                type: "address"
            }],
            constant: !0,
            name: "dimsum",
            stateMutability: "View",
            type: "Function"
        }, {
            outputs: [{
                type: "uint256"
            }],
            constant: !0,
            name: "dimsumPerBlock",
            stateMutability: "View",
            type: "Function"
        }, {
            inputs: [{
                name: "_pid",
                type: "uint256"
            }],
            name: "emergencyWithdraw",
            stateMutability: "Nonpayable",
            type: "Function"
        }, {
            outputs: [{
                type: "uint256"
            }],
            constant: !0,
            inputs: [{
                name: "_from",
                type: "uint256"
            }, {
                name: "_to",
                type: "uint256"
            }],
            name: "getMultiplier",
            stateMutability: "View",
            type: "Function"
        }, {
            outputs: [{
                type: "bool"
            }],
            constant: !0,
            name: "isOwner",
            stateMutability: "View",
            type: "Function"
        }, {
            name: "massUpdatePools",
            stateMutability: "Nonpayable",
            type: "Function"
        }, {
            inputs: [{
                name: "amount",
                type: "uint256"
            }],
            name: "mint",
            stateMutability: "Nonpayable",
            type: "Function"
        }, {
            outputs: [{
                type: "address"
            }],
            constant: !0,
            name: "owner",
            stateMutability: "View",
            type: "Function"
        }, {
            outputs: [{
                type: "uint256"
            }],
            constant: !0,
            inputs: [{
                name: "_pid",
                type: "uint256"
            }, {
                name: "_user",
                type: "address"
            }],
            name: "pendingDimsum",
            stateMutability: "View",
            type: "Function"
        }, {
            outputs: [{
                name: "lpToken",
                type: "address"
            }, {
                name: "allocPoint",
                type: "uint256"
            }, {
                name: "lastRewardBlock",
                type: "uint256"
            }, {
                name: "accDimsumPerShare",
                type: "uint256"
            }],
            constant: !0,
            inputs: [{
                type: "uint256"
            }],
            name: "poolInfo",
            stateMutability: "View",
            type: "Function"
        }, {
            outputs: [{
                type: "uint256"
            }],
            constant: !0,
            name: "poolLength",
            stateMutability: "View",
            type: "Function"
        }, {
            name: "renounceOwnership",
            stateMutability: "Nonpayable",
            type: "Function"
        }, {
            inputs: [{
                name: "_pid",
                type: "uint256"
            }, {
                name: "_allocPoint",
                type: "uint256"
            }, {
                name: "_withUpdate",
                type: "bool"
            }],
            name: "set",
            stateMutability: "Nonpayable",
            type: "Function"
        }, {
            outputs: [{
                type: "uint256"
            }],
            constant: !0,
            name: "startBlock",
            stateMutability: "View",
            type: "Function"
        }, {
            outputs: [{
                type: "uint256"
            }],
            constant: !0,
            name: "totalAllocPoint",
            stateMutability: "View",
            type: "Function"
        }, {
            inputs: [{
                name: "newOwner",
                type: "address"
            }],
            name: "transferOwnership",
            stateMutability: "Nonpayable",
            type: "Function"
        }, {
            inputs: [{
                name: "_pid",
                type: "uint256"
            }],
            name: "updatePool",
            stateMutability: "Nonpayable",
            type: "Function"
        }, {
            outputs: [{
                name: "amount",
                type: "uint256"
            }, {
                name: "rewardDebt",
                type: "uint256"
            }],
            constant: !0,
            inputs: [{
                type: "uint256"
            }, {
                type: "address"
            }],
            name: "userInfo",
            stateMutability: "View",
            type: "Function"
        }, {
            inputs: [{
                name: "_pid",
                type: "uint256"
            }, {
                name: "_amount",
                type: "uint256"
            }],
            name: "withdraw",
            stateMutability: "Nonpayable",
            type: "Function"
        }]
    },
    qO53: function(t, e) {}
}, ["NHnr"]);
//# sourceMappingURL=app.b5c27fb6e258e1e8455b.js.map

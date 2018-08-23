(ns cryptoarbitrage.cryptoapis-test
  (:require [clojure.test :refer :all]
            [cryptoarbitrage.cryptoapis :as apis]))

(deftest cex-io-test
  (testing "I check CEX.IO API end point for getting btc usd price pair "
    (let [price (apis/cex-io-price "btc" "usd")]
      (println (str "CEX.IO PRICE: " price))
      (is (not (= 0 price)))
      )
    )
  )

(deftest bit-stamp-test
  (testing "I check BitStamp API end point for getting btc usd price pair "
    (let [price (apis/bit-stamp-price "btc" "usd")]
      (println (str "BIT STAMP PRICE: " price))
      (is (not (= 0 price)))
      )
    )
  )

(deftest bit-fenix-test
  (testing "I check bit fenix API end point for getting btc usd price pair "
    (let [price (apis/bit-fenix-price "btc" "usd")]
      (println (str "BIT FENIX PRICE: " price))
      (is (not (= 0 price)))
      )
    )
  )
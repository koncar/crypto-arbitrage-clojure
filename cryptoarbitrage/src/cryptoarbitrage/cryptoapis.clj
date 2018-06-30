(ns cryptoarbitrage.cryptoapis
  (:require [clj-http.client :as client])
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
           )
  )

(defn cex-io-price
  [a b]
  (:price (json/read-json (str/replace (:body (client/get (str/join ["https://cex.io / api / last_price /" a "/" b]))) "lprice" "price") true)))


(defn bit-stamp-price
  [a b]
  (:last (json/read-json (:body (client/get (str/join ["https://www.bitstamp.net/api/v2/ticker_hour/" (str/lower-case a) (str/lower-case b)]))) true)))

(defn compare-cex-bitstamp
  [a b]
  (Math/abs (- (Double/parseDouble (cex-io-price a b)) (Double/parseDouble (bit-stamp-price a b))))
  )
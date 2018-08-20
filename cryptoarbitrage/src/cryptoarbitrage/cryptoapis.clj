(ns cryptoarbitrage.cryptoapis
  (:require [clj-http.client :as client])
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [cryptoarbitrage.mongodb :as mongo]

           )
  )

(defn cex-io-price
  [a b]
  (Double/parseDouble (:price (json/read-json (str/replace (:body (client/get (str/join ["https://cex.io/api/last_price/" (str/upper-case a) "/" (str/upper-case b)]))) "lprice" "price") true))))


(defn bit-stamp-price
  [a b]
  (Double/parseDouble (:last (json/read-json (:body (client/get (str/join ["https://www.bitstamp.net/api/v2/ticker_hour/" (str/lower-case a) (str/lower-case b)]))) true))))

(defn bit-fenix-price
  [a b]
  (get (get (json/read-json (:body (client/get (str/join ["https://api.bitfinex.com/v2/tickers?symbols=t" (str/upper-case a) (str/upper-case b)])))) 0) 7)
  )

(defn get-price
  [a b exchange]
  (cond
    (= exchange "CEX") (cex-io-price a b)
    (= exchange "BST") (bit-stamp-price a b)
    (= exchange "BFX") (bit-fenix-price a b)
    )
  )

(defn cex_io-populate-all-pairs
  [& should_remove]
  (let [pairs (:pairs (:data (json/read-json (:body (client/get "https://cex.io/api/currency_limits")))))]
    (do
      (println pairs)
      (when (or (nil? should_remove) should_remove) (mongo/remove "pairs" {:exchange "CEX"}))
      (loop [x 0]
       (when (< x (count pairs))
         (let [currency_1 (doto (:symbol1 (get pairs x)))
               currency_2 (doto (:symbol2 (get pairs x)))]
           (mongo/insert "pairs" {:_id          (str x "-cex")
                                  :exchange     (mongo/find "exchanges" {:_id "CEX"})
                                  :currency_1   currency_1
                                  :currency_2   currency_2
                                  :name         (str currency_1 currency_2)
                                  :display_name (str currency_1 "/" currency_2)})
           (when (not (mongo/find "unique_pairs" {:_id (str currency_1 "/" currency_2)}))
             (mongo/insert "unique_pairs" {:_id (str currency_1 "/" currency_2)
                                           :id (str currency_1 "/" currency_2)
                                           :current_1 currency_1
                                           :current_2 currency_2
                                           :name (str currency_1 currency_2)
                                           :display_name (str currency_1 "/" currency_2)
                                           :label (str currency_1 "/" currency_2)
                                           })
             )
           )
         (recur (+ x 1))
         )
       )
      )
    )
  )
(defn bit_stamp-populate-all-pairs
  [& should_remove]
  (let [pairs (json/read-json (:body (client/get "https://www.bitstamp.net/api/v2/trading-pairs-info/")))]
    (do
      (println pairs)
      (when (or (nil? should_remove) should_remove) (mongo/remove "pairs" {:exchange "BST"}))
      (loop [x 0]
        (when (< x (count pairs))
          (let [pair (doto(get pairs x))
                pair_name (doto (str/split (:name pair) #"/"))
                currency_1 (doto (get pair_name 0))
                currency_2 (doto (get pair_name 1))]
            (mongo/insert "pairs" {:_id          (str x "-bst")
                                   :exchange     (mongo/find "exchanges" {:_id "BST"})
                                   :currency_1   currency_1
                                   :currency_2   currency_2
                                   :name         (str currency_1 currency_2)
                                   :display_name (str currency_1 "/" currency_2)})
            (when (not (mongo/find "unique_pairs" {:_id (str currency_1 "/" currency_2)}))
              (mongo/insert "unique_pairs" {:_id (str currency_1 "/" currency_2)
                                            :id (str currency_1 "/" currency_2)
                                            :current_1 currency_1
                                            :current_2 currency_2
                                            :name (str currency_1 currency_2)
                                            :display_name (str currency_1 "/" currency_2)
                                            :label (str currency_1 "/" currency_2)
                                            })
              )
            )
          (recur (+ x 1))
          )
        )
      )
    )
  )


(defn bit_finex-populate-all-pairs
  [& should_remove]
  (let [pairs (json/read-json (:body (client/get "https://api.bitfinex.com/v1/symbols")))]
    (do
      (println pairs)
      (when (or (nil? should_remove) should_remove) (mongo/remove "pairs" {:exchange "BFX"}))
      (loop [x 0]
        (when (< x (count pairs))
          (let [pair (doto(get pairs x))
                currency_1 (doto (str/upper-case (subs pair 0 3)))
                currency_2 (doto (str/upper-case (subs pair 3 6)))]
            (mongo/insert "pairs" {:_id          (str x "-bfx")
                                   :exchange     (mongo/find "exchanges" {:_id "BFX"})
                                   :currency_1   currency_1
                                   :currency_2   currency_2
                                   :name         (str currency_1 currency_2)
                                   :display_name (str currency_1 "/" currency_2)})
            (when (not (mongo/find "unique_pairs" {:_id (str currency_1 "/" currency_2)}))
              (mongo/insert "unique_pairs" {:_id (str currency_1 "/" currency_2)
                                            :id (str currency_1 "/" currency_2)
                                            :current_1 currency_1
                                            :current_2 currency_2
                                            :name (str currency_1 currency_2)
                                            :display_name (str currency_1 "/" currency_2)
                                            :label (str currency_1 "/" currency_2)
                                            })
              )
            )
          (recur (+ x 1))
          )
        )
      )
    )
  )


(defn ascending_price
  [a b]

  (let [exchanges (doto (mongo/find-all "exchanges"))]
    (loop [i 0
           prices ()] ;<= only thing thats different
      (if (= i (count exchanges))
        (sort-by :price prices)
        (recur (inc i) (conj prices {:price (get-price a b (:_id (nth exchanges i))) :exchange (:_id (nth exchanges i))} )))
      )
    )
  )

(defn descending_price
  [a b]
  (let [exchanges (doto (mongo/find-all "exchanges"))]
    (loop [i 0
           prices ()] ;<= only thing thats different
      (if (= i (count exchanges))
        (reverse(sort-by :price prices))
        (recur (inc i) (conj prices {:price (get-price a b (:_id (nth exchanges i))) :exchange (:_id (nth exchanges i))} )))
      )
    )
  )
(defn matrica
  []
  {(ascending_price "btc" "usd") (descending_price "btc" "usd")})

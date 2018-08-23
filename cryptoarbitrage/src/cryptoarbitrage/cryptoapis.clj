(ns cryptoarbitrage.cryptoapis
  (:require [clj-http.client :as client])
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [cryptoarbitrage.mongodb :as mongo]

           )
  )


(defn cex-io-price
  [a b]
  (try (Double/parseDouble (:price (json/read-json (str/replace (:body (client/get (str/join ["https://cex.io/api/last_price/" (str/upper-case a) "/" (str/upper-case b)]))) "lprice" "price") true)))
       (catch Exception e -1)
       )
  )


(defn bit-stamp-price
  [a b]
  (try
    (Double/parseDouble (:last (json/read-json (:body (client/get (str/join ["https://www.bitstamp.net/api/v2/ticker_hour/" (str/lower-case a) (str/lower-case b)]))) true)))
     (catch Exception e -1)
      )
  )


(defn bit-fenix-price
  [a b]
  (try
    (let [price (get (get (json/read-json (:body (client/get (str/join ["https://api.bitfinex.com/v2/tickers?symbols=t" (str/upper-case a) (str/upper-case b)])))) 0) 7)]
     (if (nil? price)
       -1
       price)
     )
    (catch Exception e -1)
    )
  )


(defn get-price
  [a b exchange]
  (println "a " a)
  (println "b " b)
  (println "ex " exchange)

  (cond
    (= exchange "CEX") (cex-io-price a b)
    (= exchange "BST") (bit-stamp-price a b)
    (= exchange "BFX") (bit-fenix-price a b)
    )
  )


(defn get-price-for-exchanges [currency_1 currency_2 exchanges]
  (println currency_1 currency_2 exchanges)
  (loop [i 0
         list (list)]
    (if (= i (count exchanges))
      list
      (recur (inc i) (conj list (get-price currency_1 currency_2 (:_id (nth exchanges i)))))
      )
    )
  )


(defn get-data-for-price-on-exchanges [pairs exchanges]
  (map #(conj % {:data (get-price-for-exchanges (:currency_1 %) (:currency_2 %) exchanges)}) (map #(select-keys % [:name :currency_1 :currency_2]) pairs))
  )

(defn get-price-on-exchanges []
  (let [pairs (mongo/find-all "duplicate_pairs")
        exchanges (mongo/find-all "exchanges")
        simple_exchanges (map :name (map #(select-keys % [:name]) exchanges))
        data (get-data-for-price-on-exchanges pairs exchanges)
        ]
     {:exchanges simple_exchanges :data data}
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
                                  :name         (str currency_1 "/" currency_2)
                                  :display_name (str currency_1 "/" currency_2)})
           (when (not (mongo/find "unique_pairs" {:_id (str currency_1 "/" currency_2)}))
             (mongo/insert "unique_pairs" {:_id (str currency_1 "/" currency_2)
                                           :id (str currency_1 "/" currency_2)
                                           :currency_1 currency_1
                                           :currency_2 currency_2
                                           :name (str currency_1 "/" currency_2)
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
                                   :name         (str currency_1 "/" currency_2)
                                   :display_name (str currency_1 "/" currency_2)})
            (when (not (mongo/find "unique_pairs" {:_id (str currency_1 "/" currency_2)}))
              (mongo/insert "unique_pairs" {:_id (str currency_1 "/" currency_2)
                                            :id (str currency_1 "/" currency_2)
                                            :currency_1 currency_1
                                            :currency_2 currency_2
                                            :name (str currency_1 "/" currency_2)
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
                                   :name         (str currency_1 "/" currency_2)
                                   :display_name (str currency_1 "/" currency_2)})
            (when (not (mongo/find "unique_pairs" {:_id (str currency_1 "/" currency_2)}))
              (mongo/insert "unique_pairs" {:_id (str currency_1 "/" currency_2)
                                            :id (str currency_1 "/" currency_2)
                                            :currency_1 currency_1
                                            :currency_2 currency_2
                                            :name (str currency_1 "/" currency_2)
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

(defn populate-duplicated [drop]
  (when drop
    (mongo/drop "duplicate_pairs"))
  (let [all_pairs (mongo/find-all "unique_pairs")]
    (doseq [pair all_pairs]
      (when (and (nil? (mongo/find "duplicate_pairs" {:name (:name pair)})) (> (count (mongo/find-all-with-query "pairs" {:name (:name pair)})) 2))
        (mongo/insert "duplicate_pairs" pair)
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
        ;;(println (sort-by :price prices))
        (remove #(= (:price %) -1) (sort-by :price prices))
        (recur (inc i) (conj prices {:price (get-price a b (:_id (nth exchanges i))) :exchange (nth exchanges i)})))
      )
    )
  )

(defn descending_price
  [a b]
  (let [exchanges (doto (mongo/find-all "exchanges"))]
    (loop [i 0
           prices ()] ;<= only thing thats different
      (if (= i (count exchanges))
        (remove #(= (:price %) -1)(reverse(sort-by :price prices)))
        (recur (inc i) (conj prices {:price (get-price a b (:_id (nth exchanges i))) :exchange (nth exchanges i)})))
      )
    )
  )

(defn calculate-profit [buy sell]
  (/ (- sell buy) (/ buy 100))
  )


(defn matrica [a b]
  (let [ascending_price (doto (ascending_price a b))
        descending_price (reverse ascending_price)
        ]
    (println ascending_price)
    (println descending_price)
    (if (> (count ascending_price) 2)
      (for [i (range (count ascending_price))]
        (for [j (range (count descending_price))]
           [{:value_i  i :value_j j
             :buy      a
             :buy_on   (:exchange (nth ascending_price i))
             :buy_for  (:price (nth ascending_price i))
             :sell_on  (:exchange (nth descending_price j))
             :sell_for (:price (nth descending_price j))
             :profit   (str (calculate-profit (:price (nth ascending_price i)) (:price (nth descending_price j))) "%")
             }
            ]
           ))
      [[{:message "THAT PAIR IS NOT AVAIABLE"}]]
      )
    )
  )

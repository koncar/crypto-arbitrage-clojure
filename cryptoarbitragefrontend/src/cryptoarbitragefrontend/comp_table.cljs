(ns cryptoarbitragefrontend.comp-table
  (:require [cryptoarbitragefrontend.comp-graphs :as comp_graphs]
            [cryptoarbitragefrontend.comp-cryptopairs :as comp_pairs]
            [reagent.core :as reagent]
            [cryptoarbitragefrontend.http-client :as client]
            [cryptoarbitragefrontend.comp-messages :as comp_messages]
            [clojure.string :as str]))


(defonce ascending_price_exchanges (reagent/atom nil))
(defonce descending_price_exchanges (reagent/atom nil))
(defonce matrix (reagent/atom nil))

(defonce table (reagent/atom nil))
(defn table-holder []
  [:div.container.table-responsive
   @table
   ]
  )

(defn descending_head []
  (loop [x 0
         final_list (list)]
    (if (= x (count @ascending_price_exchanges))
      final_list
      (let [exchange  (nth @ascending_price_exchanges x)]
        (recur (inc x) (conj final_list [:th.table-dark

                                         {
                                          :style {:background-color "#696969"}
                                          :scope "col"
                                          :key x
                                          }
                                         [:h2 (:name (:exchange exchange))]
                                         [:a
                                          {:href (:website (:exchange exchange))}
                                          (:website (:exchange exchange))
                                          ]
                                         [:h5 (:price exchange)]
                                         ]

                             ))
        )
      )
    )
  )

(defn table-dat []
  (doall(for [i (range (count @ascending_price_exchanges))]
     [:tr {:key i} (let
                     [exchange (nth @ascending_price_exchanges i)] [:th.table-dark {:scope "row"
                                                                                    :style {:background-color "#696969"}
                                                                                    }
                                                                    [:h2 (:name (:exchange exchange))]
                                                                    [:a
                                                                     {:href (:website (:exchange exchange))}
                                                                     (:website (:exchange exchange))
                                                                     ]
                                                                    [:h5 (:price exchange)]
                                                                    ])
      (doall (for [j (range (count @descending_price_exchanges))]
               (let [current_cell (nth (nth (nth @matrix i) j) 0)]
                 [:td.table-dark.table-sm {:key j
                                  :scope "cell"
                                  }
                  [:h4 (str "BUY " (:buy current_cell) " ON " (:name (:buy_on current_cell)))]
                  [:p (str "FOR " (:buy_for current_cell) " " (:sell_valute current_cell))]
                  [:h4 (str "SELL ON " (:name (:sell_on current_cell)))]
                  [:p (str "FOR " (:sell_for current_cell) " " (:sell_valute current_cell))]
                  [:h3
                   {:style {:color (if (= "0.0%" (:profit current_cell))
                                     "blue"
                                     (if (= "-" (first (:profit current_cell)))
                                       "red"
                                       "green")

                                     )
                            }}
                   (str "PROFIT: " (:profit current_cell))

                   ]

                  ]
                 )
               ))
      ]
     ))
  )

(defn make-table-from-response []
  (reset! table [:table.table.table-bordered
                 [:thead
                  [:tr
                   [:th.cell-background {:scope "col"} [:div
                                                        [:span.cell-bottom "ascending"]
                                                        [:span.cell-top "descending"]
                                                        [:div.cell-line]
                                                        ]
                    ]
                   (descending_head)
                   ]
                  ]
                 [:tbody
                  (table-dat)
                  ]
                 ]
          )
  )

(defn success-collect-inner-matrix [body]
  (comp_messages/success-message-from-response body)
  (reset! ascending_price_exchanges (:ascending_price body))
  (reset! descending_price_exchanges (:descending_price body))
  (reset! matrix (:result body))
  (make-table-from-response)
  ;(doseq [i (range (count @ascending_price_exchanges))]
  ;  (doseq [j (range (count @descending_price_exchanges))]
  ;    (println i "," j ": " (nth (nth (nth @matrix i) j) 0) )
  ;    )
  ;  )

  )
(defn table-did-mount [this]
  (if (or (nil? @ascending_price_exchanges) (nil? @descending_price_exchanges) (nil? @matrix))
    (client/get-resource (str "http://localhost:8080/get-inner-matrix/" (nth (str/split @comp_pairs/selected-pair-id #"/") 0) "/" (nth (str/split @comp_pairs/selected-pair-id #"/") 1))
                         success-collect-inner-matrix
                         comp_messages/fail-message-from-response
                         )
    (make-table-from-response)
    )
  )
(defn trigger-table-refresh [] "refresh-table"
  (client/get-resource (str "http://localhost:8080/get-inner-matrix/" (nth (str/split @comp_pairs/selected-pair-id #"/") 0) "/" (nth (str/split @comp_pairs/selected-pair-id #"/") 1))
                       success-collect-inner-matrix
                       comp_messages/fail-message-from-response
                       )
  )
(defn refresh-table [this]
  )
(defn crypto-table []
  (reagent/create-class {:reagent-render       table-holder
                         :component-did-mount  table-did-mount
                         :component-did-update refresh-table}))


(defn crypto-arbitrage-table []
  [:div.container
   {:style {:margin-top "100px"}}
   [:h1.float-left.col-md-9
    "CRYPTOMATRIX"
    ]
   [:div.row.col-md-3.border.p-2
    [:div.col-md-10
     (comp_pairs/populate_pairs)]
    [:button.btn.btn-primary.fas.fa-sync-alt.col-md-2.text-center
     {:on-click #(trigger-table-refresh)}
     ]
    ]


   [crypto-table]
   [:div.container-fluid
    [:div {:style {:height "1px"
                   :background-color "black"
                   }}]
    ]
   [:div.container.mt-5
    [:div.text-center {:style {:display @comp_graphs/display_loader}}
     [:h1 "CALCULATING PRICES"]
     [:div.loader
      ]]
    [:div.float-right.border.p-2
     {:style {:display (if (= "none" @comp_graphs/display_loader) "inline" "none") }}
     [:h5 "Refresh graph prices"]
     [:button.btn.btn-primary.fas.fa-sync-alt {:on-click #(comp_graphs/trigger-refresh)}]
     ]
    [comp_graphs/chart]
    ]
   ])
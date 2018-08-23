(ns cryptoarbitragefrontend.comp-graphs
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [cljs-http.client :as client]
            [cljs.core.async :refer [<!]]
            ))

(defn chart-holder []

  [:div {:style {:min-width "310px"
                 :height "1000px" :margin "0 auto"}}
   ])

(defn chart-config [exchanges series]

  {:chart {:type "bar"}
   :title {:text "ALL CRYPTO PAIRS GRAPH"}
   :subtitle {:text "Source: Crypto-Arbitrager"}
   :xAxis {:categories exchanges
           :title {:text nil}}
   :yAxis {:min 0
           :title {:text "Price (decimals)"
                   :align "high"}
           :labels {:overflow "justify"}}
   :tooltip {:valueSuffix " millions"}
   :plotOptions {:bar {:dataLabels {:enabled true}}}
   :legend {:layout "vertical"
            :align "right"
            :verticalAlign "top"
            :x 0
            :y 230
            :floating true
            :borderWidth 1
            :shadow true}
   :credits {:enabled false}
   :series series
   }
  )
;(js/Highcharts.Chart. (reagent/dom-node this) (clj->js chart-config))
(defn chart-did-mount [this]
  (go (let [response (<! (client/get "http://localhost:8080/get-price-on-exchanges"
                                     {:with-credentials? false}))]
        (println (:body response))
        (if (= (:status response) 200)
           (js/Highcharts.Chart. (reagent/dom-node this) (clj->js (chart-config (:exchanges (:body response)) (:data (:body response)))))
          )
        )
      )
  )

(defn chart []
  (reagent/create-class {:reagent-render      chart-holder
                         :component-did-mount chart-did-mount}))
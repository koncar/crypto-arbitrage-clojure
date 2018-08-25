(ns cryptoarbitragefrontend.comp-graphs
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [cljs-http.client :as client]
            [cljs.core.async :refer [<!]]
            ))

(defonce display_loader (reagent/atom "inline"))
(defn chart-holder []

  [:div {
         :id "chart-holder"

         :style {:min-width "310px"
                 :height "1000px" :margin "0 auto"
                 :visibility (if (= "none" @display_loader) "visible" "hidden")
                 }}
   ])
(defonce atom_exchanges (reagent/atom []))
(defonce atom_series (reagent/atom []))
(defonce atom_date (reagent/atom []))


(defn chart-config []

  {:chart       {:type "bar"}
   :title       {:text "ALL CRYPTO PAIRS GRAPH"}
   :subtitle    {:text (str "Prices calculated on: " @atom_date)}
   :xAxis       {:categories @atom_exchanges
                 :title      {:text nil}}
   :yAxis       {:min    0
                 :title  {:text  "Price (decimals)"
                          :align "high"}
                 :labels {:overflow "justify"}}
   :tooltip     {:valueSuffix " millions"}
   :plotOptions {:bar {:dataLabels {:enabled true}}}
   :legend      {:layout        "vertical"
                 :align         "right"
                 :verticalAlign "top"
                 :x             0
                 :y             230
                 :floating      true
                 :borderWidth   1
                 :shadow        true}
   :credits     {:enabled false}
   :series      @atom_series
   }
  )
;(js/Highcharts.Chart. (reagent/dom-node this) (clj->js chart-config))
(defonce chart-component (reagent/atom nil))

(defn chart-did-mount [this]
  (if (or (nil? @chart-component) (nil? @atom_exchanges) (nil? @atom_series) (nil? @atom_date))
    (go (let [response (<! (client/get "http://localhost:8080/get-price-on-exchanges"
                                       {:with-credentials? false}))]
          (println (:body response))
          (if (= (:status response) 200)
            (do
              (reset! display_loader "none")
              (reset! atom_exchanges (:exchanges (:body response)))
              (reset! atom_series (:data (:body response)))
              (reset! atom_date (:date (:body response)))
              (js/Highcharts.Chart. (reagent/dom-node this) (clj->js (chart-config)))
              (reset! chart-component this)
              )
            )
          )
        )
    (do
      (println "izvrseno")
      (reset! display_loader "none")
        (js/Highcharts.Chart. (reagent/dom-node this) (clj->js (chart-config))))
    )
  )

(defn trigger-refresh []
  (println "trigger refresh")
  (reset! display_loader "inline")
  (go (let [response (<! (client/get "http://localhost:8080/refresh-price-on-exchanges"
                                     {:with-credentials? false}))]
        (println (:body response))
        (if (= (:status response) 200)
          (do
            (reset! display_loader "none")
            (reset! atom_exchanges (:exchanges (:body response)))
            (reset! atom_series (:data (:body response)))
            (reset! atom_date (:date (:body response)))
            (reagent/force-update @chart-component)
            )
          )
        )
      )
  )

(defn refresh-graph-prices [this]
  (println "refresh-called")
  (js/Highcharts.Chart. (reagent/dom-node this) (clj->js (chart-config )))
  )

(defn chart []
  (reagent/create-class {:reagent-render      chart-holder
                         :component-did-mount chart-did-mount
                         :component-did-update refresh-graph-prices}))


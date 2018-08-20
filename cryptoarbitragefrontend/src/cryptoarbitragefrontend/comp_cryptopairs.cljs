(ns cryptoarbitragefrontend.comp-cryptopairs
  (:require [reagent.core :as reagent]
            [re-com.core :refer [h-box v-box box gap line single-dropdown input-text checkbox label title hyperlink-href button p alert-list alert-box]]
            [cryptoarbitragefrontend.http-client :as client]
            [cryptoarbitragefrontend.comp-messages :as comp_messages])
  )


(defonce pairs (reagent/atom []))
(defonce selected-pair-id (reagent/atom "BTC/USD"))


(defn swap_pairs
  [body]
  (reset! pairs body))

(defn get_pairs []
  (when (= 0 (count @pairs))
    (println "called")
    (client/get-resource "http://localhost:8080/get-pairs" swap_pairs comp_messages/fail-message-from-response))
  )


(defn populate_pairs
  []
  (get_pairs)
  [v-box
   :gap "10px"
   :children
   [
    [h-box
     :gap "10px"
     :align :center
     :children
     [
      [single-dropdown
       :choices   pairs
       :filter-box? true
       :model     selected-pair-id
       :width     "100%"
       :placeholder "chose a pair"
       :on-change #(reset! selected-pair-id %)]
      ;[:div
      ; [:strong "Selected country: "]
      ; (if (nil? @selected-country-id)
      ;   "None"
      ;   (str (:label (item-for-id @selected-country-id grouped-countries)) " [" @selected-country-id "]"))]
      ]]]])
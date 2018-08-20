(ns cryptoarbitragefrontend.comp-countries
  (:require [reagent.core :as reagent]
            [re-com.core :refer [h-box v-box box gap line single-dropdown input-text checkbox label title hyperlink-href button p alert-list alert-box]]
            [cryptoarbitragefrontend.http-client :as client]
            [cryptoarbitragefrontend.comp-messages :as comp_messages]))


(defonce countries (reagent/atom []))

(defn swap_countries
  [body]
  (reset! countries body))

(defn get_countries []
  (when (= 0 (count @countries))
    (println "called")
    (client/get-resource "http://localhost:8080/get-countries" swap_countries comp_messages/fail-message-from-response))
  )

(defn populate_countries
  [selected-country-id & selected-country]
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
       :choices countries
       :model selected-country-id
       :title? true
       :placeholder "Choose a country"
       :width "100%"
       :max-height "400px"
       :filter-box? true
       :on-change #(reset! selected-country-id %)]
      ;[:div
      ; [:strong "Selected country: "]
      ; (if (nil? @selected-country-id)
      ;   "None"
      ;   (str (:label (item-for-id @selected-country-id grouped-countries)) " [" @selected-country-id "]"))]
      ]]]])

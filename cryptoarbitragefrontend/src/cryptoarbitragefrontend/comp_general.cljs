(ns cryptoarbitragefrontend.comp_general
  (:require [cryptoarbitragefrontend.http-client :as client]
            [re-com.core :refer [h-box v-box box gap line single-dropdown input-text checkbox label title hyperlink-href button p alert-list alert-box]]
            [re-com.dropdown :refer [filter-choices-by-keyword single-dropdown-args-desc]]
            [re-com.util :refer [item-for-id remove-id-item insert-nth]]
            [reagent.core :as reagent]
            [secretary.core :as secretary]
            [cryptoarbitragefrontend.comp_alert-messages :as comp_messages]
            [cryptoarbitragefrontend.util :as util]
            [cryptoarbitragefrontend.comp_countries :as comp_countries])

  )


(defonce user (reagent/atom nil))
(defonce user_logged (reagent/atom false))
(defn is_user_logged [] @user_logged)

(defn home-panel []
  [:header.masthead.bg-primary.text-white.text-center
   [:div.container
    [:img.img-fluid.mb-5.d-block.mx-auto {:src "img/bitcoin-icon.png" :height 256 :width 256 :alt "bitcoin-icon"}]
    [:h1.text-uppercase.mb-0 "CRYPTO-ARBITRAGER"]
    [:hr.star-light]
    [:h2.font-weight-light.mb-0 "crypto arbitrage calculator collects data from famous crypto exchanges and calculates circles you can use to make profit"]
    ]])

(defn crypto-arbitrage-table [] nil)


(defn write-blog [] nil)

(defn populate-countries []
  (client/post-resource "http://localhost:8080/populate-countries" {:password "admin"} comp_messages/success-message-from-response comp_messages/fail-message-from-response)
  )

(defn populate-exchanges []
  (client/post-resource "http://localhost:8080/populate-exchanges" {:password "admin"} comp_messages/success-message-from-response comp_messages/fail-message-from-response)
  )
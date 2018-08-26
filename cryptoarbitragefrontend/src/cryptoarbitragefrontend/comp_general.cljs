(ns cryptoarbitragefrontend.comp-general
  (:require [cryptoarbitragefrontend.http-client :as client]
            [re-com.core :refer [h-box v-box box gap line single-dropdown input-text checkbox label title hyperlink-href button p alert-list alert-box]]
            [re-com.dropdown :refer [filter-choices-by-keyword single-dropdown-args-desc]]
            [re-com.util :refer [item-for-id remove-id-item insert-nth]]
            [reagent.core :as reagent]
            [cryptoarbitragefrontend.comp-messages :as comp_messages]
            [cryptoarbitragefrontend.comp-contact-form :as comp_contact_form]
            [clojure.string :as str])

  )


(defonce user (reagent/atom nil))
(defonce user_logged (reagent/atom false))
(defn is_user_logged [] @user_logged)

(defn home-panel []
  [:header.masthead.bg-primary.text-white.text-center
   [:div.container
    [:img.img-fluid.mb-5.d-block.mx-auto {:src "img/bitcoin-icon.png" :height 256 :width 256 :alt "bitcoin-icon"}]
    [:h1.text-uppercase.mb-0 "CRYPTO-ARBITRAGER"]
    [:hr]
    [:hr.fas.fa-star]
    [:h2.font-weight-light.mb-0 "crypto arbitrage calculator collects data from famous crypto exchanges and calculates circles you can use to make profit"]
    ]])

(defn footer []

  [:footer.footer.text-center.sticky-bottom
   {:style {:margin-top "100px"}
    }
   [:div.container
    [:div.row
     [:div.col-md-4.mb-5.mb-lg-0
      [:h4.text-uppercase.mb-4 "DEVELOPMENT"]
      [:p.lead.mb-0
       "Crypto-Arbitrager has front-end developed as single page application using ClojureScript and Reagent engine. Back-end is developed as REST service with Clojure and uses MongoDB as database."]]
     [:div.col-md-4.mb-5.mb-lg-0
      [:h4.text-uppercase.mb-4 "Author"]
      [:ul.list-inline.mb-0
       [:li.list-inline-item
        [:a.btn.btn-outline-light.btn-social.text-center.rounded-circle
         {:href "https://www.linkedin.com/in/stevan-koncar-a628508b/"}
         [:i.fab.fa-linkedin-in]]]
       [:li.list-inline-item
        [:a.btn.btn-outline-light.btn-social.text-center.rounded-circle
         {:href "mailto:skoncar@live.com"}
         [:i.fa.fa-fw.fa-envelope]]]
       ]
      [:div.container
       (comp_contact_form/contact-form)
       ]

      ]
     [:div.col-md-4
      [:h4.text-uppercase.mb-4 "About Crypto-Arbitrager"]
      [:p.lead.mb-0
       "crypto arbitrage calculator collects data from famous crypto exchanges and calculates circles you can use to make profit"]]]]]
  )




(defn populate-countries []
  (client/post-resource "http://localhost:8080/populate-countries" {:password "admin"} comp_messages/success-message-from-response comp_messages/fail-message-from-response)
  )

(defn populate-exchanges []
  (client/post-resource "http://localhost:8080/populate-exchanges" {:password "admin"} comp_messages/success-message-from-response comp_messages/fail-message-from-response)
  )

(defn populate-pairs []
  (client/post-resource "http://localhost:8080/populate-pairs" {:password "admin"} comp_messages/success-message-from-response comp_messages/fail-message-from-response)
  )
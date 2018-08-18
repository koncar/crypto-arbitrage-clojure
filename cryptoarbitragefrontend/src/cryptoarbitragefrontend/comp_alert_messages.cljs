(ns cryptoarbitragefrontend.comp_alert-messages
  (:require [reagent.core :as reagent]))


(defonce messages (reagent/atom (sorted-map)))
(defonce counter (reagent/atom 0))

(defn delete [id] (swap! messages dissoc id))

(defn add-message [text type]
  (let [id (swap! counter inc)]
    (swap! messages assoc id {:id id :title text :type type})
    id
    ))


(defn add-success-message-with-timeout [text]
  (let [id (add-message text "alert-success")]
    (js/setTimeout #(delete id) 5000)))

(defn add-warning-message-with-timeout [text]
  (let [id (add-message text "alert-warning")]
    (js/setTimeout #(delete id) 5000)))

(defn add-danger-message-with-timeout [text]
  (let [id (add-message text "alert-danger")]
    (js/setTimeout #(delete id) 5000)))


(defn message []
  (fn [{:keys [id title type]}]
    [:div.alert
     {:class type
      }
     [:strong title]
     [:a.close {:on-click #(delete id)}
      "remove"]
     ]
    ))

(defn messages-holder []
  (let [filed (reagent/atom :all)]
    (let [items (vals @messages)]
      [:div.fixed-top.mt-5
       {:style {:width "30%"
                :left "auto"}
        }
       (when (-> items count pos?)
         [:div
          (for [todo (filter (case @filed
                               :all identity) items)]
            ^{:key (:id todo)} [message todo])
          ]
         )
       ]
      )))

(defn fail-message-from-response
  [body]
  (add-danger-message-with-timeout (:message body))
  )

(defn success-message-from-response
  [body]
  (add-success-message-with-timeout (:message body))
  )
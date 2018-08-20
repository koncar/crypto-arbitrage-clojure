(ns cryptoarbitragefrontend.comp-write-post
  (:require [cryptoarbitragefrontend.comp-messages :as comp_messages]
            [secretary.core :as secretary]
            [cryptoarbitragefrontend.comp-blog :as comp_blog]
            [cryptoarbitragefrontend.http-client :as client]
            [reagent.core :as reagent]
            [cryptoarbitragefrontend.comp-general :as comp_general]))


(defonce post_title (reagent/atom nil))
(defonce post_description (reagent/atom nil))
(defn success-save-post [body]
  (comp_messages/success-message-from-response body)
  (secretary/dispatch! "#/blog")
  (comp_blog/fill-posts)
  )
(defn get-data []
  (client/post-resource (str "http://localhost:8080/save-blog-post/" (:_id @comp_general/user)) {:title @post_title :description @post_description :text (js-invoke js/takedata "takedata")}
                        success-save-post
                        comp_messages/fail-message-from-response)
  )

(defn write-blog []
  [:div.container
   {:style {:margin-top "100px"}}
   [:h1.mt-5 "WRITE NEW POST"]
   [:form
    [:label "Title"]
    [:input.form-control {:type "text"
                          :on-change #(reset! post_title (-> % .-target .-value))}]
    [:label "Description"]
    [:input.mb-5.form-control {:type "text"
                               :on-change #(reset! post_description (-> % .-target .-value))}]
    [:input {:id "x"
             :type "hidden"
             :name "content"}]
    [:trix-editor.trix-content {:input "x"}]
    [:div.trix-content]
    [:input.btn.btn-primary.mt-2
     {:type "button"
      :value "SAVE POST"
      :href "#/blog"
      :on-click #(get-data)}

     ]
    ]
   ])
(ns cryptoarbitragefrontend.components
  (:require [cryptoarbitragefrontend.http-client :as client]
            [re-com.core :refer [h-box v-box box gap line single-dropdown input-text checkbox label title hyperlink-href button p alert-list alert-box]]
            [re-com.dropdown :refer [filter-choices-by-keyword single-dropdown-args-desc]]
            [re-com.util :refer [item-for-id remove-id-item insert-nth]]
            [reagent.core :as reagent]))

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
      [:div.fixed-top
       {:style {:width "50%"
                :left "auto"}}
       (when (-> items count pos?)
         [:div
            (for [todo (filter (case @filed
                                 :all identity) items)]
              ^{:key (:id todo)} [message todo])
          ]
          )
       ]
      )))

(defn home-navigation
  [])

(defn home-panel []
  [:header.masthead.bg-primary.text-white.text-center
   [:div.container
    [:img.img-fluid.mb-5.d-block.mx-auto {:src "img/bitcoin-icon.png" :height 256 :width 256 :alt "bitcoin-icon"}]
    [:h1.text-uppercase.mb-0 "CRYPTO ARBITRAGE"]
    [:hr.star-light]
    [:h2.font-weight-light.mb-0 "crypto arbitrage calculator collects data from famous crypto exchanges and calculates circles you can use to make profit"]
    ]])

(defonce countries (reagent/atom []))
(defonce selected-country-id (reagent/atom nil))

(defn populate_countries
  []
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

(defn swap_countries
  [body]
  (reset! countries body))

(defn get_countries []
  (when (= 0 (count @countries))
    (client/get-resource "http://localhost:8080/get-countries" swap_countries))
  )

(defonce v_name (reagent/atom nil))
(defonce v_email (reagent/atom nil))
(defonce v_username (reagent/atom nil))
(defonce v_password (reagent/atom nil))
(defonce v_confirm_password (reagent/atom nil))

(defn sign_up_success []
  (client/post-resource "http://localhost:8080/register" {:name @v_name :email @v_email :username @v_username :password @v_password :country (item-for-id @selected-country-id @countries)} )
  )

(defn sign_up []
  (if (or (or (nil? @v_name) (= "" @v_name))
          (or (nil? @v_name) (= "" @v_email))
          (or (nil? @v_name) (= "" @v_username))
          (or (nil? @v_name) (= "" @v_password))
          (or (nil? @v_name) (= "" @v_confirm_password))
          (or (nil? @selected-country-id) (= "" @selected-country-id))
          )
    (add-danger-message-with-timeout "Name, email, username and password fields cannot be empty")
    (if (= @v_password @v_confirm_password)
      (sign_up_success)
      (add-danger-message-with-timeout "Passwords don't match")
      )
    )
  )

(defn sign-up-form []
  (get_countries)

  [:h1 "SIGN UP"]
  [:form
   [:ul.list-group
    [:li.list-group-item [:input.form-control
                          {:type        "text"
                           :placeholder "Name"
                           :on-change   #(reset! v_name (-> % .-target .-value))}]
     ]
    [:li.list-group-item [
                          :input.form-control
                          {:type        "email"
                           :placeholder "Email"
                           :on-change   #(reset! v_email (-> % .-target .-value))}]]
    [:li.list-group-item [
                          :input.form-control
                          {:type        "text"
                           :placeholder "Username"
                           :on-change   #(reset! v_username (-> % .-target .-value))}]]
    [:li.list-group-item [
                          :input.form-control
                          {:type        "password"
                           :placeholder "Password"
                           :on-change   #(reset! v_password (-> % .-target .-value))}]]
    [:li.list-group-item [
                          :input.form-control
                          {:type        "password"
                           :placeholder "Confirm password"
                           :on-change   #(reset! v_confirm_password (-> % .-target .-value))}]]
    [:li.list-group-item
     (populate_countries)
     ;[:input {:type     "button" :value "Click me!"
     ;         :on-click #(get_countries)}]
     ]
    [:li-list-group-item [:button.btn.btn-primary.mt-2 {:type     "button"
                                                        :on-click #(sign_up)} "SIGN UP"]]
    ]
   ]

  )
(defn sign_in []
  (println "sign in"))

(defn sign-in-form []
  [:div.container.mt-4.text-center
   [:h1 "SIGN IN"]
   [:form
    [:ul.list-group
     [:li.list-group-item [
                           :input.form-control
                           {:type "email"
                            :placeholder "Email"
                            :on-change #(reset! v_email (-> % .-target .-value))}]]
     [:li.list-group-item [
                           :input.form-control
                           {:type "password"
                            :placeholder "Password"
                            :on-change #(reset! v_password (-> % .-target .-value))}]]
     [:li.list-group-item
      ;[:input {:type     "button" :value "Click me!"
      ;         :on-click #(get_countries)}]
      [:button.btn.btn-primary.mt-2 {:type "button"
                                     :on-click #(sign_in)} "SIGN IN"]
      ]
     ]
    ]
   ]
  )

(ns cryptoarbitragefrontend.comp_sign_forms
  (:require [reagent.core :as reagent]
            [re-com.core :refer [h-box v-box box gap line single-dropdown input-text checkbox label title hyperlink-href button p alert-list alert-box]]
            [re-com.dropdown :refer [filter-choices-by-keyword single-dropdown-args-desc]]
            [re-com.util :refer [item-for-id remove-id-item insert-nth]]
            [cryptoarbitragefrontend.http-client :as client]
            [cryptoarbitragefrontend.comp_alert-messages :as comp_messages]
            [cryptoarbitragefrontend.comp_general :as comp_general]
            [cryptoarbitragefrontend.comp_menus :as comp_menus]
            [cryptoarbitragefrontend.comp_countries :as comp_countries]
            [secretary.core :as secretary]
            [cryptoarbitragefrontend.util :as util]))


(defonce v_name (reagent/atom nil))
(defonce v_email (reagent/atom nil))
(defonce v_username (reagent/atom nil))
(defonce v_password (reagent/atom nil))
(defonce v_confirm_password (reagent/atom nil))
(defonce selected-country-id (reagent/atom nil))

(defn sign-up-success [body]
  (reset! comp_general/user (:user body))
  (reset! comp_general/user_logged true)
  (comp_menus/populate-menu (:admin (:user body)))
  (comp_messages/success-message-from-response body)
  (secretary/dispatch! "/home")
  )

(defn sign_up []
  (client/post-resource "http://localhost:8080/register"
                        {:name @v_name :email @v_email :username @v_username :password @v_password :country (item-for-id @selected-country-id @comp_countries/countries)}
                        sign-up-success
                        comp_messages/fail-message-from-response
                        )
  )

(defn sign_up_validate []
  (if (or (or (nil? @v_name) (= "" @v_name))
          (or (nil? @v_name) (= "" @v_email))
          (or (nil? @v_name) (= "" @v_username))
          (or (nil? @v_name) (= "" @v_password))
          (or (nil? @v_name) (= "" @v_confirm_password))
          (or (nil? @selected-country-id) (= "" @selected-country-id))
          )
    (comp_messages/add-danger-message-with-timeout "Name, email, username, country and password fields cannot be empty")
    (if (util/validate-email @v_email)
      (if (= @v_password @v_confirm_password)
        (sign_up)
        (comp_messages/add-danger-message-with-timeout "Passwords don't match")
        )
      (comp_messages/add-warning-message-with-timeout "Email is not in good format")
      )
    )
  )

(defn sign-up-form []
  (comp_countries/get_countries)
  [:div.container.mt-4.text-center
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
                            :aria-describedby "emailHelp"
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
      (comp_countries/populate_countries selected-country-id)
      ;[:input {:type     "button" :value "Click me!"
      ;         :on-click #(get_countries)}]
      ]
     [:li-list-group-item [:a.btn.btn-primary.mt-2.sign-button {
                                                                    :type     "button"
                                                                     :href "#/home"
                                                                     :on-click #(sign_up_validate)} "SIGN UP"]]
     ]
    ]
   ]

  )


(defn sign-in-success
  [body]
  (reset! comp_general/user (:user body))
  (reset! comp_general/user_logged true)
  (comp_messages/success-message-from-response body)
  (comp_menus/populate-menu (:admin (:user body)))
  (println @comp_general/user)
  (secretary/dispatch! "/home")
  )

(defn sign_in []
  (if (util/validate-email @v_email)
    (client/post-resource "http://localhost:8080/login" {:email @v_email :password @v_password}
                          sign-in-success
                          comp_messages/fail-message-from-response)
    (comp_messages/add-warning-message-with-timeout "Email format is invalid")
    ))

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
      [:a.btn.btn-primary.mt-2.sign-button {:type "button"
                                                 :href "#/home"
                                                 :on-click #(sign_in)} "SIGN IN"]
      ]
     ]
    ]
   ]
  )

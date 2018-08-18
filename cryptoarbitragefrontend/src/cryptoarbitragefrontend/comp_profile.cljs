(ns cryptoarbitragefrontend.comp_profile
  (:require [cryptoarbitragefrontend.comp_countries :as comp_countries]
            [reagent.core :as reagent]
            [cryptoarbitragefrontend.comp_general :as comp_general]
            [clojure.string :as str]
            [cryptoarbitragefrontend.comp_alert-messages :as comp_messages]
            [cryptoarbitragefrontend.http-client :as client]
            [re-com.util :refer [item-for-id remove-id-item insert-nth]]
            [cryptoarbitragefrontend.util :as util]
            [cljs-http.client :as client-lib]))


(defonce not_editing (reagent/atom true))
(defonce edit_profile_button_color (reagent/atom "#18BC9C"))
(defonce edit_profile_button_lbl (reagent/atom "EDIT PROFILE"))
(defonce changing_password (reagent/atom false))
(defonce change_password_button_color (reagent/atom "#18BC9C"))
(defonce change_password_button_lbl (reagent/atom "CHANGE PASSWORD"))


(defonce p_name (reagent/atom (:name @comp_general/user)))
(defonce p_username (reagent/atom (:username @comp_general/user)))
(defonce p_password (reagent/atom nil))
(defonce p_confirm_password (reagent/atom nil))
(defonce p_selected-country-id (reagent/atom nil))

(defn success-change-password [body]
  (comp_messages/add-success-message-with-timeout (:message body))
  (reset! changing_password false)
  (reset! change_password_button_lbl "CHANGE PASSWORD")
  (reset! change_password_button_color "#18BC9C")
  )

(defn change-password []
  (if (= @change_password_button_lbl "CHANGE PASSWORD")
    (do (reset! changing_password true)
        (reset! change_password_button_lbl "CHANGE PASSWORD: CONFIRM")
        (reset! change_password_button_color "#ffc107")
        )
    (do (if (not (str/blank? @p_password))
          (if (= @p_password @p_confirm_password)
            (client/post-resource (str "http://localhost:8080/change-password/" (:_id @comp_general/user))
                                  {:password @p_password}
                                  success-change-password comp_messages/fail-message-from-response)
            (comp_messages/add-warning-message-with-timeout "Password don't match")
            )
          (comp_messages/add-danger-message-with-timeout "Password cannot be empty")
          )
        )
    )
  )

(defn update-me-success [body]
  (reset! comp_general/user (:user body))
  (reset! not_editing true)
  (reset! edit_profile_button_lbl "EDIT PROFILE")
  (reset! edit_profile_button_color "#18BC9C")

  (comp_messages/success-message-from-response body)
  )

(defn update-me []
  (client/post-resource (str "http://localhost:8080/update-me/" (:_id @comp_general/user))
                        {:name @p_name :email (:email @comp_general/user) :username @p_username :password (:password @comp_general/user) :country (item-for-id @p_selected-country-id @comp_countries/countries)}
                        update-me-success
                        comp_messages/fail-message-from-response
                        )
  )

(defn edit-profile []
  (if (= @edit_profile_button_lbl "EDIT PROFILE")
    (do (reset! not_editing false)
        (reset! edit_profile_button_lbl "EDIT PROFILE: CONFIRM")
        (reset! edit_profile_button_color "#ffc107")

        )
    (if (or (or (nil? @p_name) (= "" @p_name))
            (or (nil? @p_selected-country-id) (= "" @p_selected-country-id))
            )
      (comp_messages/add-danger-message-with-timeout "Name and country cannot be empty")
      (update-me)
      )
    )
  )

(def profile_picture_file (reagent/atom nil))
(def swap_counter (reagent/atom 0))
(def image_url (reagent/atom (str "http://localhost:8080/profilepictures/" (:_id @comp_general/user) ".jpg?param=" @swap_counter)))

(defn success-upload-picture [body]
  (comp_messages/success-message-from-response body)
  (swap! swap_counter inc)
  (reset! image_url (str "http://localhost:8080/profilepictures/" (:_id @comp_general/user) ".jpg?param=" @swap_counter))
  )

(defn upload-profile-picture []
  (client/post-image (str "http://localhost:8080/upload-profile-picture/" (:_id @comp_general/user)) @profile_picture_file success-upload-picture comp_messages/fail-message-from-response)
  )



(defn my-profile []
  [:div.container.mt-4.text-center
   [:div.row
    [:div.col-xs-4.col-md-4.col-xl-4.mt-5
     [:object {:data   @image_url
               :height 256
               :width  255
               :type   "image/png"}
     [:img {:src    "http://localhost:8080/profilepictures/default-profile-image.png"
            :height 256
            :width  256}]
      ]
     ;[:input.mt-3 {:type "file"
     ;         :accept "image/png, image/jpeg, image/jpg"
     ;              :on-change   #(reset! profile_picture_file (-> % .-target .-value))
     ;              }
     ; ]
     [:input {:type "file" :id "file" :name "file"
              :on-change
                    #(reset! profile_picture_file (-> % .-target .-files (aget 0)))}]
     [:button.mt-2.btn.btn-primary
      {:on-click #(upload-profile-picture)}
      "Change Profile Picture"]
     ]
    [:div.col-xs-8.col-md-8.col-xl-8.mt-5
     [:h1 "Profile"]
     [:form
      [:ul.list-group
       [:li.list-group-item
        [:label.float-left "Name"]
        [:input.form-control
         {:type        "text"
          :placeholder "Name"
          :disabled    @not_editing
          :value       @p_name
          :on-change   #(reset! p_name (-> % .-target .-value))}]
        ]
       [:li.list-group-item
        [:label.float-left "Email"]
        [:input.form-control
         {:type        "email"
          :placeholder "Email"
          :disabled    true
          :value       (:email @comp_general/user)
          }]]
       [:li.list-group-item
        [:label.float-left "Username"]
        [:input.form-control
         {:type        "text"
          :placeholder "Username"
          :value       @p_username
          :disabled    @not_editing
          :on-change   #(reset! p_username (-> % .-target .-value))}]]
       [:li.list-group-item
        [
         :input.form-control
         {:type        "password"
          :style       {
                        :display (if @changing_password "inline" "none")
                        }
          :placeholder "Password"
          :on-change   #(reset! p_password (-> % .-target .-value))}]]
       [:li.list-group-item [
                             :input.form-control
                             {:type        "password"
                              :placeholder "Confirm password"
                              :style       {
                                            :display (if @changing_password "inline" "none")
                                            }
                              :on-change   #(reset! p_confirm_password (-> % .-target .-value))}]]
       [:li.list-group-item.text-left
        {:style {:display (if @not_editing "inline" "none")}}
        [:div "Country: "
         [:ul.list-group
          [:li.list-group-item "Name: " (:label (:country @comp_general/user))]
          [:li.list-group-item "Code 1: " (:alpha2Code (:country @comp_general/user))]
          [:li.list-group-item "Code 2: " (:alpha3Code (:country @comp_general/user))]
          ]
         ]
        ;[:input {:type     "button" :value "Click me!"
        ;         :on-click #(get_countries)}]
        ]
        [:li.list-group-item
         {:style {:display (if @not_editing "none" "inline")}}
         [:div
          (comp_countries/populate_countries p_selected-country-id)]]
       [:li-list-group-item
        [:div
         [:a.btn.btn-primary.mt-2.sign-button.col-xl-4.col-xs-12.mr-5 {:type     "button"
                                               :style    {
                                                          :background-color @edit_profile_button_color
                                                          }
                                               :on-click #(edit-profile)} @edit_profile_button_lbl]
         [:a.btn.btn-primary.mt-2.sign-button.col-xl-4.col-xs-12 {:type     "button"
                                               :style    {
                                                          :background-color @change_password_button_color
                                                          }

                                               :on-click #(change-password)} @change_password_button_lbl]]]
       ]
      ]
     ]]

   ]
  )

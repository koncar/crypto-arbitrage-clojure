(ns cryptoarbitragefrontend.comp_contact_form
  (:require [reagent.core :as reagent]
            [cryptoarbitragefrontend.util :as util]
            [cryptoarbitragefrontend.comp_alert-messages :as comp_messages]
            [cryptoarbitragefrontend.http-client :as client]))

(def message_name (reagent/atom nil))
(def message_email (reagent/atom nil))
(def message_subject (reagent/atom nil))
(def message_message (reagent/atom nil))

(defn send-email []
  (if (or (nil? @message_email) (nil? @message_subject) (nil? @message_message) (nil? @message_name))
    (comp_messages/add-danger-message-with-timeout "Name, email, subject and message cannot be empty")
    (if (util/validate-email @message_email)
      (client/post-resource "http://localhost:8080/email-me" {:name @message_name :email @message_email :subject @message_subject :message @message_message}
                            comp_messages/success-message-from-response
                            comp_messages/fail-message-from-response
                            )
      (comp_messages/add-danger-message-with-timeout "Email is not in good format")
      )
    )

  )



(defn contact-form []
  [:ul.list-group.mt-4.mr-5.ml-5
   [:form
    [:input.form-control {:type        "text"
                          :placeholder "Your name"
                          :on-change       #(reset! message_name (-> % .-target .-value))
                          }
     ]
    [:input.form-control.mt-1 {:type        "text-area"
                               :on-change       #(reset! message_email (-> % .-target .-value))

                               :placeholder "Your email"}
     ]
    [:input.form-control.mt-1 {:type        "text-area"
                               :on-change       #(reset! message_subject (-> % .-target .-value))
                               :placeholder "Subject"}
     ]
    [:textarea.form-control.mt-1 {:rows        8
                                  :on-change       #(reset! message_message (-> % .-target .-value))
                                  :placeholder "Your message"}
     ]
    [:button.btn.btn-primary.mt-2 {:on-click #(send-email)} "send message"]
    ]
   ])
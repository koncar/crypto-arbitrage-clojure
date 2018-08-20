(ns cryptoarbitragefrontend.pages
  (:require [cryptoarbitragefrontend.comp-general :as comp_pages]
            [cryptoarbitragefrontend.comp-messages :as comp_messages]
            [cryptoarbitragefrontend.comp-menus :as comp_menus]
            [cryptoarbitragefrontend.comp-sign-forms :as comp_forms]
            [reagent.core :as reagent]
            [cryptoarbitragefrontend.comp-profile :as comp_profile]
            [cryptoarbitragefrontend.comp-blog :as comp_blog]
            [cryptoarbitragefrontend.comp-write-post :as comp_blog_write]
            )
  )

(defonce form (reagent/atom (comp_forms/sign-up-form)))
(defonce other_form (reagent/atom "SIGN IN"))

(defn swap-form []
  (if (= @other_form "SIGN IN")
    (do
      (println "other form is sign in")
      (reset! form (-> (comp_forms/sign-in-form)))
      (reset! other_form (-> "SIGN UP"))
      )
    (do
      (println "otherform is sign up")
      (reset! form (-> (comp_forms/sign-up-form)))
      (reset! other_form (-> "SIGN IN"))))
  )
(defn home []
  [:div
   (comp_menus/home-navigation)
   (comp_messages/messages-holder)
   (comp_pages/home-panel)
   [:div.container.mt-4.text-center.col-xl-4
   @form
   [:input.btn.btn-link
    {:type     "button"
     :on-click #(swap-form)
     :value    @other_form
     }
    ]
    ;[:button {:on-click #(components/add_success_with_timeout "Ovo je test poruka")} "Test"]
    ]
   (comp_pages/footer)
   ]
  )

(defn home-logged []
  [:div
   (comp_menus/home-navigation)
   (comp_messages/messages-holder)
   (comp_pages/crypto-arbitrage-table)
   (comp_pages/footer)

   ])

(defn blog []
  [:div
   (comp_menus/home-navigation)
   (comp_messages/messages-holder)
   (comp_blog/posts-holder)
   (comp_pages/footer)

   ]
  )

(defn post []
  [:div
   (comp_menus/home-navigation)
   (comp_messages/messages-holder)
   (comp_blog/opened-post)
   (comp_pages/footer)
   ]
  )

(defn write-blog []
  [:div
   (comp_menus/home-navigation)
   (comp_messages/messages-holder)
   (comp_blog_write/write-blog)
   (comp_pages/footer)
   ]
  )

(defn my-profile []
  [:div
   (comp_menus/home-navigation)
   (comp_messages/messages-holder)
   (comp_profile/my-profile)
   (comp_pages/footer)

   ])

(defn not-found []
  :div [:h1 "Page not found"])

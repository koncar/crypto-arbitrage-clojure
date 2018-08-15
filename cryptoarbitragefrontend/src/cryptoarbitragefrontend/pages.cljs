(ns cryptoarbitragefrontend.pages
  (:require [cryptoarbitragefrontend.components :as components]
            [reagent.core :as reagent])
  )

(defonce form (reagent/atom (components/sign-up-form)))
(defonce other_form (reagent/atom "SIGN IN"))
(defn swap-form []
  (if (= @other_form "SIGN IN")
    (do
      (println "other form is sign in")
      (reset! form (-> (components/sign-in-form)))
      (reset! other_form (-> "SIGN UP"))
      )
    (do
      (println "otherform is sign up")
      (reset! form (-> (components/sign-up-form)))
      (reset! other_form (-> "SIGN IN"))))
  )
(defn home []
  [:div
   (components/home-navigation)
   (components/messages-holder)
   (components/home-panel)
   [:div.container.mt-4.text-center
   @form
   [:input.btn.btn-link
    {:type     "button"
     :on-click #(swap-form)
     :value    @other_form
     }
    ]
    ;[:button {:on-click #(components/add_success_with_timeout "Ovo je test poruka")} "Test"]
    ]
   ]
  )

(defn about []
  [:div [:h1 "About Page"]
   [:a {:href "#/"} "home page"]
   ])

(defn not-found []
  :div [:h1 "Page not found"])

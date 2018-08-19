(ns cryptoarbitragefrontend.comp_menus
  (:require [reagent.core :as reagent]
            [cryptoarbitragefrontend.comp_general :as comp_general]
            [cryptoarbitragefrontend.comp_profile :as comp_profile]
            [cryptoarbitragefrontend.comp_blog :as comp_blog]
            [cryptoarbitragefrontend.comp-write-post :as comp_write_blog]
            ))

(defonce is_admin (reagent/atom false))
(defonce left-menu-list (reagent/atom nil))
(defonce right-menu-list (reagent/atom nil))

(defn sign-out []
  (reset! left-menu-list nil)
  (reset! right-menu-list nil)
  (reset! comp_general/user nil)
  (reset! comp_general/user_logged false)
  )

(defn client_menu_left []
  (list [:li.mr-2 {:key 1} [:a {:on-click #(comp_profile/open_profile @comp_general/user true)
             :href     "#/me"} "Profile"]]
    [:li.mr-2 {:key 2} [:a {:on-click #(comp_blog/fill-posts)
                            :href     "#/blog"} "Blog"]])
  )
(defn client_menu_right
  []
  (list [:li {:key 3} [:a {:on-click #(sign-out)
                      :href     "#/"} "SIGN OUT"]])
  )
(defn admin_menu_left []
  (client_menu_left)
  )

(defn admin_menu_right []
  (concat
          (list [:li.mr-2 {:key 4} [:a {:on-click #(comp_write_blog/write-blog)
                               :href     "#/write-blog"}
                           "WRITE BLOG POST"]]
                [:li.mr-2 {:key 5} [:a {:on-click #(comp_general/populate-countries)
                               :href "#/"}
                           "reset countries"]]
                [:li.mr-2 {:key 6} [:a {:on-click #(comp_general/populate-exchanges)
                               :href "#/"}
                           "reset exchanges"]]
                )
          (client_menu_right)
          )
  )


(defn populate-menu [admin]
  (reset! is_admin admin)
  (if @is_admin
    (do
      (println "admin")
      (reset! left-menu-list (admin_menu_left))
      (reset! right-menu-list (admin_menu_right))
      )
    (do
      (println "not admin")
      (reset! left-menu-list (client_menu_left))
      (reset! right-menu-list (client_menu_right))
      )
    )

  )

(defn home-navigation []
  [:nav.navbar.navbar-expand-md.navbar-dark.fixed-top.bg-dark
   [:a.navbar-brand {:href "#"} "CRYPTO-ARBITRAGER"]
   [:button.navbar-toggler {:type        "button"
                            :data-toggle "collapse"
                            :data-target "#navbarCollapse"
                            :aria-controls "navbarCollapse"
                            :aria-expanded "false"
                            :aria-label "Toggle navigation"}
    :span.navbar-toggler-icon]
   [:div.collapse.navbar-collapse {:id "navbarCollapse"}
    [:ul.navbar-nav.mr-auto
     @left-menu-list
     ]
    [:ul.navbar-nav
     @right-menu-list
     ]
    ]
   ]
  )
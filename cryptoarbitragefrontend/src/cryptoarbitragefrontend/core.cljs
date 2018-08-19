(ns cryptoarbitragefrontend.core
   (:require-macros [cljs.core.async.macros :refer [go]]
                    [secretary.core :refer [defroute]])
   (:import goog.history.Html5History)
  (:require
    [reagent.core :as reagent]
    [secretary.core :as secretary]
    [goog.events :as events]
    [goog.history.EventType :as EventType]
    [cryptoarbitragefrontend.pages :as pages]
    [re-com.box :as box]
    [cryptoarbitragefrontend.comp_general :as comp_general]))

(enable-console-print!)

(def page-state (reagent/atom {}))
(defmulti current-page #(@page-state :page))

(defn hook-browser-navigation! []
  (doto (Html5History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
            (swap! page-state assoc :page :home))

  (defroute "/home" []
            (swap! page-state assoc :page :home-logged))

  (defroute "/me" []
            (swap! page-state assoc :page :profile))

  (defroute "/blog" []
            (swap! page-state assoc :page :blog))

  (hook-browser-navigation!))

(defmethod current-page :home []

  (if (comp_general/is_user_logged)
    [pages/home-logged]
    [pages/home]
    )
  )

(defmethod current-page :home-logged []
  (if (comp_general/is_user_logged)
    [pages/home-logged]
    [pages/home]
    )
  )

(defmethod current-page :profile []
  (if (comp_general/is_user_logged)
    [pages/my-profile]
    [pages/home]
    )
  )

(defmethod current-page :blog []
  (if (comp_general/is_user_logged)
    [pages/blog]
    [pages/home]
    )
  )

(defmethod current-page :default []
  [pages/not-found])

(app-routes)

;; -------------------------
(reagent/render [current-page] (js/document.querySelector "#app"))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

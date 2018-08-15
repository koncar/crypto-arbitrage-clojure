(ns cryptoarbitragefrontend.core
   (:require-macros [cljs.core.async.macros :refer [go]]
                    [secretary.core :refer [defroute]])
   (:import goog.history.Html5History)
    (:require
      [cljs-http.client :as client]
      [reagent.core :as reagent]
      [secretary.core :as secretary]
      [goog.events :as events]
      [goog.history.EventType :as EventType]
      [cryptoarbitragefrontend.pages :as pages]
      [cryptoarbitragefrontend.http-client :as cli]
      [re-com.box :as box]
      ))

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

  (defroute "/about" []
            (swap! page-state assoc :page :about))

  (hook-browser-navigation!))

(defmethod current-page :home []
  [pages/home])

(defmethod current-page :about []
  [pages/about])

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

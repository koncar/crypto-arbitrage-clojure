(ns cryptoarbitrage.core
  (:gen-class)
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [cryptoarbitrage.handler :as handler])

  )
;; :as -> require entire name space
;; :refer -> takes only symbols and transfers it into working namespace
(defn -main
  [& args]
  (println "Server started"))

(defn app []
  (routes

    (GET "/get-me/:id" [id] (handler/get_me id))
    (GET "/get-countries" [] (handler/get_countries))
    (POST "/populate-countries" req (handler/populate_countries req))
    (POST "/populate-exchanges" req (handler/populate_exchanges req))
    (POST "/populate-pairs" req (handler/populate_pairs req))
    (POST "/update-me/:id" [id :as req] (handler/update_me id req))
    (POST "/register" req (handler/register req))
    (POST "/login" req (handler/login req))
    (POST "/upload-profile-picture/:id" [id :as req] (handler/upload-profile-picture id req))
    (route/not-found (handler/not_found))
    )
  )

(defn stop-server
  [server]
  (server :timeout 100))

(defn init-server
  []
  (server/run-server (app) {:port 8080}))


(def server (init-server))

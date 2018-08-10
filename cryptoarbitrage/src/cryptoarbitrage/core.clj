(ns cryptoarbitrage.core
  (:gen-class)
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [cryptoarbitrage.handler :as handler])

  )
;; :as -> require entire name space
;; :refer -> takes only symbols and transfers it into working namespace


(defn app []
  (routes
    (GET "/get-me/:id" [id] (handler/get_me id))
    (GET "/countries" [] (handler/get_countries))
    (POST "/populate-countries" req (handler/populate_countries req))
    (POST "/update-me/:id" [id :as req] (handler/update_me id req))
    (POST "/register" req (handler/register req))
    (POST "/login" req (handler/login req))
    (POST "/upload-profile-picture/:id" [id :as req] (handler/upload-profile-picture id req))
    (route/not-found (handler/not_found))
    ))

(defn stop-server
  [server]
  (server :timeout 100))

(defn init-server
  []
  (server/run-server (app) {:port 8080}))

;;test
(defroutes approutes
           (GET "/" [] "<h1>Hello World</h1>")
           (GET "/:user-name" [user-name :as request]
             (format "<h1>Hello World %s </h1>" user-name)
                )
           (route/not-found "<h1>Page not found</h1>")
           )


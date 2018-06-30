(ns cryptoarbitrage.core
  (:gen-class)
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route])

  )
;; :as -> require entire name space
;; :refer -> takes only symbols and transfers it into working namespace

(defn app []
  (routes
    (GET "/" [:as req]
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    "Hello local route"})
    (GET "/:user-name" [user-name :as request]
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (format "Hello %s" user-name)}
      )
    )
  )

(defroutes approutes
           (GET "/" [] "<h1>Hello World</h1>")
           (GET "/:user-name" [user-name :as request]
             (format "<h1>Hello World %s </h1>" user-name)
             )
           (route/not-found "<h1>Page not found</h1>")
           )
(defn handler
  [request]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    "{}"}
  )

(defn init-server
  []
  (server/run-server (app) {:port 8080}))

(defn stop-server
  [server]
  (server :timeout 100))
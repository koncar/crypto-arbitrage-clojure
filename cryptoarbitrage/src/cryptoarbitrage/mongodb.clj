(ns cryptoarbitrage.mongodb
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.credentials :as mcr]
            [clojure.data.json :as json]
           )

  )

;;mongod --auth -> connect with credentials
;(let [admin-db "admin"
;      u "username"
;      p (.toCharArray "password")
;      cred (mcr/create u admin-db p)
;      host "127.0.0.1"]
;  (def conn (mg/connect-with-credentials host cred)))

;;mongod -> connect simple
(def conn (mg/connect))
(def db (mg/get-db conn "monger-test"))

(defn insert
  [col document]
  (mc/insert db col document))

;;query->{:key :value, :key1 :value}
(defn find
  [collection query]
  (mc/find-one-as-map db collection query)
  )

(defn find-all-with-query [collection query]
  (mc/find-maps db collection query)
  )

(defn find-all
  [collection & query]
  (mc/find-maps db collection query)
  )

(defn remove
  [collection query]
  (mc/remove db collection query)
  )

(defn update
  [collection query newdocument]
  (mc/update db collection query newdocument)
  )

(defn drop
  [collection]
  (mc/drop db collection)
  )

(defn dissconnect
  []
  (mg/disconnect conn))
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

(defn find
  [collection query]
  (let [coll collection]
    (mc/find-one-as-map db coll query)
    )
  )

(defn find-all
  [collection query]
  (let [coll collection]
    (mc/find coll query)
    )
  )

(defn remove
  [collection query]
  (let [coll collection]
    (mc/remove db coll query)
    )
  )

(defn update
  [collection query newdocument]
  (let [coll collection]
    (mc/update db coll query newdocument)
    )
  )
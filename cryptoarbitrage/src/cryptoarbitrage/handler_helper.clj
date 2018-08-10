(ns cryptoarbitrage.handler-helper
  (:require [clojure.data.json :as json]))

(defn form-response
  [status-code body & content-type]
  {:status  status-code
   :headers (if content-type {"Content-Type" content-type} {"Content-Type" "application/json"})
   :body   body}
  )
(defn form-json_body
  [data]
  (str (json/json-str data))
  )
(defn read-body
  [req]
  (json/read-json (slurp (get-in req [:body]))))

(defn form-success
  [body]
  (form-response 200 (form-json_body body))
  )
(defn form-fail
  [body]
  (form-response 400 (form-json_body body))
  )

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn slurp-bytes
  [x]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (clojure.java.io/copy (clojure.java.io/input-stream x) out)
    (.toByteArray out)))
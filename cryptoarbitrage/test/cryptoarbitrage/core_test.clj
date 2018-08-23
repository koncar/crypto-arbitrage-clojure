(ns cryptoarbitrage.core-test
  (:require [clojure.test :refer :all]
            [cryptoarbitrage.core :refer :all]
            [clj-http.client :as client]
            [cryptoarbitrage.handler-helper :as helper]))

(deftest test-self-as-API-end-point
  (testing "If i fail, core failed to start as API end point"
    (start-server)
    (let [response (:body (client/get "http://localhost:8080/get-pairs"))]
      (stop-server server)
      (is (not (nil? response)))
      )
    )
  )


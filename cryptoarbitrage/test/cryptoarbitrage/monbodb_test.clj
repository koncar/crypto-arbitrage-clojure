(ns cryptoarbitrage.monbodb-test
  (:require [clojure.test :refer :all]
            [cryptoarbitrage.mongodb :refer :all]
            [cryptoarbitrage.mongodb :as mongo]))

(deftest db-test
  (testing "I check if database is working"
    (insert "test" {:_id "this is test document"})
    (let [document_after_inserting (find "test" {:_id "this is test document"})]
      (drop "test")
      (is (not (nil? document_after_inserting)))
      )
    ))
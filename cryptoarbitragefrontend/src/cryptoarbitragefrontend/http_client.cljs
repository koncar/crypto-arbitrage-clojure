(ns cryptoarbitragefrontend.http-client
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as client]
            [cljs.core.async :refer [<!]])
  )

(defn get-resource
  [url callback]
  (go (let [response (<! (client/get url
                                     {:with-credentials? false}))]
         (callback (:body response)))
      )
  (println "get called: " url)
  )

(defn post-resource
  [url body]
  ;(client/post url
  ;             {
  ;              :json-params body
  ;              :with-credentials? false
  ;              }
  ;             )
  (go (let [response (<! (client/post "http://localhost:8080/register"
                                      {:json-params body}
                                   ))]
        (println (:body response))
        )
      )
  (println "post called: " url)
  (println "body" body)
  )


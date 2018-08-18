(ns cryptoarbitragefrontend.http-client
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as client]
            [cljs.core.async :refer [<!]]
            )
  )

(defn get-resource
  [url success-callback fail-callback]
  (go (let [response (<! (client/get url
                                     {:with-credentials? false}))]
        (println (:body response))
        (if (= (:status response) 200)
          (success-callback (:body response))
          (fail-callback (:body response))
          )
        )
      )
  (println "get called: " url)
  )

(defn post-resource
  [url body success-callback fail-callback]
  (go (let [response (<! (client/post url
                                      {:with-credentials? false
                                       :body              (js/JSON.stringify (clj->js body))
                                       }
                                      )
                         )
            ]
        (println (:body response))
        (if (= (:status response) 200)
          (success-callback (:body response))
          (fail-callback (:body response))
          )
        )
        )
  (println "post called: " url)
  (println "body" body)
  )

(defn post-image
  [url body success-callback fail-callback]
  (go (let [response (<! (client/post url
                                      {:with-credentials? false
                                       :body              body
                                       }
                                      )
                         )
            ]
        (println (:body response))
        (if (= (:status response) 200)
          (success-callback (:body response))
          (fail-callback (:body response))
          )
        )
      )
  (println "post called: " url)
  (println "body" body)
  )




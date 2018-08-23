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
    (GET "/get-countries" [] (handler/get_countries))
    (GET "/get-pairs" [] (handler/get_pairs))
    (GET "/get-price-on-exchanges" [] (handler/get-price-on-exchanges))
    (GET "/get-inner-matrix/:a/:b" [a b :as req] (handler/inner-matrix a b))
    (GET "/get-ascending-price/:a/:b" [a b :as req] (handler/ascending-price a b))
    (GET "/get-descending-price/:a/:b" [a b :as req] (handler/descending-price a b))
    (POST "/populate-countries" req (handler/populate_countries req))
    (POST "/populate-exchanges" req (handler/populate_exchanges req))
    (POST "/populate-pairs" req (handler/populate_pairs req))
    (POST "/update-me/:id" [id :as req] (handler/update_me id req))
    (POST "/change-password/:id" [id :as req] (handler/change-password id req))
    (POST "/register" req (handler/register req))
    (POST "/save-blog-post/:id" [id :as req] (handler/save_blog_post id req))
    (POST "/blog-post-thumbs-up/:id" [id :as req] (handler/blog_post_thumbs_up id req))
    (POST "/blog-post-thumbs-down/:id" [id :as req] (handler/blog_post_thumbs_down id req))
    (POST "/email-me" req (handler/email-me req))
    (GET "/get-blog-posts" req (handler/get_blog_posts))
    (GET "/get-blog-posts-sorted" req (handler/get_blog_posts_sorted))
    (GET "/get-my-blog-posts/:id" [id :as req] (handler/get_my_blog_posts id))
    (POST "/login" req (handler/login req))
    (POST "/upload-profile-picture/:id" [id :as req] (handler/upload-profile-picture id req))
    (route/resources "/")
    (route/not-found (handler/not_found))
    )
  )

(defn stop-server
  [server]
  (server :timeout 100))

(defn init-server
  []
  (server/run-server (app) {:port 8080}))

(defonce server nil)

(defn start-server []
  (if (nil? server)
    (def server (init-server))
    (do
      (println "Server stoped")
      (stop-server server)
        (def server (init-server))
        )
    )
  (println "Server started")
  )

(start-server)

(defn -main
  [& args]
  (start-server)
  )


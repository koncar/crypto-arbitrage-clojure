(ns cryptoarbitragefrontend.comp_blog
  (:require [reagent.core :as reagent]
            [cryptoarbitragefrontend.http-client :as client]
            [cryptoarbitragefrontend.comp_alert-messages :as comp_messages]
            [cryptoarbitragefrontend.comp_profile :as comp_profile]))

(defonce blog-posts (reagent/atom (sorted-map)))
(defonce sorted-posts (reagent/atom (sorted-map)))
(defonce counter (reagent/atom 0))

(defn add-post [title description text date thumbs user]
  (let [id (swap! counter inc)]
    (swap! blog-posts assoc id {:title title :description description :text text :date date :thumbs thumbs :user user})
    id
    ))

(defn add-post-link [title description text date thumbs user]
  (let [id (swap! counter inc)]
    (swap! sorted-posts assoc id {:title title :description description :text text :date date :thumbs thumbs :user user})
    id
    ))

(defn post []
  (fn [{:keys [title description text date thumbs user]}]
    [:div.card.mt-5.p-2
     [:h2 title]
     [:h5 (str description ", " date)]
     [:p text]
     [:div.row.ml-1
      [:label.mr-1 "Posted by: "]
      [:a {:href     (str "#/me?id=" (:_id user))
           :on-click #(comp_profile/open_profile user false)} (:username user)]
      [:span.glyphicon.glyphicon-thumbs-up.ml-2.mr-1]
      [:p thumbs]
      ]
     ]
    ))


(defn post-link []
  (fn [{:keys [title description text date thumbs user]}]
    [:div.card.mt-1.p-2
     [:h2 title]
     [:h6 (str description ", " date)]
     [:div.row.ml-1
      [:label.mr-1 "Posted by: "]
      [:a {:href     (str "#/me?id=" (:_id user))
           :on-click #(comp_profile/open_profile user false)} (:username user) ]
       [:span.glyphicon.glyphicon-thumbs-up.ml-2.mr-1]
      [:p thumbs]
      ]


     ]
    ))

(defn posts-holder []
  (let [filed (reagent/atom :all)]
    (let [items (doto (vals @blog-posts))
          sorted-items (doto (vals @sorted-posts))]
      [:div.container
       {:style {:margin-top 100}}
       [:div.row.mt-5
        [:div.leftcolumn.col-xl-8.col-md-10.col-xs-12
         [:h1 "Blog posts"]
         (when (-> items count pos?)
           [:div
            (for [todo (filter (case @filed
                                 :all identity) items)]
              ^{:key (:id todo)} [post todo])
            ]
           )
         ]
        [:div.rightcolumn.col-xl-4.col-md-2.col-xs-12
        [:h1 "Popular posts"]
         (when (-> sorted-items count pos?)
           [:div
            (for [todo (filter (case @filed
                                 :all identity) sorted-items)]
              ^{:key (:id todo)} [post-link todo])
            ]
           )
         ]
        ]]
      )))

(defn success-get-posts [body]
  (let [sorted (reverse (sort-by :thumbs body))]
    (loop [x 0]
      (when (if (< (count body) 5)  (< x (count body)) (< x 5))
        (do
          (println x)
          (println (nth sorted x))
            (add-post-link (:title (nth sorted x)) (:description (nth sorted x)) (:text (nth sorted x)) (:date (nth sorted x)) (:thumbs (nth sorted x)) (:user (nth sorted x))))
        (recur (+ x 1))
        )
      ))
  (loop [x 0]
    (when (< x (count body))
      (do (println (nth body x))
        (add-post (:title (nth body x)) (:description (nth body x)) (:text (nth body x)) (:date (nth body x)) (:thumbs (nth body x)) (:user (nth body x))))
      (recur (+ x 1))
      )
    )
  (println "success blog posts")
  )

(defn fill-posts []
  (reset! blog-posts (sorted-map))
  (reset! sorted-posts (sorted-map))
  (client/get-resource "http://localhost:8080/get-blog-posts" success-get-posts comp_messages/fail-message-from-response)
  )

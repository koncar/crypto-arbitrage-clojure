(ns cryptoarbitragefrontend.comp_blog
  (:require [reagent.core :as reagent]
            [cryptoarbitragefrontend.http-client :as client]
            [cryptoarbitragefrontend.comp_alert-messages :as comp_messages]
            [cryptoarbitragefrontend.comp_profile :as comp_profile]
            [cryptoarbitragefrontend.comp_general :as comp_general]
            [hickory.core :as hick]
            )
)

(defonce blog-posts (reagent/atom (sorted-map)))
(defonce sorted-posts (reagent/atom (sorted-map)))
(defonce counter (reagent/atom 0))

(defn add-post [post_id title description text date thumbs user]
  (let [id (swap! counter inc)]
    (swap! blog-posts assoc id {:id post_id :title title :description description :text text :date date :thumbs thumbs :user user})
    id
    ))

(defn add-post-link [post_id title description text date thumbs user]
  (let [id (swap! counter inc)]
    (swap! sorted-posts assoc id {:id post_id :title title :description description :text text :date date :thumbs thumbs :user user})
    id
    ))


(defonce post-that-is-opened (reagent/atom nil))
(defonce opened-post-thumbs (reagent/atom nil))
(defn success-thumbs-up [body]
  (reset! opened-post-thumbs (+ 1 @opened-post-thumbs))
  (comp_messages/success-message-from-response body)
  )
(defn success-thumbs-down [body]
  (reset! opened-post-thumbs (- @opened-post-thumbs 1))
  (comp_messages/success-message-from-response body)
  )
(defn thumbs-up [id]
  (client/post-resource (str "http://localhost:8080/blog-post-thumbs-up/" id) {:id (:_id @comp_general/user)} success-thumbs-up comp_messages/fail-message-from-response)
  )
(defn thumbs-down [id]
  (client/post-resource (str "http://localhost:8080/blog-post-thumbs-down/" id) {:id (:_id @comp_general/user)} success-thumbs-down comp_messages/fail-message-from-response)
  )

(defn post-full []
  (fn [{:keys [id title description text date thumbs user]}]
    [:div.card.mt-5.p-2.col-xl-12.full-post

     [:a {:href (str "#/post" id)}
      [:h2 title]
      ]
     [:h5 (str description ", " date)]
     (map hick/as-hiccup (hick/parse-fragment text))

     [:div.row.ml-1
      [:label.mr-1 "Posted by: "]
      [:a {:href     (str "#/me?id=" (:_id user))
           :on-click #(comp_profile/open_profile user false)} (:username user)]
      [:span.glyphicon.glyphicon-thumbs-up.ml-2.mr-1]
      [:p @opened-post-thumbs]
      ]
     [:h6 "Vote:"]
     [:div.row.ml-1
      [:a.glyphicon-thumbs-up.mr-2 {
                                    :on-click #(thumbs-up id)}]
      [:a.glyphicon-thumbs-down {
                                 :on-click #(thumbs-down id)}]
      ]]

    ))

(defn opened-post []
  [:div.container {:style {:margin-top 100}}
   [:div.row.mt-5.col-xl-12
    [:div.col-xl-12
     ^{:key (:id post-that-is-opened)} [post-full @post-that-is-opened]
     ]
    ]
   ]
  )

(defn open-post [id title description text date thumbs user]
  (reset! opened-post-thumbs thumbs)
  (reset! post-that-is-opened {:id id :title title :description description :text text :date date :thumbs thumbs :user user} )
  )

(defn post []
  (fn [{:keys [id title description text date thumbs user]}]
    [:div.card.mt-5.p-2
     [:a {:href (str "#/post/" id)
          :on-click #(open-post id title description text date thumbs user)}
      [:h2 title]]
     [:h5 (str description ", " date)]
     [:div
      (map hick/as-hiccup (hick/parse-fragment text))]
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
  (fn [{:keys [id title description text date thumbs user]}]
    [:div.card.mt-1.p-2
     [:a {:href     (str "#/post/" id)
          :on-click #(open-post id title description text date thumbs user)
          } [:h2 title]]
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
            (for [item (filter (case @filed
                                 :all identity) items)]
              ^{:key (:id item)} [post item])
            ]
           )
         ]
        [:div.rightcolumn.col-xl-4.col-md-2.col-xs-12
        [:h1 "Popular posts"]
         (when (-> sorted-items count pos?)
           [:div
            (for [item (filter (case @filed
                                 :all identity) sorted-items)]
              ^{:key (:id item)} [post-link item])
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
          ;(println x)
          (println (hick/as-hiccup (hick/parse (:text (nth sorted x)))))
          ;(println (nth sorted x))
            (add-post-link (:_id (nth sorted x)) (:title (nth sorted x)) (:description (nth sorted x)) (:text (nth sorted x)) (:date (nth sorted x)) (:thumbs (nth sorted x)) (:user (nth sorted x))))
        (recur (+ x 1))
        )
      ))
  (loop [x 0]
    (when (< x (count body))
      (do
        ;(println (nth body x))
        (add-post (:_id (nth body x)) (:title (nth body x)) (:description (nth body x)) (:text (nth body x)) (:date (nth body x)) (:thumbs (nth body x)) (:user (nth body x))))
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

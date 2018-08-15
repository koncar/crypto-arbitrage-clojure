(ns cryptoarbitragefrontend.syntax
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core :refer [defroute]])
  (:import goog.history.Html5History)
  (:require
    [cljs-http.client :as client]
    [reagent.core :as reagent]
    [secretary.core :as secretary]
    [goog.events :as events]
    [goog.history.EventType :as EventType]
    ))

(enable-console-print!)

(println "This text is printed from src/cryptoarbitragefrontend/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defonce todos (reagent/atom (sorted-map)))

(defonce counter (reagent/atom 0))

(defn add-todo [text]
  (let [id (swap! counter inc)]
    (swap! todos assoc id {:id id :title text :done false})))

(defn toggle [id] (swap! todos update-in [id :done] not))
(defn save [id title] (swap! todos assoc-in [id :title] title))
(defn delete [id] (swap! todos dissoc id))

(defn mmap [m f a] (->> m (f a) (into (empty m))))
(defn complete-all [v] (swap! todos mmap map #(assoc-in % [1 :done] v)))
(defn clear-done [] (swap! todos mmap remove #(get-in % [1 :done])))

(defonce init (do
                (add-todo "Rename Cloact to Reagent")
                (add-todo "Add undo demo")
                (add-todo "Make all rendering async")
                (add-todo "Allow any arguments to component functions")
                (complete-all true)))

(defn todo-input [{:keys [title on-save on-stop]}]
  (let [val (reagent/atom title)
        stop #(do (reset! val "")
                  (if on-stop (on-stop)))
        save #(let [v (-> @val str clojure.string/trim)]
                (if-not (empty? v) (on-save v))
                (stop))]
    (fn [{:keys [id class placeholder]}]
      [:input {:type "text" :value @val
               :id id :class class :placeholder placeholder
               :on-blur save
               :on-change #(reset! val (-> % .-target .-value))
               :on-key-down #(case (.-which %)
                               13 (save)
                               27 (stop)
                               nil)}])))

(def todo-edit (with-meta todo-input
                          {:component-did-mount #(.focus (reagent/dom-node %))}))

(defn todo-stats [{:keys [filt active done]}]
  (let [props-for (fn [name]
                    {:class (if (= name @filt) "selected")
                     :on-click #(reset! filt name)})]
    [:div
     [:span#todo-count
      [:strong active] " " (case active 1 "item" "items") " left"]
     [:ul#filters
      [:li [:a (props-for :all) "All"]]
      [:li [:a (props-for :active) "Active"]]
      [:li [:a (props-for :done) "Completed"]]]
     (when (pos? done)
       [:button#clear-completed {:on-click clear-done}
        "Clear completed " done])]))

(defn todo-item []
  (let [editing (reagent/atom false)]
    (fn [{:keys [id done title]}]
      [:li {:class (str (if done "completed ")
                        (if @editing "editing"))}
       [:div.view
        [:input.toggle {:type "checkbox" :checked done
                        :on-change #(toggle id)}]
        [:label {:on-double-click #(reset! editing true)} title]
        [:button.destroy {:on-click #(delete id)}]]
       (when @editing
         [todo-edit {:class "edit" :title title
                     :on-save #(save id %)
                     :on-stop #(reset! editing false)}])])))

(defn todo-app [props]
  (let [filt (reagent/atom :all)]
    (fn []
      (let [items (vals @todos)
            done (->> items (filter :done) count)
            active (- (count items) done)]
        [:div
         [:section#todoapp
          [:header#header
           [:h1 "todos"]
           [todo-input {:id "new-todo"
                        :placeholder "What needs to be done?"
                        :on-save add-todo}]]
          (when (-> items count pos?)
            [:div
             [:section#main
              [:input#toggle-all {:type "checkbox" :checked (zero? active)
                                  :on-change #(complete-all (pos? active))}]
              [:label {:for "toggle-all"} "Mark all as complete"]
              [:ul#todo-list
               (for [todo (filter (case @filt
                                    :active (complement :done)
                                    :done :done
                                    :all identity) items)]
                 ^{:key (:id todo)} [todo-item todo])]]
             [:footer#footer
              [todo-stats {:active active :done done :filt filt}]]])]
         [:footer#info
          [:p "Double-click to edit a todo"]]]))))
(defn simple-component []
  [:div
   [:p "I am a component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red "] "text."]])

(defn atom-input [value]
  [:input.textclass {:type "text"
                     :value @value
                     :on-change #(reset! value (-> % .-target .-value))}])

(defn alert-message
  [message]
  (reagent/create-element "div"
                          #js{:className "alert alert-success"}
                          (reagent/create-element "strong"
                                                  #js{}
                                                  message)
                          )
  )
(defn make-request
  []
  (fn [] (do (go (let [response (<! (client/get "http://localhost:8080/get-countries"
                                                {:with-credentials? false
                                                 :query-params      {"since" 135}}))]
                   (prn (:status response))
                   (prn (:body response))))
             (alert-message "Message"))))

(defn shared-state []
  (let [val (reagent/atom "foo")]
    [:div
     [:p "The value is now: " @val]
     [:p "Change it here: " [atom-input val]]
     [:input {:type "button" :value "Click" :on-click (make-request)}
      ]
     ]))

(defn shared-state1 []
  (let [val (reagent/atom "foo")]
    (fn []
      [:div
       [:p "The value is now: " @val]
       [:p "Change it here: " [atom-input val]]
       [:input {:type "button" :value "Click" :on-click (make-request)}
        ]
       ])))


(def page-state (reagent/atom {}))

(defn hook-browser-navigation! []
  (doto (Html5History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
            (swap! page-state assoc :page :home))

  (defroute "/about" []
            (swap! page-state assoc :page :about))

  (defroute "/todo" []
            (swap! page-state assoc :page :todo))


  (hook-browser-navigation!))

(defn home []
  [:div [:h1 "Home Page"]
   [:a {:href "#/about"} "about page"]
   [:a {:href "#/todo"} "to do page"]
   (simple-component)
   (alert-message "Message")])

(defn about []
  [:div [:h1 "About Page"]
   [:a {:href "#/"} "home page"]
   [:a {:href "#/todo"} "to do page"]])

(defn todo []
  [:div [:h1 "To do Page"]
   [:a {:href "#/home"} "home page"]
   [:a {:href "#/about"} "about page"]]
  (todo-app [])
  )

(defmulti current-page #(@page-state :page))

(defmethod current-page :home []
  [home])
(defmethod current-page :about []
  [about])
(defmethod current-page :todo []
  [todo])

(defmethod current-page :default []
  [:div ])

(app-routes)

;; -------------------------
(reagent/render [current-page] (js/document.querySelector "#app"))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

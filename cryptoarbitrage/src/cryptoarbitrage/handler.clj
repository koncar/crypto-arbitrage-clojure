(ns cryptoarbitrage.handler
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [cryptoarbitrage.mongodb :as mongo]
            [cryptoarbitrage.handler-helper :as helper]
            [clj-http.client :as client]
            [clojure.java.io :as io]
            ))

(defn login [req]
  (let
    [json_body (doto (helper/read-body req))
     email (doto (:email json_body))
     password (doto (:password json_body))
     ]
    (if (and (not (str/blank? email)) (not (str/blank? password)))
      (let [doc_user (doto(mongo/find "users" {:email email}))
            doc_password (doto (:password doc_user))]
        (if doc_user
          (if (= doc_password password)
            (helper/form-success {:message "User successfully logged in"})
            (helper/form-fail  {:message "Wrong password"}))
          (helper/form-fail {:message "User not found"})))
      (helper/form-fail {:message "Email and password cannot be empty"})
      )
    )
  )

(defn register [req]
  (let
    [json_body (doto (helper/read-body req))
     email (doto(:email json_body))
     password (doto (:password json_body))
     name (doto (:name json_body))
     username (doto (:username json_body))
     country (doto (:country json_body))
     ]
    (if (and (not(str/blank? email)) (not(str/blank? password)) (not (str/blank? name)) (not (str/blank? username))  (not (nil? country)))
      (if (mongo/find "countries" {:_id (:_id country)})
        (if (mongo/find "users" {:email email})
         (helper/form-fail {:message "That email is already used"})
         (do (mongo/insert "users" (assoc json_body :_id (helper/uuid)))
             (helper/form-success {:message "User successfully registered"})))
        (helper/form-fail {:message "That country doesn't exist"}))
      (helper/form-fail {:message "Email, password, username, country and name cannot be empty"}))
    )
)
(defn get_me [id]
  (if (and (not (str/blank? id)))
    (let [doc_user (mongo/find "users" {:_id id})]
      (if doc_user
        (helper/form-success (apply dissoc doc_user [:_id]))
        (helper/form-fail {:message "User doesn't exist"})))
    (helper/form-fail {:message "Email cannot be empty"})
    )
  )
(defn update_me [id req]
     (let
       [json_body (doto (helper/read-body req))
        email (doto(:email json_body))
        password (doto (:password json_body))
        name (doto (:name json_body))
        username (doto (:username json_body))
        country (doto (:country json_body))
        ]
       (if (and (not(str/blank? email)) (not(str/blank? password)) (not (str/blank? name)) (not (str/blank? username)) (not (nil? country)))
         (if (mongo/find "users" {:_id id})
           (if (not (nil? (mongo/find "countries" {:_id (:_id country)})))
             (do (mongo/update "users" {:_id id} (assoc json_body :_id id))
                (helper/form-success {:message "User successfully updated"}))
             (helper/form-fail {:message "That country doesn't exist"}))
           (helper/form-fail {:message "That id is not found"}))
         (helper/form-fail {:message "Email, password, username, country and name cannot be empty"}))
       )
     )


(defn upload-profile-picture
  [id req]
  (if (not (nil? (mongo/find "users" {:_id id})))
    (if (and (not (nil? (:body req))))
      (let [picture (helper/slurp-bytes (:body req))]
       (if (and (not (nil? picture)) (not (<= (count picture) 17)))
         (do
           (when (not (.exists (io/file "photos")))
             (.mkdir (io/file "photos")))
           (with-open [w (io/output-stream (str "photos/" id ".jpg"))]
             (.write w picture))
           (helper/form-success {:message "Successfully uploaded profile picture"})
           )
         (helper/form-fail {:message "Picture cannot be empty"})
         ))
      (helper/form-fail {:message "Picture cannot be null"}))
    (helper/form-fail {:message "User not found"})))

(defn get_countries []
  (helper/form-success (mongo/find-all "countries"))
  )

(defn populate_countries [req]
  (let [json_body (doto (helper/read-body req))
        password (doto (:password json_body))]
    (if (= password "admin")
     (do
       (mongo/drop "countries")
       (let [all_countries (json/read-json (:body (client/get "https://restcountries.eu/rest/v2/all")))]
         (loop [x 0]
           (when (< x (count all_countries))
             (mongo/insert "countries" (assoc (select-keys (get all_countries x) [:name :alpha2Code :alpha3Code]) :_id (:alpha2Code (get all_countries x))))
             (recur (+ x 1))))
         )
       (helper/form-success {:message "Successfully populated countries"})
       )
     (helper/form-fail {:message "Wrong password"})
     ))
  )

(defn not_found []
  (helper/form-response 404 (helper/form-json_body {:message "requested resource is not found"})))


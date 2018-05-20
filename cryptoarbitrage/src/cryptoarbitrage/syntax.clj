(ns cryptoarbitrage.syntax
  (:require [clojure.string :as str]))

(def _long 10)
(def _double 12.3)
(def _bool false)
(type _bool)
(false? _bool)
(pos? 10)
(even? 9)
(odd? 10)
(number? "asdf")
(float? 1.2)
(zero? (- 1 1))

(defn string_formating
  "JAVA DOC :D
  & args parametars not required
  args parametars required"

  [& args]

  (def _string1 "Hello")
  (def _long1 15)
  (def _double1 15.1234142)
  (format "This is string %s" _string1)
  (format "5 spaces and %5d" _long1)
  (format "Leading zeros %04d" _long1)
  (format "%-4d left justified" _long1)
  (format "right justified %+4d" _long1)
  (format "2 decimals %.2f" _double1)
  )


(defn using_strings
  []
  (def _string2 "This is 2nd string")
  (str/blank? _string2)
  (str/includes? _string2 "bla")
  (str/index-of _string2 "is")
  (str/split _string2 #" ")
  (str/split _string2 #"\d")
  (str/join " " ["skupljamo" "array" "u string veliki"])
  (str/replace "Hej zameni 123" #"123" "456")
  (str/upper-case _string2)
  (str/lower-case _string2)

  )

(defn lists
  []
  (println (list "Dog" 1 3.4 true))
  (println (rest (list 1 2 3)))
  (println (first (list 1 2 3)))
  (println (nth (list 1 2 3) 1))
  (println (list* 1 2 [3 4]))
  (println (cons 3 (list 1 2)))

  )

(defn sets
  []
  (println (contains? (set '(1 1 3)) 1))
  (println (disj (set '(1 1 3)) 3))
  )

(defn vectors
  []
  (println (get (vector 3 2) 1))
  (println (conj (vector 3 2) 1))
  (println (pop (vector 3 2)))
  (println (subvec (vector 1 2 3 4) 1 3))
  )

(defn maps
  []
  (println (hash-map "Name", "Voste" "Age" 25))
  (println (sorted-map 3 42 2 "Koncar" 1 "Voste"))
  (println (get (hash-map "Name" "Voste" "Age" 25) "Age"))
  (println (contains? (hash-map "Name" "Voste" "Age" 25) "Age"))
  (println (keys (hash-map "Name" "Voste" "Age" 25)))
  (println (vals (hash-map "Name" "Voste" "Age" 25)))
  (println (merge-with + (hash-map "Name" "Voste") (hash-map "Age" 25)))
  )

(defn atom-ex
  "dodaje watchera koji prati menjanje
   promenljive atomEx i svaki put kad se
   to desi ispisuje prethodnu i trenutnu
   vrednost"
  [x]

  (def atomEx (atom x))

  (add-watch atomEx :watcher
             (fn [key atom old-state new-state]
               (println "atomEx change from " old-state " to " new-state)))

  (println "1st x" @atomEx)
  (reset! atomEx 10)
  (println "2nd x" @atomEx)
  (swap! atomEx inc)
  (println "Increment x" @atomEx)
  )

(defn agent-ex
  "changing variable"
  []

  (def tickets-sold (agent 0))
  (send tickets-sold + 15)
  (println "Tickets " @tickets-sold)
  (send tickets-sold + 10)
  (await-for 100 tickets-sold)
  (println "Tickets" @tickets-sold)

  (shutdown-agents)
  )

(defn math-stuff
  []
  (println (+ 1 2 3))
  (println (- 5 3 2))
  (println (* 6 5))
  (println (/ 15 2))
  (println (mod 12 5))

  (println (inc 5))
  (println (dec 5))

  (println(Math/abs -10)) ;; Absolute Value
  (println(Math/cbrt 8)) ;; Cube Root
  (println(Math/sqrt 4)) ;; Square Root
  (println(Math/ceil 4.5)) ;; Round up
  (println(Math/floor 4.5)) ;; Round down
  (println(Math/exp 1)) ;; e to the power of 1
  (println(Math/hypot 2 2)) ;; sqrt(x^2 + y^2)
  (println(Math/log 2.71828)) ;; Natural logarithm
  (println(Math/log10 100)) ;; Base 10 log
  (println(Math/max 1 5))
  (println(Math/min 1 5))
  (println(Math/pow 2 10)) ;; Power

  (println (rand-int 20))
  (println (reduce + [1 2 3]))
  )
;;methods
(defn hello

  [name]
  (str "Hello " name)

  )
(defn get-sum
  [x y]
  (+ x y)
  )

(defn hello-all
  [& names]
  (map hello names)
  )

;; conditions
(defn can-vote
  [age]
  (if (>= age 18)
    (println "no")
    (println "yes"))
  )
(defn can-do-more
  [age]
  (if (>= age 18)
    (do (println "drive")
        (println "vote"))
    (println "no vote"))
  )

(defn when-ex
  [tof]
  (when tof
    (println "1st")
    (println "2nd")
    )
  )
(defn what-grade
  [n]
  (cond
    (< n 5) (println "Preschool")
    (= n 5) (println "Kindergarden")
    (and (> n 5) (<= n 18)) (println "Go to grade" (- n 5))
    :else "Go to collage"
    )
  )
;;loops
(defn one-to-x
  [x]
  (def i (atom 1))
  (while (<= @i x)
    (do
      (println @i)
      (swap! i inc)
      )
    ))


(defn dbl-to-x
  [x]
  (dotimes [i x]
    (println (* i 2)))
  )

(defn triple-to-x
  [x y]
  (loop [i x]
    (when (< i y)
      (println (* i 3))
      (recur (+ i 1))
      )
    )
  )

(defn print-list
  [& nums]
  (doseq [x nums]
    (println x)
    )
  )
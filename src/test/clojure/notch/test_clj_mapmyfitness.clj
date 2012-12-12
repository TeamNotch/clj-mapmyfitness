(ns notch.test-clj-mapmyfitness
  (:use clojure.set)
  (:use clojure.tools.logging)
  (:require [clojure.data.json :as json])
  (:require [clj-http.client :as http])
  (:require [clojure.java.io :as io])
  (:require [clojure.java.browse])
  (:use clojure.test)
  (:require [clojure.string :as str])
  (:use notch.clj-mapmyfitness :reload))

(def access_token (:test_access_token properties))

;;;Get a request token
;(try
;  (def request_token (get-request-token "http://localhost"))
;  (catch Exception e (error e)))
;
;;;Authenticate in browser
;(clojure.java.browse/browse-url (get-auth-uri request_token "http://localhost"))
;
;;;Get the access token
;(try
;  (def access_token (get-access-token request_token))
;  (catch Exception e (error e)))



(deftest test-basics
;(do

  ;;Test getting user profile
  (is (not (str/blank? (:username (get-user-info access_token)))))

  ;;Test listing workouts
  (is (not-empty (get-workouts access_token)))

  ;;test getting a detailed workout
  (is (not (str/blank?
             (->> (get-workouts access_token)
               first
               (:workout_id)
               (get-workouts-full access_token)
               :route_id ))))

  ;;test getting a route
  (is (->> (get-workouts access_token)
        first
        (:workout_id)
        (get-workouts-full access_token)
        (:route_id)
        (get-route access_token)))

  ;;test getting workout time series
  (is (->> (get-workouts access_token)
        first
        (:workout_id)
        (get-workout-time-series access_token)
        ))

  )

;(run-tests 'notch.test-clj-mapmyfitness)

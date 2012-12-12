(ns notch.clj-mapmyfitness
  (:use clojure.set)
  (:use clojure.tools.logging)
  (:require [clojure.data.json :as json])
  (:require [oauth.client :as oauth] :reload)
  (:require [clj-http.client :as http])
  (:require [clojure.string :as str])
  (:require [clojure.java.io :as io]))


(do
  (def properties (-> (clojure.java.io/resource "mapmyfitness.properties.clj")
                    (clojure.java.io/reader)
                    (java.io.PushbackReader.)
                    (read)))

  (def ^{:dynamic true} *consumer*
    (oauth/make-consumer (:client_id properties)
      (:client_secret properties)
      "https://api.mapmyfitness.com/3.1/oauth/request_token"
      (str "https://api.mapmyfitness.com/3.1/oauth/access_token")
      "https://api.mapmyfitness.com/3.1/oauth/authorize"
      :hmac-sha1 ))

  (def api_uri "http://api.mapmyfitness.com/3.1"))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Authentication Calls
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn get-request-token
  "Get request token for oauth1 start"
  ([callback_url]
    (let [result (oauth/request-token *consumer* callback_url)]
      (when (and (contains? result :oauth_token )
              (contains? result :oauth_token_secret ))
        result
        ))))

(defn get-auth-uri
  "Send the user to this URL for first part of OAuth"
  ([request_token callback_url]
    (let [uri (oauth/user-approval-uri *consumer* (:oauth_token request_token))]
      (str uri "&oauth_callback=" (java.net.URLEncoder/encode callback_url ))
    )))

(defn get-access-token
  "Get the final access token for OAuth.
  Returns the token as a map if successful
  Returns nil otherwise"
  [request_token]
  (let [result (oauth/access-token *consumer* request_token)]
    result

    )
  )



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;HTTP Helper Calls
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn http-get [access_token path params]
  (let [uri (str api_uri path)
        ;;Add the api_key param, required by Mashery/BM
        params (merge params {:api_key (:key *consumer*)})
        query_params (merge
                        ;;Oauth Credentials
                        (oauth/credentials *consumer*
                              (:oauth_token access_token)
                              (:oauth_token_secret access_token)
                              :GET
                              uri
                              params)
                        params)]
    (debug "GET: " uri " " query_params)
    (-> (http/get uri {:query-params query_params})
        :body
        json/read-json)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;API Calls
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn get-user-info
  "Get basic user profile information"
  [access_token]
  (->> (http-get access_token "/users/get_user" {})
    :result
    :output
    :user
    ))

(defn get-workouts
  "Get workouts list"
  [access_token]
  (->> (http-get access_token "/workouts/get_workouts" {})
    :result
    :output
    :workouts
    ))

(defn get-workouts
  "Returns a list of workouts (runs)"
  ([access_token]
    (get-workouts access_token 0))
  ([access_token page_number]
    (get-workouts access_token page_number 25))
  ([access_token page_number page_size]
    (->> (http-get access_token "/workouts/get_workouts" {:start_record (* page_number page_size)
                                                          :limit page_size})
      :result
      :output
      :workouts)))

(defn get-workouts-full
  "Get details of single workout"
  [access_token workout_id]
  (->> (http-get access_token "/workouts/get_workout_full" {:workout_id workout_id})
    :result
    :output
    :workout
    ))

(defn get-route
  "Get a route"
  [access_token route_id]
  (->> (http-get access_token "/routes/get_route" {:route_id route_id})
    :result
    :output

    ))

(defn get-workout-time-series
  "Get a workout time series"
  [access_token workout_id]
  (->> (http-get access_token "/workouts/get_time_series" {:workout_id workout_id})
    :workout
    ))
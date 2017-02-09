(ns markov-bot.aws
  (:require [environ.core :refer [env]]
            [taoensso.faraday :as faraday]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.data.json :as json]))

(def client-opts
  {:access-key    (env :aws-access-key)
   :secret-key    (env :aws-access-secret)
   :endpoint      (env :aws-dynamodb-endpoint)})

(defn cache-tweets [user-id tweets]
  (faraday/put-item client-opts
                    :UserTweets
                    {:UserId user-id
                     :tweets (faraday/freeze {:vector tweets})}))

(defn get-tweets [user-id]
  (->> (faraday/get-item client-opts :UserTweets {:UserId user-id})
       :tweets :vector))

(get-tweets "12345")

(defn key->keyword [key-string]
  (-> key-string
      (string/replace #"([a-z])([A-Z])" "$1-$2")
      (string/replace #"([A-Z]+)([A-Z])" "$1-$2")
      (string/lower-case)
      (keyword)))

(defn lambda-handler [is os context handle-fn]
  (let [writer (io/writer os)]
    (-> (json/read (io/reader is) :key-fn key->keyword)
        (handle-fn)
        (json/write writer))
    (.flush writer)))

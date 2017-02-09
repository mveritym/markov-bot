(ns markov-bot.get-tweets-lambda
  (:require [markov-bot.twitter :as twitter]
            [clojure.data.json :as json]
            [clojure.string :as s]
            [clojure.java.io :as io])
  (:gen-class
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]
   :name get-tweets-lambda))

(defn input->users [inputStr]
  (if-not (s/blank? inputStr)
    (-> inputStr (s/split #",") vec)
    []))

(defn user->tweet [user]
  (hash-map
   (keyword user)
   (twitter/get-all-tweets-for-user user)))

(defn users->tweets [users]
  (->> users
       (map user->tweet)
       merge
       first))

(defn handle-event [params]
  (->> params
       (#(or (:users %) ""))
       input->users
       users->tweets))

(defn key->keyword [key-string]
  (-> key-string
      (s/replace #"([a-z])([A-Z])" "$1-$2")
      (s/replace #"([A-Z]+)([A-Z])" "$1-$2")
      (s/lower-case)
      (keyword)))

(defn -handleRequest [this is os context]
  (let [w (io/writer os)]
    (-> (json/read (io/reader is) :key-fn key->keyword)
        (handle-event)
        (json/write w))
    (.flush w)))

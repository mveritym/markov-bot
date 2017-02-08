(ns markov-bot.core
  (:require [markov-bot.make-bot :refer [make-bot]]
            [clojure.data.json :as json]
            [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]])
  (:gen-class
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]
   :name stream_handler))

(defn create-maps [type inputStr]
  (if (s/blank? inputStr)
    []
    (let [inputs (s/split inputStr #",")]
      (->> inputs
           (map (fn [input] (assoc {} type input)))
           vec))))

(defn get-bot-input [users searches]
  (let [uservec (create-maps :user users)
        searchvec (create-maps :search searches)]
    (into [] (concat uservec searchvec))))

(defn handle-event [params]
  (let [users (or (:users params) "")
        searches (or (:searches params) "")
        bot-input (get-bot-input users searches)
        bot (make-bot bot-input)]
    (bot 10)))

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

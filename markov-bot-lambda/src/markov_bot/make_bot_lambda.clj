(ns markov-bot.make-bot-lambda
  (:require [markov-bot.aws :as aws]
            [markov-bot.twitter :as twitter]
            [markov-bot.make-bot :as make-bot]
            [taoensso.faraday :as faraday]
            [clojure.string :as string])
  (:gen-class
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]
   :name make-bot-lambda))

(defn input->users [inputStr]
  (if-not (string/blank? inputStr)
    (-> inputStr (string/split #",") vec)
    []))

(defn user->tweet [user]
  (hash-map
   (keyword user)
   (twitter/get-all-tweets-for-user user)))

(defn user->id-str [user]
  (hash-map
   (keyword user)
   (twitter/get-id-str user)))

(defn handle-event [params]
  (let [bot-name (->> params :bot-name str)
        users    (->> params (#(or (:users %) "")) input->users)
        user-ids (->> users (map user->id-str) (into {}))]
    (aws/cache-bot bot-name user-ids)))

(defn -handleRequest [this is os context]
  (aws/lambda-handler is os context handle-event))

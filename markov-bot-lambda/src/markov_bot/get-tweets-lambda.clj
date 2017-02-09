(ns markov-bot.get-tweets-lambda
  (:require [markov-bot.aws :as aws]
            [markov-bot.twitter :as twitter]
            [taoensso.faraday :as faraday])
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

(defn -handleRequest [this is os context]
  (aws/lambda-handler is os context handle-event))

(ns markov-bot.run-bot-lambda
  (:require [markov-bot.aws :as aws]
            [markov-bot.make-bot :refer [make-bot]]
            [clojure.string :as string])
  (:gen-class
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]
   :name run-bot-lambda))

(defn handle-event [params]
  (let [bot-input (->> params :bot-name str (aws/get-bot))
        users     (->> bot-input :users :map keys (map name))
        bot       (->> users make-bot)]
    (bot 10)))

(defn -handleRequest [this is os context]
  (aws/lambda-handler is os context handle-event))

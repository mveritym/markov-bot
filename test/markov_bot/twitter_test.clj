(ns markov-bot.twitter-test
  (:require [clojure.test :refer :all]
            [markov-bot.twitter :refer :all]
            [twitter.api.restful :as twitter]))

(deftest test-get-tweets
  (testing "Get a person's tweets by name"
    (let [tweets ["Tweet1" "Tweet2" "Tweet3"]
          fake-api-call (fn [& args] {:body tweets})]
      (with-redefs [twitter/statuses-user-timeline fake-api-call]
        (is (= tweets (get-tweets "mveritym")))))))

(deftest test-parse-tweets
  (testing "Has text"
    (let [text "this is some text"
          tweet {:full_text text}]
      (is (= (parse-tweet tweet) {:text text :urls '()}))))
  (testing "Has a url"
    (let [url "hi.com"
          tweet {:entities {:urls [{:url url}]}}]
      (is (= (parse-tweet tweet) {:text nil :urls `(~url)}))))
  (testing "Has a media url"
    (let [url "media.com"
          tweet {:entities {:media [{:url url}]}}]
      (is (= (parse-tweet tweet) {:text nil :urls `(~url)})))))

(deftest test-remove-url
  (testing "No urls"
    (let [urls []
          text "This is some text."]
      (is (= (remove-url urls text) text))))
  (testing "One url"
    (let [urls ["url1"]
          text "A text with url1"]
      (is (= (remove-url urls text) "A text with"))))
  (testing "Multiple urls"
    (let [urls ["url1" "url2"]
          text "A text url1 that is url2 nice"]
      (is (= (remove-url urls text) "A text that is nice"))))
  (testing "Missing url"
    (let [urls ["url1"]
          text "No url in here"]
      (is (= (remove-url urls text) text)))))

(deftest test-strip-tweet
  (testing "Removes urls from a tweet"
    (let [tweet {:text "This is a tweet url1"
                 :urls '("url1")}]
      (is (= (strip-tweet tweet) "This is a tweet")))))

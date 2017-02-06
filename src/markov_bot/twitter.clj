(ns markov-bot.twitter
  (:require [twitter.api.restful :as twitter]
            [twitter.oauth :as oauth]
            [environ.core :refer [env]]
            [clojure.string :as string]))

(def creds (oauth/make-oauth-creds (env :consumer-key)
                                   (env :consumer-secret)
                                   (env :user-access-token)
                                   (env :user-access-secret)))

(defn get-tweets-for-user [user & max-id]
  (let [params (merge {:screen-name user
                       :count 200
                       :include-rts true
                       :tweet-mode "extended"}
                      (if (some? max-id) {:max-id (first max-id)} {}))
        tweets (twitter/statuses-user-timeline :oauth-creds creds
                                               :params params)]
    (:body tweets)))

(defn get-tweets-for-search [search-term & max-id]
  (let [params (merge {:q search-term
                       :count 100
                       :lang "en"
                       :tweet-mode "extended"}
                      (if (some? max-id) {:max-id (first max-id)} {}))
        tweets (twitter/search-tweets :oauth-creds creds :params params)]
    (->> tweets :body :statuses)))

(defn get-num-tweets [user-name]
  (let [user (twitter/users-show :oauth-creds creds
                                 :params {:screen-name user-name})]
    (->> user :body :statuses_count)))

(defn get-max-id [tweets]
  (let [ids (map :id tweets)]
    (apply min ids)))

(defn parse-tweet [tweet]
  (let [text (:full_text tweet)
        urls (map :url (-> tweet :entities :urls))
        media-urls (map :url (-> tweet :entities :media))]
    {:text text
     :urls (concat urls media-urls)}))

(defn remove-url [urls text]
  (let [pattern (->> urls (interpose \|) (apply str) re-pattern)]
    (->> pattern
         (#(string/replace text % ""))
         (#(string/split % #"\s+"))
         (remove string/blank?)
         (string/join " ")
         string/trim)))

(defn strip-tweet [tweet]
  (let [{:keys [text urls]} tweet]
    (remove-url (vec urls) text)))

(defn get-all-tweets [get-fn to-get max]
  (loop [result (get-fn to-get)]
    (if (< (count result) max)
      (let [max-id (get-max-id result)
            new-tweets (get-fn to-get max-id)]
        (recur (concat result new-tweets)))
      (->> result
           (map parse-tweet)
           (map strip-tweet)))))

(defn get-all-tweets-for-user [user]
  (let [num-tweets (get-num-tweets user)
        max (min num-tweets 3200)]
    (get-all-tweets get-tweets-for-user user max)))

(defn get-all-tweets-for-search [search-term]
  (get-all-tweets get-tweets-for-search search-term 3200))

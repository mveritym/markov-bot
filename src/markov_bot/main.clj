(ns markov-bot.main
  (:require [markov-bot.twitter :as twitter]
            [markov-bot.generator :refer :all]))

(defn get-tweets-for-inputs [inputs]
  (->> inputs
       (map get-tweets-by-input)
       (apply concat)))

(defn get-tweets-by-input [{:keys [user search]}]
  (if (some? user)
    (twitter/get-all-tweets-for-user user)
    (twitter/get-all-tweets-for-search search)))

(defn gen-chain-from-tweets [tweets]
  (->> (map clojure.string/lower-case tweets)
       (map text-to-word-chain)
       (apply merge-with clojure.set/union)))

(defn gen-rand-start-phrase [tweets]
  (->> tweets
       shuffle
       first
       (#(clojure.string/split % #" "))
       (take 2)
       (clojure.string/join " ")
       clojure.string/lower-case))

(defn reject-tweet [tweet orig-tweets]
  (let [is-same (contains? (set orig-tweets) tweet)]
    (if (= is-same true) (println "Removing" tweet))
    is-same))

(defn make-bot [inputs]
  (let [tweets (get-tweets-for-inputs inputs)
        chain (gen-chain-from-tweets tweets)
        make-text #(generate-text (gen-rand-start-phrase tweets) chain)]
    (fn [num]
      (loop [num-to-gen num
             result []]
        (let [new-tweets (repeatedly num make-text)
              should-reject #(reject-tweet % tweets)
              filtered (remove should-reject new-tweets)]
          (if (< (count filtered) num-to-gen)
            (recur (- num-to-gen (count filtered)) (concat result filtered))
            (concat result filtered)))))))

(def sad-trump (make-bot [{:user "realDonaldTrump"}
                          {:user "sosadtoday"}]))
(sad-trump 10)

(def react-bot (make-bot [{:user "dan_abramov"}
                          {:user "mxstbr"}
                          {:user "nikgraf"}
                          {:user "wesbos"}
                          {:user "matzatorski"}
                          {:user "jaredforsyth"}
                          {:user "acdlite"}
                          {:user "sebmck"}
                          {:user "ken_wheeler"}
                          {:user "kuizinas"}
                          {:user "mjackson"}
                          {:user "ryanflorence"}
                          {:user "sebmarkbage"}]))

(react-bot 10)

(def tech-bot (make-bot [{:user "TechFlunk"}
                         {:user "internetofshit"}]))
(tech-bot 10)

(def kittens-bot (make-bot [{:user "sebmck"}
                            {:user "iamdevloper"}]))
(kittens-bot 10)

(def kanye-trump (make-bot ["realDonaldTrump" "kanyewest"]))
(kanye-trump 10)

(def drump (make-bot ["realDonaldTrump" "dril"]))
(count (drump 10))
(drump 10)

(def djtrump (make-bot ["realDonaldTrump" "djsnake" "Skrillex" "IamAkademiks"]))
(djtrump 10)

(def tech-bot (make-bot tech-users))
(tech-bot 10)

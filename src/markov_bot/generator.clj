(ns markov-bot.generator)

(defn word-chain [word-transitions]
  (reduce (fn [r t] (merge-with clojure.set/union r
    (let [[a b c] t]
      {[a b] (if c #{c} #{})})))
    {}
    word-transitions))

(defn text-to-word-chain [input]
  (let [words (clojure.string/split input #"[\s|\n]")
        word-transitions (partition-all 3 1 words)]
    (word-chain word-transitions)))

(defn chain->text [chain]
  (apply str (interpose " " chain)))

(defn count-result-size [result suffix]
  (let [result-size (count (chain->text result))
        suffix-size (inc (count suffix))]
    (+ result-size suffix-size)))

(defn walk-chain [prefix chain result]
  (let [suffixes (get chain prefix)]
    (if (empty? suffixes)
      result
      (let [suffix (first (shuffle suffixes))
            new-prefix [(last prefix) suffix]]
        (if (>= (count-result-size result suffix) 140)
          result
          (recur new-prefix chain (conj result suffix)))))))

(defn generate-text [start-phrase word-chain]
  (let [prefix (clojure.string/split start-phrase #" ")
        result-chain (walk-chain prefix word-chain prefix)
        result-text (chain->text result-chain)]
    result-text))

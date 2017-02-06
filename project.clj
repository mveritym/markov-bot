(defproject markov-bot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.456"]
                 [twitter-api "0.7.9"]
                 [environ "1.1.0"]
                 [com.cemerick/url "0.1.1"]]
  :main ^:skip-aot markov-bot.core
  :target-path "target/%s"
  :plugins [[lein-environ "1.1.0"]
            [lein-cljsbuild "1.1.5"]]
  :cljsbuild {:builds [{:source-paths ["src"]
                        :compiler {:output-to "target/cljsbuild-main.js"
                                   :pretty-print true}}]}
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[com.jakemccrary/lein-test-refresh "0.18.1"]]
                   :env {:consumer-key "Kkhxl0qZ2AzaFEc6WXa5bDD8L"
                         :consumer-secret "a5OypITUyZ7fjL71p5TpUL5fzofOrnuKYvb92PUI3hDzaedCp6"
                         :user-access-token "823920672124628992-WBHtoq80X6KbXZ5A1m54axDHXsynyUh"
                         :user-access-secret "GqEjWCLV8qJoOF7aiYyRHKmnaI093iSWLJzEfdc0Opp9g"}}})

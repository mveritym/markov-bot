(defproject markov-bot "0.1.0"
  :description "Generate new tweets given twitter handles and/or search terms"
  :license {:name "MIT License"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [twitter-api "0.7.9"]
                 [environ "1.1.0"]
                 [com.cemerick/url "0.1.1"]
                 [org.slf4j/slf4j-log4j12 "1.7.12"]
                 [org.clojure/tools.logging "0.3.1"]
                 [log4j/log4j "1.2.17"]
                 [com.amazonaws/aws-lambda-java-core "1.1.0"]
                 [com.amazonaws/aws-lambda-java-events "1.3.0"]
                 [org.clojure/data.json "0.2.6"]]
  :plugins [[lein-environ "1.1.0"]]
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[com.jakemccrary/lein-test-refresh "0.18.1"]]
                   :env {:consumer-key "Kkhxl0qZ2AzaFEc6WXa5bDD8L"
                         :consumer-secret "a5OypITUyZ7fjL71p5TpUL5fzofOrnuKYvb92PUI3hDzaedCp6"
                         :user-access-token "823920672124628992-WBHtoq80X6KbXZ5A1m54axDHXsynyUh"
                         :user-access-secret "GqEjWCLV8qJoOF7aiYyRHKmnaI093iSWLJzEfdc0Opp9g"}}})

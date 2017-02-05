# markov-bot

A tool for generating twitter bots from input lists of Twitter usernames or search terms.

## Usage

Eventually this will be better but for now we'll just run this in `lein-repl`.

    $ lein-repl
    $ (def test-bot (make-bot [{:user "realDonaldTrump"} {:user "sosadtoday"}]))
    $ (pprint (test-bot 10))

## License

Copyright © 2017 Melissa Marshall

Distributed under the MIT License.

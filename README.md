# markov-bot

A tool for generating twitter bots from input lists of Twitter usernames or search terms.

## Usage

Eventually this will be better but for now we'll just run this in `lein-repl`.

    $ lein-repl
    $ (def test-bot (make-bot [{:user "realDonaldTrump"} {:user "sosadtoday"}]))
    $ (pprint (test-bot 10))

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

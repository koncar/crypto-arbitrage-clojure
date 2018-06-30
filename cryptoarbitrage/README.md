# cryptoarbitrage

The idea of a project is to have a service that would provide positive profit circles for trading crypto currencies between different exchanges.
Service would list all possible circles and also calculate potential profit that users can make by following those trading paterns.

At first 

BACKEND: Service is written in clojure and works as REST web server. It will use MongoDB for storing data.

FRONTEND: Front will be written in eather Clojure or Angular 4.


## Installation

BACK-END
run lein REPL

## Usage

run following commands in the repl:

(require '[cryptoarbitrage.core :as c])
(def server (c/init-server))

rerun project
(c/stop-server server)
(def server (c/init-server))

## License

Copyright © Stevan Koncar


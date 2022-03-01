(ns xtdb-grpc.server
  (:gen-class) ; for -main method in uberjar
  (:require [io.pedestal.http :as server]
            [xtdb.api :as xt]
            [xtdb-grpc.service :as service]))

(defonce node (xt/start-node {}))

(defn start-server [node host]
  (-> node
      (service/service host)
      server/create-server
      server/start))

#_{:clj-kondo/ignore [:unused-binding]}
(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (start-server node "localhost"))



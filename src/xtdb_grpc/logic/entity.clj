(ns xtdb-grpc.logic.entity
  (:require [xtdb.api :as xt]))

(defn xt-entity-history [node id order optionals]
  (if (nil? optionals)
    (xt/entity-history (xt/db node) id order)
    (xt/entity-history (xt/db node) id order optionals)))
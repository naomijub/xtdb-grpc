(ns xtdb-grpc.controllers
  (:require [xtdb.api :as xt]
            [xtdb-grpc.utils :as utils]
            [com.grpc.xtdb :refer [new-EntityResponse new-EntityHistoryResponse]]
            [xtdb-grpc.adapters.submit-tx :as adapters.submit]
            [xtdb-grpc.adapters.status :as adapters.status]
            [xtdb-grpc.adapters.entity :as adapters.entity]
            [xtdb-grpc.logic.entity :as logic.entity])
  (:gen-class))

(defn status [node]
  (-> (xt/status node)
      (adapters.status/edn->grpc)))

(defn submit-tx [node tx-ops]
  (->> tx-ops
       adapters.submit/grpc->edn
       (xt/submit-tx node)
       adapters.submit/edn->grpc))

(defn entity [node eid]
  (->> eid
       utils/edn-or-str
       (xt/entity (xt/db node))
       adapters.entity/ent-edn->grpc
       new-EntityResponse))

(defn entity-tx [node eid]
  (->> eid
       utils/edn-or-str
       (xt/entity-tx (xt/db node))
       adapters.entity/tx-edn->grpc))

(defn entity-history [node {{:keys [eid sort-order]} :grpc-params :as request}]
  (let [id (utils/edn-or-str eid)
        order (if (nil? sort-order) :asc sort-order)]
    (->> request
         adapters.entity/entity-history-optionals
         (logic.entity/xt-entity-history node id order)
         adapters.entity/history-list->grpc
         (assoc {} :list)
         new-EntityHistoryResponse)))
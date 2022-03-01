(ns xtdb-grpc.adapters.entity
  (:require [xtdb.api]
            [xtdb-grpc.utils :as utils])
  (:gen-class))

(defn tx-edn->grpc [edn]
  (->
   {:content-hash (str (:xtdb.api/content-hash edn))
    :valid-time (utils/->inst-str (:xtdb.api/valid-time edn))
    :tx-time (utils/->inst-str (:xtdb.api/tx-time edn))
    :tx-id (:xtdb.api/tx-id edn)}
   (utils/assoc-with-fn :xt-id (:xt/id edn) str)))

(defn ent-edn->grpc [edn]
  {:edn (str edn)})

(defn doc-or-nil [map]
  (when (seq map) map))

(defn entity-history-optionals [{{:keys [start-tx-id end-tx-id with-corrections start-tx-time start-valid-time end-tx-time with-docs end-valid-time]} :grpc-params :as _request}]
  (-> {}
      (utils/assoc-with-fn :with-corrections? with-corrections (fn [corretions] (if (false? corretions) nil corretions)))
      (utils/assoc-with-fn :with-docs? with-docs (fn [docs] (if (false? docs) nil docs)))
      (utils/assoc-with-fn :start-tx-id start-tx-id (fn [st-id] (if (or (nil? st-id) (= 0 st-id)) nil st-id)))
      (utils/assoc-with-fn :end-tx-id end-tx-id (fn [st-id] (if (or (nil? st-id) (= 0 st-id)) nil st-id)))
      (utils/assoc-with-fn :start-tx-time start-tx-time (fn [stt] (utils/->inst stt)))
      (utils/assoc-with-fn :start-valid-time start-valid-time (fn [svt] (utils/->inst svt)))
      (utils/assoc-with-fn :end-tx-time end-tx-time (fn [ett] (utils/->inst ett)))
      (utils/assoc-with-fn :end-valid-time end-valid-time (fn [evt] (utils/->inst evt)))
      doc-or-nil))

(defn history-list->grpc [list]
  (->> list
       (mapv (fn [edn]
               (let [doc (:xtdb.api/doc edn)
                     tx-edn (tx-edn->grpc edn)]
                 (utils/assoc-with-fn tx-edn :doc doc str))))))

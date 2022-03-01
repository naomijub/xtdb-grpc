(ns xtdb-grpc.adapters.submit-tx
  (:require [xtdb-grpc.utils :as utils]
            [xtdb.api :as xt])
  (:gen-class))

(defn ->document [{:keys [document]}]
  (utils/value->edn document))

(defn ->put [transaction]
  (let [record         (get transaction :put)
        document       (->document record)
        valid-time     (-> record :valid-time  utils/->inst)
        end-valid-time (when (utils/not-nil? valid-time) (-> record :end-valid-time  utils/->inst))]
    #_{:clj-kondo/ignore [:unresolved-namespace]}
    (filterv utils/not-nil? [::xt/put document valid-time end-valid-time])))

(defn ->match [transaction]
  (let [record         (get transaction :match)
        xt-id          (:document-id record)
        document       (-> record :document-match utils/value->edn)
        valid-time     (-> record :valid-time  utils/->inst)]
    #_{:clj-kondo/ignore [:unresolved-namespace]}
    (filterv utils/not-nil? [::xt/match xt-id document valid-time])))

(defn ->delete [transaction]
  (let [record         (get transaction :delete)
        document-id       (:document-id record)
        valid-time     (-> record :valid-time  utils/->inst)
        end-valid-time (when (utils/not-nil? valid-time) (-> record :end-valid-time  utils/->inst))]
    #_{:clj-kondo/ignore [:unresolved-namespace]}
    (filterv utils/not-nil? [::xt/delete document-id valid-time end-valid-time])))

(defn ->evict [transaction]
  (let [record         (get transaction :evict)
        document-id       (:document-id record)]
    #_{:clj-kondo/ignore [:unresolved-namespace]}
    [::xt/evict document-id]))

(defn ->call-fn [transaction]
  (let [record         (get transaction :function)
        xt-id       (:xt-id record)
        args       (mapv utils/value->edn (:arguments record))]
    #_{:clj-kondo/ignore [:unresolved-namespace]}
    (apply conj [::xt/fn xt-id] args)))

(defn ->put-fn [transaction]
  (let [record         (get transaction :put-function)
        xt-id       (:xt-id record)
        xt-fn       (-> record :xt-fn utils/tokenize)]
    #_{:clj-kondo/ignore [:unresolved-namespace]}
    [::xt/put {:xt/id xt-id :xt/fn xt-fn}]))

(defn transaction-type [transaction]
  (condp #(get %2 %1) transaction
    :put      (->put transaction)
    :match    (->match transaction)
    :delete   (->delete transaction)
    :evict    (->evict transaction)
    :function (->call-fn transaction)
    :put-function (->put-fn transaction)
    :else     (throw
               (ex-info "Unknown transaction type"
                        {:execution-id        :unknown-transaction-type
                         :available-transactions #{:put :match :delete :evict :put-function :function}}))))

(defn edn->grpc [edn]
  {:tx-id (:xtdb.api/tx-id edn)
   :tx-time (utils/->inst-str (:xtdb.api/tx-time edn))})

(defn grpc->edn [grpc]
  (mapv (comp transaction-type :transaction-type) grpc))

(ns xtdb-grpc.utils
  (:require [clojure.instant :refer [read-instant-date]]
            [clojure.string :as str])
  (:import java.text.SimpleDateFormat))

(defmacro tokenize [x] `(let [x# ~x] x#))

(defn assoc-some
  "Associates a key with a value in a map, if and only if the value is not nil."
  ([m k v]
   (if (nil? v) m (assoc m k {:option {:some v}}))))

(defn assoc-with-fn
  "Associates a key in a map if funtion applied to value is a non nil return"
  ([m k v f]
   (let [fn-v (f v)]
     (if (or (nil? v) (nil? fn-v)) m (assoc m k fn-v)))))

(defn nil->default
  ([m k v default]
   (if (nil? v) (assoc m k default) (assoc m k v))))

(defn not-nil? [value]
  (not (nil? value)))

(defn ->inst [value]
  (try
    (read-instant-date value)
    (catch Exception _e nil)))

(defn ->inst-str [inst]
  (.format (SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") inst))

(defn edn-or-str [value]
  (if (str/starts-with? value ":")
    (keyword (subs value 1))
    value))

(defn value->edn [proto-json]
  (let [kind (:kind proto-json)]
    (case (-> kind keys first)
      :struct-value (->> kind :struct-value :fields (reduce (fn [coll [k v]] (assoc coll (edn-or-str k) (value->edn v))) {}))
      :string-value (->> kind :string-value str edn-or-str)
      :list-value (->> kind :list-value :values (reduce (fn [coll v] (conj coll (value->edn v))) []))
      :null-value nil
      :bool-value (->> kind :bool-value boolean)
      :number-value (->> kind :number-value num))))

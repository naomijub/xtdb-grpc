(ns xtdb-grpc.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]

            [xtdb-grpc.controllers :as controllers]

            ;; -- PROTOC-GEN-CLOJURE --
            [protojure.pedestal.core :as protojure.pedestal]
            [protojure.pedestal.routes :as proutes]
            [com.grpc.xtdb.GrpcApi.server :as grpc-api])
  (:gen-class))

(defn health-check-page
  [_request]
  (ring-resp/response "healthy"))

;; -- PROTOC-GEN-CLOJURE --

(deftype XtdbGrpcAPI [node]
  grpc-api/Service
  (status [_this _request]
    {:status 200
     :body (controllers/status node)})
  (submit_tx [_this {{:keys [tx-ops]} :grpc-params :as _request}]
    {:status 200
     :body (controllers/submit-tx node tx-ops)})
  (entity_tx [_this {{:keys [eid]} :grpc-params :as _request}]
    {:status 200
     :body (controllers/entity-tx node eid)})
  (entity [_this {{:keys [eid]} :grpc-params :as _request}]
    {:status 200
     :body (controllers/entity node eid)})
  (entity_history [_this req]
    {:status 200
     :body (controllers/entity-history node req)}))

(def common-interceptors [(body-params/body-params) http/html-body])

;; Tabular routes
;; Health check
(def routes #{["/health-check" :get (conj common-interceptors `health-check-page)]})

;; -- PROTOC-GEN-CLOJURE --
(defn grpc-routes [node] (reduce conj routes (proutes/->tablesyntax {:rpc-metadata grpc-api/rpc-metadata :interceptors common-interceptors :callback-context (XtdbGrpcAPI. node)})))
(defn service [node host]
  {:env :prod
   ::http/routes (grpc-routes node)

              ;; -- PROTOC-GEN-CLOJURE --
              ;; We override the chain-provider with one provided by protojure.protobuf
              ;; and based on the Undertow webserver.  This provides the proper support
              ;; for HTTP/2 trailers, which GRPCs rely on.  A future version of pedestal
              ;; may provide this support, in which case we can go back to using
              ;; chain-providers from pedestal.
   ::http/type protojure.pedestal/config
   ::http/chain-provider protojure.pedestal/provider

   ::http/host host
   ::http/port 50051})

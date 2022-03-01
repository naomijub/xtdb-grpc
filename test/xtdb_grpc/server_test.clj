(ns xtdb-grpc.server-test
  (:require [com.grpc.xtdb.GrpcApi.client :as client]
            [protojure.grpc.client.providers.http2 :as grpc-client]))

(deref (client/status @(grpc-client/connect {:uri (str "http://localhost:" 50051)}) {}))
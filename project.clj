(def protobuf-version "3.19.1")

(defproject xtdb-grpc "0.0.1-SNAPSHOT"
  :description "XTDB gRPC Server"
  :url "http://github.com/naomijub/xtdb-grpc"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :plugins [[com.appsflyer/lein-protodeps "1.0.3"]
            [lein-cljfmt "0.8.0"]]
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [io.pedestal/pedestal.service "0.5.10"]
                 [com.xtdb/xtdb-core "1.20.0"]
                 [com.appsflyer/pronto "2.0.10"]
                 [com.google.protobuf/protobuf-java ~protobuf-version]

                 ;; Remove this line and uncomment one of the next lines to
                 ;; use Immutant or Tomcat instead of Jetty:
                 [io.pedestal/pedestal.jetty "0.5.10"]
                 ;; [io.pedestal/pedestal.immutant "0.5.10"]
                 ;; [io.pedestal/pedestal.tomcat "0.5.10"]

                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.26"]
                 [org.slf4j/jcl-over-slf4j "1.7.26"]
                 [org.slf4j/log4j-over-slf4j "1.7.26"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  ;; If you use HTTP/2 or ALPN, use the java-agent to pull in the correct alpn-boot dependency
  ;:java-agents [[org.mortbay.jetty.alpn/jetty-alpn-agent "2.0.5"]]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "xtdb-grpc.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.10"]]}
             :test {:dependencies [[io.pedestal/pedestal.service-tools "0.5.10"]]}
             :uberjar {:aot [xtdb-grpc.server]}}
  :main ^{:skip-aot true} xtdb-grpc.server
  
  :lein-protodeps {:output-path "src/java"
                   :proto-version ~protobuf-version
                   :repos {:examples {:repo-type :filesystem
                                      :config {:path "."}
                                      :proto-paths ["resources"]
                                      :dependencies [resources]}}})

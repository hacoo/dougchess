(defproject dougchess "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.374"]
                 [org.clojure/data.json "0.2.6"]
                 [com.keminglabs/zmq-async "0.1.0"]]
  :main ^:skip-aot dougchess.core
  :resource-paths ["resources/jars/client.jar"
                   "resources/jars/jeromq.jar"
                   "resources/jars/json.jar"]
  :java-source-paths ["src/java"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
